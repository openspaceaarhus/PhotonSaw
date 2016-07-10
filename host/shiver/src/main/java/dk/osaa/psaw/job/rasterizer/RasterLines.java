package dk.osaa.psaw.job.rasterizer;

import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Turns a one-bit raster into a number of lines of pixels, the lines of pixels are sorted in such a way that rasterizing
 * them in the presented order gives a close to optimal runtime with as little dead time as possible, though fast travel back to
 * the top of the job is will some times be needed.
 */
@SuppressWarnings("unchecked")
public class RasterLines {

    public static final int Y_DISTANCE_WEIGHT = 10;
    /**
     * A slice is a number of RasterLines where y==key
     * Each slice is also a treemap where the key is the x0 coordinate of the RasterLines
     *
     * This datastructure makes it cheap to find a line from a coordinate set, which is very handy when having to plan
     * the order of lines by least travel time.
     */
    TreeMap<Integer, TreeMap<Integer, RasterLine>> linesBySlice = new TreeMap<>();

    public RasterLines(BufferedImage onebit, int maxDeadSpace) {
        for (int yc = 0; yc < onebit.getHeight(); yc++) {

            TreeMap<Integer, RasterLine> slice = new TreeMap<>();
            RasterLine line = null;
            int deadPixels = 0;
            for (int xc = 0; xc < onebit.getWidth(); xc++) {
                boolean pixel = (onebit.getRGB(xc, yc) & 0xffffff) == 0;

                if (line != null) {
                    line.getPixels().add(pixel);

                    if (pixel) {
                        deadPixels = 0;
                    } else {
                        if (deadPixels++ > maxDeadSpace) {
                            line.removeDeadTail();
                            slice.put(line.getX0(), line);
                            line = null;
                        }
                    }
                } else {
                    if (pixel) {
                        line = new RasterLine(xc, yc, false);
                        deadPixels = 0;
                        line.getPixels().add(pixel);
                    }
                }
            }

            if (line != null) {
                line.removeDeadTail();
                slice.put(line.getX0(), line);
            }

            if (!slice.isEmpty()) {
                linesBySlice.put(yc, slice);
            }
        }
    }


    /**
     * For now we'll just return all the lines from the top down, this is not very optimal as there's still a lot of
     * dead time between lines in a slice
     *
     * @return RasterLines in order of execution
     */
    public List<RasterLine> getLines() {
        List<RasterLine> lines = new ArrayList<>();

        boolean reverse = false;
        for (TreeMap<Integer, RasterLine> slice : linesBySlice.values()) {
            for (Integer x : reverse ? slice.descendingKeySet() : slice.keySet()) {
                RasterLine rl = slice.get(x);
                rl.setReverse(reverse);
                lines.add(rl);
            }

            reverse = !reverse;
        }

        return lines;
    }


    public List<RasterLine> getOptimizedLines() {
        List<RasterLine> lines = new ArrayList<>();

        if (linesBySlice.isEmpty()) {
            return lines; // Well, that was easy...
        }

        // Pick the first raster line from the top-right and start there.
        RasterLine last = linesBySlice.firstEntry().getValue().firstEntry().getValue();

        while (!linesBySlice.isEmpty()) {

            RasterLine current = takeClosest(last);
            lines.add(current);
            last = current;
        }

        return lines;
    }

    private RasterLine takeClosest(RasterLine last) {

        // Slices to look for the closet line in
        List<TreeMap<Integer, RasterLine>> slices = new ArrayList<>();

        // Find the next line from the same slice by going in the same direction as the last line, if there are any
        TreeMap<Integer, RasterLine> sameSlice = linesBySlice.get(last.getY());
        if (sameSlice != null) {
            slices.add(sameSlice);
        }

        // Find the slice just above the current line
        Map.Entry<Integer, TreeMap<Integer, RasterLine>> higherSlice = linesBySlice.higherEntry(last.getY());
        if (higherSlice != null) {
            slices.add(higherSlice.getValue());
        }

        // Find the slice just below the current line
        Map.Entry<Integer, TreeMap<Integer, RasterLine>> lowerSlice = linesBySlice.lowerEntry(last.getY());
        if (lowerSlice != null) {
            slices.add(lowerSlice.getValue());
        }

        int lastX = last.isReverse() ? last.getX0() : last.getX1();

        BestRasterLine bestRasterLine = null;
        for (TreeMap<Integer, RasterLine> slice : slices) {
            bestRasterLine = findBestRasterLine(bestRasterLine, last, lastX, slice.floorEntry(lastX), slice.higherEntry(lastX));
        }

        if (bestRasterLine != null) {
            RasterLine winner = bestRasterLine.getLine();
            TreeMap<Integer, RasterLine> slice = linesBySlice.get(winner.getY());
            slice.remove(winner.getX0());
            if (slice.isEmpty()) {
                linesBySlice.remove(winner.getY());
            }

            winner.setReverse(bestRasterLine.isReverse());
            return winner;
        } else {
            throw new RuntimeException("Did not find a raster line");
        }
    }

    private static BestRasterLine findBestRasterLine(BestRasterLine bestRasterLine, RasterLine last, int lastX, Map.Entry<Integer, RasterLine>... entries) {

        for (Map.Entry<Integer, RasterLine> entry : entries) {
            if (entry == null) {
                continue;
            }

            // First look at the distance to the x0 end:
            RasterLine candidate = entry.getValue();
            bestRasterLine = pickIfBest(bestRasterLine, candidate, false,
                    weightedDistance(lastX, last.getY(), candidate.getX0(), candidate.getY()));

            // Then try for the end of the line
            bestRasterLine = pickIfBest(bestRasterLine, candidate, true,
                    weightedDistance(lastX, last.getY(), candidate.getX1(), candidate.getY()));

        }

        return bestRasterLine;
    }

    private static BestRasterLine pickIfBest(BestRasterLine bestRasterLine, RasterLine candidate, boolean reverse, int distance) {
        if (bestRasterLine == null || bestRasterLine.getDistance() > distance) {
            return new BestRasterLine(distance, reverse, candidate);
        } else {
            return bestRasterLine;
        }
    }

    private static int weightedDistance(int x0, int y0, int x1, int y1) {
        return (int)Math.round(Math.sqrt(Math.pow(Math.abs(x0-x1),2) + Math.pow(Math.abs(y0-y1)* Y_DISTANCE_WEIGHT,2)));
    }
}
