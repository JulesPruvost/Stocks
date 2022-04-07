package me.gamendecat.stocks.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import me.gamendecat.stocks.Stocks;
import me.gamendecat.stocks.manager.InventoryManager;
import me.gamendecat.stocks.manager.PlayerManager;
import me.gamendecat.stocks.utils.FriendlyFormat;
import me.gamendecat.stocks.utils.FriendlyName;
import me.gamendecat.stocks.utils.ib;
import me.gamendecat.stocks.wrappers.ItemWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
public class ConfigManager {
    public static List<Category> cat;

    public static String colorCats;

    public static int restock;

    public static void restart() {
        Stocks.instance.reloadConfig();
        setup();
        closeInvs();
        InventoryManager.setup();
        Bukkit.getConsoleSender().sendMessage("restocked!");
    }

    public static void setup() {
        int id = 0;
        if (cat == null) {
            cat = Collections.synchronizedList(new LinkedList<>());
        } else {
            cat.clear();
        }
        Set<String> keys_cats = Stocks.instance.getConfig().getConfigurationSection("limited_shop.categories")
                .getKeys(false);
        for (String s : keys_cats) {
            Category category = new Category();
            category.name = s;
            Bukkit.getConsoleSender().sendMessage("" + s + " created.");
            ConfigurationSection sec = Stocks.instance.getConfig()
                    .getConfigurationSection("limited_shop.categories." + s);
            String colorr = Stocks.instance.getConfig().getString("limited_shop.categories." + s + ".COLOR")
                    .replaceAll("&", "§");
            category.color = colorr;
            for (String l : sec.getKeys(false)) {
                if (l.equalsIgnoreCase("SLOT")) {
                    category.slot = sec.getInt(l);
                    continue;
                }
                if (l.equalsIgnoreCase("DISPLAY")) {
                    category.display = ib.mat(Material.getMaterial(sec.getString(l).toUpperCase()))
                            .name(colorr + category.name);
                    continue;
                }
                if (!l.equalsIgnoreCase("COLOR")) {
                    Material m;
                    if (l.equalsIgnoreCase("execute")) {
                        String command = sec.getString(l).toLowerCase();
                        category.command = command;
                        continue;
                    }
                    if (isInt(l.replaceAll("_1", ""))) {
                        int i = Integer.valueOf(l.replaceAll("_1", "")).intValue();
                        m = Material.getMaterial(l);
                    } else {
                        m = Material.getMaterial(l.replaceAll("_1", "").toUpperCase());
                    }
                    if (m == null) {
                        Bukkit.getConsoleSender().sendMessage("§c§l" + l + " does not exist as material!");
                        continue;
                    }
                    Bukkit.getConsoleSender().sendMessage("" + m + " to category " + s + "...");
                    ItemWrapper wrapper = new ItemWrapper();
                    wrapper.available = sec.getInt(String.valueOf(l) + ".initial_amount") / 2;
                    wrapper.sellprice = sec.getDouble(String.valueOf(l) + ".sell_price");
                    wrapper.buyprice = sec.getDouble(String.valueOf(l) + ".buy_price");
                    wrapper.fbuy = wrapper.buyprice;
                    wrapper.fsell = wrapper.sellprice;
                    wrapper.maxamount = wrapper.available * 2;
                    wrapper.id = id++;
                    if (sec.contains(String.valueOf(l) + ".command"))
                        wrapper.command = sec.getString(String.valueOf(l) + ".command");
                    int data = sec.contains(String.valueOf(l) + ".data") ? sec.getInt(String.valueOf(l) + ".data") : 0;
                    String name = FriendlyName.mat(m, (short)data);
                    wrapper.handle = (ItemStack)ib.mat(m).data(data).name("§f" + name)
                            .lore(new String[] { "§a▌  §7Buy Price: §a§l§a" + FriendlyFormat.format(wrapper.buyprice), " ",
                            (wrapper.command == null) ? ("§c▌  §7Sell Price: §a§l§a" + FriendlyFormat.format(wrapper.sellprice)) :
                    "§7Selling unavailable",
                    " ", "§a§l◄ §aLeft click to buy", "§c§l► §cRight click to sell",
                    "§9▥ Total Stock: §a" + wrapper.available + "/" + wrapper.maxamount });
                    category.items.put("§f"+ name, wrapper);
                }
            }
            cat.add(category);
        }
        for (Category c : cat) {
            double i = c.items.values().size();
            c.pages = (int)Math.ceil(i / 28.0D);
        }
    }

    public static int percent(int a, int b) {
        return 1 - (int)(a / b);
    }

    public static void closeInvs() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                if (p.getOpenInventory() != null) {
                    if (p.getOpenInventory().getTitle().equalsIgnoreCase("§7§lShop")) {
                            p.closeInventory();
                    p.sendMessage("§c<!> The Shop has been restocked. Please, open it up again.");
                    continue;
                }
                if (PlayerManager.cases.containsKey(p.getUniqueId())) {
                    p.closeInventory();
                    p.sendMessage("§c<!> The Shop has been restocked. Please, open it up again.");
                    continue;
                }
                if (PlayerManager.pages.containsKey(p.getUniqueId())) {
                    p.closeInventory();
                    p.sendMessage("§c<!> The Shop has been restocked. Please, open it up again.");
                }
            }
        } catch (Exception exception) {}
    }
}

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception ss) {
            return false;
        }
    }
}
