package uk.ac.nulondon;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class EnergyCalculationTest {
    @Test
    public void testEnergy() {
        ArrayList<ArrayList<Color>> pixels = new ArrayList<>();
        ArrayList<Color> blackRow = new ArrayList<>(Arrays.asList(
                new Color(0, 0, 0),
                new Color(0, 0, 0),
                new Color(0, 0, 0)
        ));
        for (int i = 0; i < 3; i ++) {
            pixels.add(blackRow);
        }

        EnergyCalculation energyCalc1 = new EnergyCalculation(pixels);
        Assertions.assertThat(energyCalc1.Energy()).isEqualTo(0.0);

        pixels.clear();
        ArrayList<Color> whiteRow = new ArrayList<>(Arrays.asList(
                new Color(255, 255, 255),
                new Color(255, 255, 255),
                new Color(255, 255, 255)
        ));
        for (int i = 0; i < 3; i ++) {
            pixels.add(whiteRow);
        }

        EnergyCalculation energyCalc2 = new EnergyCalculation(pixels);
        Assertions.assertThat(energyCalc2.Energy()).isEqualTo(0.0);

        pixels.clear();
        ArrayList<Color> firstRow = new ArrayList<>(Arrays.asList(
                new Color(3, 0, 0),
                new Color(6, 0, 0),
                new Color(9, 0, 0)
        ));
        ArrayList<Color> secondRow = new ArrayList<>(Arrays.asList(
                new Color(12, 0, 0),
                new Color(15, 0, 0),
                new Color(18, 0, 0)
        ));
        ArrayList<Color> thirdRow = new ArrayList<>(Arrays.asList(
                new Color(21, 0, 0),
                new Color(24, 0, 0),
                new Color(27, 0, 0)
        ));
        pixels.add(firstRow);
        pixels.add(secondRow);
        pixels.add(thirdRow);

        EnergyCalculation energyCalc3 = new EnergyCalculation(pixels);
        Assertions.assertThat(energyCalc3.Energy()).isCloseTo(
                25.298, Percentage.withPercentage(0.01)
        );
    }
}
