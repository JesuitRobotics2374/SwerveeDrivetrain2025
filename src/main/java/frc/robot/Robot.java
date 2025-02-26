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

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final Core m_robotContainer;

  private Orchestra orchestra = new Orchestra();

  private boolean hasRun = false;

  private String teleopSong = "jeopardy.chrp";
  private String outroSong = "wii.chrp";

  TalonFX instrument1 = new TalonFX(27, "FastFD");

  AudioConfigs audio = new AudioConfigs().withAllowMusicDurDisable(true);

  public Robot() {
    m_robotContainer = new Core();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {
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
    orchestra.clearInstruments();

    // if (orchestra.isPlaying()) {
    //   orchestra.stop();
    // }
    // AudioConfigs audio = new AudioConfigs().withAllowMusicDurDisable(false);

    // instrument1.getConfigurator().apply(audio);

    // if (m_autonomousCommand != null) {
    //   m_autonomousCommand.cancel();
    // }

    // orchestra.loadMusic(teleopSong);
  }

  @Override
  public void teleopPeriodic() {
    // if (!hasRun) {
    //   orchestra.play();
    //   hasRun = true;
    //   System.out.println("Playing Teleop Music");
    // }
  }

  @Override
  public void teleopExit() {
    // hasRun = false;
    // System.out.println("Playing Outro Music");

    orchestra.addInstrument(new TalonFX(27, "FastFD"));

    AudioConfigs audio = new AudioConfigs().withAllowMusicDurDisable(true);

    instrument1.getConfigurator().apply(audio);

    orchestra.loadMusic(outroSong);
    orchestra.play();
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
