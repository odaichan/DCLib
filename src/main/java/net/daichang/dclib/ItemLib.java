package net.daichang.dclib;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ItemLib {
    default void itemClassSet(Item targetItem, Class<?> newClazz) {
        HelperLib.setClass(targetItem, newClazz);
    }

    default void inventoryItemGet(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (player.level().isClientSide()) player.displayClientMessage(Component.literal(" " + stack.getItem().getClass()), false);
        }
    }

    
}
