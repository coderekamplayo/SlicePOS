package com.amplayo.slicepos.data.models;

public class PizzaFlavor {
    private String name;
    private double price;

    public PizzaFlavor(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
