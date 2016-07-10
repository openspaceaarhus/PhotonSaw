package dk.osaa.psaw.job;

/**
 * Used to specify the strategy for optimizing the rasters
 */
public enum RasterOptimization {
    NONE,      // Only optimize the ends of each line
    FAST,      // Replace dead space with fast moves
    FASTEST    // Re-order rastering to avoid fast moving too.
}

/*
Some benchmarks on these optimization modes:

frandsen.svg, low-res:
    NONE:    337 s
    FAST:    318 s
    FASTEST: 302 s

frandsen.svg, high-res:
    NONE:    1060 s
    FAST:     995 s
    FASTEST:  901 s


flemming,svg, high-res:
    NONE:    3191 s
    FAST:    2566 s
    FASTEST: 2005 s

flemming,svg, low-res:
    NONE:     1000 s
    FAST:      808 s
    FASTEST:   707 s

*/