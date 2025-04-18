package org.gamesdonewr0ng.loot_locator.client.searchers;

import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.Structure;
import com.seedfinding.mcterrain.TerrainGenerator;
import org.gamesdonewr0ng.loot_locator.client.LootLocatorClient;

import java.util.Comparator;
import java.util.PriorityQueue;

abstract class Searcher<T extends Structure> {
    private final long SEED = LootLocatorClient.INSTANCE.seed;
    private final MCVersion VERSION = LootLocatorClient.INSTANCE.VERSION;
    private final T STRUCTURE;
    private final int spacing;
    private final int salt;
    private final int separation;
    private final int biomeY;

    private PriorityQueue<RPos> regionQueue;
    private PriorityQueue<CPos> structureQueue;
    private int n;

    Searcher(T structure, int spacing, int separation, int salt, int biomeY) {
        STRUCTURE = structure;
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
        this.biomeY = biomeY;
    }

    Searcher(T structure, int salt) {
        this(structure, 32, 8, salt,  70);
    }

    public void fillStructures(BPos startPos, BiomeSource source, TerrainGenerator terrainGenerator) {
        RPos startRPos = startPos.toRegionPos(spacing * 16);
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
            while ((nextRPos = regionQueue.poll()) != null && nextRPos.distanceTo(startRPos, DistanceMetric.EUCLIDEAN) <= n) {
                CPos cPos = getInRegion(SEED, nextRPos, rand);
                if (STRUCTURE.isValidBiome(source.getBiome(cPos.toBlockPos(biomeY))) && STRUCTURE.isValidTerrain(terrainGenerator, cPos.getX(), cPos.getZ())) {
                    structureQueue.offer(cPos);
                }
            }
            if (!structureQueue.isEmpty()) {
                return;
            }
        }
    }

    public CPos nextStructurePos(BPos startPos, BiomeSource source, TerrainGenerator terrainGenerator) {
        if (structureQueue.isEmpty()) {
            fillStructures(startPos, source, terrainGenerator);
        }
        return structureQueue.poll();
    }

    private void getRegions(RPos center, int n) {
        if (n == 0) {
            regionQueue.add(new RPos(center.getX(), center.getZ(), center.getRegionSize()));
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

    private CPos getInRegion(long seed, RPos regionPos, ChunkRand rand) {
        rand.setRegionSeed(seed, regionPos.getX(), regionPos.getZ(), salt, VERSION);

        return new CPos(
                regionPos.getX() * spacing + rand.nextInt(spacing - separation),
                regionPos.getX() * spacing + rand.nextInt(spacing - separation)
        );
    }
}