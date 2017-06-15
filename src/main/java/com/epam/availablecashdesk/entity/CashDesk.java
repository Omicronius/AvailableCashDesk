package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;

import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class CashDesk extends Thread {
    private long id;
    private int cashbox;
    private ArrayDeque<Customer> queue = new ArrayDeque<>();

    public CashDesk(long id) {
        super("CashDesk# " + id);
        this.id = id;
    }

    @Override
    public void run() {

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        while (true) {
            Customer customer = queue.pollFirst();
            if (customer != null && !customer.isServed()) {
                int income = customer.serve();
                try {
                    TimeUnit.SECONDS.sleep(Generator.generateRandom(3) + 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cashbox += income;
                System.out.println(customer.toString() + " is served by the " + toString());
            }
        }
    }

    public void registerCustomer(Customer customer) {
        queue.add(customer);
        System.out.println(customer.toString() + " added to the " + toString());
    }

    public int getSize() {
        return queue.size();
    }

    @Override
    public String toString() {
        return "CashDesk" + id +
                "(cashbox = " + cashbox +
                ")";
    }
}
