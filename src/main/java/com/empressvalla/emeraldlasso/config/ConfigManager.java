package com.empressvalla.emeraldlasso.config;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class is responsible for providing
 * access to the common config values as
 * well as building the config file itself.
 */
public class ConfigManager {

    /**
     * Responsible for storing the spec for the common config. Used for registering config.
     */
    public static final ForgeConfigSpec SPEC_COMMON;

    /**
     * Responsible for storing the config value which controls how many entities
     * can be stored in the lasso.
     */
    private static final ForgeConfigSpec.ConfigValue<Integer> NUM_ENTITIES_ALLOWED;

    /**
     * Responsible for storing the config value which controls whether
     * the lasso has durability and can be broken.
     */
    private static final ForgeConfigSpec.ConfigValue<Boolean> HAS_DURABILITY;

    /**
     * Responsible for storing the config value which controls whether
     * the lasso can store all entities.
     */
    private static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_ALL_ENTITIES;

    /**
     * Responsible for storing the config value which controls whether
     * the lasso will use the entity health system. I.E the entity will
     * need to be lowered to a certain heart threshold before it can be
     * stored.
     */
    private static final ForgeConfigSpec.ConfigValue<Boolean> ENTITY_HEALTH_SYSTEM;

    /**
     * Responsible for storing the config value which controls the
     * minimum health threshold for an entity to be stored.
     *
     * This will only be used if the ENTITY_HEALTH_SYSTEM is set to true.
     *
     * @see ConfigManager#ENTITY_HEALTH_SYSTEM
     */
    private static final ForgeConfigSpec.ConfigValue<Double> MIN_ENTITY_HEALTH;

    /**
     * Responsible for storing the config value which controls which entities can be picked
     * up by the lasso.
     */
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> VANILLA_ENTITY_WHITELIST;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOD_ENTITY_WHITELIST;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * The default list of entities that will be added to the config file when first generated.
     */
    private static final List<String> DEFAULT_ENTITIES = new ArrayList<>() {
        {
            add("minecraft:pig");
            add("minecraft:bee");
            add("minecraft:sheep");
            add("minecraft:cow");
            add("minecraft:wolf");
            add("minecraft:cat");
            add("minecraft:ocelot");
            add("minecraft:chicken");
            add("minecraft:villager");
            add("minecraft:cod");
            add("minecraft:dolphin");
            add("minecraft:rabbit");
            add("minecraft:polar_bear");
            add("minecraft:squid");
        }
    };

    static {
        BUILDER.push("Emerald Lasso Config");

        NUM_ENTITIES_ALLOWED = BUILDER.comment("How many entities should the lasso store?")
                                      .defineInRange("num_entities_allowed", 1, 1, 5);

        HAS_DURABILITY = BUILDER.comment("Should the lasso have a durability? Set to false if you'd like it to take no damage")
                                .define("has_durability", true);


        ALLOW_ALL_ENTITIES = BUILDER.comment("Do you want the lasso to pick up all entity types? Note this will bypass whatever is in the whitelists")
                                    .define("allow_all_entities", false);

        ENTITY_HEALTH_SYSTEM = BUILDER.comment("Do you want the lasso to use the entity health system? I.E An entity must be lowered to a certain heart threshold to be stored in the lasso")
                .define("entity_health_system", false);

        MIN_ENTITY_HEALTH = BUILDER.comment("What should the minimum health threshold be for an entity to be stored in the lasso?")
                .defineInRange("min_entity_health", 1.5, 1.0, 500.0);

        //The validator checks that the input is a string and that it follows the expected resource pattern. I.E minecraft:pig
        Predicate<Object> entityWhitelistValidator =  s -> s instanceof String && ((String) s).matches("[a-z0-9]+:[a-z_]+");

        VANILLA_ENTITY_WHITELIST = BUILDER.comment("Add the vanilla entities you want the lasso to pick up. Use the same pattern as the examples below.")
                                          .defineList("vanilla_entity_whitelist", DEFAULT_ENTITIES, entityWhitelistValidator);

        MOD_ENTITY_WHITELIST = BUILDER.comment("Same as above but for mod entities. You can put them in either, but this allows you to manage them better.")
                                      .defineList("mod_entity_whitelist", Collections.emptyList(), entityWhitelistValidator);

        BUILDER.pop();

        SPEC_COMMON = BUILDER.build();
    }

    /**
     * This method is responsible for building up a list
     * of EntityTypes from the resource tags that
     * are provided in the VANILLA_ENTITY_WHITELIST and
     * MOD_ENTITY_WHITELIST config values.
     *
     * @see ConfigManager#VANILLA_ENTITY_WHITELIST
     *
     * @return A list of the entity types extracted from the whitelist.
     */
    public static List<EntityType<?>> getEntityWhiteList() {
        List<? extends String> whitelist = VANILLA_ENTITY_WHITELIST.get();

        List<? extends String> modWhitelist = MOD_ENTITY_WHITELIST.get();

        List<EntityType<?>> allowedEntityTypes = new ArrayList<>();

        for(String entityResourceTag : whitelist) {
            Optional<EntityType<?>> currentEntity = EntityType.byString(entityResourceTag);

            if(currentEntity.isEmpty()) {
               LOGGER.error("The entity could not be found with resource {}, please check you provided the correct tag. Skipping Entity", entityResourceTag);
               continue;
            }

            allowedEntityTypes.add(currentEntity.get());
        }

        for(String entityResourceTag : modWhitelist) {
            Optional<EntityType<?>> currentEntity = EntityType.byString(entityResourceTag);

            if(currentEntity.isEmpty()) {
                LOGGER.error("The entity could not be found with resource {}, please check you provided the correct tag. Skipping Entity", entityResourceTag);
                continue;
            }

            allowedEntityTypes.add(currentEntity.get());
        }

        return allowedEntityTypes;
    }

    /**
     * This method is responsible for returning the number
     * which was provided in the NUM_ENTITIES_ALLOWED
     * config value.
     *
     * @see ConfigManager#NUM_ENTITIES_ALLOWED
     *
     * @return The integer value retrieved from NUM_ENTITIES_ALLOWED.
     */
    public static int getNumAllowedEntities() {
        return NUM_ENTITIES_ALLOWED.get();
    }

    /**
     * This method is responsible for returning the number
     * which was provided in the MIN_ENTITY_HEALTH
     * config value.
     *
     * This will only be used if the ENTITY_HEALTH_SYSTEM is set to true.
     *
     * @see ConfigManager#NUM_ENTITIES_ALLOWED
     * @see ConfigManager#ENTITY_HEALTH_SYSTEM
     *
     * @return The integer value retrieved from NUM_ENTITIES_ALLOWED.
     */
    public static Double getMinEntityHealth() {
        return MIN_ENTITY_HEALTH.get();
    }

    /**
     * This method is responsible for returning the boolean
     * which was provided in the HAS_DURABILITY
     * config value.
     *
     * @see ConfigManager#HAS_DURABILITY
     *
     * @return The boolean value retrieved from HAS_DURABILITY.
     */
    public static boolean hasDurability() {
        return HAS_DURABILITY.get();
    }

    /**
     * This method is responsible for returning the boolean
     * which was provided in the ALLOW_ALL_ENTITIES
     * config value.
     *
     * @see ConfigManager#ALLOW_ALL_ENTITIES
     *
     * @return The boolean value retrieved from ALLOW_ALL_ENTITIES.
     */
    public static boolean allEntitiesAllowed() {
        return ALLOW_ALL_ENTITIES.get();
    }

    /**
     * This method is responsible for returning the boolean
     * which was provided in the ENTITY_HEALTH_SYSTEM
     * config value.
     *
     * @see ConfigManager#ENTITY_HEALTH_SYSTEM
     *
     * @return The boolean value retrieved from ENTITY_HEALTH_SYSTEM.
     */
    public static boolean entityHealthSystemEnabled() {
        return ENTITY_HEALTH_SYSTEM.get();
    }

}

