package frc.robot.Commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.Subsystems.WristSubsystem;

public class moveWristCommand extends Command
{
    private WristSubsystem wristSubsystem;
    private DoubleSupplier m_speed;

    public moveWristCommand(DoubleSupplier speed, WristSubsystem wristSubsystem)
    {
        this.wristSubsystem = wristSubsystem;
        this.m_speed = speed;
        addRequirements(wristSubsystem);
    }

    @Override
    public void execute()
    {
        wristSubsystem.setPosition(m_speed.getAsDouble() * Constants.WristConstants.WRIST_SPEED_MODIFIER);
    }
}
