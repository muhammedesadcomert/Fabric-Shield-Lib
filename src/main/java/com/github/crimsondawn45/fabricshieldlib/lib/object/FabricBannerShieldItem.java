package com.github.crimsondawn45.fabricshieldlib.lib.object;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

import static com.github.crimsondawn45.fabricshieldlib.lib.object.RepairType.getRepairType;

/**
 * Pre-made class for quickly making custom shields which support banners.
 */
public class FabricBannerShieldItem extends Item implements FabricShield {

    private final int coolDownTicks;
    private final int enchantability;

    //Repair stuff
    private Item[] repairItems;
    private TagKey<Item> repairTag;
    private Ingredient repairIngredients;
    private Collection<TagKey<Item>> repairTags;

    private final RepairItemType repairType;

    /**
     * @param settings       item settings.
     * @param coolDownTicks  ticks shield will be disabled for when it with axe. Vanilla: 100
     * @param enchantability enchantability of shield. Vanilla: 9
     * @param repairItems    item(s) for repairing shield.
     */
    public FabricBannerShieldItem(Settings settings, int coolDownTicks, int enchantability, Item... repairItems) {
        super(settings);

        //Register dispenser equip behavior
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);

        //Register that item has a blocking model
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ModelPredicateProviderRegistry.register(new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
            });
        }

        this.coolDownTicks = coolDownTicks;
        this.enchantability = enchantability;
        this.repairType = RepairItemType.ARRAY;
        this.repairItems = repairItems;
    }

    /**
     * @param settings      item settings.
     * @param coolDownTicks ticks shield will be disabled for when it with axe. Vanilla: 100
     * @param material      tool material.
     */
    public FabricBannerShieldItem(Settings settings, int coolDownTicks, ToolMaterial material) {
        super(settings.maxDamage(material.getDurability())); //Make durability match material

        //Register dispenser equip behavior
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);

        //Register that item has a blocking model
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ModelPredicateProviderRegistry.register(new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
            });
        }

        this.coolDownTicks = coolDownTicks;
        this.enchantability = material.getEnchantability();
        this.repairType = RepairItemType.INGREDIENT;
        this.repairIngredients = material.getRepairIngredient();
    }

    /**
     * @param settings       item settings.
     * @param coolDownTicks  ticks shield will be disabled for when it with axe. Vanilla: 100
     * @param enchantability enchantability of shield. Vanilla: 9
     * @param repairItemTag  item tag for repairing shield.
     */
    public FabricBannerShieldItem(Settings settings, int coolDownTicks, int enchantability, TagKey<Item> repairItemTag) {
        super(settings);

        //Register dispenser equip behavior
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);

        //Register that item has a blocking model
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ModelPredicateProviderRegistry.register(new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
            });
        }

        this.coolDownTicks = coolDownTicks;
        this.repairType = RepairItemType.TAG;
        this.repairTag = repairItemTag;
        this.enchantability = enchantability;
    }

    /**
     * @param settings       item settings.
     * @param coolDownTicks  ticks shield will be disabled for when it with axe. Vanilla: 100
     * @param enchantability enchantability of shield. Vanilla: 9
     * @param repairItemTags list of item tags for repairing shield.
     */
    public FabricBannerShieldItem(Settings settings, int coolDownTicks, int enchantability, Collection<TagKey<Item>> repairItemTags) {
        super(settings);

        //Register dispenser equip behavior
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);

        //Register that item has a blocking model
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ModelPredicateProviderRegistry.register(new Identifier("blocking"), (itemStack, clientWorld, livingEntity, i) -> {
                return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
            });
        }

        this.coolDownTicks = coolDownTicks;
        this.repairType = RepairItemType.TAG_ARRAY;
        this.repairTags = repairItemTags;
        this.enchantability = enchantability;
    }

    public String getTranslationKey(ItemStack stack) {
        if (stack.getSubNbt("BlockEntityTag") != null) {
            String key = this.getTranslationKey();
            return key + "." + getColor(stack).getName();
        } else {
            return super.getTranslationKey(stack);
        }
    }

    public static DyeColor getColor(ItemStack stack) {
        return DyeColor.byId(stack.getOrCreateSubNbt("BlockEntityTag").getInt("Base"));
    }

    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }

    @Override
    public void appendShieldTooltip(ItemStack stack, List<Text> tooltip, TooltipContext context) {
    }

    @Override
    public int getCoolDownTicks() {
        return this.coolDownTicks;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return getRepairType(ingredient, this.repairType, this.repairItems, this.repairTag, this.repairIngredients, this.repairTags);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return !stack.hasEnchantments();
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public boolean supportsBanner() {
        return true;
    }
}