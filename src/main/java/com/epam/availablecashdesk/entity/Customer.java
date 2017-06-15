package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;

import java.util.concurrent.locks.ReentrantLock;

public class Customer extends Thread {
    private long id;
    private static Restaurant restaurant = Restaurant.getInstance();
    private ReentrantLock lock = new ReentrantLock();
    private int availableCash;
    private boolean isServed;

    public Customer(long id) {
        super("Customer# " + id);
        this.id = id;
        this.availableCash = Generator.generateRandom(100);
    }

    public int getAvailableCash() {
        return availableCash;
    }

    public boolean isServed() {
        return isServed;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {
        System.out.println(toString() + " is coming ...");
        CashDesk cashDesk = restaurant.defineRecommendedCashDesk();
        cashDesk.registerCustomer(this);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                '}';
    }

    public int serve() {
        isServed = true;
        int spentMoney = Generator.generateRandom(availableCash);
        availableCash -= spentMoney;
        return spentMoney;
    }
}
