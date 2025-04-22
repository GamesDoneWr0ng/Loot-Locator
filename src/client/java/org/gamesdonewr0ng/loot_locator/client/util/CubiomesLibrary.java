package org.gamesdonewr0ng.loot_locator.client.util;

import com.sun.jna.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface CubiomesLibrary extends Library {
    CubiomesLibrary INSTANCE = Native.load("/lib/libcubiomes.dylib", CubiomesLibrary.class);

    int getBiomeAt(Generator g, int scale, int x, int y, int z);
    void setupGenerator(Generator g, int mc, int flags);
    void applySeed(Generator g, int dim, long seed);

    class PerlinNoise extends Structure {
        public byte[] d = new byte[256 + 1]; // 257 elements
        public byte h2;
        public double a;
        public double b;
        public double c;
        public double amplitude;
        public double lacunarity;
        public double d2;
        public double t2;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "d", "h2", "a", "b", "c",
                    "amplitude", "lacunarity", "d2", "t2"
            );
        }

        // Default constructor
        public PerlinNoise() {
            super();
        }

        // Constructor for existing memory allocation
        public PerlinNoise(Pointer p) {
            super(p);
            read();  // Auto-read from native memory
        }
    }
    class OctaveNoise extends Structure {
        int octcnt;
        PerlinNoise octaves;

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList(
                    "octcnt", "octaves"
            );
        }

        public OctaveNoise() {
            super();
        }

        public OctaveNoise(Pointer p) {
            super(p);
            read();
        }
    }
    class DoublePerlinNoise extends Structure {
        public double amplitude;
        public OctaveNoise octA;
        public OctaveNoise octB;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("amplitude", "octA", "octB");
        }

        public DoublePerlinNoise() {
            super();
            octA = new OctaveNoise();
            octB = new OctaveNoise();
        }

        public DoublePerlinNoise(Pointer p) {
            super(p);
            read();
        }
    }
    class Spline extends Structure {
        public int len;
        public int typ;
        public float[] loc = new float[12];
        public float[] der = new float[12];
        public Pointer[] val = new Pointer[12];  // Array of pointers to Spline

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("len", "typ", "loc", "der", "val");
        }

        // Constructors
        public Spline() { super(); }
        public Spline(Pointer p) { super(p); read(); }
    }
    class FixSpline extends Structure {
        public int len;
        public float val;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("len", "val");
        }

        public FixSpline() { super(); }
        public FixSpline(Pointer p) { super(p); read(); }
    }
    class SplineStack extends Structure {
        public Spline[] stack;  // Array of 42 Splines
        public FixSpline[] fstack;  // Array of 151 FixSplines
        public int len;
        public int flen;

        public SplineStack() {
            // Initialize array elements with proper structure instances
            stack = (Spline[]) new Spline().toArray(42);
            fstack = (FixSpline[]) new FixSpline().toArray(151);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("stack", "fstack", "len", "flen");
        }

        // Constructor for existing memory
        public SplineStack(Pointer p) {
            super(p);
            read();  // Auto-populate the arrays
        }
    }
    class BiomeNoise extends Structure {
        // Assuming NP_MAX is defined as 6 (common in cubiomes configurations)
        public static final int NP_MAX = 6;

        public DoublePerlinNoise[] climate = (DoublePerlinNoise[]) new DoublePerlinNoise().toArray(NP_MAX);
        public PerlinNoise[] oct = (PerlinNoise[]) new PerlinNoise().toArray(2 * 23); // 46 elements
        public Pointer sp;  // Pointer to Spline
        public SplineStack ss;
        public int nptype;
        public int mc;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "climate", "oct", "sp", "ss", "nptype", "mc"
            );
        }

        // Constructors
        public BiomeNoise() {
            super();
        }

        public BiomeNoise(Pointer p) {
            super(p);
            read();
        }

        // Helper method to access the Spline pointer
        public Spline getSplinePointer() {
            return new Spline(sp);
        }

        // Optional: Initialize octaves array with proper structure instances
        protected void initOctaves() {
            for(int i = 0; i < oct.length; i++) {
                oct[i] = new PerlinNoise();
            }
        }
    }
    class NetherNoise extends Structure {
        public DoublePerlinNoise temperature;
        public DoublePerlinNoise humidity;
        public PerlinNoise[] oct = (PerlinNoise[]) new PerlinNoise().toArray(8); // 8 elements

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("temperature", "humidity", "oct");
        }

        // Constructors
        public NetherNoise() {
            super();
        }

        public NetherNoise(Pointer p) {
            super(p);
            read();
        }
    }
    class EndNoise extends Structure {
        public PerlinNoise perlin;
        public int mc;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("perlin", "mc");
        }

        // Constructors
        public EndNoise() {
            super();
        }

        public EndNoise(Pointer p) {
            super(p);
            read();
        }
    }
    interface MapFunc extends Callback {
        int invoke(Pointer layer, Pointer out, int x, int z, int w, int h);
    }
    class Layer extends Structure {
        public MapFunc getMap;
        public byte mc;         // int8_t
        public byte zoom;       // int8_t
        public byte edge;       // int8_t
        public int scale;       // int
        public long layerSalt;  // uint64_t
        public long startSalt;  // uint64_t
        public long startSeed;  // uint64_t
        public Pointer noise;   // void*
        public Pointer data;    // void*
        public Pointer p;       // Layer* (parent 1)
        public Pointer p2;      // Layer* (parent 2)

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "getMap", "mc", "zoom", "edge", "scale",
                    "layerSalt", "startSalt", "startSeed",
                    "noise", "data", "p", "p2"
            );
        }

        // Structure for parent layers when needed
        public static class ByReference extends Layer implements Structure.ByReference {}
        public static class ByValue extends Layer implements Structure.ByValue {}
    }
    class LayerStack extends Structure {
        public Layer.ByReference[] layers = new Layer.ByReference[61];
        public Pointer entry_1;     // Layer* (L_VORONOI_1)
        public Pointer entry_4;     // Layer* (L_RIVER_MIX_4/L_OCEAN_MIX_4)
        public Pointer entry_16;    // Layer* (L_SWAMP_RIVER_16/L_SHORE_16)
        public Pointer entry_64;    // Layer* (L_HILLS_64/L_SUNFLOWER_64)
        public Pointer entry_256;   // Layer* (L_BIOME_256/L_BAMBOO_256)
        public PerlinNoise oceanRnd;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(
                    "layers",
                    "entry_1", "entry_4", "entry_16", "entry_64", "entry_256",
                    "oceanRnd"
            );
        }
    }
    class BiomeNoiseBeta extends Structure {
        // Array of 3 OctaveNoise structs (not pointers)
        public OctaveNoise[] climate = (OctaveNoise[]) new OctaveNoise().toArray(3);

        // Array of 10 PerlinNoise structs (not pointers)
        public PerlinNoise[] oct = (PerlinNoise[]) new PerlinNoise().toArray(10);

        public int nptype;
        public int mc;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("climate", "oct", "nptype", "mc");
        }

        // Initialize arrays in constructor
        public BiomeNoiseBeta() {
            // Initialize OctaveNoise array elements
            for (int i = 0; i < climate.length; i++) {
                climate[i] = new OctaveNoise();
            }
            // Initialize PerlinNoise array elements
            for (int i = 0; i < oct.length; i++) {
                oct[i] = new PerlinNoise();
            }
        }
    }
    class Generator extends Structure {
        public int mc;
        public int dim;
        public int flags;
        public long seed;
        // Use NativeLong if seed is architecture-dependent (32/64-bit)
        public long sha;
        public GeneratorUnion u;
        public NetherNoise nn;
        public EndNoise en;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("mc", "dim", "flags", "seed", "sha", "u", "nn", "en");
        }

        // Add explicit default constructor
        public Generator() {
            super();
        }

        public Generator(Pointer p) {
            super(p);
            read();
        }

        // Union must have a public no-arg constructor
        public static class GeneratorUnion extends Union {
            public MC1_0_1_17 mc1_0_1_17;
            public MC1_18 mc1_18;
            public MCBeta mcBeta;

            // Required no-arg constructor
            public GeneratorUnion() {
                super();
            }

            public GeneratorUnion(Pointer p) {
                super(p);
                read();
            }

            // Helper method to set active union type
            public void setVersion(int mcVersion) {
                setType(MC1_18.class);
            }
        }

        // Nested structs must also have explicit constructors
        public static class MC1_0_1_17 extends Structure {
            public LayerStack ls;
            public Layer[] xlayer = new Layer[5];
            public Pointer entry;

            public MC1_0_1_17() {
                super();
                // Initialize array elements to prevent NPE
                for (int i = 0; i < xlayer.length; i++) {
                    xlayer[i] = new Layer();
                }
            }

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList("ls", "xlayer", "entry");
            }
        }

        public static class MC1_18 extends Structure {
            public BiomeNoise bn;

            public MC1_18() {
                super();
                bn = new BiomeNoise();
            }

            @Override
            protected List<String> getFieldOrder() {
                return Collections.singletonList("bn");
            }
        }

        public static class MCBeta extends Structure {
            public BiomeNoiseBeta bnb;

            public MCBeta() {
                super();
                bnb = new BiomeNoiseBeta();
            }

            @Override
            protected List<String> getFieldOrder() {
                return Collections.singletonList("bnb");
            }
        }

        // Manually initialize the union after structure fields are set
        @Override
        public void read() {
            super.read();
            if (u != null) {
                u.setVersion(mc);
                u.read();
            }
        }
    }
}
