package me.gamendecat.stocks.config;

import me.gamendecat.stocks.wrappers.ItemWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Category {

    public String color;

    public ItemStack display;

    public String name;

    public int pages;

    public String command;

    public int slot;

    public Map<String, ItemWrapper> items = Collections.synchronizedMap(new LinkedHashMap<>());

    public List<ItemWrapper> orderedList;
}
