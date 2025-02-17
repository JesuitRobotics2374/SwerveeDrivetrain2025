package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Amp;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.RadiansPerSecond;

import java.io.ObjectInputFilter.Config;
import java.security.InvalidParameterException;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANrange;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.configs.CANrangeConfiguration;
import com.ctre.phoenix6.configs.CustomParamsConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.ConnectedMotorValue;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix6.controls.Follower;

import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ClimbingSubsystem extends SubsystemBase {

    public TalonFX climbMotor;
    public CANcoder climbCoder;

    public ClimbingSubsystem() {
        this.climbMotor = new TalonFX(27, "FastFD");
        this.climbCoder = new CANcoder(26, "FastFD");

        TalonFXConfiguration talonFXConfigs = new TalonFXConfiguration();
        Slot0Configs slot0Configs = talonFXConfigs.Slot0;
        MotionMagicConfigs motionMagicConfigs = talonFXConfigs.MotionMagic;

        slot0Configs.kG = 0; //Output of voltage to overcome gravity
        slot0Configs.kV = 0.1; //Output per unit target velocity, perhaps not needed
        slot0Configs.kA = 0; //Output per unit target acceleration, perhaps not needed
        slot0Configs.kP = 0; //Controls the response to position error—how much the motor reacts to the difference between the current position and the target position.
        slot0Configs.kI = 0; //Addresses steady-state error, which occurs when the motor doesn’t quite reach the target position due to forces like gravity or friction.
        slot0Configs.kD = 0; //Responds to the rate of change of the error, damping the motion as the motor approaches the target. This reduces overshooting and oscillations.

        motionMagicConfigs.MotionMagicCruiseVelocity = 25; // Target velocity in rps
        motionMagicConfigs.MotionMagicAcceleration = 100; // Target acceleration in rps/s
        motionMagicConfigs.MotionMagicJerk = 500; // Target jerk in rps/s/s

        climbMotor.getConfigurator().apply(talonFXConfigs);
        climbMotor.getConfigurator().apply(slot0Configs);
        climbMotor.getConfigurator().apply(motionMagicConfigs);

        climbMotor.setPosition(climbCoder.getPosition().getValueAsDouble() * 100);

        climbMotor.setNeutralMode(NeutralModeValue.Brake);
    }

    public void climbRaise() {
        MotionMagicVoltage m_request = new MotionMagicVoltage(climbMotor.getPosition().getValueAsDouble() + 3);

        climbMotor.setControl(m_request);
    }

    public void climbLower() {
        MotionMagicVoltage m_request = new MotionMagicVoltage(climbMotor.getPosition().getValueAsDouble() - 3);

        climbMotor.setControl(m_request);
    }

    public void raise() {
        MotionMagicVoltage m_request = new MotionMagicVoltage(-10);

        climbMotor.setControl(m_request);
    }

    public void lower() {
        MotionMagicVoltage m_request = new MotionMagicVoltage(0);

        climbMotor.setControl(m_request);
    }

    public void speed(double s) {
        climbMotor.set(s);
    }

    public void stop() {
        climbMotor.stopMotor();
        climbMotor.set(0);
    }

    public void zeroSystem() {
        climbCoder.setPosition(0.0);
        climbMotor.setPosition(0.0);

        MotionMagicVoltage m_request = new MotionMagicVoltage(0);
        climbMotor.setControl(m_request);
    }
    int clock = 0;

    @Override

    
    public void periodic() {
        clock++;

        if (clock == 20) {
            System.out.println("Motor: " + climbMotor.getPosition().getValueAsDouble());
            System.out.println("Encoder: " + climbMotor.getPosition().getValueAsDouble());
            clock = 0;
        }
    }
}
  
