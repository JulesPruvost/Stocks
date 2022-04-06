package me.gamendecat.stocks;

import org.bukkit.plugin.java.JavaPlugin;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import me.gamendecat.stocks.cmd.StocksCMD;
import me.gamendecat.stocks.config.ConfigManager;
import me.gamendecat.stocks.listener.PlayerListeners;
import me.gamendecat.stocks.manager.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Stocks extends JavaPlugin {

    public static Stocks instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Bukkit.getConsoleSender().sendMessage("are up!");
        ConfigManager.setup();
        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        getCommand("shop").setExecutor(new StocksCMD());
        InventoryManager.setup();
        if (!setupVault())
            getServer().getPluginManager().disablePlugin(this);
        ConfigManager.restart();
        Timer t = new Timer();
        SimpleDateFormat fr = new SimpleDateFormat("dd:HH:mm");
        fr.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String string = fr.format(new Date());
        Date date = new Date();
        int hour = Integer.valueOf(string.split(":")[1]).intValue();
        int minute = Integer.valueOf(string.split(":")[2]).intValue();
        int day = Integer.valueOf(string.split(":")[0]).intValue();
        date.setHours(hour);
        date.setDate(day);
        date.setMinutes(minute);
        if (date.getHours() > 12) {
            int hourny = date.getHours();
            date = new Date();
            int timeDiff = date.getHours() - hourny;
            date.setHours(12 - timeDiff);
            date.setDate(date.getDate() + 1);
            t.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    ConfigManager.restart();
                }
            },  date, 86400000L);
            Bukkit.getConsoleSender().sendMessage("tomorrow in 1 day and " + timeDiff + " hours.");
        } else if (date.getHours() == 12) {
            t.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    ConfigManager.restart();
                }
            },  10L, 86400000L);
        } else {
            int timeDiff = 12 - date.getHours();
            date = new Date();
            date.setMinutes(0);
            date.setHours(date.getHours() + timeDiff);
            t.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    ConfigManager.restart();
                }
            },  date, 86400000L);
            Bukkit.getConsoleSender().sendMessage("in " + timeDiff + " hours.");
        }
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("going down!");
        ConfigManager.closeInvs();
    }

    public static Economy econ = null;

    private boolean setupVault() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (economyProvider != null)
            econ = (Economy)economyProvider.getProvider();
        return (econ != null);
    }
}
