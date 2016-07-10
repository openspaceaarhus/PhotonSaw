package dk.osaa.psaw.job.rasterizer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Just a class used to keep track of the best raster line seen so far when searching
 */
@Getter
@AllArgsConstructor
public class BestRasterLine {
    private final int distance;
    private final boolean reverse;
    private final RasterLine line;
}
