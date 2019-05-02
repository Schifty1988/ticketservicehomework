package com.walmart.ticketservicehomework;

import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michail Ostrowski
 */
public class TicketServiceImplTest {
    
    public TicketService createDefaultService() {              
        int testTimeout = 100;
        Level[] levels = new Level[4];
        levels[0] = new Level(1, "Orchestra", 10000, 25, 50);
        levels[1] = new Level(2, "Main", 7500, 25, 100);
        levels[2] = new Level(3, "Balcony 1", 5000, 15, 100);
        levels[3] = new Level(4, "Balcony 2", 4000, 15, 100); 
        return new TicketServiceImpl(levels, 100);
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
        String testEmail = "mail@michail-ostrowski.de";
        TicketService testService = createDefaultService();
        int capacityTotal = testService.numSeatsAvailable(Optional.empty());      
        int capacityHighestLevel = testService.numSeatsAvailable(maxRank);
        
        SeatHold testHold = 
            testService.findAndHoldSeats(numberOfRequestedSeats, minRank, maxRank, testEmail);

        Assert.assertEquals(testEmail, testHold.getEmail());
        
        int totalNumberOfSeatsAvailableAfterHold = testService.numSeatsAvailable(Optional.empty());
        Assert.assertEquals(capacityTotal - numberOfRequestedSeats, totalNumberOfSeatsAvailableAfterHold);
        
        int expectedSeatsHeldOnHighestLevel = Math.min(capacityHighestLevel, numberOfRequestedSeats);
        Assert.assertEquals(expectedSeatsHeldOnHighestLevel, testHold.getSeatsOnHold(maxRank.orElse(3)));
        
        int freeSeatsExpected = Math.max(capacityHighestLevel - numberOfRequestedSeats, 0);
        int numberOfSeatsAvailableOnHighestLevel = testService.numSeatsAvailable(maxRank);
        Assert.assertEquals(freeSeatsExpected, numberOfSeatsAvailableOnHighestLevel);
        
        String confirmationId = testService.reserveSeats(testHold.getSeatHoldId(), testEmail);
        Assert.assertNotNull(confirmationId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSeatHoldWithInvalidNumber() {
        TicketService testService = createDefaultService();
        testService.findAndHoldSeats(0, null, null, "mail@michail-ostrowski.de");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSeatHoldWithInvalidMinRank() {
        TicketService testService = createDefaultService();
        testService.findAndHoldSeats(5, Optional.of(17), null, "mail@michail-ostrowski.de");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSeatHoldWithInvalidMaxRank() {
        TicketService testService = createDefaultService();
        testService.findAndHoldSeats(5, null, Optional.of(17), "mail@michail-ostrowski.de");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSeatHoldWithInvalidAddress() {
        TicketService testService = createDefaultService();
        testService.findAndHoldSeats(5, null, null, "invalidEmail@");
    }
}
