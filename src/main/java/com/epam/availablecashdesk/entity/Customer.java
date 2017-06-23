package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Customer extends Thread {
    public static Logger logger = LogManager.getLogger(Customer.class);
    private long id;
    private boolean isQuickOrder;
    private boolean isServed;
    private int availableCash;
    private static Restaurant restaurant = Restaurant.getInstance();
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public Customer(long id, boolean isQuickOrder, int availableCash) {
        super("Customer# " + id);
        this.id = id;
        this.isQuickOrder = isQuickOrder;
        this.availableCash = availableCash;
    }

    @Override
    public long getId() {
        return id;
    }

    public boolean isQuickOrder() {
        return isQuickOrder;
    }

    public void setServed(boolean served) {
        isServed = served;
    }

    public int getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(int availableCash) {
        this.availableCash = availableCash;
    }

    @Override
    public void run() {
        CashDesk cashDesk;
        if (isQuickOrder) {
            restaurant.registerQuickOrder(this);
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                logger.log(Level.WARN, "Customer thread has been interrupted!");
            } finally {
                lock.unlock();
            }
        } else {
            cashDesk = restaurant.registerOrder(this);
            while (!isServed) {
                lock.lock();
                try {
                    if (cashDesk.getQueue().indexOf(this) > restaurant.defineShortestQueue().getSize()) {
                        cashDesk = restaurant.relocate(this, cashDesk);
                    }
                } finally {
                    lock.unlock();
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    logger.log(Level.WARN, "Customer thread has been interrupted!");
                }
            }
        }
        System.out.println(toString() + " <--- ");
    }


    public int serve() {
        int spentMoney;
        lock.lock();
        try {
            setServed(true);
            spentMoney = Generator.generateRandom(5) + 1;
            setAvailableCash(getAvailableCash() - spentMoney);
            condition.signal();
        } finally {
            lock.unlock();
        }
            return spentMoney;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != customer.id) return false;
        if (isQuickOrder != customer.isQuickOrder) return false;
        if (isServed != customer.isServed) return false;
        if (availableCash != customer.availableCash) return false;
        if (lock != null ? !lock.equals(customer.lock) : customer.lock != null) return false;
        return condition != null ? condition.equals(customer.condition) : customer.condition == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (isQuickOrder ? 1 : 0);
        result = 31 * result + (isServed ? 1 : 0);
        result = 31 * result + availableCash;
        result = 31 * result + (lock != null ? lock.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                '}';
    }
}
