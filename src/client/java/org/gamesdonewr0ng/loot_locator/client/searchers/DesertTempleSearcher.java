package org.gamesdonewr0ng.loot_locator.client.searchers;

import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mcfeature.loot.item.Item;
import com.seedfinding.mcfeature.structure.DesertPyramid;

public class DesertTempleSearcher extends Searcher<DesertPyramid> {

    public DesertTempleSearcher() {
        super();
        this.STRUCTURE = new DesertPyramid(VERSION);
    }

    public boolean isInStructure(CPos cPos, Item target) {
        return false;
    }
}
