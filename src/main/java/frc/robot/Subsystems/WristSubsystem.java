package frc.robot.Subsystems;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class WristSubsystem extends SubsystemBase{
    private SparkMax wristMotor;
    private SparkBaseConfig wristMotorConfig;
    private PIDController wristPidController;
    private ArmFeedforward wristFeedForward;

    public WristSubsystem() {
        wristMotor = new SparkMax(Constants.WristConstants.WRIST_MOTOR_ID, MotorType.kBrushless);

        wristMotorConfig.inverted(false);
        wristMotorConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);

        wristMotor.configure(wristMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);

        wristPidController = new PIDController(Constants.WristConstants.WRIST_kP, Constants.WristConstants.WRIST_kI, Constants.WristConstants.WRIST_kD);
        wristPidController.setTolerance(Constants.WristConstants.WRIST_PID_TOLERANCE);

        wristFeedForward = new ArmFeedforward(Constants.WristConstants.WRIST_kS, Constants.WristConstants.WRIST_kG, Constants.WristConstants.WRIST_kV);
    }

    // zero the wrist encoder
    public void zeroWrist()
    {
        wristMotor.getEncoder().setPosition(0);
    }

    // hold the wrist at a current pose using pid and feed forward. i really hope i dont need to use this
    public void setPosition(double positionRadians)
    {
        wristPidController.setSetpoint(positionRadians);

        double wristRadians = (wristMotor.getEncoder().getPosition() * 2 * Math.PI) / 25; // convert to radians then compensate for 25:1 gear ratio
        double wristVelocityRadSec = (wristMotor.getEncoder().getVelocity() * 2 * Math.PI) / 25;

        // calculate the pid using feed forward and set the motor to maintain position
        double pidOutput = wristPidController.calculate(wristRadians, (wristPidController.getSetpoint() * 2 * Math.PI));
        double feedForward = wristFeedForward.calculate(wristRadians, wristVelocityRadSec);

        double speed = pidOutput + feedForward;

        // ensure @param speed is within -1 to 1
        speed = (speed > 1) ? 1 : speed;
        speed = (speed < -1) ? -1 : speed;

        // set the motor speed
        wristMotor.set(speed);
    }

    public void stopWrist()
    {
        wristMotor.stopMotor();
    }

    public void setWristSpeed(double speed)
    {
        // ensure @param speed is within -1 to 1
        speed = (speed > 1) ? 1 : speed;
        speed = (speed < -1) ? -1 : speed;

        wristMotor.set(speed);
    }
}
