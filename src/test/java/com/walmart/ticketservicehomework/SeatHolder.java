/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.walmart.ticketservicehomework;

import java.util.Optional;
import java.util.Queue;

/**
 * Thread that is designed to test a TicketService implementation by holding and
 * reserving seats. Successfully reserved SeatHold objects are added to the
 * provided SeatHold queue. The thread is stopped when no more seats are
 * available
 *
 * @author Schifty
 */
public class SeatHolder extends Thread {

    TicketService service;
    Queue<SeatHold> seatHolds;

    public SeatHolder(TicketService ticketService, Queue<SeatHold> ticketHolds) {
        this.service = ticketService;
        this.seatHolds = ticketHolds;
    }

    @Override
    public void run() {
        while (true) {
            int availableSeats = this.service.numSeatsAvailable(Optional.empty());

            if (availableSeats == 0) {
                break;
            }

            SeatHold currentSeatHold = this.service.findAndHoldSeats(1, Optional.empty(), Optional.empty(), "mail@michail-ostrowski.de");
            String confirmation = this.service.reserveSeats(currentSeatHold.getSeatHoldId(), currentSeatHold.getEmail());

            if (confirmation != null) {
                this.seatHolds.add(currentSeatHold);
            }
        }
    }
}
