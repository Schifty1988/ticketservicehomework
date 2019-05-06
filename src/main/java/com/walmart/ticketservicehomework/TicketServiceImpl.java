package com.walmart.ticketservicehomework;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Thread safe implementation of the TicketService interface.
 *
 * @author Michail Ostrowski
 */
public class TicketServiceImpl implements TicketService {

    final private long timeout;
    int[] availableSeats;

    int seatHoldId;
    Map<Integer, SeatHold> seatHolds;

    public TicketServiceImpl(Level[] levels, long serviceTimeout) {

        if (levels == null || levels.length == 0) {
            throw new IllegalArgumentException();
        }

        availableSeats = new int[levels.length];
        seatHolds = new HashMap<Integer, SeatHold>();
        seatHoldId = 0;

        for (int i = 0; i < levels.length; ++i) {
            availableSeats[i] = levels[i].getRows() * levels[i].getSeatsInRow();
        }
        this.timeout = serviceTimeout;
    }

    @Override
    public synchronized int numSeatsAvailable(Optional<Integer> venueLevel) {
        invalidateExpiredSeatHolds();

        if (venueLevel != null && venueLevel.isPresent()) {
            int venueLevelValue = venueLevel.get();

            if (isValidVenueLevel(venueLevelValue) == false) {
                throw new IllegalArgumentException();
            }

            return availableSeats[venueLevelValue];
        }

        int numberOfSeatsAvailable = 0;
        for (int i = 0; i < availableSeats.length; ++i) {
            numberOfSeatsAvailable += availableSeats[i];
        }

        return numberOfSeatsAvailable;
    }

    @Override
    public synchronized SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel, String customerEmail) {
        invalidateExpiredSeatHolds();

        if (numSeats <= 0) {
            throw new IllegalArgumentException();
        }

        int minLevelValue = minLevel == null ? 0 : minLevel.orElse(0);

        if (isValidVenueLevel(minLevelValue) == false) {
            throw new IllegalArgumentException();
        }

        int maxLevelValue = maxLevel == null ? availableSeats.length - 1 : maxLevel.orElse(availableSeats.length - 1);

        if (isValidVenueLevel(maxLevelValue) == false) {
            throw new IllegalArgumentException();
        }

        if (isValidEmail(customerEmail) == false) {
            throw new IllegalArgumentException();
        }

        int uniqueId = getNextSeatHoldId();
        int requiredSeats = numSeats;
        SeatHold seatHold = new SeatHold(customerEmail, uniqueId);

        for (int i = maxLevelValue; i >= minLevelValue; --i) {
            if (availableSeats[i] >= requiredSeats) {
                availableSeats[i] = availableSeats[i] - requiredSeats;
                seatHold.setSeatsOnHold(i, requiredSeats);
                break;
            }

            seatHold.setSeatsOnHold(i, availableSeats[i]);
            requiredSeats -= availableSeats[i];
            availableSeats[i] = 0;
        }

        seatHolds.put(uniqueId, seatHold);
        return seatHold;
    }

    @Override
    public synchronized String reserveSeats(int seatHoldId, String customerEmail) {
        invalidateExpiredSeatHolds();

        SeatHold confirmedSeatHold = seatHolds.remove(seatHoldId);

        if (confirmedSeatHold == null) {
            return null;
        }

        return String.valueOf(seatHoldId);
    }

    private boolean isValidVenueLevel(final int venueLevel) {
        return venueLevel >= 0 && venueLevel < availableSeats.length;
    }

    private boolean isValidEmail(final String emailToVerify) {
        if (emailToVerify == null) {
            return false;
        }

        String[] splittedEmail = emailToVerify.split("@");

        if (splittedEmail.length != 2) {
            return false;
        }

        if (splittedEmail[0].length() == 0 || splittedEmail[1].length() == 0) {
            return false;
        }

        if (splittedEmail[1].contains(".") == false) {
            return false;
        }

        return true;
    }

    private int getNextSeatHoldId() {
        seatHoldId++;
        return seatHoldId;
    }

    private void invalidateExpiredSeatHolds() {
        long currentTime = System.currentTimeMillis();
        long seatHoldAge;

        Collection<SeatHold> seatHoldList = seatHolds.values();
        List<SeatHold> invalidatedSeatHolds = new LinkedList<SeatHold>();

        for (SeatHold currentSeatHold : seatHoldList) {
            seatHoldAge = currentTime - currentSeatHold.getCreationTime();

            if (seatHoldAge > timeout) {
                invalidateSeatHold(currentSeatHold);
            }
        }

        seatHoldList.removeAll(invalidatedSeatHolds);
    }

    private void invalidateSeatHold(final SeatHold seatHold) {
        int numberOfSeatsHeld;

        for (int i = 0; i < availableSeats.length; ++i) {
            numberOfSeatsHeld = seatHold.getSeatsOnHold(i);
            availableSeats[i] += numberOfSeatsHeld;
        }
    }
}
