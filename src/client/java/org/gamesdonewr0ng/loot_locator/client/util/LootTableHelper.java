package org.gamesdonewr0ng.loot_locator.client.util;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.ReloadableRegistries;

public class LootTableHelper {
    private final ReloadableRegistries.Lookup reloadableServerRegistries;

    public LootTableHelper() {
        this.reloadableServerRegistries = null;
    }

    public LootTableHelper(ReloadableRegistries.Lookup reloadableServerRegistries) {
        this.reloadableServerRegistries = reloadableServerRegistries;
    }

    public LootTable getLootTable(RegistryKey<LootTable> lootTableKey) {
        if (reloadableServerRegistries == null) {
            return LootTable.EMPTY;
        }
        return reloadableServerRegistries.getLootTable(lootTableKey);
    }
}
