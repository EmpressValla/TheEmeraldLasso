package com.empressvalla.emeraldlasso.item.advanced;

import com.empressvalla.emeraldlasso.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for providing all functionality related to the Emerald Lasso custom
 * item. Including pickup and release.
 *
 */
public class EmeraldLassoItem extends Item {

    /**
     * This list holds the whitelist of entity types which are allowed in the lasso.
     * This will start off empty but be populated from the config file the first
     * time an entity is interacted with.
     *
     * @see ConfigManager#getEntityWhiteList() For more details.
     */
    private static List<EntityType<?>> entityWhitelist = new ArrayList<>();

    public EmeraldLassoItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(250));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        ListTag entityList = getEntities(itemStack);

        return !entityList.isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltip, flag);

        tooltip.add(MutableComponent.create(new TranslatableContents("emeraldlasso.tooltips.pickup"))
                                    .setStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW)));

        tooltip.add(MutableComponent.create(new TranslatableContents("emeraldlasso.tooltips.release"))
                                    .setStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW)));

        ListTag entityList = getEntities(itemStack);

        if(!entityList.isEmpty()) {
            for (Tag currentEntityTag : entityList) {
                tooltip.add(MutableComponent.create(new TranslatableContents("emeraldlasso.tooltips.entities",
                        EntityType.by((CompoundTag) currentEntityTag)
                                  .map(EntityType::getDescription)
                                  .orElse(MutableComponent.create(new LiteralContents("Unknown Entity")))))
                                  .setStyle(Style.EMPTY.applyFormat(ChatFormatting.LIGHT_PURPLE)));
            }
        }
    }


    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity targetEntity) {
        InteractionHand hand = player.getUsedItemHand();

        ListTag entityList = getEntities(stack);

        boolean requirementsMet = hand == InteractionHand.MAIN_HAND
                                  && isEntityValid(targetEntity)
                                  && entityList.size() != ConfigManager.getNumAllowedEntities();

        Level level = player.getLevel();

        if(ConfigManager.entityHealthSystemEnabled()) {
            // Reminder: We already checked if the target is of type LivingEntity in isEntityValid, so we can safely cast it.
            LivingEntity livingEntityTarget = (LivingEntity) targetEntity;

            float health = livingEntityTarget.getHealth();

            double minEntityHealth = ConfigManager.getMinEntityHealth();

            requirementsMet = requirementsMet && health <= minEntityHealth;

            if(health > minEntityHealth && !level.isClientSide()){
                player.sendSystemMessage(
                         MutableComponent.create(new TranslatableContents("emeraldlasso.messages.entity_health_high", health, minEntityHealth))
                                         .setStyle(Style.EMPTY.applyFormat(ChatFormatting.RED)));
            }
        }

        if(requirementsMet) {

            if(!level.isClientSide()) {
                targetEntity.stopRiding();

                targetEntity.ejectPassengers();

                CompoundTag entityTag = new CompoundTag();

                targetEntity.save(entityTag);

                entityList.add(entityTag);

                targetEntity.remove(RemovalReason.DISCARDED);

                saveEntities(stack, entityList);

                BlockPos position = player.getOnPos();

                level.playSound(null, position, SoundEvents.ENDERMAN_TELEPORT, SoundSource.AMBIENT, 0.5f, 1f);
            }

            return true;
        }

        return super.onLeftClickEntity(stack, player, targetEntity);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if(level.isClientSide()) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();

        BlockPos position = context.getClickedPos().relative(context.getClickedFace());

        if(player == null) {
            return InteractionResult.FAIL;
        }

        InteractionHand hand = context.getHand();

        ItemStack heldItemStack = player.getItemInHand(hand);

        ListTag entityList = getEntities(heldItemStack);

        if(entityList.isEmpty() || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.FAIL;
        }

        int itemIndex = entityList.size() - 1;

        CompoundTag entityTag = entityList.getCompound(itemIndex);

        entityList.remove(itemIndex);

        saveEntities(heldItemStack, entityList);

        Entity entityToLoad = EntityType.loadEntityRecursive(entityTag, level, entity -> entity);

        if(entityToLoad == null) {
            return InteractionResult.FAIL;
        }

        entityToLoad.setPos(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);

        level.addFreshEntity(entityToLoad);

        if(ConfigManager.hasDurability()) {
            heldItemStack.hurtAndBreak(5, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        level.playSound(null, position, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 0.5f, 1f);

        return InteractionResult.SUCCESS;
    }

    /**
     * This method is responsible for determining
     * if the entity the player is attempting to pick
     * up can be stored in the lasso.
     *
     * @param target The target entity the player is trying to store.
     *
     * @return {@code true} if the entity is valid {@code false} otherwise.
     */
    private static boolean isEntityValid(Entity target) {
        boolean baseCheck = target instanceof LivingEntity && target.isAlive() && !target.isInWall();

        if(ConfigManager.allEntitiesAllowed()) {
            return baseCheck;
        }

        boolean whitelistCheck = false;

        if(entityWhitelist.isEmpty()) {
            entityWhitelist = ConfigManager.getEntityWhiteList();
        }

        for(EntityType<?> type : entityWhitelist) {
            if(target.getType().equals(type)) {
                whitelistCheck = true;

                break;
            }
        }

        return baseCheck && whitelistCheck;
    }

    /**
     * This method will retrieve the entity list for
     * a given item stack. If it does not
     * already have a list then an empty one will
     * be stored.
     *
     * @param itemStack The item stack to get the entity list from.
     *
     * @return The list of entities stored in the item stack.
     */
    private static ListTag getEntities(ItemStack itemStack) {
        CompoundTag stackTag = itemStack.getOrCreateTag();

        if(stackTag.contains(NBTIdentifiers.ENTITIES, CompoundTag.TAG_LIST)) {
            return stackTag.getList(NBTIdentifiers.ENTITIES, CompoundTag.TAG_COMPOUND);
        }

        ListTag entityList = new ListTag();

        stackTag.put(NBTIdentifiers.ENTITIES, entityList);

        return entityList;
    }
    
    /**
     * This method is responsible for saving
     * an entity list for a given item stack.
     *
     * @param itemStack The respective item stack.
     *
     * @param entityList The entity list to be saved to the item stack.
     */
    private static void saveEntities(ItemStack itemStack, ListTag entityList) {
        CompoundTag stackTag = itemStack.getOrCreateTag();

        stackTag.put(NBTIdentifiers.ENTITIES, entityList);
    }

    /**
     * An inner class for containing NBT key identifiers
     */
    private static class NBTIdentifiers {
        /**
         * The key identifier for entities. This is the key which will point
         * to the list of entities that are stored in an item stack.
         */
        public static final String ENTITIES = "entities";
    }

}
