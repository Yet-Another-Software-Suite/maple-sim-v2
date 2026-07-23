package org.ironmaple.simulation.drivesims;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearAcceleration;
import org.dyn4j.geometry.Vector2;
import org.ironmaple.simulation.SimulatedArena;
import org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig;
import org.ironmaple.utils.mathutils.GeometryConvertor;
import org.ironmaple.utils.mathutils.MapleCommonMath;

/**
 *
 *
 * <h1>Simulates a Drivetrain Chassis Driven Directly by Chassis Speeds.</h1>
 *
 * <p>Check <a href='https://shenzhen-robotics-alliance.github.io/maple-sim/swerve-simulation-overview/'>Online
 * Documentation</a>
 *
 * <h3>1. Purpose</h3>
 *
 * <p>This class simulates a holonomic drivetrain (e.g. swerve) as a single rigid body, driven directly by a commanded
 * {@link ChassisSpeeds} rather than by individually-modeled wheel modules or motors.
 *
 * <p>The commanded chassis speeds are treated as the drivetrain's idealized, "if there was no physics" target
 * velocity (exactly what a real robot's drive code computes every loop); this class converts that idealized target
 * into realistic motion by applying a wheel-grip-limited force and torque that pulls the chassis' actual physics-body
 * velocity toward it, respecting mass, rotational inertia, and friction limits.
 *
 * <h3>2. Simulation Dynamics</h3>
 *
 * <ul>
 *   <li>1. Friction force that "pulls" the robot from its current ground velocity toward the commanded chassis
 *       speeds, both translational and rotational.
 *   <li>2. Centripetal force generated when the commanded direction of travel changes.
 * </ul>
 *
 * <p>Both are limited by the wheels' gripping force, computed from {@link DriveTrainSimulationConfig#robotMass} and
 * {@link DriveTrainSimulationConfig#wheelCoefficientOfFriction}.
 *
 * <h3>Vision Simulation</h3>
 *
 * <p>You can obtain the real robot pose from {@link #getSimulatedDriveTrainPose()} and feed it to the <a
 * href="https://docs.photonvision.org/en/latest/docs/simulation/simulation-java.html#updating-the-simulation-world">PhotonVision
 * simulation</a> to simulate vision.
 */
public class DriveTrainSimulation extends AbstractDriveTrainSimulation {
    private static final double GRAVITY_ACCELERATION_METERS_PER_SECOND_SQUARED = 9.8;
    private static final double FRICTION_FORCE_GAIN = 3.0;
    private static final double FRICTION_TORQUE_GAIN = 1.0;
    private static final double NEAR_ZERO_ANGULAR_VELOCITY_RAD_PER_SEC = 0.02;

    private ChassisSpeeds desiredChassisSpeedsRobotRelative = new ChassisSpeeds();
    private Translation2d previousDesiredSpeedsFieldRelative = new Translation2d();

    /**
     *
     *
     * <h2>Creates a Drivetrain Simulation.</h2>
     *
     * <p>This constructor initializes a drivetrain simulation with the given robot mass, bumper dimensions, wheel
     * coefficient of friction, and initial pose on the field.
     *
     * @param config a {@link DriveTrainSimulationConfig} instance containing the configurations of this drivetrain
     * @param initialPoseOnField the initial pose of the drivetrain in the simulation world, represented as a
     *     {@link Pose2d}
     */
    public DriveTrainSimulation(DriveTrainSimulationConfig config, Pose2d initialPoseOnField) {
        super(config, initialPoseOnField);

        super.setLinearDamping(1.4);
        super.setAngularDamping(1.4);
    }

    /**
     *
     *
     * <h2>Sets the Desired Chassis Speeds.</h2>
     *
     * <p>This is the primary input to the simulation: the chassis speeds the drivetrain would achieve if it were not
     * subject to physics (mass, inertia, friction limits). Call this every loop, just like a real robot's drive
     * subsystem periodic.
     *
     * @param desiredChassisSpeedsRobotRelative the desired chassis speeds, robot-relative
     */
    public void setDesiredChassisSpeeds(ChassisSpeeds desiredChassisSpeedsRobotRelative) {
        this.desiredChassisSpeedsRobotRelative = desiredChassisSpeedsRobotRelative;
    }

    /**
     *
     *
     * <h2>Updates the Drivetrain Simulation.</h2>
     *
     * <p>This method performs the following actions during each sub-tick of the simulation:
     *
     * <ul>
     *   <li>Applies the translational friction force to the physics engine.
     *   <li>Applies the rotational friction torque to the physics engine.
     * </ul>
     */
    @Override
    public void simulationSubTick() {
        simulateChassisFrictionForce();

        simulateChassisFrictionTorque();
    }

    /**
     *
     *
     * <h2>Simulates the Translational Friction Force and Applies It to the Physics Engine.</h2>
     *
     * <p>This method simulates the translational friction forces acting on the robot and applies them to the physics
     * engine. There are two components of the friction forces:
     *
     * <ul>
     *   <li>A portion of the friction force pushes the robot from its current ground speeds
     *       ({@link #getDriveTrainSimulatedChassisSpeedsRobotRelative()}) toward the desired chassis speeds
     *       ({@link #setDesiredChassisSpeeds(ChassisSpeeds)}).
     *   <li>Another portion of the friction force is the centripetal force, which occurs when the chassis changes its
     *       direction of movement.
     * </ul>
     *
     * <p>The total friction force should not exceed the tire's grip limit.
     */
    private void simulateChassisFrictionForce() {
        /* The friction force that tries to bring the chassis from floor speeds to the desired speeds */
        final ChassisSpeeds differenceBetweenFloorSpeedAndDesiredSpeedsRobotRelative =
                desiredChassisSpeedsRobotRelative.minus(getDriveTrainSimulatedChassisSpeedsRobotRelative());
        final Translation2d floorAndDesiredSpeedsDiffFieldRelative = new Translation2d(
                        differenceBetweenFloorSpeedAndDesiredSpeedsRobotRelative.vxMetersPerSecond,
                        differenceBetweenFloorSpeedAndDesiredSpeedsRobotRelative.vyMetersPerSecond)
                .rotateBy(getSimulatedDriveTrainPose().getRotation());
        final double totalGrippingForce = getTotalGrippingForceNewtons();
        final Vector2 speedsDifferenceFrictionForce = Vector2.create(
                Math.min(
                        FRICTION_FORCE_GAIN * totalGrippingForce * floorAndDesiredSpeedsDiffFieldRelative.getNorm(),
                        totalGrippingForce),
                MapleCommonMath.getAngle(floorAndDesiredSpeedsDiffFieldRelative).getRadians());

        /* the centripetal friction force during turning */
        final ChassisSpeeds desiredSpeedsFieldRelative = ChassisSpeeds.fromRobotRelativeSpeeds(
                desiredChassisSpeedsRobotRelative, getSimulatedDriveTrainPose().getRotation());
        final Rotation2d dTheta = MapleCommonMath.getAngle(
                        GeometryConvertor.getChassisSpeedsTranslationalComponent(desiredSpeedsFieldRelative))
                .minus(MapleCommonMath.getAngle(previousDesiredSpeedsFieldRelative));

        final double orbitalAngularVelocity =
                dTheta.getRadians() / SimulatedArena.getSimulationDt().in(Seconds);
        final Rotation2d centripetalForceDirection =
                MapleCommonMath.getAngle(previousDesiredSpeedsFieldRelative).plus(Rotation2d.fromDegrees(90));
        final Vector2 centripetalFrictionForce = Vector2.create(
                previousDesiredSpeedsFieldRelative.getNorm()
                        * orbitalAngularVelocity
                        * config.robotMass.in(Kilograms),
                centripetalForceDirection.getRadians());
        previousDesiredSpeedsFieldRelative =
                GeometryConvertor.getChassisSpeedsTranslationalComponent(desiredSpeedsFieldRelative);

        /* apply force to physics engine */
        final Vector2
                totalFrictionForceUnlimited = centripetalFrictionForce.copy().add(speedsDifferenceFrictionForce),
                totalFrictionForce =
                        Vector2.create(
                                Math.min(totalGrippingForce, totalFrictionForceUnlimited.getMagnitude()),
                                totalFrictionForceUnlimited.getDirection());
        super.applyForce(totalFrictionForce);
    }

    /**
     *
     *
     * <h2>Simulates the Rotational Friction Torque and Applies It to the Physics Engine.</h2>
     *
     * <p>This method simulates the rotational friction torque acting on the robot and applies them to the physics
     * engine.
     *
     * <p>The friction torque pushes the robot from its current ground angular velocity
     * ({@link #getDriveTrainSimulatedChassisSpeedsRobotRelative()}) toward the desired angular velocity
     * ({@link #setDesiredChassisSpeeds(ChassisSpeeds)}).
     */
    private void simulateChassisFrictionTorque() {
        final double
                desiredOmega = desiredChassisSpeedsRobotRelative.omegaRadiansPerSecond,
                actualOmega = getAngularVelocity(),
                differenceBetweenFloorSpeedAndDesiredSpeed = desiredOmega - actualOmega,
                grippingTorqueMagnitude = getTotalGrippingForceNewtons() * config.driveBaseRadius().in(Meters);

        if (Math.abs(actualOmega) < NEAR_ZERO_ANGULAR_VELOCITY_RAD_PER_SEC
                && Math.abs(desiredOmega) < NEAR_ZERO_ANGULAR_VELOCITY_RAD_PER_SEC) super.setAngularVelocity(0);
        else
            super.applyTorque(Math.copySign(
                    Math.min(
                            FRICTION_TORQUE_GAIN
                                    * grippingTorqueMagnitude
                                    * Math.abs(differenceBetweenFloorSpeedAndDesiredSpeed),
                            grippingTorqueMagnitude),
                    differenceBetweenFloorSpeedAndDesiredSpeed));
    }

    private double getTotalGrippingForceNewtons() {
        return config.wheelCoefficientOfFriction
                * config.robotMass.in(Kilograms)
                * GRAVITY_ACCELERATION_METERS_PER_SECOND_SQUARED;
    }

    /**
     *
     *
     * <h2>Obtains the maximum achievable linear acceleration of the chassis.</h2>
     *
     * @return the maximum linear acceleration, limited by the wheels' grip on the floor
     */
    public LinearAcceleration maxLinearAcceleration() {
        return MetersPerSecondPerSecond.of(
                config.wheelCoefficientOfFriction * GRAVITY_ACCELERATION_METERS_PER_SECOND_SQUARED);
    }

    /**
     *
     *
     * <h2>Obtains the maximum achievable angular acceleration of the chassis.</h2>
     *
     * @return the maximum angular acceleration, limited by the wheels' grip on the floor
     */
    public AngularAcceleration maxAngularAcceleration() {
        return RadiansPerSecondPerSecond.of(
                getTotalGrippingForceNewtons() * config.driveBaseRadius().in(Meters) / super.getMass().getInertia());
    }

    /**
     *
     *
     * <h2>Obtains the drive base radius of the drivetrain.</h2>
     *
     * @return the approximate drive base radius, see {@link DriveTrainSimulationConfig#driveBaseRadius()}
     */
    public Distance driveBaseRadius() {
        return config.driveBaseRadius();
    }
}
