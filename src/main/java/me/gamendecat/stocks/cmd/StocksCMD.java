package me.gamendecat.stocks.cmd;

import me.gamendecat.stocks.Stocks;
import me.gamendecat.stocks.config.Category;
import me.gamendecat.stocks.config.ConfigManager;
import me.gamendecat.stocks.manager.InventoryManager;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StocksCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (arg3.length > 0 && arg0.hasPermission("shop.master") && arg3[0].equalsIgnoreCase("reload")) {
            Stocks.instance.reloadConfig();
            ConfigManager.restart();
            return false;
        }

        if(arg3.length > 0) {
            for(Category cat : ConfigManager.cat) {
                if(cat.name.equalsIgnoreCase(arg3[0])) {
                    InventoryManager.createCatMap((Player)arg0, cat);
                    return true;
                }
            }
        }

        if (arg0 instanceof Player) {
            Player p = (Player)arg0;
            if (arg3.length > 0 && arg0.hasPermission("shop.master") && arg3[0].equalsIgnoreCase("cat")) {
                if (arg3.length == 1) {
                    String s = "";
                    for (Category c : ConfigManager.cat) {
                        if (s.isEmpty()) {
                            s = "§c" + c.name;
                            continue;
                        }
                        s = String.valueOf(s) + "" + c.name;
                    }
                    p.sendMessage("" + s);
                } else {
                    Category c = null;
                    for (Category cat : ConfigManager.cat) {
                        if (cat.name.toLowerCase().contains(arg3[1].toLowerCase()))
                            c = cat;
                    }
                    if (c != null) {
                        p.playSound(p.getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);
                        if (c.command != null) {
                            p.performCommand(c.command.replaceAll("/", ""));
                            return false;
                        }
                        InventoryManager.createCatMap(p, c);
                    }
                }
                return false;
            }
            if (p.hasPermission("shop.master")) {
                p.openInventory(InventoryManager.inv);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            }
        } else {
            arg0.sendMessage("has to be player");
        }
        return false;
    }
}
