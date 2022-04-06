package me.gamendecat.stocks.utils;

import java.util.Arrays;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ib extends ItemStack {
    public ib(ItemStack item) {
        super(item);
    }

    public ib(Material material) {
        super(material);
    }

    public static ib mat(Material m) {
        return new ib(m);
    }

    public ib name(String name) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(name);
        setItemMeta(meta);
        return this;
    }

    public ib enchant() {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        setItemMeta(meta);
        addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return this;
    }

    public ib lore(String... lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(Arrays.asList(lore));
        setItemMeta(meta);
        return this;
    }

    public ib amount(int amount) {
        setAmount(amount);
        return this;
    }

    public ib data(int data) {
        if (data == 0)
            return this;
        setDurability((short)data);
        return this;
    }

    public ib glow() {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        meta.addEnchant(Enchantment.values()[0], 1, true);
        setItemMeta(meta);
        return this;
    }

    public ib color(Color c) {
        LeatherArmorMeta meta = (LeatherArmorMeta)getItemMeta();
        meta.setColor(c);
        setItemMeta((ItemMeta)meta);
        return this;
    }
}
