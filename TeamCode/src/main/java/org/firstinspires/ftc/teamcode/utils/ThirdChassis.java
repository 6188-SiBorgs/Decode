package org.firstinspires.ftc.teamcode.utils;

import android.os.Environment;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;
import org.firstinspires.ftc.teamcode.atlas.ChassisConfig;
import org.firstinspires.ftc.teamcode.atlas.utils.ColorUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ThirdChassis extends AtlasChassis {
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/motifScannedWithLimelightDuringAutonomousToBeUsedInTeleopToMakeItEasierToAccess/";
    private final File motifSaveDirectory;
    public static final String AUTONOMOUS_ARTIFACTS_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/artifactsLeftOverFromAutonomousSavedSoWeKnowWhatIsInTheIndexerDuringTeleop/artifacts.artifacts";
    private final File artifactsSaveFile;

    public static final double LAUNCH_SERVO_UPPER = 0;
    public static final double LAUNCH_SERVO_LOWER = 0;
    public static final double TARGET_LAUNCH_VELOCITY = 0;

    public static final double COLOR_DIST_COUNT = 0.05;

    public static final double PURPLE_BALL_RED = 0;
    public static final double PURPLE_BALL_GREEN = 0;
    public static final double PURPLE_BALL_BLUE = 0;

    public static final double GREEN_BALL_RED = 0;
    public static final double GREEN_BALL_GREEN = 0;
    public static final double GREEN_BALL_BLUE = 0;

    public Limelight3A limelight;
    public enum Artifact {
        PURPLE,
        GREEN,
        NONE
    }

    private enum MotifState {
        NONE,
        PREPARING,
        LAUNCHING
    }

    private enum IntakeState {
        NONE,
        PREPARE_INTAKE,
        INTAKING,
        FINALIZE_INTAKE,
        COMPLETE
    }

    // Specifically for launching ANY ball (last resort if we cant get a motif)
    private enum LaunchingState {
        NONE,
        PREPARING,
        LAUNCHING
    }

    // Index =
    private static final double TICKS_PER_POSITION = 28 * 18.8803 / 6;
    private int index = 0;

    public Motif motif;
    private int greenMotifPosition = 0;
    private ArrayList<Artifact> artifacts = new ArrayList<>();

    private MotifState motifState = MotifState.NONE;
    public boolean launching;
    private boolean spinLaunchMotors;

    private IntakeState intakeState = IntakeState.NONE;

    private static final long TIME_TO_LAUNCH = 500;
    private LaunchingState launchingState = LaunchingState.NONE;
    private long launchTimer = 0;

    private final DcMotorEx indexerMotor, intakeMotor, leftLaunchMotor, rightLaunchMotor;
    private final Servo launchServo;

    private DigitalLED leftLED, rightLED;

    private ColorSensor colorSensor;

    public ThirdChassis(OpMode opMode) {
        super(opMode);
        ChassisConfig config = new ChassisConfig();
        config.frontLeftName = "frontLeft";
        config.frontRightName = "rearRight";
        config.backLeftName = "rearLeft";
        config.backRightName = "frontRight";
        config.backRightIsReversed = true;
        config.frontRightIsReversed = true;
        config.imuParameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                ));



        init(config);

        motifSaveDirectory = new File(PATH);
        if (!motifSaveDirectory.exists())
            motifSaveDirectory.mkdirs();

        artifactsSaveFile = new File(AUTONOMOUS_ARTIFACTS_PATH);
        if (!artifactsSaveFile.exists()) {
            artifactsSaveFile.mkdirs();
            try {
                artifactsSaveFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        indexerMotor = getDcMotorEx(opMode.hardwareMap, "indexer");
        indexerMotor.setTargetPosition(0);
        indexerMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        intakeMotor = getDcMotorEx(opMode.hardwareMap, "intake");
        leftLaunchMotor = getDcMotorEx(opMode.hardwareMap, "launcherLeft");
        leftLaunchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightLaunchMotor = getDcMotorEx(opMode.hardwareMap, "launcherRight");
        rightLaunchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        launchServo = opMode.hardwareMap.get(Servo.class, "launchServo");

//        leftLED = new DigitalLED(opMode.hardwareMap, "LeftLED");
//        rightLED = new DigitalLED(opMode.hardwareMap, "rightLED");

        colorSensor = opMode.hardwareMap.get(ColorSensor.class, "colorSensor");

        limelight = opMode.hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    public int getMotifId() {
        limelight.pipelineSwitch(1);
        long startTime = System.currentTimeMillis();
        long TIME_OUT = 2000;
        int id = -1;
        while (id == -1 && System.currentTimeMillis() - startTime < TIME_OUT) {
            LLResult result = limelight.getLatestResult();
            if (!(result.getPipelineIndex() != 1 || !result.isValid())) {
                List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();
                if (!tags.isEmpty()) {
                    id = tags.get(0).getFiducialId();
                }
            }
        }
        limelight.pipelineSwitch(0);
        return id;
    }

    public void indexerInit(Motif motif, Collection<Artifact> artifacts) {
        this.motif = motif;
        greenMotifPosition = this.motif.getIndex();
        this.artifacts.addAll(artifacts);
    }

    public void indexerInit(int motifId, Collection<Artifact> artifacts) {
        if (motifId == -1) {
            System.out.println("No motif found, defaulting");
            motifId = 21;
        }
        greenMotifPosition = motifId - 21;
        this.motif = Motif.getMotifFromIndex(motifId);
        this.artifacts.addAll(artifacts);
    }

    public void manualOverrideCycleMotif(int amount) {
        int newIndex = Math.floorMod(motif.getIndex() + amount, 3);
        motif = Motif.getMotifFromIndex(newIndex);
        greenMotifPosition = newIndex;
    }

    @Override
    public void tick() {
        int indexerMotorPosition = index * 2 + (intakeState != IntakeState.NONE && intakeState != IntakeState.COMPLETE ? 1 : 0);
        int intakePosition = getPosition(2);
        int encoderPosition = (int) (TICKS_PER_POSITION * indexerMotorPosition);
        indexerMotor.setTargetPosition(encoderPosition);
        indexerMotor.setPower(0.25);

        switch (motifState) {
            case PREPARING:
                if (indexerNotReady() || getLaunchVelocity() < TARGET_LAUNCH_VELOCITY) break;
                launching = true;
                boolean spinRight = indexOfNextBall() == getPosition(1);
                if (spinRight) index += 2;
                else index -= 2;
                motifState = MotifState.LAUNCHING;
                break;
            case LAUNCHING:
                if (indexerMotor.isBusy()) break;
                motifState = MotifState.NONE;
                launching = false;
                spinLaunchMotors = false;
                break;
        }

        launchServo.setPosition(launching ? LAUNCH_SERVO_UPPER : LAUNCH_SERVO_LOWER);
        leftLaunchMotor.setVelocity(spinLaunchMotors ? TARGET_LAUNCH_VELOCITY : 0);
        rightLaunchMotor.setVelocity(spinLaunchMotors ? TARGET_LAUNCH_VELOCITY : 0);

        switch (intakeState) {
            case PREPARE_INTAKE:
                if (indexerNotReady()) break;
                intakeMotor.setPower(0.1);
                break;
            case INTAKING:
                double purpleDist = ColorUtils.colorDist(PURPLE_BALL_RED, PURPLE_BALL_GREEN, PURPLE_BALL_BLUE, colorSensor.red(), colorSensor.blue(), colorSensor.green());
                double greenDist = ColorUtils.colorDist(GREEN_BALL_RED, GREEN_BALL_GREEN, GREEN_BALL_BLUE, colorSensor.red(), colorSensor.blue(), colorSensor.green());

                if (purpleDist < COLOR_DIST_COUNT) {
                    artifacts.set(intakePosition, Artifact.PURPLE);
                } else if (greenDist < COLOR_DIST_COUNT) {
                    artifacts.set(intakePosition, Artifact.GREEN);
                } else break;

                intakeState = IntakeState.FINALIZE_INTAKE;
                break;
            case FINALIZE_INTAKE:
                intakeMotor.setPower(0);
                if (artifacts.contains(Artifact.NONE)) {
                    int difference = Math.floorMod(artifacts.indexOf(Artifact.NONE) - getPosition() + 2, 3);
                    index += difference;
                    intakeState = IntakeState.PREPARE_INTAKE;
                }
                else {
                    intakeState = IntakeState.COMPLETE;
                }

                break;
        }

        switch (launchingState) {
            case PREPARING:
                if (indexerNotReady() || getLaunchVelocity() < TARGET_LAUNCH_VELOCITY) break;
                launching = true;
                launchTimer = System.currentTimeMillis();
                launchingState = LaunchingState.LAUNCHING;
                break;
            case LAUNCHING:
                if (System.currentTimeMillis() - launchTimer < TIME_TO_LAUNCH) break;
                launching = false;
                spinLaunchMotors = false;
                launchTimer = 0;
                break;
        }
    }

    @Override
    public void initLoop(OpMode opMode) {
        launchServo.setPosition(launching ? LAUNCH_SERVO_UPPER : LAUNCH_SERVO_LOWER);

    }

    public void startIntaking() {
        intakeState = IntakeState.PREPARE_INTAKE;
        // Get nearest intake

    }
    public void stopIntaking() {
        intakeState = IntakeState.NONE;
    }

    public void launchMotif() {
        if (indexOfNextBall() == -1) return;
        if (artifacts.stream().filter(a -> a.equals(Artifact.PURPLE)).count() != 2) return;
        prepareNextBall();
        motifState = MotifState.PREPARING;
        spinLaunchMotors = true;
    }

    public void launchAnything() {
        prepareNextBall();
        launchingState = LaunchingState.PREPARING;
        spinLaunchMotors = true;
    }

    public void prepareNextBall() {
        boolean cycle = true;
        int indexerIndex = indexOfNextBall();
        if (indexerIndex == -1) {
            indexerIndex = artifacts.indexOf(getBallNotNextInMotif());
            cycle = false;
        }
        if (indexerIndex != -1) {
            index = indexerIndex;
            if (cycle) cycleMotif();
            artifacts.set(getPosition(), Artifact.NONE);
        }
    }

    private int indexOfNextBall() {
        return artifacts.indexOf(getNextBallInMotif());
    }

    private Artifact getNextBallInMotif() {
        return greenMotifPosition == 0 ? Artifact.GREEN : Artifact.PURPLE;
    }

    private Artifact getBallNotNextInMotif() {
        return greenMotifPosition == 0 ? Artifact.PURPLE : Artifact.GREEN;
    }

    public int getPosition() {
        return getPosition(0);
    }

    public int getPosition(int offset) {
        return Math.floorMod(index + offset, 3);
    }

    private void cycleMotif() {
        greenMotifPosition--;
        if (greenMotifPosition < 0) {
            greenMotifPosition = 2;
        }
    }

    public double getLaunchVelocity() {
        return (leftLaunchMotor.getVelocity() + rightLaunchMotor.getVelocity()) / 2.0;
    }

    public void changeLEDColor(DigitalLED.Color color) {
        leftLED.color = color;
        rightLED.color = color;
    }

    public boolean indexerNotReady() {
        return indexerMotor.isBusy();
    }

    public void saveMotifIdToFile() throws IOException {
        int motifId = getMotifId();

        if (!motifSaveDirectory.exists() || !motifSaveDirectory.isDirectory())
            throw new IllegalArgumentException("Motif save directory does not exist!!! " + PATH);

        File motifFile = new File(PATH + motifId + ".motif");
        motifFile.createNewFile();
    }

    public void saveArtifactsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(artifactsSaveFile))) {
            oos.writeObject(artifacts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getSavedMotifId() {
        if (!motifSaveDirectory.exists() || !motifSaveDirectory.isDirectory())
            throw new IllegalArgumentException("Motif save directory does not exist!!! " + PATH);

        File[] motifFiles = motifSaveDirectory.listFiles((dir, name) -> name.endsWith(".motif"));

        if (motifFiles == null || motifFiles.length == 0)
            throw new IllegalStateException("No motif files found!!!");

        String fileName = motifFiles[0].getName();
        String numberPart = fileName.substring(0, fileName.lastIndexOf(".motif"));

        return Integer.parseInt(numberPart);
    }

    public ArrayList<Artifact> getSavedArtifacts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(artifactsSaveFile))) {
            return (ArrayList<Artifact>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}