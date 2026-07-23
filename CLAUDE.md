# Maple-Sim Developer Knowledge Base

## Project Overview

**Maple-Sim** is an advanced FRC (FIRST Robotics Competition) Java robot simulation library that integrates the open-source Java rigid-body dynamics engine **dyn4j** to simulate realistic 2D forces and collisions between rigid shapes.

### Project Information
- **Repository**: https://github.com/Shenzhen-Robotics-Alliance/maple-sim
- **Documentation**: https://shenzhen-robotics-alliance.github.io/maple-sim/
- **JavaDocs**: https://shenzhen-robotics-alliance.github.io/maple-sim/javadocs/
- **Physics Engine**: dyn4j 5.0.2 (2D rigid-body dynamics)
- **Java Version**: 17 (source & target compatibility)
- **WPILib Version**: 2025.3.2
- **Gradle Version**: 8.11

---

## Build System

### Project Structure
```
maple-sim/
├── project/                    # Main library source
│   ├── src/main/java/          # Java source code
│   ├── src/main/native/        # C++ source (if applicable)
│   ├── build.gradle            # Main build configuration
│   ├── publish.gradle          # Publishing configuration
│   ├── config.gradle           # C++ configuration
│   └── settings.gradle         # Gradle settings
├── docs/                       # Documentation (Markdown)
│   ├── vendordep/              # VendorDep JSON and Maven repo
│   └── javadocs/               # Generated JavaDocs
└── templates/                  # Example projects

```

### Gradle Plugins Used
- `java` - Java compilation
- `cpp` - C++ support (minimal usage)
- `google-test` - Testing framework
- `com.diffplug.spotless` 7.0.0.BETA4 - Code formatting
- `edu.wpi.first.wpilib.repositories.WPILibRepositoriesPlugin` 2025.0 - WPILib repos
- `edu.wpi.first.NativeUtils` 2025.9.0 - Native utilities
- `edu.wpi.first.GradleJni` 1.1.0 - JNI support
- `edu.wpi.first.GradleVsCode` 2.1.0 - VSCode integration
- `edu.wpi.first.GradleRIO` 2025.3.2 - FRC robot support
- `maven-publish` - Publishing to Maven

### Dependencies

**Core Physics**:
- `org.dyn4j:dyn4j:5.0.2` - Physics engine

**WPILib Dependencies** (version 2025.3.2):
- `cscore-java` - Camera support
- `cameraserver-java` - Camera server
- `ntcore-java` - NetworkTables
- `wpilibj-java` - Core WPILib
- `wpiutil-java` - Utilities
- `wpimath-java` - Math library
- `wpiunits-java` - Units library
- `wpilibNewCommands-java` - Command-based framework
- `hal-java` - Hardware abstraction layer

**Additional Libraries**:
- `org.ejml:ejml-simple:0.43.1` - Matrix operations
- `com.fasterxml.jackson.*:2.15.2` - JSON processing
- `edu.wpi.first.thirdparty.frc2024.opencv:opencv-java:4.8.0-2` - OpenCV

### Build Tasks

**Compilation**:
```bash
./gradlew build              # Compiles and runs tests
./gradlew compileJava        # Compiles Java only (triggers spotlessApply)
```

**Code Formatting**:
```bash
./gradlew spotlessApply      # Formats all code (automatic on compile)
./gradlew spotlessCheck      # Checks formatting without applying
```

**Documentation**:
```bash
./gradlew javadoc            # Generates JavaDocs to ../javadocs/
```

**Publishing**:
```bash
./gradlew publish            # Publishes to local Maven repo (docs/vendordep/repos/releases)
./gradlew publishToMavenLocal # Publishes to ~/.m2 for local testing
```

**Artifact Generation**:
```bash
./gradlew outputJar          # Creates main JAR
./gradlew outputSourcesJar   # Creates sources JAR
./gradlew outputJavadocJar   # Creates JavaDoc JAR
./gradlew copyAllOutputs     # Copies all outputs to build/allOutputs
```

### Publishing Configuration

**Maven Coordinates**:
- **Group ID**: `org.ironmaple`
- **Artifact ID**: `maplesim-java`
- **Version**: Defined in `project/publish.gradle` (currently `0.3.14-test`)

**Publishing Location**:
- Local test repo: `docs/vendordep/repos/releases`
- VendorDep JSON: `docs/vendordep/maple-sim.json` (auto-generated with version)

**Artifacts Published**:
1. Main JAR (`maplesim-java-{version}.jar`)
2. Sources JAR (`maplesim-java-{version}-sources.jar`)
3. JavaDoc JAR (`maplesim-java-{version}-javadoc.jar`)

### Testing Changes

To test your changes:

1. **Update version** in `project/publish.gradle`:
   ```groovy
   def pubVersion = 'my-feature-test'
   ```

2. **Publish to Maven Local**:
   ```bash
   ./gradlew publishToMavenLocal
   ```
   Or use IntelliJ: Click "Publish to Maven Local" in Gradle tool window

3. **Copy vendordep** from `docs/vendordep/maple-sim.json` to a template project's `vendordeps/` folder

4. **Test** in one of the template projects

---

## Code Style Guidelines

### ⚠️ CRITICAL: Automatic Formatting

**Spotless** is configured to automatically format code on every compilation. Your code will be formatted when you run `./gradlew build` or `./gradlew compileJava`.

- **Java**: Palantir Java Format 2.39.0 (with JavaDoc formatting)
- **Groovy/Gradle**: Greclipse formatter
- **Markdown/Misc**: 4-space indentation

### Formatting Rules Applied

**Java**:
- Palantir Java Format style
- JavaDoc formatting enabled
- Unused imports removed
- Trailing whitespace trimmed
- Files end with newline

**Gradle Files**:
- 4-space indentation
- Trailing whitespace trimmed
- Files end with newline

### Code Style Requirements

#### ✅ Things You MUST Do

**1. Be a "Never Nester"**
- Avoid deeply nested code
- Use early returns and guard clauses
- Reference: [CodeAesthetic - Why You Shouldn't Nest Your Code](https://www.youtube.com/watch?v=CFRhGnuXG-4)

Example:
```java
// BAD - Nested
public void process(Item item) {
    if (item != null) {
        if (item.isValid()) {
            if (item.isReady()) {
                item.process();
            }
        }
    }
}

// GOOD - Never nested
public void process(Item item) {
    if (item == null) return;
    if (!item.isValid()) return;
    if (!item.isReady()) return;

    item.process();
}
```

**2. Naming Conventions**
- **Variables**: `camelCase`
- **Constants**: `ALL_CAPS`
- **Classes**: `PascalCase`

**3. Descriptive Variable Names**
Variables and constants must be self-explanatory:

```java
// GOOD
double differenceBetweenGroundAndDesiredVelocityMetersPerSecond;
Mass ROBOT_MASS_WITH_BUMPERS;
Distance trackLengthX;

// BAD
double diff;
double m;
double x;
```

**4. Use WPILib Units Library**
- Use `edu.wpi.first.units` for all public APIs and configurations
- OK to use `double` for internal calculations, but:
  - Must use SI units
  - Must include units in variable names

```java
// GOOD - Public API
public void setVelocity(LinearVelocity velocity) { ... }

// GOOD - Internal calculation
private void calculate() {
    double accelerationMetersPerSecondSquared = forceNewtons / massKilograms;
    double delaySeconds = 0.02;
}

// BAD - Ambiguous units
private void calculate() {
    double acceleration = force / mass;  // What units?
    double delay = 0.02;                 // Seconds? Milliseconds?
}
```

**5. JavaDoc for All Public APIs**
- Every public class, method, and constructor needs JavaDoc
- Method JavaDocs must start with `<h2>` tag for title

```java
/**
 *
 *
 * <h2>Sets the Robot's Current Pose in the Simulation World.</h2>
 *
 * <p>This method instantly teleports the robot to the specified pose in the simulation world.
 * The robot does not drive to the new pose; it is moved directly.
 *
 * @param robotPose the desired robot pose, represented as a {@link Pose2d}
 */
public void setSimulationWorldPose(Pose2d robotPose) {
    // implementation
}
```

**6. Provide References for Math/Physics**
When implementing equations, add comments with references:

```java
// Calculate projectile position using kinematic equation
// Reference: https://en.wikipedia.org/wiki/Projectile_motion
final double height = initialHeight + initialVerticalSpeedMPS * t - 0.5 * GRAVITY * t * t;
```

#### ❌ Things You MUST AVOID

**1. Hungarian Notation**
```java
// BAD
private double m_velocity;
private static final double k_maxSpeed = 5.0;

// GOOD
private double velocity;
private static final double MAX_SPEED = 5.0;
```

**2. Excessively Long Files**
- Keep files under **600 lines**
- If a file exceeds 600 lines, refactor into multiple classes
- Extract nested classes into separate files
- Split complex logic into helper classes

---

## Architecture Overview

### Core Package Structure

```
org.ironmaple.simulation/
├── SimulatedArena.java                    # Main simulation world/arena
├── IntakeSimulation.java                   # Intake mechanism simulation
├── Goal.java                               # Goal interface
│
├── drivesims/                              # Drivetrain simulation
│   ├── AbstractDriveTrainSimulation.java
│   ├── DriveTrainSimulation.java
│   ├── COTS.java                           # Wheel coefficient-of-friction reference values
│   └── configs/
│       ├── DriveTrainSimulationConfig.java
│       └── BoundingCheck.java
│
├── gamepieces/                             # Game piece simulations
│   ├── GamePiece.java
│   ├── GamePieceOnFieldSimulation.java
│   └── GamePieceProjectile.java
│
├── seasonspecific/                         # Season-specific implementations
│   ├── crescendo2024/
│   │   ├── Arena2024Crescendo.java
│   │   ├── CrescendoNoteOnField.java
│   │   ├── NoteOnFly.java
│   │   ├── CrescendoHumanPlayerSimulation.java
│   │   ├── CrescendoAmp.java
│   │   └── CrescendoSpeaker.java
│   ├── reefscape2025/
│   │   ├── Arena2025Reefscape.java
│   │   ├── ReefscapeAlgaeOnField.java
│   │   ├── ReefscapeAlgaeOnFly.java
│   │   ├── ReefscapeCoralOnField.java
│   │   ├── ReefscapeCoralOnFly.java
│   │   ├── ReefscapeCoralAlgaeStack.java
│   │   ├── ReefscapeReefSimulation.java
│   │   ├── ReefscapeReefBranch.java
│   │   ├── ReefscapeBargeSimulation.java
│   │   └── ReefscapeProcessorSimulation.java
│   └── evergreen/
│       └── ArenaEvergreen.java
│
└── utils/                                  # Utility classes
    ├── FieldMirroringUtils.java
    ├── LegacyFieldMirroringUtils2024.java
    └── mathutils/
        ├── GeometryConvertor.java
        └── MapleCommonMath.java
```

---

## Key Classes Reference

### Core Simulation Classes

#### `SimulatedArena` (Abstract Class)
**Location**: `org.ironmaple.simulation.SimulatedArena`

**Purpose**: The heart of the simulator - the main simulation world that manages all interactions within the arena field.

**Key Responsibilities**:
- Manages the physics world (dyn4j World instance)
- Coordinates all simulated objects (drivetrains, game pieces, intakes)
- Handles simulation timing and sub-tick updates (default: 5 sub-ticks per period)
- Manages scoring and match data
- Provides field reset functionality

**Important Methods**:
- `getInstance()` - Gets/creates the default simulation world
- `overrideInstance(SimulatedArena)` - Overrides default instance
- `simulationPeriodic()` - Main update loop
- `simulationSubTick(int)` - Processes single sub-tick
- `addDriveTrainSimulation(AbstractDriveTrainSimulation)` - Registers a drivetrain
- `addGamePiece(GamePieceOnFieldSimulation)` - Adds game piece to field
- `addGamePieceProjectile(GamePieceProjectile)` - Launches projectile
- `addIntakeSimulation(IntakeSimulation)` - Registers intake (called automatically)
- `addCustomSimulation(Simulatable)` - Registers custom simulation
- `getGamePiecesPosesByType(String)` - Gets positions of game pieces by type
- `resetFieldForAuto()` - Resets field to auto starting configuration
- `placeGamePiecesOnField()` - Abstract method to place game pieces (season-specific)

**Key Properties**:
- `physicsWorld` - dyn4j World instance (zero gravity)
- `driveTrainSimulations` - Set of registered drivetrains
- `gamePieces` - Set of game pieces (both on field and projectiles)
- `intakeSimulations` - List of registered intakes
- `customSimulations` - List of custom Simulatable objects
- Default instance: `Arena2025Reefscape` (current season)

**Nested Classes**:
- `Simulatable` - Interface for custom simulations with `simulationSubTick(int)` method
- `FieldMap` - Abstract class for field obstacle layouts

**Timing Configuration**:
- Default: 5 sub-ticks per robot period (250Hz for 50Hz robot)
- Override via `overrideSimulationTimings(Time, int)`
- Sub-tick period: `SIMULATION_DT` (default 0.004 seconds)

---

### Drivetrain Simulation

Maple-sim simulates the drivetrain as a single rigid body driven directly by a commanded `ChassisSpeeds`, rather than by individually modeled swerve modules/motors/gyro. The commanded speeds are treated as the idealized, "if there was no physics" target velocity (exactly what a real robot's drive code computes every loop); the simulation converts that into realistic motion by applying a wheel-grip-limited force/torque toward it, respecting mass, rotational inertia, and friction limits. There is no simulated motor current, gear ratio, or gyro drift/noise — reported pose and chassis speeds are ground truth.

#### `DriveTrainSimulation`
**Location**: `org.ironmaple.simulation.drivesims.DriveTrainSimulation`

**Purpose**: Simulates a holonomic drivetrain chassis driven directly by chassis speeds.

**Simulation Physics**:
1. **Friction force** pulling the robot from its current ground velocity toward the desired chassis speeds (`simulateChassisFrictionForce()`)
2. **Rotational friction** torque doing the same for angular velocity (`simulateChassisFrictionTorque()`)
3. **Centripetal forces** when the commanded direction of travel changes

Both are limited by the wheels' gripping force, computed from `config.robotMass` and `config.wheelCoefficientOfFriction` (Coulomb friction: `F = μmg`).

**Important Methods**:
- `setDesiredChassisSpeeds(ChassisSpeeds)` - **Primary input.** Sets the target chassis speeds, robot-relative, called every loop like a real robot's drive periodic
- `simulationSubTick()` - Updates physics (called by SimulatedArena)
- `maxLinearAcceleration()` - Max linear acceleration, limited by wheel grip
- `maxAngularAcceleration()` - Max angular acceleration, limited by wheel grip
- `driveBaseRadius()` - Approximate drive base radius (derived from bumper size, since individual module/wheel positions are no longer modeled)

**Configuration**: Uses `DriveTrainSimulationConfig`

---

#### `AbstractDriveTrainSimulation`
**Location**: `org.ironmaple.simulation.drivesims.AbstractDriveTrainSimulation`

**Purpose**: Base class for all drivetrain simulations, handling mass, collision space, and basic physics.

**Key Constants**:
- `BUMPER_COEFFICIENT_OF_FRICTION = 0.65`
- `BUMPER_COEFFICIENT_OF_RESTITUTION = 0.08`

**Important Methods**:
- `setSimulationWorldPose(Pose2d)` - Teleports robot to pose
- `setRobotSpeeds(ChassisSpeeds)` - Sets robot velocity instantly (bypasses physics; use `DriveTrainSimulation#setDesiredChassisSpeeds` for grip-limited convergence instead)
- `getSimulatedDriveTrainPose()` - Returns actual (ground-truth) robot pose in simulation
- `getDriveTrainSimulatedChassisSpeedsRobotRelative()` / `...FieldRelative()` - Returns actual (ground-truth) chassis speeds
- `simulationSubTick()` - Abstract method for physics updates

**Extends**: `org.dyn4j.dynamics.Body` (physics body)

---

#### `DriveTrainSimulationConfig`
**Location**: `org.ironmaple.simulation.drivesims.configs.DriveTrainSimulationConfig`

**Purpose**: Configuration object for the drivetrain simulation. Deliberately minimal — no module, motor, or gyro configuration.

**Key Fields**:
- `robotMass` - Mass (includes bumpers)
- `bumperLengthX` - Front-to-back dimension
- `bumperWidthY` - Left-to-right dimension
- `wheelCoefficientOfFriction` - Wheel-to-floor coefficient of friction (limits grip-based acceleration/torque)

**Important Methods**:
- `Default()` - Creates default config (45kg, 0.76m bumpers, Colson wheel COF)
- `withRobotMass(Mass)` - Sets robot mass
- `withBumperSize(Distance, Distance)` - Sets bumper dimensions
- `withWheelCoefficientOfFriction(double)` - Sets wheel COF
- `driveBaseRadius()` - Approximates the center-to-wheel distance from bumper size (used only to convert grip force into a torque limit)
- `getDensityKgPerSquaredMeters()` - Calculates chassis density

---

#### `COTS`
**Location**: `org.ironmaple.simulation.drivesims.COTS`

**Purpose**: Reference coefficient-of-friction values for common wheels.

**Wheels** (nested enum `WHEELS`):
- `COLSONS`, `DEFAULT_NEOPRENE_TREAD`, `BLUE_NITRILE_TREAD`, `VEX_GRIP_V2`, `SLS_PRINTED_WHEELS`
- Each has a `cof` (coefficient of friction) field, usable directly as `DriveTrainSimulationConfig#wheelCoefficientOfFriction`

---

### Game Piece Simulation

#### `GamePieceOnFieldSimulation`
**Location**: `org.ironmaple.simulation.gamepieces.GamePieceOnFieldSimulation`

**Purpose**: Simulates a game piece on the field with collision detection and physics.

**Key Constants**:
- `COEFFICIENT_OF_FRICTION = 0.8`
- `MINIMUM_BOUNCING_VELOCITY = 0.2` m/s

**Important Fields**:
- `type` - String identifier (e.g., "Note", "Coral", "Algae")
- `zPositionSupplier` - DoubleSupplier for height (normally fixed)

**Important Methods**:
- `getPoseOnField()` - Returns Pose2d (2D position)
- `getPose3d()` - Returns Pose3d (includes height from zPositionSupplier)
- `setVelocity(ChassisSpeeds)` - Sets game piece velocity
- `onIntake(String)` - Callback when intaken (overridable)
- `getType()` - Returns type string
- `getVelocity3dMPS()` - Returns 3D velocity
- `isGrounded()` - Returns true (always on ground)

**Nested Record**: `GamePieceInfo`
- `type` - String identifier
- `shape` - Convex (dyn4j shape)
- `gamePieceHeight` - Distance
- `gamePieceMass` - Mass
- `linearDamping` - double
- `angularDamping` - double
- `coefficientOfRestitution` - double

**Extends**: `org.dyn4j.dynamics.Body`
**Implements**: `GamePiece`

---

#### `GamePieceProjectile`
**Location**: `org.ironmaple.simulation.gamepieces.GamePieceProjectile`

**Purpose**: Simulates game pieces launched into the air using projectile motion.

**Key Constants**:
- `GRAVITY = 11` m/s² (adjusted for lack of air drag simulation)

**Important Fields**:
- `info` - GamePieceInfo
- `gamePieceType` - String type
- `initialPosition` - Translation2d
- `initialLaunchingVelocityMPS` - Translation2d (horizontal velocity)
- `initialHeight` - double
- `initialVerticalSpeedMPS` - double
- `gamePieceRotation` - Rotation3d
- `launchedTimer` - Timer (elapsed time since launch)
- `calculatedHitTargetTime` - double (-1 if no hit)
- `becomesGamePieceOnGroundAfterTouchGround` - boolean

**Important Methods**:
- `launch()` - Starts projectile, calculates trajectory and hit detection
- `hasHitGround()` - Returns true if touched ground
- `hasHitTarget()` - Returns true if hit target at current time
- `willHitTarget()` - Returns true if will eventually hit target
- `hasGoneOutOfField()` - Returns true if flew out of bounds
- `getPose3d()` - Returns current 3D position
- `getVelocity3dMPS()` - Returns current 3D velocity
- `getPositionAtTime(double)` - Calculates position at time t
- `addGamePieceAfterTouchGround(SimulatedArena)` - Creates GamePieceOnFieldSimulation on landing

**Configuration Methods** (builder pattern):
- `withTargetPosition(Supplier<Translation3d>)` - Sets target
- `withTargetTolerance(Translation3d)` - Sets hit tolerance (default: 0.2, 0.2, 0.2)
- `withHitTargetCallBack(Runnable)` - Callback on hit
- `withProjectileTrajectoryDisplayCallBack(Consumer<List<Pose3d>>)` - Visualize trajectory
- `withProjectileTrajectoryDisplayCallBack(Consumer, Consumer)` - Separate callbacks for hit/miss
- `enableBecomesGamePieceOnFieldAfterTouchGround()` - Convert to field piece on landing
- `disableBecomesGamePieceOnFieldAfterTouchGround()` - Don't convert on landing
- `withTouchGroundHeight(double)` - Sets ground height threshold (default: 0.5)
- `cleanUp()` - Clears trajectory visualization

**Static Method**:
- `updateGamePieceProjectiles(SimulatedArena, Set<GamePieceProjectile>)` - Updates all projectiles, removes if hit/landed/out

**Implements**: `GamePiece`

---

### Intake Simulation

#### `IntakeSimulation`
**Location**: `org.ironmaple.simulation.IntakeSimulation`

**Purpose**: Simulates an intake mechanism that can collect game pieces.

**Key Characteristics**:
- Idealized "touch it, get it" intake
- Rectangular or custom shape attached to chassis side
- Extends when activated, retracts when deactivated
- Listens for contacts with specific game piece types
- Has capacity limit (max 100)

**Important Fields**:
- `capacity` - int (max game pieces)
- `gamePiecesInIntakeCount` - int (current count)
- `intakeRunning` - boolean (is active)
- `targetedGamePieceType` - String (type to collect)
- `driveTrainSimulation` - AbstractDriveTrainSimulation (attached chassis)
- `gamePiecesToRemove` - Queue<GamePieceOnFieldSimulation> (pending removals)
- `customIntakeCondition` - Predicate<GamePieceOnFieldSimulation> (custom filter)

**Important Methods**:
- `startIntake()` - Activates intake (adds fixture to chassis)
- `stopIntake()` - Deactivates intake (removes fixture from chassis)
- `getGamePiecesAmount()` - Returns count
- `obtainGamePieceFromIntake()` - Removes one piece, returns success boolean
- `addGamePieceToIntake()` - Adds one piece, returns success boolean
- `setGamePiecesCount(int)` - Sets count (clamped 0-capacity)
- `isRunning()` - Returns if active
- `setCustomIntakeCondition(Predicate)` - Sets custom intake filter
- `getGamePieceContactListener()` - Returns new contact listener
- `removeObtainedGamePieces(SimulatedArena)` - Removes collected pieces (called by arena)
- `register()` / `register(SimulatedArena)` - Registers to arena

**Factory Methods**:
- `InTheFrameIntake(String, AbstractDriveTrainSimulation, Distance, IntakeSide, int)` - ITF intake (tight to chassis)
- `OverTheBumperIntake(String, AbstractDriveTrainSimulation, Distance, Distance, IntakeSide, int)` - OTB intake (extends)

**Nested Enum**: `IntakeSide` - FRONT, LEFT, RIGHT, BACK

**Nested Class**: `GamePieceContactListener` - Implements `ContactListener<Body>`
- Detects contacts between intake and game pieces
- Flags game pieces for removal if conditions met

**Collection Conditions** (all must be true):
1. Game piece type matches `targetedGamePieceType`
2. Game piece contacts intake fixture (not other chassis parts)
3. Intake is running (`intakeRunning == true`)
4. Not at capacity (`gamePiecesInIntakeCount < capacity`)
5. Passes `customIntakeCondition` (if set)

**Extends**: `org.dyn4j.dynamics.BodyFixture`

---

### Season-Specific Classes

#### Crescendo 2024
**Package**: `org.ironmaple.simulation.seasonspecific.crescendo2024`

**`Arena2024Crescendo`**
- Extends `SimulatedArena`
- Full 2024 field with obstacles
- Note placement logic
- Speaker and Amp simulation

**`CrescendoNoteOnField`**
- Extends `GamePieceOnFieldSimulation`
- Type: "Note"
- Orange foam ring game piece

**`NoteOnFly`**
- Extends `GamePieceProjectile`
- Helper methods: `asSpeakerShotNote(Runnable)`, `asAmpShotNote(Runnable)`

**`CrescendoHumanPlayerSimulation`**
- Simulates human player auto-spawning notes

**`CrescendoAmp`** / **`CrescendoSpeaker`**
- Field element simulations

---

#### Reefscape 2025
**Package**: `org.ironmaple.simulation.seasonspecific.reefscape2025`

**`Arena2025Reefscape`**
- Extends `SimulatedArena`
- Default arena for 2025 season
- Coral and Algae placement
- Reef, Barge, Processor simulations

**`ReefscapeCoralOnField`** / **`ReefscapeAlgaeOnField`**
- Extend `GamePieceOnFieldSimulation`
- Types: "Coral", "Algae"

**`ReefscapeCoralOnFly`** / **`ReefscapeAlgaeOnFly`**
- Extend `GamePieceProjectile`
- Projectile versions

**`ReefscapeCoralAlgaeStack`**
- Simulates stacked coral/algae
- Can be intaken by coral or algae intakes

**`ReefscapeReefSimulation`**
- Simulates reef structure
- Contains branches (`ReefscapeReefBranch`)

**`ReefscapeBargeSimulation`** / **`ReefscapeProcessorSimulation`**
- Field element simulations

---

#### Evergreen
**Package**: `org.ironmaple.simulation.seasonspecific.evergreen`

**`ArenaEvergreen`**
- Extends `SimulatedArena`
- Generic arena for testing
- No season-specific elements
- Minimal field obstacles

---

### Utility Classes

#### `GeometryConvertor`
**Location**: `org.ironmaple.utils.mathutils.GeometryConvertor`

**Purpose**: Converts between WPILib and dyn4j geometry classes.

**Key Methods**:
- `toDyn4jVector2(Translation2d)` - WPILib → dyn4j Vector2
- `toWpilibTranslation2d(Vector2)` - dyn4j → WPILib Translation2d
- `toDyn4jTransform(Pose2d)` - WPILib → dyn4j Transform
- `toWpilibPose2d(Transform)` - dyn4j → WPILib Pose2d
- `toDyn4jLinearVelocity(ChassisSpeeds)` - Extracts linear velocity vector
- `getChassisSpeedsTranslationalComponent(ChassisSpeeds)` - Gets translation as Translation2d

---

#### `FieldMirroringUtils`
**Location**: `org.ironmaple.utils.FieldMirroringUtils`

**Purpose**: Handles field mirroring for red/blue alliances.

**Key Methods**:
- `toCurrentAlliancePose(Pose2d)` - Mirrors pose based on alliance
- `toCurrentAllianceTranslation(Translation2d/3d)` - Mirrors translation
- `getCurrentAllianceDriverStationFacing()` - Gets driver station facing direction
- Methods query `DriverStation.getAlliance()` for current alliance color

---

#### `MapleCommonMath`
**Location**: `org.ironmaple.utils.mathutils.MapleCommonMath`

**Purpose**: Common math utilities for simulation.

**Key Methods**:
- `getAngle(Translation2d)` - Gets angle of 2D vector
- Other mathematical helper functions

---

## Contributing Workflow

### 1. Create a Fork
Fork the repository at https://github.com/Shenzhen-Robotics-Alliance/maple-sim

**Important**: Do NOT select "Copy the main branch only" - you need all branches.

### 2. Develop Your Feature
- Follow code style guidelines (Spotless will auto-format)
- Keep files under 600 lines
- Add JavaDocs for all public APIs
- Use descriptive variable names
- Be a "never nester"

### 3. Test Your Changes
```bash
# 1. Update version in project/publish.gradle
def pubVersion = 'my-feature-test'

# 2. Publish to Maven Local
./gradlew publishToMavenLocal

# 3. Test in a template project
# Copy docs/vendordep/maple-sim.json to template's vendordeps/
```

### 4. Create a Pull Request
Target branches:
- `main` - Bug fixes for known issues
- `dev` - New features without dedicated branch
- `feature-branch` - Features with existing branch

**Include in PR**:
- Detailed description of changes
- Updated documentation (if API changed)
- Updated templates/examples (if applicable)

### 5. Stay Connected
Join Discord: https://discord.com/invite/tWn45Qm6ub

---

## Physics Constants Reference

### Friction Coefficients
- Game pieces on field: 0.8
- Bumper-to-bumper: 0.65

### Restitution (Bounciness)
- Bumper-to-bumper: 0.08
- Game pieces: Varies by GamePieceInfo

### Gravity
- Projectiles: 11 m/s² (adjusted for lack of air drag)

### Simulation Timing
- Default sub-ticks: 5 per robot period
- Default robot period: 0.02 seconds (50 Hz)
- Default simulation frequency: 250 Hz
- Sub-tick duration: 0.004 seconds

---

## Important Development Notes

### Simulation Safety
- `SimulatedArena` checks `RobotBase.isReal()` and throws exception
- `ALLOW_CREATION_ON_REAL_ROBOT` is false by default (keep it that way)

### JavaDoc Generation
- Output directory: `../javadocs` (relative to project/)
- Excludes: `org/json/simple/**`
- Links to Java 17 docs and WPILib docs

### Native Exports
- Export config: `MapleSim` (exports everything)
- Minimal C++ usage (mostly Java)

### Repository Structure
- Maven repo: `docs/vendordep/repos/releases`
- VendorDep JSON: `docs/vendordep/maple-sim.json`
- Templates: `templates/` (example projects, not formatted by Spotless)

---

## Credits

- **Shenzhen Robotics Alliance**: Main development team
- **@GrahamSH-LLK**: Online documentation setup
- **@nstrike**: JavaDocs and VendorDep publishing
- **dyn4j**: Physics engine

---

*This knowledge base is for developers working on the maple-sim library itself.*
