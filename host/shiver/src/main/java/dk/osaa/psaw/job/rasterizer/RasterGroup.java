package dk.osaa.psaw.job.rasterizer;

import dk.osaa.psaw.job.JobNodeGroup;
import dk.osaa.psaw.job.JobRenderTarget;
import dk.osaa.psaw.job.LaserNodeSettings;
import dk.osaa.psaw.job.RasterNode;
import lombok.extern.java.Log;
import lombok.val;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ff on 7/9/16.
 */
@Log
class RasterGroup {

    Rectangle2D bb;
    ArrayList<RasterNode> nodes;
    LaserNodeSettings settings;

    RasterGroup(RasterNode rn) {
        bb = rn.getBoundingBox();
        nodes = new ArrayList<RasterNode>();
        nodes.add(rn);
        settings = rn.getSettings();
    }

    boolean canAdd(RasterNode rn, JobRenderTarget target) {
        if (!rn.getSettings().equalsRaster(settings)) {
            return false; // The laser settings aren't the same, so we can't add the raster to this group.
        }

        Rectangle2D obb = rn.getBoundingBox();

        if (obb.getMinY() > bb.getMaxY() || obb.getMaxY() < bb.getMinY()) {
            return false; // The new raster is entirely outside the y-span of this group, so it might as well be in another group.
        }

        /*
         * TODO: calculate the cost of engraving this raster as part of this group or separately and only allow
         * adding if the total cost is lower when added
         */

        return true;
    }

    void add(RasterNode rn) {
        nodes.add(rn);
        bb.add(rn.getBoundingBox());
    }



    static List<RasterGroup> getRasterGroups(JobNodeGroup root, JobRenderTarget target) {
        val rasters = root.getRasters();

        // Sort by height, so the tallest rasters get to form groups.
        Collections.sort(rasters, new Comparator<RasterNode>() {
            public int compare(RasterNode c1, RasterNode c2) {
                int r1 = (int)Math.round(c2.getBoundingBox().getHeight() - c1.getBoundingBox().getHeight());
                if (r1 != 0) {
                    return r1;
                }

                return c1.getId().compareTo(c2.getId());
            }
        });

        val merged = new ArrayList<RasterGroup>();
        for (val rn : rasters) {
            log.info("Adding raster: "+rn.getId()+" with height: "+rn.getBoundingBox().getHeight());

            boolean added = false;
            for (val eg : merged) {
                if (eg.canAdd(rn, target)) {
                    added = true;
                    eg.add(rn);
                    break;
                }
            }

            if (!added) {
                merged.add(new RasterGroup(rn));
            }
        }

        // We now have all the rasters merged into groups of similar parameters
        // We sort the merged groups, so we start with the top one, this is to minimize travel.
        Collections.sort(merged, new Comparator<RasterGroup>() {
            public int compare(RasterGroup c1, RasterGroup c2) {
                return (int)Math.round(c1.bb.getMinY() - c2.bb.getMinY());
            }
        });
        return merged;
    }

}
