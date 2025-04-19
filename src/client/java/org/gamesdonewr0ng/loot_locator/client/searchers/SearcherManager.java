package org.gamesdonewr0ng.loot_locator.client.searchers;

import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.gamesdonewr0ng.loot_locator.client.LootLocatorClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SearcherManager {
    long SEED = LootLocatorClient.INSTANCE.seed;
    MCVersion VERSION = LootLocatorClient.INSTANCE.VERSION;
    private final List<Searcher> searchers;
    private final List<CPos> positions;
    private BiomeSource source;
    private final BPos pos;

    public SearcherManager(Stream<RegistryKey<LootTable>> lootTables, BPos pos, RegistryKey<DimensionType> dimension) {
        searchers = new ArrayList<>();
        this.pos = pos;

        if (dimension.equals(DimensionTypes.OVERWORLD) && (lootTables.anyMatch(p -> p == LootTables.DESERT_PYRAMID_CHEST || p == LootTables.DESERT_PYRAMID_ARCHAEOLOGY))) {
            searchers.add(new DesertTempleSearcher());
        }

        positions = new ArrayList<>(searchers.size());
        init(dimension);
    }

    public SearcherManager(String structure, BPos pos, RegistryKey<DimensionType> dimension) {
        searchers = new ArrayList<>();
        this.pos = pos;

        switch (structure) {
            case "Desert_Temple" -> searchers.add(new DesertTempleSearcher());
        }

        positions = new ArrayList<>(searchers.size());
        init(dimension);
    }

    private void init(RegistryKey<DimensionType> dimension) {
        if      (dimension.equals(DimensionTypes.OVERWORLD))  {source = BiomeSource.of(Dimension.OVERWORLD, VERSION, SEED);}
        else if (dimension.equals(DimensionTypes.THE_NETHER)) {source = BiomeSource.of(Dimension.NETHER, VERSION, SEED);}
        else {source = BiomeSource.of(Dimension.END, VERSION, SEED);}

        for (Searcher searcher : searchers) {
            positions.add(searcher.nextStructurePos(pos, source));
        }
    }

    public CPos getNextPos() {
        return getNextPos(null);
    }

    public CPos getNextPos(Item item) {
        int minIdx = 0;
        double minDistance = pos.shr(4).distanceTo(positions.get(0), DistanceMetric.EUCLIDEAN_SQ);
        double d;
        for (int i = 1; i < positions.size(); i++) {
            if ((d = pos.shr(4).distanceTo(positions.get(i), DistanceMetric.EUCLIDEAN_SQ)) < minDistance) {
                minIdx = i;
                minDistance = d;
            }
        }
        while (true) {
            CPos res = positions.get(minIdx);
            positions.set(minIdx, searchers.get(minIdx).nextStructurePos(pos, source));
            if (item == null || searchers.get(minIdx).isInStructure(res, item)) {
                return res;
            }
        }
    }
}
