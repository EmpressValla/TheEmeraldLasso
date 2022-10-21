package com.empressvalla.emeraldlasso.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class is responsible for providing all functionality related to the Emerald Lasso custom
 * item. Including pickup and release.
 *
 * @implNote There is a constant provided for number allowed, which currently
 * just stores 1 but this will be replaced when a config is added for the mod.
 *
 * @see EmeraldLassoItem#NUMBER_ALLOWED
 */
public class EmeraldLassoItem extends Item {

    /**
     * This constant represents the number of entities the lasso can store.
     */
    private static final int NUMBER_ALLOWED = 1;

    public EmeraldLassoItem(Properties pProperties) {
        super(pProperties);
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

        tooltip.add(new TranslatableComponent("emeraldlasso.tooltips.pickup").setStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW)));

        tooltip.add(new TranslatableComponent("emeraldlasso.tooltips.release").setStyle(Style.EMPTY.applyFormat(ChatFormatting.YELLOW)));

        ListTag entityList = getEntities(itemStack);

        if(!entityList.isEmpty()) {

            for (Tag currentEntityTag : entityList) {
                tooltip.add(new TranslatableComponent("emeraldlasso.tooltips.entities",
                        EntityType.by((CompoundTag) currentEntityTag)
                                  .map(EntityType::getDescription)
                                  .orElse(new TextComponent("Unknown Entity")))
                                  .setStyle(Style.EMPTY.applyFormat(ChatFormatting.LIGHT_PURPLE)));
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity targetEntity) {
        InteractionHand hand = player.getUsedItemHand();

        ItemStack heldItemStack = player.getItemInHand(hand);

        ListTag entityList = getEntities(heldItemStack);

        boolean requirementsMet = hand == InteractionHand.MAIN_HAND && isEntityValid(targetEntity)
                && entityList.size() != NUMBER_ALLOWED;

        if(requirementsMet) {
            if(!player.level.isClientSide()) {
                targetEntity.stopRiding();

                targetEntity.ejectPassengers();

                CompoundTag entityTag = new CompoundTag();

                targetEntity.save(entityTag);

                entityList.add(entityTag);

                targetEntity.remove(RemovalReason.DISCARDED);

                saveEntities(heldItemStack, entityList);
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

        if(entityList.isEmpty()) {
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
    private boolean isEntityValid(Entity target) {
        //Remove these cumbersome checks by adding a config file later
        boolean mobCheck =  target instanceof Animal || target instanceof WaterAnimal ||
                            target instanceof AbstractVillager || target instanceof NeutralMob;

        return target.isAlive() && !target.isInWall() && mobCheck;
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
    private ListTag getEntities(ItemStack itemStack) {
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
    private void saveEntities(ItemStack itemStack, ListTag entityList) {
        CompoundTag stackTag = itemStack.getOrCreateTag();

        stackTag.put(NBTIdentifiers.ENTITIES, entityList);
    }

    /**
     * An inner class for containing NBT key identifiers
     */
    private static class NBTIdentifiers {
        /**
         * The key identifier for entities.
         */
        public static final String ENTITIES = "entities";
    }

}
