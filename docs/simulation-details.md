# Simulation Details

## Drivetrain Simulation Dynamics with Interactive Field
The standout feature of this project is the integration of the [dyn4j physics engine](https://github.com/dyn4j/dyn4j), which enables a highly realistic and interactive simulation environment.

Rather than modeling individual swerve modules or motors, maple-sim drives the chassis directly from a commanded `ChassisSpeeds` — the idealized velocity your drive code would achieve with no physics in the way. The simulation converts that into realistic motion by applying a wheel-grip-limited friction force and torque toward it, modeling the drivetrain as a single rigid body so it can interact accurately with the field environment.

![swerve drive dynamics.gif](media%2Fswerve%20drive%20dynamics.gif)

The realistic simulation dynamics allow you to test and refine auto paths as they would perform in real-world conditions.

![path following simulation.gif](media%2Fpath%20following%20simulation.gif)

**[View Full Document on Drivetrain Simulation](./swerve-simulation-overview.md) >>>**

## Vision Simulation

The simulation reports the chassis' ground-truth pose and chassis speeds (no simulated encoder noise or gyro drift), which you can feed into [photonlib](https://docs.photonvision.org/en/latest/docs/simulation/simulation-java.html) to simulate how vision-based odometry can correct your robot's position.

![vision simulation.gif](media%2Fvision%20simulation.gif)

## Game Pieces and Intake Simulation
In **maple-sim**, game pieces on the field have collision boundaries and can interact with the robot.
The simulator also supports a fixed intake module, allowing the robot to automatically collect game pieces upon contact.

![game pieces simulation.gif](media%2Fgame%20pieces%20simulation.gif)

![intakesim.gif](media/intakesim.gif)

**[View Full Document on Intake Simulation](./simulating-intake.md) >>>**

## Projectile Simulation
In FRC, game pieces are often launched into the air. 
**maple-sim** offers a straightforward physics simulation to model the behavior of these projectiles.

![projectile simulation.gif](media%2Fprojectile%20simulation.gif)

**[View Full Document on Projectile Simulation](./simulating-projectiles.md) >>>**

## Opponent Robots Simulation

Simulated opponent robots can be manually controlled with a gamepad for defensive play or set to follow pre-programmed cycle paths. 
Just like real robots, these opponents have collision boundaries, enabling drivers to practice both defensive and offensive strategies effectively.
![opponentrobotsim.gif](media/opponent%20robot%20simulation.gif)

