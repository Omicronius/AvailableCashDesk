package com.epam.availablecashdesk;

import com.epam.availablecashdesk.entity.Customer;
import com.epam.availablecashdesk.entity.Restaurant;
import com.epam.availablecashdesk.util.Generator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Restaurant r = Restaurant.getInstance();
        ExecutorService es = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            es.execute(new Customer(Generator.generateCustomerId()));
            TimeUnit.SECONDS.sleep(Generator.generateRandom(5));
        }
    }
}
