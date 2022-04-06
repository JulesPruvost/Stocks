package me.gamendecat.stocks.utils;

public class Pricer {
    public static double newPrice(double currentValue, double maxStock, double price) {
        double delta = price * 0.08D;
        double m = -delta / maxStock / 2.0D;
        return currentValue * m + delta;
    }
}
