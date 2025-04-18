package org.gamesdonewr0ng.loot_locator.client.searchers;

import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mcfeature.loot.item.Item;
import com.seedfinding.mcfeature.structure.DesertPyramid;

public class DesertTempleSearcher extends Searcher<DesertPyramid> {

    public DesertTempleSearcher(DesertPyramid structure, int salt) {
        super(structure, salt);
    }

    public boolean isInStructure(CPos cPos, Item target) {
        return false;
    }
}
