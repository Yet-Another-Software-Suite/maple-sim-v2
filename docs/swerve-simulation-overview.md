# Simulating the Drivetrain

!!! info
      You are reading the documentation for a Beta version of maple-sim. API references are subject to change in future versions.

!!! tip
      This realistic simulation allows you to accomplish a variety of tasks without the real robot. You can practice driving, test autonomous routines, and fine-tune advanced functions such as auto-alignment, just to name a few.

---
## 0. Creating the Drivetrain Configuration

The `DriveTrainSimulationConfig` object holds the physical properties of the drivetrain. It is intentionally minimal: there is no module, motor, or gyro configuration to set up — just the robot's mass, bumper size, and how much grip its wheels have on the floor.

```java
// Create and configure a drivetrain simulation configuration
final DriveTrainSimulationConfig driveTrainSimulationConfig = DriveTrainSimulationConfig.Default()
        // Configures the mass of the robot (including bumpers)
        .withRobotMass(Kilograms.of(50))
        // Configures the bumper size (dimensions of the robot bumper)
        .withBumperSize(Inches.of(30), Inches.of(30))
        // Configures how much grip the wheels have on the floor
        // See COTS.WHEELS for reference coefficients of common wheels
        .withWheelCoefficientOfFriction(COTS.WHEELS.COLSONS.cof);
```

---
## 1. Instantiate and Register a Drivetrain Simulation

The `DriveTrainSimulation` class represents the simulated chassis within the simulation environment. It models the chassis as a single rigid body: it has mass, rotational inertia, and a collision space, allowing it to interact with the field and other robots through the physics engine.

You can instantiate the drivetrain simulation using the following code:

```java
/* Create a drivetrain simulation */
this.driveTrainSimulation = new DriveTrainSimulation(
        // Specify configuration
        driveTrainSimulationConfig,
        // Specify starting pose
        new Pose2d(3, 3, new Rotation2d())
);
```

The simulation must be registered to the simulation world for it to function correctly:

```java
// Register the drivetrain simulation to the default simulation world
SimulatedArena.getInstance().addDriveTrainSimulation(driveTrainSimulation);
```

---
## 2. Driving the Simulated Chassis

The **only input** the simulation needs is a `ChassisSpeeds`, representing the speeds the drivetrain would achieve **if it were not subject to physics** — exactly what your drive code already computes every loop on a real robot (from joystick input, a holonomic drive controller, PathPlanner's output, etc.).

Call `setDesiredChassisSpeeds(...)` every loop with that value; the simulation takes care of converting it into realistic motion, respecting the chassis' mass, rotational inertia, and the wheels' grip limit (aggressive commands will cause realistic, grip-limited acceleration rather than an instant jump to speed):

```java
@Override
public void periodic() {
    // Robot-relative chassis speeds, exactly as you would command a real drivetrain
    final ChassisSpeeds desiredSpeeds = ...;
    driveTrainSimulation.setDesiredChassisSpeeds(desiredSpeeds);
}
```

!!! tip
      Since there's no module or motor simulation to abstract away, this is the *only* API you need to drive the simulated chassis — whether you're doing simple joystick teleop, closed-loop PathPlanner following, or defensive AI opponent-robot control. See [Simulating Opponent Robots](./simulating-opponent-robots.md) for a complete example.

---
## 3. Reading Back the Simulated State

- `getSimulatedDriveTrainPose()` — returns the actual (ground-truth) `Pose2d` of the chassis. Use this to display the robot on [AdvantageScope Field3d](https://docs.advantagescope.org/tab-reference/3d-field/) or to update a [PhotonVision simulation](https://docs.photonvision.org/en/latest/docs/simulation/simulation-java.html#updating-the-simulation-world).
- `getDriveTrainSimulatedChassisSpeedsRobotRelative()` / `getDriveTrainSimulatedChassisSpeedsFieldRelative()` — returns the actual (ground-truth) chassis speeds, after friction/grip limiting has been applied.
- `driveBaseRadius()`, `maxLinearAcceleration()`, `maxAngularAcceleration()` — geometry/grip-derived helpers, useful for scaling joystick input or configuring a path-following controller.

!!! note
      All of the above are **ground truth** — there is no simulated encoder noise or gyro drift. If your drive code relies on odometry to compute the pose it feeds back to `setDesiredChassisSpeeds` (e.g. through a `SwerveDrivePoseEstimator`/`PoseEstimator`), you can build that on top of `getSimulatedDriveTrainPose()` yourself the same way you would report a "true" pose from a simulated sensor.
