package org.gamesdonewr0ng.loot_locator.client.util;

import com.seedfinding.mcfeature.loot.item.Item;
import com.seedfinding.mcfeature.loot.item.Items;

import java.util.function.Supplier;

public class ItemHelper {
    public static Item getItem(net.minecraft.item.Item mcItem) {
        for (int i = 0; i < Items.getNumberItems(); i++) {
            Supplier<Item> itemSupplier = Items.getItems().get(i);
            if (itemSupplier.get().getName().equals(mcItem.toString().substring(10))) {
                return itemSupplier.get();
            }
        }
        return Items.AIR;
    }
}
