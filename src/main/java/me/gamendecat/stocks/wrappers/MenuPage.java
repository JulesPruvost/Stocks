package me.gamendecat.stocks.wrappers;

import java.util.HashMap;
import java.util.Map;
import me.gamendecat.stocks.config.Category;
import org.bukkit.inventory.Inventory;

public class MenuPage {
    public int page;

    public Category c;

    public Inventory inv;

    public Map<Integer, Integer> openID = new HashMap<>();

    public MenuPage(Category c, Inventory inv) {
        this.c = c;
        this.inv = inv;
        this.page = 1;
    }
}
