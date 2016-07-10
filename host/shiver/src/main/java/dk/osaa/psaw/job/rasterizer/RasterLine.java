package dk.osaa.psaw.job.rasterizer;

import com.kitfox.svg.A;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A horizontal of pixels starting at x0
 * The first and last pixel must always be true as blank pixels separate lines unless two lines are merged.
 */
@Getter
@AllArgsConstructor
public class RasterLine {
    private final int x0;
    private final int y;
    private final ArrayList<Boolean> pixels = new ArrayList<>();

    @Setter
    @Getter
    private boolean reverse;

    public void removeDeadTail() {
        while (!pixels.get(pixels.size()-1)) {
            pixels.remove(pixels.size()-1);
        }
    }

    public boolean[] getPixelsAsArray() {
        boolean[] result = new boolean[pixels.size()];
        int i=0;
        for (Boolean b : pixels) {
            result[i++] = b;
        }
        return result;
    }

    public int getX1() {
        return x0+pixels.size();
    }
}
