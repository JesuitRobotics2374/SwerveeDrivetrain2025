// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathfindingCommand;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.subsystems.ClimbingSubsystem;
import frc.robot.subsystems.LightsSubsystem;
import frc.robot.subsystems.LightsSubsystem.LEDStripMode;
import frc.robot.subsystems.drivetrain.CommandSwerveDrivetrain;
import frc.robot.subsystems.drivetrain.TunerConstants;
import frc.robot.utils.LimelightObject;
import frc.robot.utils.LimelightObject.LLType;

public class Core {
    public double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond) * Constants.MAX_SPEED; // kSpeedAt12Volts
                                                                                                        // desired top
                                                                                                        // speed
    public double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond) * Constants.MAX_ANGULAR_RATE; // 3/4
                                                                                                                   // of
                                                                                                                   // a
                                                                                                                   // rotation
                                                                                                                   // per
                                                                                                                   // second
                                                                                                                   // max
                                                                                                                   // angular
                                                                                                                   // velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController driveController = new CommandXboxController(0);
    
    private final CommandXboxController operatorController = new CommandXboxController(1);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    public final ClimbingSubsystem climbingSubsystem = new ClimbingSubsystem();
    
    public final LightsSubsystem lightsSubsystem = new LightsSubsystem();

    private final SendableChooser<Command> autoChooser;

    private Command pathfindingCommand;

    public Core() {
        registerAutoCommands();
        autoChooser = AutoBuilder.buildAutoChooser();
        configureBindings();
        configureShuffleBoard();

        drivetrain.setRobotPose(new Pose2d(7.5, 1.5, new Rotation2d(180 * (Math.PI / 180))));
    }

    public void registerAutoCommands() {
        // NamedCommands.registerCommand("Test Pathfind", new PathfindBasic(drivetrain,
        // Constants.TEST_PATHFIND_TARGET));

        // PathfindingCommand.warmupCommand().schedule();
    }

    public void configureShuffleBoard() {

        ShuffleboardTab tab = Shuffleboard.getTab("Test");

        // Limelight
        // HttpCamera httpCamera = new HttpCamera("Limelight",
        // "http://limelight.local:5800");
        // CameraServer.addCamera(httpCamera);
        // tab.add(httpCamera).withPosition(7, 0).withSize(3, 2);

        // New List Layout
        // ShuffleboardContainer pos = tab.getLayout("Position", "List
        // Layout").withPosition(0, 0).withSize(2, 3);

        // Field
        tab.add(drivetrain.getField()).withPosition(2, 1).withSize(5, 3);

        // Modes
        // tab.addBoolean("Slow Mode", () -> isSlow()).withPosition(2, 0).withSize(2,
        // 1);
        // tab.addBoolean("Roll Mode", () -> isRoll()).withPosition(5, 0).withSize(1,
        // 1);

        // Robot (Reverse order for list layout)
        // pos.addDouble("Robot R", () -> drivetrain.getRobotR())
        // .withWidget("Gyro");
        // ;
        // pos.addDouble("Robot Y", () -> drivetrain.getRobotY())
        // .withWidget("Number Bar");
        // pos.addDouble("Robot X", () -> drivetrain.getRobotX());

        tab.add("Auto Chooser", autoChooser);

    }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
                // Drivetrain will execute this command periodically
                drivetrain.applyRequest(() -> drive.withVelocityX(-driveController.getLeftY() * MaxSpeed * getAxisMovementScale()) // Drive
                                                                                                          // forward
                                                                                                          // with
                                                                                                          // negative Y
                                                                                                          // (forward)
                        .withVelocityY(-driveController.getLeftX() * MaxSpeed * getAxisMovementScale()) // Drive left with negative X (left)
                        .withRotationalRate(-driveController.getRightX() * MaxAngularRate * getAxisMovementScale()) // Drive counterclockwise
                                                                                           // with negative X (left)
                ));

        // driveController.a().whileTrue(drivetrain.applyRequest(() -> brake));
        // driveController.b().whileTrue(drivetrain.applyRequest(() ->
        // point.withModuleDirection(new Rotation2d(-driveController.getLeftY(),
        // -driveController.getLeftX()))
        // ));

        // driveController.a()
        //         .onTrue(drivetrain.runOnce(() -> drivetrain.alignToVision(Constants.LIMELIGHTS_ON_BOARD[0], true)));

       // driveController.b().onTrue(new Outtake(outtakeSubsystem));

        // driveController.rightBumper().onTrue(climbingSubsystem.runOnce(() -> climbingSubsystem.climbRaise()));
        // driveController.leftBumper().onTrue(climbingSubsystem.runOnce(() -> climbingSubsystem.climbLower()));

        //driveController.a().onTrue(climbingSubsystem.runOnce(() -> climbingSubsystem.zeroSystem()));
        driveController.a().onTrue(lightsSubsystem.runOnce(() -> lightsSubsystem.setAllLEDToIdle()));
        //driveController.leftTrigger().onTrue(climbingSubsystem.runOnce(() -> climbingSubsystem.servoPosition(0.0)));

        driveController.b().onTrue(lightsSubsystem.runOnce(() -> lightsSubsystem.setAllLEDToOff()));

        driveController.x().onTrue(lightsSubsystem.runOnce(() -> lightsSubsystem.setAllLEDToColor(0, 255, 0)));

        driveController.y().onTrue(lightsSubsystem.runOnce(() -> lightsSubsystem.setLEDMode(LEDStripMode.JesuitWave)));

        // driveController.y().onTrue(lightsSubsystem.runOnce(() -> climbingSubsystem.speed(-0.5)));


        

        // driveController.povUp().onTrue(climbingSubsystem.runOnce(() -> climbingSubsystem.raise()));
        // driveController.povDown().onTrue(climbingSubsystem.runOnce(() -> climbingSubsystem.lower()));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        // driveController.back().and(driveController.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        // driveController.back().and(driveController.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        // driveController.start().and(driveController.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        // driveController.start().and(driveController.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // reset the field-centric heading on left bumper press
        driveController.back().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }

    public void doPathfind(Pose2d target) {
        PathConstraints constraints = new PathConstraints(
                3, 4, // 3 - 4
                Units.degreesToRadians(540),
                Units.degreesToRadians(720));

        System.out.println(target);

        // Since AutoBuilder is configured, we can use it to build pathfinding commands
        pathfindingCommand = AutoBuilder.pathfindToPose(
                target,
                constraints,
                0);

        pathfindingCommand.schedule();

        System.out.println("PATHFIND TO " + target.toString() + " STARTED");
    }

    public void doPathfindToPath(String path) {
        try {

            PathPlannerPath pathData = PathPlannerPath.fromPathFile(path);

            PathConstraints constraints = new PathConstraints(
                    3, 4, // 3 - 4
                    Units.degreesToRadians(540),
                    Units.degreesToRadians(720));

            // Since AutoBuilder is configured, we can use it to build pathfinding commands
            pathfindingCommand = AutoBuilder.pathfindThenFollowPath(
                    pathData,
                    constraints);

            pathfindingCommand.schedule();

            System.out.println("PATHFIND TO " + path + " STARTED");

        } catch (Exception e) {
            DriverStation.reportError("Pathing failed: " + e.getMessage(), e.getStackTrace());
        }
    }

    public Command getPath(String id) {
        try {
            // Load the path you want to follow using its name in the GUI
            PathPlannerPath path = PathPlannerPath.fromPathFile(id);

            // Create a path following command using AutoBuilder. This will also trigger
            // event markers.
            return AutoBuilder.followPath(path);

        } catch (Exception e) {
            DriverStation.reportError("Pathing failed: " + e.getMessage(), e.getStackTrace());
            return Commands.none();
        }
    }

    public double getAxisMovementScale() {
        return (1 - (driveController.getRightTriggerAxis() * 0.75));
    }
}
