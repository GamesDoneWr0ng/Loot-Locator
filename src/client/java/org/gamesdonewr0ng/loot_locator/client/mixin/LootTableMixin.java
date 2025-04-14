package org.gamesdonewr0ng.loot_locator.client.mixin;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.gamesdonewr0ng.loot_locator.client.util.ILootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LootTable.class)
public class LootTableMixin implements ILootTable {
    @Final
    @Shadow
    private List<LootPool> pools;

    public List<LootPool> lootLocator$getPools() {
        return pools;
    }
}
