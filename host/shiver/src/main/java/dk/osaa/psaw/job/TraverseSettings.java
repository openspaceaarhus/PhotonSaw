package dk.osaa.psaw.job;

import lombok.Value;

/**
 * The constraints to obey when traversing, aka moving without the laser on.
 */
@Value
public class TraverseSettings {
    boolean accurate;
    boolean speedUnlimited;
    boolean exitSpeedMandatory;
    double maxSpeed;

    /**
     * Move as quickly as possible, but hit the target point accurately without optimization
     */
    static final public TraverseSettings ACCURATE = new TraverseSettings(true, true, false, 0);

    /**
     * Move as quickly as possible, but don't generate a move if too little distance is covered
     */
    static final public TraverseSettings FAST = new TraverseSettings(false, true, false, 0);

    /**
     * Creates constraints that sets a max speed for the move and allow the move to be optimized out if it's too short.
     *
     * @param maxSpeed The speed limit in mm/s
     * @return The constraints constructed
     */
    static public TraverseSettings atMaxSpeed(double maxSpeed) {
        return new TraverseSettings(false, false, false, maxSpeed);
    }

    /**
     * Creates constraints that sets a speed for the move that must be archived and allow the move to be optimized out if it's too short.
     *
     * @param maxSpeed The speed limit in mm/s
     * @return The constraints constructed
     */
    static public TraverseSettings hitExitSpeed(double maxSpeed) {
        return new TraverseSettings(false, false, true, maxSpeed);
    }
}
