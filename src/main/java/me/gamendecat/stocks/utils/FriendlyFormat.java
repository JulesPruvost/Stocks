package me.gamendecat.stocks.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import me.gamendecat.stocks.listener.PlayerListeners;

public class FriendlyFormat {
    private static final NavigableMap<Double, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(Double.valueOf(1000.0D), "K");
        suffixes.put(Double.valueOf(1000000.0D), "M");
        suffixes.put(Double.valueOf(1.0E9D), "B");
        suffixes.put(Double.valueOf(1.0E12D), "T");
        suffixes.put(Double.valueOf(1.0E15D), "Q");
        suffixes.put(Double.valueOf(1.0E18D), "P");
    }

    public static String format(double value) {
        if (value == Double.MIN_VALUE) {
            return format(1.0D);
        }
        if (value < 0.0D) {
            return "-" + format(-value);
        }
        if (value < 1000.0D) {
            String str = Double.toString(value);

            if ((str.split("\\.")).length == 2 &&
                    str.split("\\.")[1].length() == 1) {
                return str + "0";
            }

            if(BigDecimal.valueOf(value).scale() > 2) {
                str = String.valueOf(new BigDecimal(value).setScale(2, RoundingMode.HALF_UP));
            }
            return str;
        }

        Map.Entry<Double, String> e = suffixes.floorEntry(Double.valueOf(value));
        double divideBy = (e.getKey()).doubleValue();
        String suffix = e.getValue();
        double truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100.0D && (truncated / 10.0D != truncated / 10.0D);
        String s = (new StringBuilder(String.valueOf(PlayerListeners.round(truncated / 10.0D, 2)))).toString();
        if ((s.split("\\.")).length == 2 &&
                s.split("\\.")[1].length() == 1)
            s = String.valueOf(s) + "0";
        return hasDecimal ? (String.valueOf(PlayerListeners.round(truncated / 10.0D, 2)) + suffix) : (String.valueOf(s) + suffix);
    }
}