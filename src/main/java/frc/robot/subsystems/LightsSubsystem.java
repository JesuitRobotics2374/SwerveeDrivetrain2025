package frc.robot.subsystems;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.awt.Color;

import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdleConfiguration;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.CANdle.LEDStripType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LightsSubsystem extends SubsystemBase {
    
    private CANdle candle;

    public static enum LEDStripMode {
        RainbowWave,
        JesuitWave,
        Green,
        Gold,

    }

    public static enum LEDPanelMode {
        RainbowWave,
        JesuitWave,
        Green,
        Gold,
    }

    public LightsSubsystem() {
        System.out.println("Initializing CANdle");
        candle = new CANdle(27, "FastFD");

        CANdleConfiguration config = new CANdleConfiguration();
        config.stripType = LEDStripType.RGB;
        config.brightnessScalar = 0.05;
        candle.configAllSettings(config);

        setAllLEDToIdle();
        System.out.println(" CANdle initialized");
    }

    public void setLEDMode(LEDStripMode mode) {
        clearAllAnimations();
        switch (mode) {
            case RainbowWave:
                RainbowAnimation rainbowAnimation = new RainbowAnimation(0.5, 0, 60, false, 8);
                candle.animate(rainbowAnimation, 0);
                break;
            case JesuitWave:
                SetStripToJesuitWave();
        }
    }

    public void setLEDMode(LEDPanelMode mode) {
        clearAllAnimations();
        switch (mode) {
            case RainbowWave:
                RainbowAnimation RainbowAnimationPanel = new RainbowAnimation(0.5, 0.5, 256, false, 68);
                candle.animate(RainbowAnimationPanel, 1);
                break;
        }
    }

    public void setAllLEDToColor(int r, int g, int b) {
        clearAllAnimations();
        System.out.printf("Turning LEDs to color {R: %d, G: %d, B: %d}", r, g, b);
        candle.setLEDs(r, g, b);
    }

    public void SetStripToJesuitWave() {
        clearAllAnimations();
        Queue<Color> gradient = generateGradientArray(
            new Color(0, 255, 0),
            new Color(255, 93, 0), 120);
        for (int i = 0; i < 60; i++) {
            Color c = gradient.remove();
            candle.setLEDs(
                c.getRed(),
                c.getGreen(),
                c.getBlue(), 0,
                i+8, 1);
            i++;
        }
    }

    public void setAllLEDToOff() {
        clearAllAnimations();
        System.out.println("Turning LEDs off");
        candle.setLEDs(0, 0, 0);
    }

    public void setAllLEDToIdle() {
        System.out.println("Turning LEDs to idle");
        clearAllAnimations();
        // RainbowAnimation rainbowAnimation = new RainbowAnimation(0.5, 0.5, 700);
        // candle.animate(rainbowAnimation);
        RainbowAnimation rainbowAnimationStrip = new RainbowAnimation(0.5, 0, 60, false, 8);
        RainbowAnimation RainbowAnimationPanel = new RainbowAnimation(0.5, 0.5, 256, false, 68);
        candle.animate(rainbowAnimationStrip, 0);
        candle.animate(RainbowAnimationPanel, 1);
    }

    public void clearAllAnimations() {
        candle.clearAnimation(0);
        candle.clearAnimation(1);
    }

    public static Queue<Color> generateGradientArray(Color start, Color end, int size) {
        System.out.println("START GRADIENT");
        Queue<Color> gradient = new LinkedList<>();
        float ratio;
        for (int i = 0; i < size/2; i++) {
            ratio = (float) i / (size - 1);
            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
            gradient.add(new Color(r, g, b));
        }
        Queue<Color> temp = new LinkedList<>();
        for (Color c : gradient) {
            temp.add(c);
        } 
        Stack<Color> s = new Stack<>();
        while (!temp.isEmpty()) {
            s.push(temp.remove());
        }
        while (!s.isEmpty()) {
            gradient.add(s.pop());
        }
        return gradient;
    }

    public static void main(String[] args) {
        Queue<Color> gradient = generateGradientArray(
            new Color(0, 255, 0),
            new Color(255, 93, 0), 60);
        System.out.println(gradient.size());
        for (Color c : gradient) {
            System.out.printf("Color: %d %d %d\n", c.getRed(), c.getGreen(), c.getBlue());
        }
        System.out.println("shifting");
        Color temp = gradient.remove();
        gradient.add(temp);
        for (Color c : gradient) {
            System.out.printf("Color: %d %d %d\n", c.getRed(), c.getGreen(), c.getBlue());
        }
    }

}
