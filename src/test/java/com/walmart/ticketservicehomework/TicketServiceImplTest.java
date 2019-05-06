package com.walmart.ticketservicehomework;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michail Ostrowski
 */
public class TicketServiceImplTest {

    final static long TIMEOUT = 100;
    final static String CUSTOMER_EMAIL = "mail@michail-ostrowski.de";

    public TicketService createDefaultService() {
        Level[] levels = new Level[4];
        levels[0] = new Level(1, "Orchestra", 10000, 25, 50);
        levels[1] = new Level(2, "Main", 7500, 25, 100);
        levels[2] = new Level(3, "Balcony 1", 5000, 15, 100);
        levels[3] = new Level(4, "Balcony 2", 4000, 15, 100);
        return new TicketServiceImpl(levels, TIMEOUT);
    }

    @Test
    public void testNumSeatsAvailable() {
        TicketService testService = createDefaultService();

        int freeSeats1 = 25 * 50;
        int freeSeats2 = 25 * 100;
        int freeSeats3 = 15 * 100;
        int freeSeats4 = 15 * 100;

        int freeSeatsTotal = freeSeats1 + freeSeats2 + freeSeats3 + freeSeats4;

        Assert.assertEquals(freeSeats2, testService.numSeatsAvailable(Optional.of(1)));
        Assert.assertEquals(freeSeats3, testService.numSeatsAvailable(Optional.of(2)));
        Assert.assertEquals(freeSeats4, testService.numSeatsAvailable(Optional.of(3)));
        Assert.assertEquals(freeSeats1, testService.numSeatsAvailable(Optional.of(0)));

        Assert.assertEquals(freeSeatsTotal, testService.numSeatsAvailable(Optional.empty()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNumSeatsAvailableWithInvalidLevel() {
        TicketService testService = createDefaultService();
        int invalidRank = 17;
        testService.numSeatsAvailable(Optional.of(invalidRank));
    }

    @Test
    public void testSeatHoldVariations() {
        testSeatHoldSeatAndReserve(5, Optional.empty(), Optional.empty());
        testSeatHoldSeatAndReserve(5, Optional.empty(), Optional.of(2));
        testSeatHoldSeatAndReserve(5, Optional.of(1), Optional.empty());
        testSeatHoldSeatAndReserve(5, Optional.of(2), Optional.of(2));
        testSeatHoldSeatAndReserve(6605, Optional.empty(), Optional.of(3));
    }

    public void testSeatHoldSeatAndReserve(int numberOfRequestedSeats, Optional<Integer> minRank, Optional<Integer> maxRank) {
        TicketService testService = createDefaultService();
        int capacityTotal = testService.numSeatsAvailable(Optional.empty());
        int capacityHighestLevel = testService.numSeatsAvailable(maxRank);

        SeatHold testHold
                = testService.findAndHoldSeats(numberOfRequestedSeats, minRank, maxRank, CUSTOMER_EMAIL);

        Assert.assertEquals(CUSTOMER_EMAIL, testHold.getEmail());

        int totalNumberOfSeatsAvailableAfterHold = testService.numSeatsAvailable(Optional.empty());
        Assert.assertEquals(capacityTotal - numberOfRequestedSeats, totalNumberOfSeatsAvailableAfterHold);

        int expectedSeatsHeldOnHighestLevel = Math.min(capacityHighestLevel, numberOfRequestedSeats);
        Assert.assertEquals(expectedSeatsHeldOnHighestLevel, testHold.getSeatsOnHold(maxRank.orElse(3)));

        int freeSeatsExpected = Math.max(capacityHighestLevel - numberOfRequestedSeats, 0);
        int numberOfSeatsAvailableOnHighestLevel = testService.numSeatsAvailable(maxRank);
        Assert.assertEquals(freeSeatsExpected, numberOfSeatsAvailableOnHighestLevel);

        String confirmationId = testService.reserveSeats(testHold.getSeatHoldId(), CUSTOMER_EMAIL);
        Assert.assertNotNull(confirmationId);
    }

    @Test
    public void testReservationAndTimeout() throws InterruptedException {
        TicketService testService = createDefaultService();

        int initiallyAvailable = testService.numSeatsAvailable(Optional.empty());
        int numberOfRequestedSeats = 10;

        SeatHold testHold
                = testService.findAndHoldSeats(numberOfRequestedSeats, Optional.empty(), Optional.empty(), CUSTOMER_EMAIL);

        Assert.assertNotNull(testHold);
        int availableAfterReservation = testService.numSeatsAvailable(Optional.empty());
        Assert.assertEquals(initiallyAvailable, availableAfterReservation + numberOfRequestedSeats);
        Thread.sleep(TIMEOUT + 1);

        int availableAfterTimeout = testService.numSeatsAvailable(Optional.empty());
        Assert.assertEquals(initiallyAvailable, availableAfterTimeout);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSeatHoldWithInvalidNumber() {
        TicketService testService = createDefaultService();
        testService.findAndHoldSeats(0, null, null, CUSTOMER_EMAIL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSeatHoldWithInvalidMinRank() {
        TicketService testService = createDefaultService();
        testService.findAndHoldSeats(5, Optional.of(17), null, CUSTOMER_EMAIL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSeatHoldWithInvalidMaxRank() {
        TicketService testService = createDefaultService();
        testService.findAndHoldSeats(5, null, Optional.of(17), CUSTOMER_EMAIL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSeatHoldWithInvalidAddress() {
        TicketService testService = createDefaultService();
        testService.findAndHoldSeats(5, null, null, "invalidEmail@");
    }

    @Test
    public void testMultiThreadedReservations() throws InterruptedException {

        final TicketService testService = createDefaultService();
        final ConcurrentLinkedQueue<SeatHold> seatHolds = new ConcurrentLinkedQueue<>();

        int initiallyAvailable = testService.numSeatsAvailable(Optional.empty());
        int reservedSeats = 0;

        for (int i = 0; i < 100; ++i) {
            Thread autoSeatHolder = new SeatHolder(testService, seatHolds);
            autoSeatHolder.setUncaughtExceptionHandler((Thread thread, Throwable thrwbl) -> {
                Assert.fail();
            });
            autoSeatHolder.start();
        }

        long timeout = System.currentTimeMillis() + 2000;

        while (System.currentTimeMillis() < timeout) {
            int availableSeats = testService.numSeatsAvailable(Optional.empty());

            if (availableSeats == 0) {
                break;
            }

            Thread.sleep(10);
        }

        for (SeatHold currentSeatHold : seatHolds) {
            reservedSeats += currentSeatHold.getSeatsOnHold();
        }

        Assert.assertEquals(initiallyAvailable, reservedSeats);
    }
}
