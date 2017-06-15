package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;

public class Customer extends Thread {
    public static Restaurant restourant = Restaurant.getInstance();
    private int availableCash;

    public Customer(int availableCash) {
        super("Customer# " + String.valueOf(Generator.generateCustomerId()));
        this.availableCash = Generator.generateRandom(100);
    }

    @Override
    public void run() {

    }
}
