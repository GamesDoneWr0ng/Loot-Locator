package org.gamesdonewr0ng.loot_locator.client.searchers;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mcfeature.loot.item.Item;
import com.seedfinding.mcfeature.structure.DesertPyramid;
import org.gamesdonewr0ng.loot_locator.client.util.CubiomesLibrary;

public class DesertTempleSearcher extends Searcher<DesertPyramid> {

    public DesertTempleSearcher() {
        super();
        this.STRUCTURE = new DesertPyramid(VERSION);
    }

    public boolean isInStructure(CPos cPos, Item target) {
        return false;
    }

    boolean isValid(CPos cPos, CubiomesLibrary.Generator generator) {
        BPos bPos = cPos.toBlockPos();
        int biomeId = CubiomesLibrary.INSTANCE.getBiomeAt(generator, 4, bPos.getX(), 64, bPos.getZ());
        return biomeId == 2;
    }
}
