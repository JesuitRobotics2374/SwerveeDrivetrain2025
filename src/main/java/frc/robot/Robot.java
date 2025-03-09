// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.Orchestra;
import com.ctre.phoenix6.configs.AudioConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.LightsSubsystem;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final Core m_robotContainer;

  public final LightsSubsystem lightsSubsystem = new LightsSubsystem();


  //For Both Robots:
  private Orchestra orchestra = new Orchestra();
  AudioConfigs audio = new AudioConfigs().withAllowMusicDurDisable(true);
  private String outroSong = "wii.chrp"; //Change to whatever song you want to play on disable

  /*
   * Also a note for Aries:
   * Your .chrp files HAVE TO BE 
   * IN A NEW FILE UNDER src/main
   * CALLED deploy
   * 
   * Without this, it cannot move the files properly to the roboRIO
   * and the music WILL NOT WORK
   */

  /* For Main Robot:
  TalonFX instrument1 = new TalonFX(1, "FastFD");
  TalonFX instrument2 = new TalonFX(2, "FastFD");
  TalonFX instrument3 = new TalonFX(3, "FastFD");
  TalonFX instrument4 = new TalonFX(4, "FastFD");
  TalonFX instrument11 = new TalonFX(11, "FastFD");
  TalonFX instrument12 = new TalonFX(12, "FastFD");
  TalonFX instrument13 = new TalonFX(13, "FastFD");
  TalonFX instrument14 = new TalonFX(14, "FastFD");
  TalonFX instrument31 = new TalonFX(31, "FastFD");
  TalonFX instrument32 = new TalonFX(32, "FastFD");
   */

   //For Swervee:
  TalonFX instrument27 = new TalonFX(27, "FastFD");
  
  public Robot() {
    m_robotContainer = new Core();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {
    lightsSubsystem.setAllLEDToIdle();
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void disabledExit() {
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
    m_autonomousCommand.schedule();
    }

    // m_robotContainer.doPathfind(Constants.TEST_PATHFIND_TARGET);

    // m_robotContainer.getPath("Prec-BLUE-TOP").schedule();

    // m_robotContainer.doPathfindToPath("Prec-BLUE-TOP");

  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void autonomousExit() {
  }

  @Override
  public void teleopInit() {
    //Both:
    orchestra.clearInstruments();

  }

  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void teleopExit() {
    //THIS NEEDS TO BE ADDED TO MAIN BOT:
    /*
    this.orchestra.addInstrument(instrument1);
    this.instrument1.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument2);
    this.instrument2.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument3);
    this.instrument3.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument4);
    this.instrument4.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument11);
    this.instrument11.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument12);
    this.instrument12.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument13);
    this.instrument13.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument14);
    this.instrument14.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument31);
    this.instrument31.getConfigurator().apply(this.audio);

    this.orchestra.addInstrument(instrument32);
    this.instrument32.getConfigurator().apply(this.audio);
    */

    //Swervee only:
    this.orchestra.addInstrument(instrument27);
    this.instrument27.getConfigurator().apply(this.audio); 

    //Both:
    this.orchestra.loadMusic(outroSong);
    this.orchestra.play();
  }

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void testExit() {
  }

  @Override
  public void simulationPeriodic() {
  }
}
