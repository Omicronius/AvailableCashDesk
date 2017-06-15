package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class CashDesk extends Thread {
    private long id;
    private static final int CASHIER_AMOUNT = 1;
    private int cashbox;
    private PriorityQueue<Customer> queue = new PriorityQueue<>(CASHIER_AMOUNT);

    public CashDesk(long id) {
        super("CashDesk# " + id);
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            Customer customer = queue.poll();
            if (customer != null && !customer.isServed()) {
                int income = customer.serve();
                try {
                    TimeUnit.SECONDS.sleep(Generator.generateRandom(10) + 3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Customer# " + customer.getId() + " is served by the " + this.getName());
                cashbox += income;
            }
        }
    }

    public int getQueueLength() {
        return queue.size();
    }

    public void registerCustomer(Customer customer) {
        queue.offer(customer);
        System.out.println(customer.getId() + " added to the " + this.getName());
    }
}
