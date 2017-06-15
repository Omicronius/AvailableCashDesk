package com.epam.availablecashdesk.util;

import java.util.Random;

public class Generator {
    private static long customerCounter = 1;
    private static long cashDeskCounter = 1;
    private static Random random = new Random();

    public static long generateCustomerId() {
        return customerCounter++;
    }

    public static long generateCashDeskId() {
        return cashDeskCounter++;
    }

    public static int generateRandom(int number) {
        return random.nextInt(number);
    }
}
