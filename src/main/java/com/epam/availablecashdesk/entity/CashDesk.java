package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CashDesk extends Thread {
    public static Logger logger = LogManager.getLogger(CashDesk.class);
    private long id;
    private int cashBox;
    private ArrayList<Customer> queue = new ArrayList<>();
    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public CashDesk(long id) {
        super("CashDesk# " + id);
        this.id = id;
    }

    public ArrayList<Customer> getQueue() {
        return queue;
    }

    public void setQueue(ArrayList<Customer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.log(Level.WARN, "CashDesk thread has been interrupted!");
        }
        while (true) {
            lock.lock();
            if (queue.size() == 0) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    logger.log(Level.WARN, "CashDesk thread has been interrupted!");
                }
            }
            Customer customer = queue.remove(0);
            lock.unlock();
            int income = customer.serve();
            System.out.println(customer.toString() + " is served by the " + toString());
            cashBox += income;
            try {
                TimeUnit.MILLISECONDS.sleep(Generator.generateRandom(20*(int) id) + 10);
            } catch (InterruptedException e) {
                logger.log(Level.WARN, "CashDesk thread has been interrupted!");
            }
        }
    }

    public int getSize() {
        return queue.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CashDesk cashDesk = (CashDesk) o;

        if (id != cashDesk.id) return false;
        if (cashBox != cashDesk.cashBox) return false;
        if (queue != null ? !queue.equals(cashDesk.queue) : cashDesk.queue != null) return false;
        if (lock != null ? !lock.equals(cashDesk.lock) : cashDesk.lock != null) return false;
        return condition != null ? condition.equals(cashDesk.condition) : cashDesk.condition == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
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
                " queue length = " + queue.size() +
                ")";
    }
}
