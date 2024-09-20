package com.jskno.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomValueGenerator {

    private static final Logger log = LoggerFactory.getLogger(RandomValueGenerator.class);
    private static final Random random = new Random();

    public static double getRandomPositiveDouble(final int lowerBound, final int upperBound, final int decimalPlaces){

        if(lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0){
            throw new IllegalArgumentException("Put error message here");
        }

        final double dbl = lowerBound + (random.nextDouble() * (upperBound - lowerBound));
//        log.info(String.format("%." + decimalPlaces + "f", dbl));
        double scale = Math.pow(10, decimalPlaces);
        final double rounded = Math.round(dbl * scale) / scale;
        return rounded;
    }

    public static double getRandomDouble(final int lowerBound, final int upperBound, final int decimalPlaces){

        if(lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0){
            throw new IllegalArgumentException("Put error message here");
        }

        boolean positiveNumber = random.nextBoolean();

        final double dbl = lowerBound + (random.nextDouble() * (upperBound - lowerBound));
//        log.info(String.format("%." + decimalPlaces + "f", dbl));
        double scale = Math.pow(10, decimalPlaces);
        final double rounded = Math.round(dbl * scale) / scale;
        return positiveNumber ? rounded : -rounded;

    }

    public static int getRandomPositiveInteger(final int upperBound){
        return random.nextInt(upperBound);
    }

}
