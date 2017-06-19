package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    private static Restaurant instance = null;
    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    private static ArrayList<CashDesk> cashDesks = new ArrayList<>();
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    private static final int CASHDESK_TOTAL_AMOUNT = 3;

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

    private static void openCashDesks() {
        cashDesks.forEach(Thread::start);
    }

    private static void restaurantInitialization() {
        for (int i = 0; i < CASHDESK_TOTAL_AMOUNT; i++) {
            cashDesks.add(new CashDesk(Generator.generateCashDeskId()));
        }
    }

    public CashDesk defineShortestQueue() {
        CashDesk result;
        lock.lock();
        result = cashDesks.stream().min(Comparator.comparingInt(CashDesk::getSize)).get();
        lock.unlock();
        return result;
    }

    public void registerQuickOrder(Customer customer) {
        lock.lock();
        CashDesk cashDesk = defineShortestQueue();
        cashDesk.getQueue().add(customer);
        cashDesk.getQueue().sort(Comparator.comparing(Customer::isQuickOrder).reversed().thenComparing(Customer::getId));
        condition.signalAll();
        lock.unlock();
        System.out.println(customer.toString() + " -quick-> " + cashDesk.toString());
    }

    public CashDesk registerOrder(Customer customer) {
        lock.lock();
        CashDesk cashDesk = defineShortestQueue();
        cashDesk.getQueue().add(customer);
        condition.signalAll();
        lock.unlock();
        System.out.println(customer.toString() + " ---> " + cashDesk.toString());
        return cashDesk;
    }

    public CashDesk relocate(Customer customer, CashDesk from) {
        lock.lock();
        from.getQueue().remove(customer);
        CashDesk to = registerOrder(customer);
        condition.signalAll();
        lock.unlock();
        System.out.println(customer.toString() + " -relocated->  from " + from.toString() + " to " + to);
        return to;
    }
}
