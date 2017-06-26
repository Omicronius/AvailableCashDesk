package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.ConfigurationManager;
import com.epam.availablecashdesk.util.Generator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CashDesk extends Thread {
    private static final Logger logger = LogManager.getLogger(CashDesk.class);
    private long id;
    private int customersServed;
    private int cashBox;
    private ArrayList<Customer> queue = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public CashDesk(long id) {
        super("CashDesk# " + id);
        this.id = id;
    }

    public ArrayList<Customer> getQueue() {
        return queue;
    }

    public int getSize() {
        return queue.size();
    }

    @Override
    public void run() {
        Customer customer;
        while (true) {
            lock.lock();
            try {
                if (queue.isEmpty()) {
                    try {
                        condition.await(); //The cashdesk will sleep until a new customer is added.
                    } catch (InterruptedException e) {
                        logger.log(Level.WARN, "CashDesk thread has been interrupted!");
                    }
                }
                customer = queue.remove(0);
            } finally {
                lock.unlock();
            }
            int income = customer.serve();
            cashBox += income;
            customersServed++;
            System.out.println(customer.toString() + " is served by the " + toString());
            try {
                TimeUnit.MILLISECONDS.sleep(Generator.generateRandom(ConfigurationManager.getProperty("serving.time")));
            } catch (InterruptedException e) {
                logger.log(Level.WARN, "CashDesk thread has been interrupted!");
            }
        }
    }


    public void notifyCashDesk() {
        lock.lock();
        try {
            condition.signal(); //The cashdesk has been informed a new customer is waiting for serving.
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashDesk cashDesk = (CashDesk) o;

        if (id != cashDesk.id) return false;
        if (customersServed != cashDesk.customersServed) return false;
        if (cashBox != cashDesk.cashBox) return false;
        if (queue != null ? !queue.equals(cashDesk.queue) : cashDesk.queue != null) return false;
        if (lock != null ? !lock.equals(cashDesk.lock) : cashDesk.lock != null) return false;
        return condition != null ? condition.equals(cashDesk.condition) : cashDesk.condition == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + customersServed;
        result = 31 * result + cashBox;
        result = 31 * result + (queue != null ? queue.hashCode() : 0);
        result = 31 * result + (lock != null ? lock.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CashDesk" + id +
                "(cashBox = " + cashBox +
                ", customers served = " + customersServed +
                ", queue length = " + queue.size() +
                ")";
    }
}
