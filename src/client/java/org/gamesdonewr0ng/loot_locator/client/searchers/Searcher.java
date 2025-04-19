package org.gamesdonewr0ng.loot_locator.client.searchers;

import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.item.Item;
import com.seedfinding.mcfeature.structure.UniformStructure;
import org.gamesdonewr0ng.loot_locator.client.LootLocatorClient;

import java.util.Comparator;
import java.util.PriorityQueue;

abstract class Searcher<T extends UniformStructure> {
    long SEED = LootLocatorClient.INSTANCE.seed;
    MCVersion VERSION = LootLocatorClient.INSTANCE.VERSION;
    T STRUCTURE;
    private PriorityQueue<RPos> regionQueue;
    private PriorityQueue<CPos> structureQueue;
    private int n;

    Searcher() {}

    public void fillStructures(BPos startPos, BiomeSource source) {
        RPos startRPos = startPos.toRegionPos(STRUCTURE.getSpacing() * 16);
        ChunkRand rand = new ChunkRand();

        if (regionQueue == null) {
            n = 0;
            regionQueue = new PriorityQueue<>(Comparator.comparingDouble(
                    p -> p.distanceTo(startRPos, DistanceMetric.EUCLIDEAN_SQ)
            ));
            structureQueue = new PriorityQueue<>(Comparator.comparingDouble(
                    p -> p.distanceTo(startRPos.toChunkPos(), DistanceMetric.EUCLIDEAN_SQ)
            ));
        }

        while (true) {
            getRegions(startRPos, n);
            n++;

            RPos nextRPos;
            while ((nextRPos = regionQueue.poll()) != null && nextRPos.distanceTo(startRPos, DistanceMetric.EUCLIDEAN_SQ) <= n*n) {
                rand.setCarverSeed(SEED, nextRPos.getX(), nextRPos.getZ(), VERSION);
                CPos cPos = STRUCTURE.getInRegion(SEED, nextRPos.getX(), nextRPos.getZ(), rand);
                if (STRUCTURE.canSpawn(cPos, source)) {
                    structureQueue.offer(cPos);
                }
            }
            if (!structureQueue.isEmpty()) {
                return;
            }
        }
    }

    public CPos nextStructurePos(BPos startPos, BiomeSource source) {
        if (structureQueue == null || structureQueue.isEmpty()) {
            fillStructures(startPos, source);
        }
        return structureQueue.poll();
    }

    private void getRegions(RPos center, int n) {
        if (n == 0) {
            regionQueue.add(new RPos(center.getX(), center.getZ(), center.getRegionSize()));
            return;
        }

        for (int dx = -n; dx <= n; dx++) {
            regionQueue.add(new RPos(center.getX() + dx, center.getZ() + n, center.getRegionSize()));  // Top
            regionQueue.add(new RPos(center.getX() + dx, center.getZ() - n, center.getRegionSize()));  // Bottom
        }
        for (int dy = -n + 1; dy <= n - 1; dy++) {
            regionQueue.add(new RPos(center.getX() + n, center.getZ() + dy, center.getRegionSize()));  // Right
            regionQueue.add(new RPos(center.getX() - n, center.getZ() + dy, center.getRegionSize()));  // Left
        }
    }

    abstract boolean isInStructure(CPos cPos, Item target);
}