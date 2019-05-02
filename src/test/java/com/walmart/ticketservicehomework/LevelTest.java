package com.walmart.ticketservicehomework;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michail Ostrowski
 */
public class LevelTest {
    
    @Test
    public void testLevelCreation() {
        
        int testId = 737;
        String testName = "LevelName";
        int testPrice = 10000;
        int testRows = 11;
        int testSeatsInRow = 30;
                
        Level testLevel = new Level(testId, testName, testPrice, testRows, testSeatsInRow);
        
        Assert.assertEquals(testId, testLevel.getId());
        Assert.assertEquals(testName, testLevel.getName());
        Assert.assertEquals(testPrice, testLevel.getPrice());
        Assert.assertEquals(testRows, testLevel.getRows());
        Assert.assertEquals(testSeatsInRow, testLevel.getSeatsInRow());
    }
}
