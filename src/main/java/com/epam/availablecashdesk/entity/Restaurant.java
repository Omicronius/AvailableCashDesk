package com.epam.availablecashdesk.entity;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    private static Restaurant instance = null;
    private int totalCash;
    private ArrayList<CashDesk> cashDesks = new ArrayList<>();
    private static ReentrantLock lock = new ReentrantLock();

    private Restaurant() {
    }

    public static Restaurant getInstance() {
        lock.lock();
        try {
            if (instance == null) {
                instance = new Restaurant();
            }
        } finally {
            lock.unlock();
        }
        return instance;
    }
}
