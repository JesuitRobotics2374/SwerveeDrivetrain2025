package frc.robot;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.simulation.VisionSystemSim;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.subsystems.drivetrain.CommandSwerveDrivetrain;

public class VisionSubsystem {

    private PhotonCamera camera;
    private CommandSwerveDrivetrain drivetrain;
    Field2d field;

    // private Transform3d robotToCam;
    // private AprilTagFieldLayout aprilTagFieldLayout;
    // private PhotonPoseEstimator photonPoseEstimator;
    // private Matrix<N3, N1> curStdDevs;
    // private static Matrix<N3, N1> kSingleTagStdDevs = VecBuilder.fill(4,0,8);
    // private static Matrix<N3, N1> kMultiTagStdDevs = VecBuilder.fill(4,0,8);

    private VisionSystemSim visionSim;

    public VisionSubsystem(CommandSwerveDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
        System.out.println(NetworkTableInstance.getDefault());
        camera = new PhotonCamera(NetworkTableInstance.getDefault(), "camera");
        field = new Field2d();

        // robotToCam = new Transform3d(new Translation3d(0, 0, 0), new Rotation3d(0, 0,
        // 0));
        // aprilTagFieldLayout =
        // AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);
        // photonPoseEstimator = new PhotonPoseEstimator(aprilTagFieldLayout,
        // PoseStrategy.CLOSEST_TO_REFERENCE_POSE, robotToCam);
        // photonPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);
        // visionSim = new VisionSystemSim("main");

        // PoseEstimator wEstimator = new Pose
    }

    public void setLabel(Pose2d pose2d, String label) {
        field.getObject(label).setPose(pose2d);
    }

    // public class AprilTagDistance {
    // public double getDistanceToAprilTag() {
    // var result = camera.getLatestResult();

    // if (result.hasTargets()) {
    // PhotonTrackedTarget target = result.getBestTarget();
    // Transform3d transform = target.getBestCameraToTarget();

    // System.out.println("Tag ID: " + target.getFiducialId());
    // System.out.println("Transform: " + transform);

    // return transform.getTranslation().getNorm();
    // }

    // return -1;
    // }
    // }

    
    // KEVIN'S CODE (with a few minor tweaks)
    //NOTE TO SELF(JULI) DON'T FORGET TO MAKE IT WORK WITH ELASTIC
    public Pose2d getEstimatedGlobalPose() {
        List<PhotonPipelineResult> results = camera.getAllUnreadResults();
        System.out.println(results.size() + " " + camera.isConnected() + " " + camera.getName() + " " + camera.getPipelineIndex());
        var latestResult = camera.getLatestResult();

        if (latestResult.hasTargets()) {
            PhotonPipelineResult result = results.get(results.size() - 1);

            Transform3d transform3d = result.getBestTarget().bestCameraToTarget;

            Translation2d translation2d = transform3d.getTranslation().toTranslation2d();
            Rotation2d rotation2d = transform3d.getRotation().toRotation2d();
            // Transform2d transform2d = new Transform2d(translation2d, rotation2d);

            //Pose2d robotPose = drivetrain.getRobotPose();
            // Pose2d targetPose = robotPose.transformBy(transform2d);

            Pose2d targetPose = new Pose2d(translation2d, rotation2d-180); //TEST THIS
            // Pose2d targetPose = new Pose2d();

            Pose2d latestTargetPose = targetPose;

            return targetPose;
        } else {
            return new Pose2d(0,0,new Rotation2d(0,0));
            //try return null again
        }

        // Optional<EstimatedRobotPose> visionEst = Optional.empty();
        // for (var change : camera.getAllUnreadResults()) {
        // visionEst = photonPoseEstimator.update(change);
        // updateEstimationStdDevs(visionEst, change.getTargets());

        // if (Robot.isSimulation()) {
        // visionEst.ifPresentOrElse(
        // est ->
        // getSimDebugField()
        // .getObject("VisionEstimation")
        // .setPose(est.estimatedPose.toPose2d()),
        // () -> {
        // getSimDebugField().getObject("VisionEstimation").setPoses();
        // });
        // }
        // }
        // return visionEst;
        // }

        // private void updateEstimationStdDevs(
        // Optional<EstimatedRobotPose> estimatedPose, List<PhotonTrackedTarget>
        // targets) {
        // if (estimatedPose.isEmpty()) {
        // // No pose input. Default to single-tag std devs
        // curStdDevs = kSingleTagStdDevs;

        // } else {
        // // Pose present. Start running Heuristic
        // var estStdDevs = kSingleTagStdDevs;
        // int numTags = 0;
        // double avgDist = 0;

        // // Precalculation - see how many tags we found, and calculate an
        // average-distance metric
        // for (var tgt : targets) {
        // var tagPose =
        // photonPoseEstimator.getFieldTags().getTagPose(tgt.getFiducialId());
        // if (tagPose.isEmpty()) continue;
        // numTags++;
        // avgDist +=
        // tagPose
        // .get()
        // .toPose2d()
        // .getTranslation()
        // .getDistance(estimatedPose.get().estimatedPose.toPose2d().getTranslation());
        // }

        // if (numTags == 0) {
        // // No tags visible. Default to single-tag std devs
        // curStdDevs = kSingleTagStdDevs;
        // } else {
        // // One or more tags visible, run the full heuristic.
        // avgDist /= numTags;
        // // Decrease std devs if multiple targets are visible
        // if (numTags > 1) estStdDevs = kMultiTagStdDevs;
        // // Increase std devs based on (average) distance
        // if (numTags == 1 && avgDist > 4)
        // estStdDevs = VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE,
        // Double.MAX_VALUE);
        // else estStdDevs = estStdDevs.times(1 + (avgDist * avgDist / 30));
        // curStdDevs = estStdDevs;
        // }
        // }
        // }

        // public Field2d getSimDebugField() {
        // if (!Robot.isSimulation()) return null;
        // return visionSim.getDebugField();
        // }
    }
}