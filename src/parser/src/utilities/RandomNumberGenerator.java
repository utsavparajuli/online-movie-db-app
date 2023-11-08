package parser.src.utilities;

import java.util.Random;

public class RandomNumberGenerator {
    public static float getPriceForMovies() {
        Random random = new Random();
        float minValue = 5.0f;
        float maxValue = 10.0f;

        return minValue + random.nextFloat() * (maxValue - minValue);
    }
}
