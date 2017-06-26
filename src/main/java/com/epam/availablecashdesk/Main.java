package com.epam.availablecashdesk;

import com.epam.availablecashdesk.entity.Customer;
import com.epam.availablecashdesk.util.ConfigurationManager;
import com.epam.availablecashdesk.util.Generator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < ConfigurationManager.getProperty("customers.amount"); i++) {
            long id = Generator.generateCustomerId();
            boolean isQuickOrder = Generator.generateRandom(100) < ConfigurationManager.getProperty("quickorder.percentage");
            int availableCash = Generator.generateRandom(ConfigurationManager.getProperty("available.cash"))
                    + ConfigurationManager.getProperty("max.cheque");
            es.execute(new Customer(id, isQuickOrder, availableCash));
            TimeUnit.MILLISECONDS.sleep(Generator.generateRandom(ConfigurationManager.getProperty("customers.delay")));
        }
    }
}
