package org.ironmaple.simulation.drivesims.configs;

import static edu.wpi.first.units.Units.Kilograms;
import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Mass;
import org.ironmaple.simulation.drivesims.COTS;

/**
 *
 *
 * <h1>Stores the configurations for a drivetrain simulation.</h1>
 *
 * <p>This class is used to hold the parameters necessary for simulating a drivetrain chassis as a single rigid body:
 * mass, bumper size, and the wheels' coefficient of friction (which limits how quickly the chassis can accelerate
 * toward a commanded {@link edu.wpi.first.math.kinematics.ChassisSpeeds}).
 */
public class DriveTrainSimulationConfig {
    public Mass robotMass;
    public Distance bumperLengthX, bumperWidthY;
    public double wheelCoefficientOfFriction;

    /**
     *
     *
     * <h2>Ordinary Constructor</h2>
     *
     * <p>Creates an instance of {@link DriveTrainSimulationConfig} with specified parameters.
     *
     * @param robotMass the mass of the robot, including bumpers.
     * @param bumperLengthX the length of the bumper (distance from front to back).
     * @param bumperWidthY the width of the bumper (distance from left to right).
     * @param wheelCoefficientOfFriction the coefficient of friction between the wheels and the floor, normally
     *     between 0.6 and 2.5, see {@link COTS.WHEELS} for reference values of common wheels.
     */
    public DriveTrainSimulationConfig(
            Mass robotMass, Distance bumperLengthX, Distance bumperWidthY, double wheelCoefficientOfFriction) {
        this.robotMass = robotMass;
        this.bumperLengthX = bumperLengthX;
        this.bumperWidthY = bumperWidthY;
        this.wheelCoefficientOfFriction = wheelCoefficientOfFriction;

        checkRobotMass();
        checkBumperSize();
        checkWheelCoefficientOfFriction();
    }

    /**
     *
     *
     * <h2>Default Constructor.</h2>
     *
     * <p>Creates a {@link DriveTrainSimulationConfig} with all the data set to default values.
     *
     * <p>Though the config starts with default values, any configuration can be modified after creation.
     *
     * <p>The default configurations are:
     *
     * <ul>
     *   <li>Robot Mass of 45 kilograms.
     *   <li>Bumper Length of 0.76 meters.
     *   <li>Bumper Width of 0.76 meters.
     *   <li>Wheel coefficient of friction of Colson wheels.
     * </ul>
     *
     * @return a new instance of {@link DriveTrainSimulationConfig} with all configs set to default values.
     */
    public static DriveTrainSimulationConfig Default() {
        return new DriveTrainSimulationConfig(
                Kilograms.of(45), Meters.of(0.76), Meters.of(0.76), COTS.WHEELS.COLSONS.cof);
    }

    /**
     *
     *
     * <h2>Sets the robot mass.</h2>
     *
     * <p>Updates the mass of the robot in kilograms.
     *
     * @param robotMass the new mass of the robot.
     * @return the current instance of {@link DriveTrainSimulationConfig} for method chaining.
     */
    public DriveTrainSimulationConfig withRobotMass(Mass robotMass) {
        this.robotMass = robotMass;
        checkRobotMass();
        return this;
    }

    /**
     *
     *
     * <h2>Sets the bumper size.</h2>
     *
     * <p>Updates the dimensions of the bumper.
     *
     * @param bumperLengthX the length of the bumper.
     * @param bumperWidthY the width of the bumper.
     * @return the current instance of {@link DriveTrainSimulationConfig} for method chaining.
     */
    public DriveTrainSimulationConfig withBumperSize(Distance bumperLengthX, Distance bumperWidthY) {
        this.bumperLengthX = bumperLengthX;
        this.bumperWidthY = bumperWidthY;

        checkBumperSize();
        return this;
    }

    /**
     *
     *
     * <h2>Sets the wheel coefficient of friction.</h2>
     *
     * <p>Updates the coefficient of friction between the wheels and the floor, which limits how quickly the chassis
     * can accelerate toward a commanded chassis speeds.
     *
     * @param wheelCoefficientOfFriction the new wheel coefficient of friction.
     * @return the current instance of {@link DriveTrainSimulationConfig} for method chaining.
     */
    public DriveTrainSimulationConfig withWheelCoefficientOfFriction(double wheelCoefficientOfFriction) {
        this.wheelCoefficientOfFriction = wheelCoefficientOfFriction;
        checkWheelCoefficientOfFriction();
        return this;
    }

    /**
     *
     *
     * <h2>Calculates the density of the robot.</h2>
     *
     * <p>Returns the density of the robot based on its mass and bumper dimensions.
     *
     * @return the density in kilograms per square meter.
     */
    public double getDensityKgPerSquaredMeters() {
        return robotMass.in(Kilograms) / (bumperLengthX.in(Meters) * bumperWidthY.in(Meters));
    }

    /**
     *
     *
     * <h2>Calculates the effective drive base radius of the robot.</h2>
     *
     * <p>Since individual wheel/module positions are not modeled, this is an approximation of the distance between
     * the robot's center and its wheels, derived from the bumper footprint. It is used only to convert the wheels'
     * gripping force into a rotational torque limit.
     *
     * @return the approximate drive base radius.
     */
    public Distance driveBaseRadius() {
        return Meters.of(Math.hypot(bumperLengthX.in(Meters), bumperWidthY.in(Meters)) / 2);
    }

    private void checkRobotMass() {
        BoundingCheck.check(robotMass.in(Kilograms), 10, 80, "robot mass", "kg");
    }

    private void checkBumperSize() {
        BoundingCheck.check(bumperLengthX.in(Meters), 0.2, 1.5, "bumper length", "meters");
        BoundingCheck.check(bumperWidthY.in(Meters), 0.2, 1.5, "bumper width", "meters");
    }

    private void checkWheelCoefficientOfFriction() {
        BoundingCheck.check(wheelCoefficientOfFriction, 0.6, 2.5, "wheel coefficient of friction", "");
    }
}
