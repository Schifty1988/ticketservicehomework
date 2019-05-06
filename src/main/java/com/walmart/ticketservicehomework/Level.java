package com.walmart.ticketservicehomework;

/**
 * Structure to hold the definition of a Level
 *
 * @author Michail Ostrowski
 */
public class Level {

    final private int id;
    final private String name;
    final private int price;
    final private int rows;
    final private int seatsInRow;

    public Level(final int levelId, final String levelName, final int levelPrice, final int levelRows, final int levelSeatsInRow) {
        this.id = levelId;
        this.name = levelName;
        this.price = levelPrice;
        this.rows = levelRows;
        this.seatsInRow = levelSeatsInRow;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public int getRows() {
        return this.rows;
    }

    public int getSeatsInRow() {
        return this.seatsInRow;
    }
}
