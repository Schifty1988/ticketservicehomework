package com.walmart.ticketservicehomework;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michail Ostrowski
 */
public class SeatHoldTest {

    @Test
    public void testSeatHold() {

        int testId = 737;
        String testEmail = "mail@michail-ostrowski.de";

        SeatHold testSeatHold = new SeatHold(testEmail, testId);

        Assert.assertEquals(testId, testSeatHold.getSeatHoldId());
        Assert.assertEquals(testEmail, testSeatHold.getEmail());
        Assert.assertNotEquals(0, testSeatHold.getCreationTime());

        int levelA = 43;
        int levelB = 12;
        int levelASeatHolds = 12;
        int levelBSeatHolds = 61;

        testSeatHold.setSeatsOnHold(levelA, levelASeatHolds);
        testSeatHold.setSeatsOnHold(levelB, levelBSeatHolds);

        Assert.assertEquals(levelASeatHolds, testSeatHold.getSeatsOnHold(levelA));
        Assert.assertEquals(levelBSeatHolds, testSeatHold.getSeatsOnHold(levelB));
        Assert.assertEquals(levelASeatHolds + levelBSeatHolds, testSeatHold.getSeatsOnHold());
    }
}
