package com.epam.availablecashdesk;

import com.epam.availablecashdesk.entity.Customer;
import com.epam.availablecashdesk.util.Generator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < 500; i++) {
            long id = Generator.generateCustomerId();
            boolean isQuickOrder = Generator.generateRandom(10) < 2;
            int availableCash = Generator.generateRandom(10) + 10;
            es.execute(new Customer(id, isQuickOrder, availableCash));
            TimeUnit.MILLISECONDS.sleep(Generator.generateRandom(10));
        }
    }
}
