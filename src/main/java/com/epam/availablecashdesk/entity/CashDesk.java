package com.epam.availablecashdesk.entity;

import com.epam.availablecashdesk.util.Generator;
import java.util.PriorityQueue;

public class CashDesk extends Thread {
    private int casher;
    private PriorityQueue<Customer> queue;

    public CashDesk() {
        super("CashDesk# " + Generator.generateCashDeskId());
    }

    @Override
    public void run() {
    }
}
