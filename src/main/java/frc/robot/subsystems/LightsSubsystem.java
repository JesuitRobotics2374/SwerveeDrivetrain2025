package frc.robot.subsystems;

import java.util.Arrays;
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

    public static int LED_STRIP_OFFSET = 8;
    public static int LED_STRIP_COUNT = 60;
    public static int LED_PANEL_LENGTH = 32;

    public static enum LEDStripMode {
        RainbowWave,
        JesuitWave,
        Green,
        Gold,
        Test

    }

    public static enum LEDPanelMode {
        RainbowWave,
        JesuitWave,
        Green,
        Gold,
        Test
    }

    // only needed for periodic
    private static enum State {
        JesuitWave,
        None,
        Test
    }

    // private static Color[] cachedJesuitWave = new Color[] { new Color(0,255,0), new Color(4,252,0), new Color(8,249,0), new Color(12,246,0), new Color(17,244,0), new Color(21,241,0), new Color(25,238,0), new Color(30,235,0), new Color(34,233,0), new Color(38,230,0), new Color(43,227,0), new Color(47,224,0), new Color(51,222,0), new Color(56,219,0), new Color(60,216,0), new Color(64,213,0), new Color(69,211,0), new Color(73,208,0), new Color(77,205,0), new Color(82,202,0), new Color(86,200,0), new Color(90,197,0), new Color(95,194,0), new Color(99,191,0), new Color(103,189,0), new Color(108,186,0), new Color(112,183,0), new Color(116,180,0), new Color(121,178,0), new Color(125,175,0), new Color(129,172,0), new Color(133,169,0), new Color(138,167,0), new Color(142,164,0), new Color(146,161,0), new Color(151,158,0), new Color(155,156,0), new Color(159,153,0), new Color(164,150,0), new Color(168,147,0), new Color(172,145,0), new Color(177,142,0), new Color(181,139,0), new Color(185,136,0), new Color(190,134,0), new Color(194,131,0), new Color(198,128,0), new Color(203,125,0), new Color(207,123,0), new Color(211,120,0), new Color(216,117,0), new Color(220,114,0), new Color(224,112,0), new Color(229,109,0), new Color(233,106,0), new Color(237,103,0), new Color(242,101,0), new Color(246,98,0), new Color(250,95,0), new Color(255,93,0) };
    private static Color[] cachedJesuitWave = new Color[] { new Color(0,255,0), new Color(8,249,0), new Color(17,243,0), new Color(26,238,0), new Color(35,232,0), new Color(43,227,0), new Color(52,221,0), new Color(61,215,0), new Color(70,210,0), new Color(79,204,0), new Color(87,199,0), new Color(96,193,0), new Color(105,187,0), new Color(114,182,0), new Color(123,176,0), new Color(131,171,0), new Color(140,165,0), new Color(149,160,0), new Color(158,154,0), new Color(167,148,0), new Color(175,143,0), new Color(184,137,0), new Color(193,132,0), new Color(202,126,0), new Color(211,120,0), new Color(219,115,0), new Color(228,109,0), new Color(237,104,0), new Color(246,98,0), new Color(255,93,0), new Color(0,255,0), new Color(8,249,0), new Color(17,243,0), new Color(26,238,0), new Color(35,232,0), new Color(43,227,0), new Color(52,221,0), new Color(61,215,0), new Color(70,210,0), new Color(79,204,0), new Color(87,199,0), new Color(96,193,0), new Color(105,187,0), new Color(114,182,0), new Color(123,176,0), new Color(131,171,0), new Color(140,165,0), new Color(149,160,0), new Color(158,154,0), new Color(167,148,0), new Color(175,143,0), new Color(184,137,0), new Color(193,132,0), new Color(202,126,0), new Color(211,120,0), new Color(219,115,0), new Color(228,109,0), new Color(237,104,0), new Color(246,98,0), new Color(255,93,0) };

    private CANdle candle;
    private State state;

    private boolean tick;

    int gradientTick;

    public LightsSubsystem() {
        state = State.None;
        tick = true;

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
        state = State.None;
        switch (mode) {
            case RainbowWave:
                RainbowAnimation rainbowAnimation = new RainbowAnimation(0.5, 0, 60, false, 8);
                candle.animate(rainbowAnimation, 0);
                break;
            case JesuitWave:
                gradientTick = 0;
                state = State.JesuitWave;
            case Gold:
                break;
            case Green:
                break;
            default:
                break;
        }
    }

    public void setLEDMode(LEDPanelMode mode) {
        clearAllAnimations();
        switch (mode) {
            case RainbowWave:
                RainbowAnimation RainbowAnimationPanel = new RainbowAnimation(0.5, 0.5, 256, false, 68);
                candle.animate(RainbowAnimationPanel, 1);
                break;
            case Gold:
                break;
            case Green:
                break;
            case JesuitWave:
                break;
            case Test:
                candle.setLEDs(255, 255, 255, 0, LED_STRIP_OFFSET+LED_STRIP_COUNT, 4);
                break;
            default:
                break;
        }
    }

    public void setAllLEDToColor(int r, int g, int b) {
        clearAllAnimations();
        System.out.printf("Turning LEDs to color {R: %d, G: %d, B: %d}", r, g, b);
        candle.setLEDs(r, g, b);
    }

    public void SetStripToJesuitWave() {
        System.out.println("setting to jesuit wave");
        clearAllAnimations();
        Queue<Color> gradient = generateGradientArray(
            new Color(0, 255, 0),
            new Color(255, 93, 0), 60);
        int i = 0;
        while (!gradient.isEmpty()) {
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
        for (int i = 0; i < size; i++) {
            ratio = (float) i / (size - 1);
            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
            gradient.add(new Color(r, g, b));
            // System.out.printf("Color: %d %d %d\n", r, g, b);
        }
        // Queue<Color> temp = new LinkedList<>();
        // for (Color c : gradient) {
        //     temp.add(c);
        // } 
        // Stack<Color> s = new Stack<>();
        // while (!temp.isEmpty()) {
        //     s.push(temp.remove());
        // }
        // while (!s.isEmpty()) {
        //     gradient.add(s.pop());
        // }
        return gradient;
    }

    @Override
    public void periodic() {
        if (tick) {
            tick = !tick;
            return;
        }
        switch (state) {
            case JesuitWave:
                for (int i = 0; i < LED_STRIP_COUNT; i++) {
                    Color c = cachedJesuitWave[(i+gradientTick) % LED_STRIP_COUNT];
                    candle.setLEDs(
                        c.getRed(),
                        c.getGreen(),
                        c.getBlue(), 0,
                        i+LED_STRIP_OFFSET, 1);
                    if (i < LED_PANEL_LENGTH) {
                        candle.setLEDs(
                            c.getRed(),
                            c.getGreen(),
                            c.getBlue(), 0,
                            LED_STRIP_OFFSET+LED_STRIP_COUNT+(i*8), 8);
                    }
                }
                gradientTick = (gradientTick+1) % LED_STRIP_COUNT;
                break;
            case None:
                break;
            default:
                break;
        }
        tick = !tick;
    }

    public static void main(String[] args) {
        // generate cached gradient array
        Queue<Color> gradient = generateGradientArray(
            new Color(0, 255, 0),
            new Color(255, 93, 0), 30);
        
        System.out.print("new Color[] {");
        for (Color c : gradient) {
            System.out.printf("new Color(%d,%d,%d), ", c.getRed(), c.getGreen(), c.getBlue());
        }
        for (Color c : gradient) {
            System.out.printf("new Color(%d,%d,%d), ", c.getRed(), c.getGreen(), c.getBlue());
        }
        System.out.print("};");
    }

}
