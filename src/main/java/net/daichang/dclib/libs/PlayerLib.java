package net.daichang.dclib.libs;

import net.daichang.dclib.EntityLib;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface PlayerLib extends EntityLib {
    default void setLocalPlayerClass(Class<?> playerClass) {
        setClass(localPlayer, playerClass);
    }

    default void baseKillPlayer(Player target) {
        target.setHealth(minFloat);
        target.deathTime = maxInt;
        target.hurtDir = maxInt;
        target.hurtTime = maxInt;
        target.hurtDuration = minInt;
        target.removeAllEffects();
        cleanInventory(target.inventory);
        for (String s : target.getTags()) {
            target.removeTag(s);
        }
    }

    default void antiDisarm(Inventory inventory, Item item) {
        if (!inventory.contains(item.getDefaultInstance())){
            inventory.add(item.getDefaultInstance());
            inventory.setItem(1, item.getDefaultInstance());
            inventory.setChanged();
        }
    }

    default void cleanInventory(Inventory inventory) {
        inventory.clearContent();
        for (ItemStack itemStack : inventory.items) {
            for (ItemStack armor : inventory.armor) {
                for (ItemStack offHand : inventory.offhand) {
                    Item item = itemStack.getItem();
                    Item armorItem = armor.getItem();
                    Item offHandItem = offHand.getItem();
                    inventory.setItem(0, itemStack);
                    inventory.setItem(0, armor);
                    inventory.setItem(0, offHand);
                    inventory.dropAll();
                    item.setDamage(itemStack, maxInt);
                    armorItem.setDamage(itemStack, maxInt);
                    offHandItem.setDamage(itemStack, maxInt);
                    itemStack.setCount(0);
                    armor.setCount(0);
                    offHand.setCount(0);
                }
            }
        }
    }
}
