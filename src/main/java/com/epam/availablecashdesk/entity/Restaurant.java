package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.ConfigurationManager;
import com.epam.availablecashdesk.util.Generator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    private static final Logger logger = LogManager.getLogger(Restaurant.class);
    private static Restaurant instance = null;
    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    private static ArrayList<CashDesk> cashDesks = new ArrayList<>();
    private static ReentrantLock lock = new ReentrantLock();

    private Restaurant() {
    }

    public static Restaurant getInstance() {
        if (!isCreated.get()) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new Restaurant();
                    restaurantInitialization();
                    openCashDesks();
                    isCreated.set(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    private static void restaurantInitialization() {
        for (int i = 0; i < ConfigurationManager.getProperty("cashdesks.amount"); i++) {
            cashDesks.add(new CashDesk(Generator.generateCashDeskId()));
        }
    }

    private static void openCashDesks() {
        cashDesks.forEach(Thread::start);
    }

    public CashDesk defineShortestQueue() {
        CashDesk result;
        lock.lock();
        try {
            result = cashDesks.stream().min(Comparator.comparingInt(CashDesk::getSize)).get();
        } finally {
            lock.unlock();
        }
        return result;
    }

    public void registerQuickOrder(Customer customer) {
        CashDesk cashDesk;
        lock.lock();
        try {
            cashDesk = defineShortestQueue();
            cashDesk.getQueue().add(customer);
            cashDesk.getQueue().sort(Comparator.comparing(Customer::isQuickOrder).reversed().thenComparing(Customer::getId));
            cashDesk.notifyCashDesk();
        } finally {
            lock.unlock();
        }
        System.out.println(customer.toString() + " -quick-> " + cashDesk.toString());
    }

    public CashDesk registerOrder(Customer customer) {
        CashDesk cashDesk;
        lock.lock();
        try {
            cashDesk = defineShortestQueue();
            cashDesk.getQueue().add(customer);
            cashDesk.notifyCashDesk();
        } finally {
            lock.unlock();
        }
        System.out.println(customer.toString() + " ---> " + cashDesk.toString());
        return cashDesk;
    }

    public CashDesk relocate(Customer customer, CashDesk from) {
        CashDesk to;
        lock.lock();
        try {
            from.getQueue().remove(customer);
            to = registerOrder(customer);
            to.notifyCashDesk();
        } finally {
            lock.unlock();
        }
        System.out.println(customer.toString() + " -relocated->  from " + from.toString() + " to " + to);
        return to;
    }
}
