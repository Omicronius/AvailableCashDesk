package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;

import java.util.concurrent.locks.ReentrantLock;

public class Customer extends Thread {
    private long id;
    private static Restaurant restaurant = Restaurant.getInstance();
    private ReentrantLock lock = new ReentrantLock();
    private int availableCash;
    private boolean isServiced;

    public Customer(long id) {
        super("Customer# " + id);
        this.id = id;
        this.availableCash = Generator.generateRandom(100);
    }

    public int getAvailableCash() {
        return availableCash;
    }

    public boolean isServed() {
        return isServiced;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void run() {
        CashDesk cashDesk = restaurant.defineRecommendedCashDesk();
        cashDesk.registerCustomer(this);
    }

    public int serve() {
        lock.lock();
        isServiced = true;
        int spentMoney = Generator.generateRandom(availableCash);
        availableCash -= spentMoney;
        lock.unlock();
        System.out.println("Customer# " + getId() + " is served");
        return spentMoney;
    }
}
