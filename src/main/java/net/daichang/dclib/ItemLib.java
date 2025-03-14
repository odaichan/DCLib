package net.daichang.dclib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public interface ItemLib {
    default void itemClassSet(Item targetItem, Class<?> newClazz) {
        Helper.setClass(targetItem, newClazz);
    }

    default void inventoryItemGet(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (player.level().isClientSide()) player.displayClientMessage(Component.literal(" " + stack.getItem().getClass()), false);
        }
    }

    String COOLDOWN_TAG = "cooldown";
    String TICK_TAG = "tick";
    String AUTO_TAG = "auto";
    String RENDER_BAR_TAG = "render_bar";
    String RENDER_BAR_WHEN_ENDS_TAG = "render_bar_when_ends";

    /**
     * 最大冷却时间
     */
    int cooldownTime();

    private void initTag(ItemStack stack) {
        if (!stack.getOrCreateTag().contains(DCLibMod.MOD_ID)) {
            stack.getOrCreateTag().put(DCLibMod.MOD_ID, new CompoundTag());
        }
        if (!stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).contains(COOLDOWN_TAG)) {
            stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putInt(COOLDOWN_TAG, 0);
        }
        if (!stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).contains(TICK_TAG)) {
            stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putBoolean(TICK_TAG, true);
        }
        if (!stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).contains(RENDER_BAR_WHEN_ENDS_TAG)) {
            stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putBoolean(RENDER_BAR_WHEN_ENDS_TAG, false);
        }
        if (!stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).contains(AUTO_TAG)) {
            stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putBoolean(AUTO_TAG, false);
        }
        if (!stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).contains(RENDER_BAR_TAG)) {
            stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putBoolean(RENDER_BAR_TAG, true);
        }
    }

    /**
     * 判断当前物品栈是否处于冷却状态
     */
    default boolean isCooldown(ItemStack stack) {
        return getCurrentCooldown(stack) > 0;
    }

    /**
     * 获取当前物品栈的冷却时间
     */
    default int getCurrentCooldown(ItemStack stack) {
        return stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).getInt(COOLDOWN_TAG);
    }

    /**
     * 使当前物品栈开始冷却，如果物品栈正处于冷却状态，则重新开始冷却
     */
    default void startCooldown(Player player, ItemStack stack) {
        initTag(stack);
        stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putInt(COOLDOWN_TAG, cooldownTime());
        onCooldownStart(player, stack);
    }

    /**
     * 使当前物品栈停止冷却，不会触发{@link #onCooldownEnd(Player, ItemStack)}
     */
    default void stopCooldown(ItemStack stack) {
        stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putInt(COOLDOWN_TAG, 0);
    }

    /**
     * 是否应该继续调用{@link #tickCooldown(Player, ItemStack)}来处理物品栈冷却
     */
    default boolean shouldTick(ItemStack stack) {
        initTag(stack);
        return stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).getBoolean(TICK_TAG);
    }

    default void setShouldTick(ItemStack stack, boolean shouldTick) {
        initTag(stack);
        stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putBoolean(TICK_TAG, shouldTick);
    }

    /**
     * 物品栈是否应该自动在冷却结束时调用{@link #onCooldownEnd(Player, ItemStack)}并重新进入冷却
     */
    default boolean autoCooldown(ItemStack stack) {
        initTag(stack);
        return stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).getBoolean(AUTO_TAG);
    }

    default void setAutoCooldown(ItemStack stack, boolean autoCooldown) {
        initTag(stack);
        stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putBoolean(AUTO_TAG, autoCooldown);
    }

    /**
     * 处理物品栈的冷却，此函数不应该被其他地方调用，否则会造成冷却计算错误
     */
    default void tickCooldown(Player player, ItemStack stack) {
        if (!shouldTick(stack)) {
            return;
        }
        if (isCooldown(stack)) {
            CompoundTag tag = stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID);
            tag.putInt(COOLDOWN_TAG, tag.getInt(COOLDOWN_TAG) - 1);
        }
        if (!isCooldown(stack)) {
            onCooldownEnd(player, stack);
            if (autoCooldown(stack)) {
                startCooldown(player, stack);
            }
        }
    }

    /**
     * 冷却条是否应该被渲染到物品栏
     */
    default boolean shouldRenderCooldownBar(ItemStack stack) {
        initTag(stack);
        return stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).getBoolean(RENDER_BAR_TAG);
    }

    default void setShouldRenderCooldownBar(ItemStack stack, boolean shouldRenderBar) {
        initTag(stack);
        stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putBoolean(RENDER_BAR_TAG, shouldRenderBar);
    }

    /**
     * 当物品不处于冷却状态时，冷却条是否应该继续被渲染
     */
    default boolean renderCooldownBarWhenEnds(ItemStack stack) {
        initTag(stack);
        return stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).getBoolean(RENDER_BAR_WHEN_ENDS_TAG);
    }

    default void setRenderCooldownBarWhenEnds(ItemStack stack, boolean shouldRenderBarWhenEnds) {
        initTag(stack);
        stack.getOrCreateTag().getCompound(DCLibMod.MOD_ID).putBoolean(RENDER_BAR_WHEN_ENDS_TAG, shouldRenderBarWhenEnds);
    }

    /**
     * 当物品栈开始冷却时调用
     */
    default void onCooldownStart(Player player, ItemStack itemStack) {

    }

    /**
     * 当物品栈冷却结束时调用
     */
    default void onCooldownEnd(Player player, ItemStack itemStack) {

    }

    /**
     * 冷却条的长度，默认以15像素为基准变化
     */
    default int cooldownBarWidth(ItemStack stack) {
        return 45;
    }
}
