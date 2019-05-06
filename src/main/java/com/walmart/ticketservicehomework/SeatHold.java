package com.walmart.ticketservicehomework;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines a SeatHold. Sets the creation date when constructed and stores the
 * number of held seats in a map.
 *
 * @author Michail Ostrowski
 */
public class SeatHold {

    final private Map<Integer, Integer> seatsOnHold;
    final private String email;
    final private int id;
    final private long creationTime;

    public SeatHold(final String seatHoldEmail, final int seatHoldId) {
        seatsOnHold = new HashMap<Integer, Integer>();
        email = seatHoldEmail;
        id = seatHoldId;
        creationTime = System.currentTimeMillis();
    }

    public void setSeatsOnHold(final int level, final int numberOfSeats) {
        this.seatsOnHold.put(level, numberOfSeats);
    }

    public int getSeatsOnHold(final int level) {
        return this.seatsOnHold.getOrDefault(level, 0);
    }

    public int getSeatsOnHold() {
        int numberOfSeatsOnHold = 0;
        Collection<Integer> levelReservations = this.seatsOnHold.values();

        for (Integer value : levelReservations) {
            numberOfSeatsOnHold += value;
        }

        return numberOfSeatsOnHold;
    }

    public String getEmail() {
        return this.email;
    }

    public int getSeatHoldId() {
        return this.id;
    }

    public long getCreationTime() {
        return this.creationTime;
    }
}
