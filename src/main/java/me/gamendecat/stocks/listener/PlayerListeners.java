package me.gamendecat.stocks.listener;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.gamendecat.stocks.Stocks;
import me.gamendecat.stocks.config.Category;
import me.gamendecat.stocks.config.ConfigManager;
import me.gamendecat.stocks.manager.InventoryManager;
import me.gamendecat.stocks.manager.PlayerManager;
import me.gamendecat.stocks.utils.FriendlyFormat;
import me.gamendecat.stocks.utils.FriendlyName;
import me.gamendecat.stocks.utils.Pricer;
import me.gamendecat.stocks.utils.TitleUpdater;
import me.gamendecat.stocks.utils.ib;
import me.gamendecat.stocks.wrappers.BuyCase;
import me.gamendecat.stocks.wrappers.ItemWrapper;
import me.gamendecat.stocks.wrappers.MenuPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListeners implements Listener {
    Inventory example;

    ItemStack set1;

    ItemStack rem10;

    ItemStack rem1;

    ItemStack add10;

    ItemStack add1;

    int[] ab;

    List<Integer> slots;

    public PlayerListeners() {
        this.ab = new int[] { 13, 14, 15, 22, 23, 24, 31, 32, 33 };
        this.slots = Arrays.asList(Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(19), Integer.valueOf(20), Integer.valueOf(21),
                Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(24), Integer.valueOf(25), Integer.valueOf(28), Integer.valueOf(29), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(33),
                Integer.valueOf(34), Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(41), Integer.valueOf(42), Integer.valueOf(43));
        this.example = Bukkit.createInventory(null, 54, "test");
        this.example.setItem(48, ib.mat(Material.RED_STAINED_GLASS_PANE).name("§c§lCancel").lore("§cCancel your selection.").lore(" ").lore("§cClick to cancel."));
        this.example.setItem(50, ib.mat(Material.GREEN_STAINED_GLASS_PANE).name("§a§lConfirm").lore("§aConfirm your selection.").lore(" ").lore("§aClick to confirm."));
        this.add10 = ib.mat(Material.LIME_STAINED_GLASS_PANE).name("§a§lAdd 10").lore("§7Add 10 items.", " ", "§cClick to add.");
        this.example.setItem(24, this.add10);
        this.add1 = ib.mat(Material.LIME_STAINED_GLASS_PANE).name("§a§lAdd 1").lore("§7Add 1 item.", " ", "§cClick to add.");
        this.example.setItem(33, this.add1);
        this.set1 = ib.mat(Material.RED_STAINED_GLASS_PANE).name("§c§lSet to 1").lore("§7Set the amount to 1 item.", " ", "§cClick to set.");
        this.rem10 = ib.mat(Material.RED_STAINED_GLASS_PANE).name("§c§lRemove 10").lore("§7Remove 10 items.", " ", "§cClick to remove.");
        this.rem1 = ib.mat(Material.RED_STAINED_GLASS_PANE).name("§c§lRemove 1").lore("§7Remove 1 item.", " ", "§cClick to remove.");
    }

    @EventHandler
    public synchronized void onClick(InventoryClickEvent event) {
        //todo: maybe fix here idk
        if (event.getView().getTitle().equals(InventoryManager.getTitle())) {
            if (event.getRawSlot() > 53)
                return;
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() &&
                    event.getCurrentItem().getItemMeta().hasDisplayName()) {
                Category c = getCategory(event.getCurrentItem().getItemMeta().getDisplayName());
                if (c != null) {
                    ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);
                    if (c.command != null) {
                        ((Player)event.getWhoClicked()).performCommand(c.command.replaceAll("/", ""));
                        return;
                    }
                    InventoryManager.createCatMap((Player)event.getWhoClicked(), c);
                }
            }
        } else if (PlayerManager.pages.containsKey(event.getWhoClicked().getUniqueId())) {
            if (event.getRawSlot() > 53)
                return;
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() &&
                    event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§c§lReturn to Market")) {
                    event.getWhoClicked().openInventory(InventoryManager.inv);
                    ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                    return;
                }
                MenuPage mp = PlayerManager.pages.get(event.getWhoClicked().getUniqueId());
                Category cat = mp.c;
                if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() &&
                        event.getCurrentItem().getItemMeta().hasDisplayName() &&
                        event.getCurrentItem().getItemMeta().getDisplayName().startsWith("§a§lNext Page")) {
                    updatePage((Player)event.getWhoClicked(), mp, ++mp.page);
                } else if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() &&
                        event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem()
                        .getItemMeta().getDisplayName().startsWith("§c§lPrevious Page")) {
                    updatePage((Player)event.getWhoClicked(), mp, --mp.page);
                } else if (cat.items.containsKey(event.getCurrentItem().getItemMeta().getDisplayName())) {
                    ItemWrapper wrp = cat.items.get(event.getCurrentItem().getItemMeta().getDisplayName());
                    boolean buy = event.getClick().isLeftClick();
                    if (buy && wrp.available == 0) {
                        event.getWhoClicked().sendMessage("§cThis item is unavailable at the moment, you have to wait");
                        event.getWhoClicked().sendMessage("§c either until somebody sells this item to the shop");
                        event.getWhoClicked().sendMessage("§c  or until the shop refills itself.");
                        event.getWhoClicked().closeInventory();
                        ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                        return;
                    }
                    if (!buy && wrp.command != null) {
                        event.getWhoClicked().sendMessage("§cThis item is not available for selling.");
                        ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                        return;
                    }
                    if (!buy && wrp.available >= wrp.maxamount) {
                        event.getWhoClicked().sendMessage("§cThis item is fully stocked.");
                        ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                        return;
                    }
                    Inventory confirm = Bukkit.createInventory(null, 54, "§8§l" + (buy ? "Buying (§2" : "Selling (§c") +
                            wrp.handle.getType().name().replaceAll("_", "") + "§8§l) §7- " + cat.color + cat.name);
                    confirm.setContents(this.example.getContents());
                    int i = wrp.handle.getMaxStackSize();
                    confirm.setItem(15, ib.mat(Material.LIME_STAINED_GLASS_PANE).name("§a§lSet to " + i)
                            .lore("§7Set the amount to " + i + " items.", " ", "§cClick to set."));
                    if (!buy) {
                        int amount = howManyInv((Player)event.getWhoClicked(), wrp.handle);
                        double ii = wrp.sellprice * amount;
                        confirm.setItem(4, ib.mat(Material.ENDER_CHEST).name("All").lore(
                                new String[] { "§c▌  §7Sell all for:" + ((ii > 0.0D) ? (new StringBuilder(FriendlyFormat.format(ii))).toString() : "-") }));
                                }
                                ib ib = me.gamendecat.stocks.utils.ib.mat(Material.PLAYER_HEAD).name("§a§nYour stats")
                                        .lore("§a▌  §7Wallet: + §a§l$§a" +
                                                FriendlyFormat.format(Stocks.econ.getBalance((OfflinePlayer)event.getWhoClicked())));
                        InventoryManager.setHeadSkin((ItemStack)ib,
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM2ZTk0ZjZjMzRhMzU0NjVmY2U0YTkwZjJlMjU5NzYzODllYjk3MDlhMTIyNzM1NzRmZjcwZmQ0ZGFhNjg1MiJ9fX0=");
                        confirm.setItem(8, ib);
                        confirm.setItem(22,
                                me.gamendecat.stocks.utils.ib.mat(wrp.handle.getType())
                                        .name("§f"+ FriendlyName.mat(wrp.handle.getType(), wrp.handle.getDurability()))
                                                .lore(new String[] { " ", buy ? ("§a▌ §7Buy Price: §a§l$§a" + FriendlyFormat.format(wrp.buyprice)) : (
                                                        "§c▌ §7Sell Price: §c§l$§c"+ FriendlyFormat.format(wrp.sellprice)) }));
                        if (buy) {
                            confirm.setItem(40, me.gamendecat.stocks.utils.ib.mat(Material.CHEST).name("§a§lBulk Buy")
                                    .lore(new String[] { "§7Buy more than " + i + " items.", " ", "§aClick to buy more." }));
                        } else {
                            confirm.setItem(40, me.gamendecat.stocks.utils.ib.mat(Material.CHEST).name("§c§lBulk Sell")
                                    .lore(new String[] { "§7Sell more than " + i + " items.", " ", "§cClick to sell more." }));
                        }
                        event.getWhoClicked().openInventory(confirm);
                        BuyCase ca = new BuyCase();
                        ca.amount = 1;
                        ca.buy = buy;
                        ca.wp = wrp;
                        ca.pageholder = mp.page;
                        ca.c = cat;
                        ca.slot = event.getRawSlot();
                        PlayerManager.cases.put(event.getWhoClicked().getUniqueId(), ca);
                        ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                        }
                    }
        } else if (event.getView().getTitle().startsWith("§8§lBuying (") ||
                        event.getView().getTitle().startsWith("§8§lSelling (")) {
            if (event.getRawSlot() > 53) return;
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
                event.setCancelled(true);
            if (PlayerManager.cases.containsKey(event.getWhoClicked().getUniqueId())) {
            BuyCase cass = PlayerManager.cases.get(event.getWhoClicked().getUniqueId());
            if (event.getRawSlot() == 48) {
                String lastWord = event.getView().getTitle().substring(event.getView().getTitle().lastIndexOf(" ")+1);
                Category c = getCategory(lastWord);
                InventoryManager.createCatMap((Player)event.getWhoClicked(), c);
                return;
            }
            int slot = event.getRawSlot();
            double price = cass.buy ? cass.wp.buyprice : cass.wp.sellprice;
            if (!cass.bulk) {
                 if (slot == 4) {
                     int amount = howManyInv((Player)event.getWhoClicked(), cass.wp.handle);
                     sell(cass.wp, (Player)event.getWhoClicked(), amount, cass);
                 } else {
                     if (slot == 50) {
                          if (cass.buy) {
                              buy(cass.wp, (Player)event.getWhoClicked(), cass.amount, cass);
                          } else {
                              sell(cass.wp, (Player)event.getWhoClicked(), cass.amount, cass);
                          }
                          return;
                     }
                     if (slot == 40) {
                         cass.bulk = true;
                         ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                 Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                         event.getInventory().clear();
                         ib ib = me.gamendecat.stocks.utils.ib.mat(Material.GRAY_STAINED_GLASS_PANE).name(" ");
                         int i;
                         for (i = 45; i < 54; i++)
                             event.getInventory().setItem(i, ib);
                         for (i = 0; i < this.ab.length; i++) {
                             event.getInventory().setItem(this.ab[i],
                                     me.gamendecat.stocks.utils.ib.mat(cass.wp.handle.getType())
                                                    .name((cass.buy ? "§a§lBuy " : "§c§lSell ") + (i + 1) + (
                                                            (i == 1) ? " stack" : " stacks"))
                                                    .lore(new String[] {"§7"+ (cass.buy ? "Buy" : "Sell") + " stacks of selected item.", " ",
                                                            "§7Price: §a§l$§a" + FriendlyFormat.format(64 * price * (i + 1)),
                                                            "§aClick to " + (cass.buy ? "buy." : "sell.") }).amount(i + 1));
                                }
                                event.getInventory().setItem(19, me.gamendecat.stocks.utils.ib.mat(Material.RED_TERRACOTTA).name("§c§lCancel")
                                        .lore(new String[] { "§7Exit Bulk " + (cass.buy ? "Buy" : "Sell") + " Menu", " ", "§cClick to exit." }));
                                return;
                            }
                        }
                        if (slot == 33) {
                            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                            if (event.getInventory().getItem(11) == null)
                                event.getInventory().setItem(11, this.set1);
                            if (event.getInventory().getItem(29) == null)
                                event.getInventory().setItem(29, this.rem1);
                            if (event.getInventory().getItem(20) == null && cass.amount > 9)
                                event.getInventory().setItem(20, this.rem10);
                            if (cass.amount + 1 >= cass.wp.handle.getMaxStackSize()) {
                                event.getInventory().setItem(33, null);
                                event.getInventory().setItem(24, null);
                                event.getInventory().setItem(15, null);
                            }
                            if (cass.amount + 10 >= cass.wp.handle.getMaxStackSize())
                                event.getInventory().setItem(24, null);
                            cass.amount++;
                            event.getInventory().getItem(22).setAmount(cass.amount);
                            changeLore(event.getInventory().getItem(22), price * cass.amount, cass.buy);
                        } else if (slot == 24) {
                            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                            if (event.getInventory().getItem(11) == null)
                                event.getInventory().setItem(11, this.set1);
                            if (event.getInventory().getItem(29) == null)
                                event.getInventory().setItem(29, this.rem1);
                            event.getInventory().setItem(20, this.rem10);
                            if (cass.amount + 10 >= cass.wp.handle.getMaxStackSize()) {
                                event.getInventory().setItem(33, null);
                                event.getInventory().setItem(24, null);
                                event.getInventory().setItem(15, null);
                            }
                            if (cass.amount + 20 > cass.wp.handle.getMaxStackSize())
                                event.getInventory().setItem(24, null);
                            cass.amount += 10;
                            event.getInventory().getItem(22).setAmount(cass.amount);
                            changeLore(event.getInventory().getItem(22), price * cass.amount, cass.buy);
                        } else if (slot == 15) {
                            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                            event.getInventory().setItem(33, null);
                            event.getInventory().setItem(24, null);
                            event.getInventory().setItem(15, null);
                            event.getInventory().setItem(11, this.set1);
                            event.getInventory().setItem(29, this.rem1);
                            event.getInventory().setItem(20, this.rem10);
                            cass.amount = cass.wp.handle.getMaxStackSize();
                            event.getInventory().getItem(22).setAmount(cass.amount);
                            changeLore(event.getInventory().getItem(22), price * cass.amount, cass.buy);
                        } else if (slot == 29) {
                            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                            if (event.getInventory().getItem(15) == null) {
                                int i = cass.wp.handle.getMaxStackSize();
                                event.getInventory().setItem(15,
                                        ib.mat(Material.LIME_STAINED_GLASS_PANE).name("§a§lSet to " + i)
                                                .lore(new String[] { "§7Set the amount to " + i + " items.", " ", "§cClick to set." }));
                            }
                            if (event.getInventory().getItem(33) == null)
                                event.getInventory().setItem(33, this.add1);
                            if (event.getInventory().getItem(24) == null &&
                                    cass.amount + 9 <= cass.wp.handle.getMaxStackSize())
                                event.getInventory().setItem(24, this.add10);
                            if (cass.amount - 1 <= 1) {
                                event.getInventory().setItem(29, null);
                                event.getInventory().setItem(20, null);
                                event.getInventory().setItem(11, null);
                            }
                            if (cass.amount - 1 <= 10)
                                event.getInventory().setItem(20, null);
                            cass.amount--;
                            event.getInventory().getItem(22).setAmount(cass.amount);
                            changeLore(event.getInventory().getItem(22), price * cass.amount, cass.buy);
                        } else if (slot == 20) {
                            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                            if (event.getInventory().getItem(15) == null) {
                                int i = cass.wp.handle.getMaxStackSize();
                                event.getInventory().setItem(15,
                                        ib.mat(Material.LIME_STAINED_GLASS_PANE).name("§a§lSet to " + i)
                                                .lore(new String[] { "§7Set the amount to " + i + " items.", " ", "§cClick to set." }));
                            }
                            if (event.getInventory().getItem(33) == null)
                                event.getInventory().setItem(33, this.add1);
                            event.getInventory().setItem(24, this.add10);
                            if (cass.amount - 10 <= 1) {
                                event.getInventory().setItem(29, null);
                                event.getInventory().setItem(20, null);
                                event.getInventory().setItem(11, null);
                            }
                            if (cass.amount - 20 <= 1)
                                event.getInventory().setItem(20, null);
                            cass.amount -= 10;
                            event.getInventory().getItem(22).setAmount(cass.amount);
                            changeLore(event.getInventory().getItem(22), price * cass.amount, cass.buy);
                        } else if (slot == 11) {
                            ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                    Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                            event.getInventory().setItem(29, null);
                            event.getInventory().setItem(20, null);
                            event.getInventory().setItem(11, null);
                            int i = cass.wp.handle.getMaxStackSize();
                            event.getInventory().setItem(15,
                                    ib.mat(Material.LIME_STAINED_GLASS_PANE).name("§a§lSet to " + i)
                                            .lore(new String[] { "§7Set the amount to " + i + " items.", " ", "§cClick to set." }));
                            event.getInventory().setItem(33, this.add1);
                            event.getInventory().setItem(24, this.add10);
                            cass.amount = 1;
                            event.getInventory().getItem(22).setAmount(1);
                            changeLore(event.getInventory().getItem(22), price * cass.amount, cass.buy);
                        }
                    } else if (slot == 19) {
                        cass.bulk = false;
                        ((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                        event.getInventory().clear();
                        int i = cass.wp.handle.getMaxStackSize();
                        ib ib = me.gamendecat.stocks.utils.ib.mat(Material.PLAYER_HEAD).name("§a§nYour stats")
                                .lore("§a▌  §7Wallet: §a§l$§a" +
                                        FriendlyFormat.format(Stocks.econ.getBalance((OfflinePlayer)event.getWhoClicked())));
                        InventoryManager.setHeadSkin((ItemStack)ib,
                                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM2ZTk0ZjZjMzRhMzU0NjVmY2U0YTkwZjJlMjU5NzYzODllYjk3MDlhMTIyNzM1NzRmZjcwZmQ0ZGFhNjg1MiJ9fX0=");
                        event.getInventory().setItem(8, ib);
                        if (!cass.buy) {
                            int amount = howManyInv((Player)event.getWhoClicked(), cass.wp.handle);
                            double ii = cass.wp.sellprice * amount;
                            event.getInventory().setItem(4, me.gamendecat.stocks.utils.ib.mat(Material.ENDER_CHEST).name("§c§lSell All").lore(new String[] { "§c▌  §7Sell all for: §c§l$§c" + ((ii > 0.0D) ? (new StringBuilder(FriendlyFormat.format(ii))).toString() : "-") }));
                                    }
                                    event.getInventory().setItem(48, me.gamendecat.stocks.utils.ib.mat(Material.RED_TERRACOTTA).name("§c§lCancel")
                                            .lore(new String[] { "§cCancel your selection." }).lore(new String[] { " " }).lore(new String[] { "§cClick to cancel." }));
                            event.getInventory().setItem(50, me.gamendecat.stocks.utils.ib.mat(Material.GREEN_TERRACOTTA).data(13).name("§a§lConfirm")
                                    .lore(new String[] { "§aConfirm your selection." }).lore(new String[] { " " }).lore(new String[] { "§aClick to confirm." }));
                            if (cass.amount < i)
                                event.getInventory().setItem(15,
                                        me.gamendecat.stocks.utils.ib.mat(Material.LIME_STAINED_GLASS_PANE).name("§a§lSet to " + i)
                                                .lore(new String[] { "§7Set the amount to " + i + " items.", " ", "§cClick to set." }));
                            if (cass.amount >= 11) {
                                event.getInventory().setItem(20, this.rem10);
                                event.getInventory().setItem(11, this.set1);
                                event.getInventory().setItem(29, this.rem1);
                            } else if (cass.amount != 1) {
                                event.getInventory().setItem(11, this.set1);
                                event.getInventory().setItem(29, this.rem1);
                            }
                            if (cass.amount + 10 <= i)
                                event.getInventory().setItem(24, this.add10);
                            if (cass.amount + 1 <= i)
                                event.getInventory().setItem(33, this.add1);
                            event.getInventory().setItem(22, me.gamendecat.stocks.utils.ib.mat(cass.wp.handle.getType())
                                    .name("§f" + FriendlyName.mat(cass.wp.handle.getType(), cass.wp.handle.getDurability()))
                                            .lore(new String[] { " ",
                                                    cass.buy ? (
                                                            "§a▌ §7Buy Price: §a§l$§a" +
                                                    FriendlyFormat.format(cass.wp.buyprice * cass.amount)) : (
                                    "§c▌ §7Sell Price: §c§l$§c" +
                            FriendlyFormat.format(cass.wp.sellprice * cass.amount)) }).amount(cass.amount));

                            if (cass.buy) {
                                event.getInventory().setItem(40, me.gamendecat.stocks.utils.ib.mat(Material.CHEST).name("§a§lBulk Buy")
                                        .lore(new String[] { "§7Buy more than " + i + " items.", " ", "§aClick to buy more." }));
                            } else {
                                event.getInventory().setItem(40, me.gamendecat.stocks.utils.ib.mat(Material.CHEST).name("§c§lBulk Buy")
                                        .lore(new String[] { "§7Sell more than " + i + " items.", " ", "§aClick to sell more." }));
                            }
                        } else {
                            int amount = event.getCurrentItem().getAmount();
                            if (cass.buy) {
                                buy(cass.wp, (Player)event.getWhoClicked(), amount * cass.wp.handle.getMaxStackSize(),
                                        cass);
                            } else {
                                sell(cass.wp, (Player)event.getWhoClicked(), amount * cass.wp.handle.getMaxStackSize(),
                                        cass);
                            }
                        }
                    } else {
                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().sendMessage("§cThere was an error. Please, try again.");
                    }
                }
            }

            private void updatePage(Player p, MenuPage page, int pg) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 2.0F, 1.0F);
                Category c = page.c;
                page.page = pg;
                if (pg == c.pages) {
                    page.inv.setItem(50, ib.mat(Material.BLACK_STAINED_GLASS_PANE).name(" "));
                } else {
                    page.inv.setItem(50,
                            ib.mat(Material.LIME_STAINED_GLASS_PANE)
                                    .name("§a§lNext Page §7【§fPage " + pg + "/" + c.pages + "§7】")
                                    .lore(new String[] { "§7Click to go to next page" }));
                }

                if (pg > 1) {
                    page.inv.setItem(48,
                            ib.mat(Material.RED_STAINED_GLASS_PANE)
                                    .name("§c§lPrevious Page §7【§fPage " + pg + "/" + c.pages + "§7】")
                                            .lore(new String[] { "§7Click to go to previous page" }));
                } else {
                    page.inv.setItem(48, ib.mat(Material.BLACK_STAINED_GLASS_PANE).name("  "));
                }
                page.openID.clear();
                int j = 10;
                int litpag = 1;
                int i;
                for (i = 0; i < c.orderedList.size(); i++) {
                    ItemWrapper iw = c.orderedList.get(i);
                    ItemStack item = iw.handle;
                    if (j == 44) {
                        if (litpag == pg)
                            break;
                        j = 10;
                        litpag++;
                    } else if ((j + 1) % 9 == 0) {
                        j += 2;
                    }
                    if (litpag == pg) {
                        page.inv.setItem(j, item);
                        page.openID.put(Integer.valueOf(iw.id), Integer.valueOf(j));
                    }
                    j++;
                }
                for (Iterator<Integer> iterator = this.slots.iterator(); iterator.hasNext(); ) {
                    i = iterator.next().intValue();
                    if (i >= j)
                        page.inv.setItem(i, null);
                }
                TitleUpdater.update(p, "§n" + ChatColor.stripColor(c.name) + " - " + pg + "/" + c.pages);
            }

            private synchronized void echoCall(ItemWrapper wp, Category c, boolean buy) {
                double initBuy = wp.fbuy;
                double initSell = wp.fsell;
                double b4buy = wp.buyprice;
                double b4sell = wp.sellprice;
                double newPriceBuy = Pricer.newPrice(wp.available, wp.maxamount, wp.fbuy);
                double newPriceSell = Pricer.newPrice(wp.available, wp.maxamount, wp.fsell);
                if (newPriceSell < -0.03D * initSell)
                    newPriceSell = -0.03D * initSell;
                newPriceSell = round(newPriceSell, 2);
                newPriceBuy = round(newPriceBuy, 2);
                wp.buyprice = wp.fbuy + newPriceBuy;
                wp.sellprice = wp.fsell + newPriceSell;
                List<String> lor = null;
                for (MenuPage mp : PlayerManager.pages.values()) {
                    if (mp.openID.containsKey(Integer.valueOf(wp.id))) {
                        int slot = mp.openID.get(Integer.valueOf(wp.id)).intValue();
                        ItemStack item = mp.inv.getItem(slot);
                        ItemMeta meta = item.getItemMeta();
                        List<String> lores = meta.getLore();
                        if (lor == null) {
                            lores.set(6, "§9▥ Total Stock: §" + colorAmount(wp.available, wp.maxamount) + wp.available + "/" +
                                    wp.maxamount);
                            if (wp.available == wp.maxamount / 2) {
                                lores.set(0, "§a▌  §7Buy Price: §a§l$§a"+ FriendlyFormat.format(wp.buyprice));
                                        lores.set(2,
                                                (wp.command == null) ? (
                                                        "§c▌  §7Sell Price: §c§l$§c"+ FriendlyFormat.format(wp.sellprice)) :
                                                "Selling unavailable");
                            } else {
                                if (initBuy + newPriceBuy > initBuy) {
                                    lores.set(0,
                                            "§a▌ §7Buy Price: §a§l$§a"+ FriendlyFormat.format(wp.buyprice) + "  §4§l" + (
                                                    (wp.buyprice > b4buy) ? "⇧": "⇩") + "  §4+§l$§4" +
                                            FriendlyFormat.format(Math.abs(newPriceBuy)));
                                } else {
                                    lores.set(0,
                                            "§a▌ §7Buy Price: §a§l$§a" + FriendlyFormat.format(wp.buyprice) + "  §2§l" + (
                                                    (wp.buyprice > b4buy) ? "⇧": "⇩") + "  §2-§l$§2"  +
                                            FriendlyFormat.format(Math.abs(newPriceBuy)));
                                }
                                if (wp.command == null)
                                    if (initSell + newPriceSell > initSell) {
                                        lores.set(2,
                                                "§c▌ §7Sell Price: §c§l$§c"+ FriendlyFormat.format(wp.sellprice) + "  §2§l"  + (
                                                        (wp.sellprice > b4sell) ? "⇧": "⇩") + "  §2+§l$§2"  +
                                                FriendlyFormat.format(Math.abs(newPriceSell)));
                                    } else {
                                        lores.set(2,
                                                "§c▌ §7Sell Price: §c§l$§c" + FriendlyFormat.format(wp.sellprice) + "  §4§l"  + (
                                                        (wp.sellprice > b4sell) ? "⇧": "⇩") + "  §4-§l$§4"  +
                                                FriendlyFormat.format(Math.abs(newPriceSell)));
                                    }
                                if (wp.available == 0) {
                                    lores.set(0, "§a▌ §7Buy Price: §4⊠  Out of Stock ⊠");
                                } else if (wp.available == wp.maxamount) {
                                    lores.set(2, "§c▌ §7Sell Price: §4⊠  Stock is Full ⊠");
                                }
                            }
                            lor = lores;
                            meta.setLore(lor);
                        } else {
                            meta.setLore(lor);
                        }
                        item.setItemMeta(meta);
                        mp.inv.setItem(slot, item);
                    }
                }
                for (ItemWrapper wrp : c.orderedList) {
                    if (wrp.id == wp.id) {
                        wrp.available = wp.available;
                        ItemStack item = wrp.handle;
                        ItemMeta meta = item.getItemMeta();
                        List<String> lores = meta.getLore();
                        if (lor == null) {
                            lores.set(6, "§9▥ Total Stock: §" + colorAmount(wp.available, wp.maxamount) + wp.available + "/" +
                                    wp.maxamount);
                            if (wp.available == wp.maxamount / 2) {
                                lores.set(0, "§a▌ §7Buy Price: §a§l$§a"+ FriendlyFormat.format(wp.buyprice));
                                        lores.set(2,
                                                (wp.command == null) ? (
                                                        "§c▌ §7Sell Price: §c§l$§c"+ FriendlyFormat.format(wp.sellprice)) :
                                                "§7Selling unavailable");
                            } else {
                                if (initBuy + newPriceBuy > initBuy) {
                                    lores.set(0,
                                            "§a▌ §7Buy Price: §a§l$§a"+ FriendlyFormat.format(wp.buyprice) + "  §4§l"  + (
                                                    (wp.buyprice > b4buy) ? "⇧": "⇩") + "  §4+§l$§4"  +
                                            FriendlyFormat.format(Math.abs(newPriceBuy)));
                                } else {
                                    lores.set(0,
                                            "§a▌ §7Buy Price: §a§l$§a"+ FriendlyFormat.format(wp.buyprice) + "  §2§l"  + (
                                                    (wp.buyprice > b4buy) ? "⇧": "⇩") + "  §2-§l$§2"  +
                                            FriendlyFormat.format(Math.abs(newPriceBuy)));
                                }
                                if (wp.command == null)
                                    if (initSell + newPriceSell > initSell) {
                                        lores.set(2,
                                                "§c▌ §7Sell Price: §c§l$§c" + FriendlyFormat.format(wp.sellprice) + "  §2§l"  + (
                                                        (wp.sellprice > b4sell) ? "⇧": "⇩") + "  §2+§l$§2"  +
                                                FriendlyFormat.format(Math.abs(newPriceSell)));
                                    } else {
                                        lores.set(2,
                                                "§c▌ §7Sell Price: §c§l$§c" + FriendlyFormat.format(wp.sellprice) + "  §4§l"  + (
                                                        (wp.sellprice > b4sell) ? "⇧": "⇩") + "  §4-§l$§4"  +
                                                FriendlyFormat.format(Math.abs(newPriceSell)));
                                    }
                                if (wp.available == 0) {
                                    lores.set(0, "§a▌ §7Buy Price: §4⊠  Out of Stock ⊠");
                                } else if (wp.available == wp.maxamount) {
                                    lores.set(2, "§c▌ §7Sell Price: §4⊠  Stock is Full ⊠");
                                }
                            }
                            lor = lores;
                            meta.setLore(lor);
                        } else {
                            meta.setLore(lor);
                        }
                        item.setItemMeta(meta);
                    }
                }
            }

            public static double round(double value, int places) {
                if (places < 0)
                    throw new IllegalArgumentException();
                BigDecimal bd = BigDecimal.valueOf(value);
                bd = bd.setScale(places, 6);
                return bd.doubleValue();
            }

            private char colorAmount(int available, int initialamount) {
                if (percentage(available, initialamount) >= 70.0D)
                    return 'a';
                if (percentage(available, initialamount) < 70.0D && percentage(available, initialamount) >= 40.0D)
                    return 'e';
                return 'c';
            }

            public double percentage(int amount, int whole) {
                return amount / whole * 100.0D;
            }

            private synchronized void sell(ItemWrapper wrp, Player p, int amount, BuyCase cass) {
                if (wrp.available >= wrp.maxamount) {
                    p.sendMessage("§cThis item is fully stocked.");
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                    p.closeInventory();
                    return;
                }
                int finalamount = amount;
                if (wrp.available + finalamount > wrp.maxamount)
                    finalamount = wrp.maxamount - wrp.available;
                int sold = removeAll(p, wrp.handle, finalamount);
                if (sold == 0) {
                    p.closeInventory();
                    p.sendMessage("§cYou do not have this item in your inventory to sell.");
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                    return;
                }
                double price = sold * wrp.sellprice;
                wrp.available += sold;
                echoCall(wrp, cass.c, false);
                Stocks.econ.depositPlayer((OfflinePlayer)p, price);
                p.sendMessage("§aSold " + sold + "x " + ChatColor.stripColor(wrp.handle.getItemMeta().getDisplayName()) +
                        " for $" + FriendlyFormat.format(price) + ".");
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
                MenuPage mp = InventoryManager.createCatMap(p, cass.c);
                updatePage(p, mp, cass.pageholder);
            }

            private synchronized void buy(ItemWrapper wrp, Player p, int amount, BuyCase cass) {
                if (wrp.command != null) {
                    if (amount > wrp.available)
                        amount = wrp.available;
                    double d = amount * wrp.buyprice;
                    if (!Stocks.econ.has((OfflinePlayer)p, d)) {
                        p.closeInventory();
                        p.sendMessage("§cYou do not have enough money to buy this item.");
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                        return;
                    }
                    wrp.available -= amount;
                    echoCall(wrp, cass.c, true);
                    Stocks.econ.withdrawPlayer((OfflinePlayer)p, d);
                    p.sendMessage("§aBought " + amount + "x " + ChatColor.stripColor(wrp.handle.getItemMeta().getDisplayName()) +
                            " for $" + FriendlyFormat.format(d) + ".");
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(),
                            wrp.command.replaceAll("<player>", p.getName()).replaceAll("<amount>", String.valueOf(amount)));
                    MenuPage menuPage = InventoryManager.createCatMap(p, cass.c);
                    updatePage(p, menuPage, cass.pageholder);
                    return;
                }
                if (wrp.available == 0) {
                    p.sendMessage("§cThis item is unavailable at the moment, you have to wait");
                    p.sendMessage("§c either until somebody sells this item to the shop");
                    p.sendMessage("§c or or until the shop refills itself.");
                    p.closeInventory();
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                    return;
                }
                int finalamount = amount;
                if (p.getInventory().firstEmpty() == -1) {
                    int howMany = howMany(p, wrp.handle);
                    if (howMany == 0) {
                        p.sendMessage("§cYour inventory is full.");
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                        p.closeInventory();
                        return;
                    }
                    finalamount = howMany;
                }
                if (finalamount > wrp.available)
                    finalamount = wrp.available;
                double price = finalamount * wrp.buyprice;
                if (!Stocks.econ.has((OfflinePlayer)p, price)) {
                    p.closeInventory();
                    p.sendMessage("§cYou do not have enough money to buy this item.");
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7F, 2.0F);
                    return;
                }
                Stocks.econ.withdrawPlayer((OfflinePlayer)p, price);
                wrp.available -= finalamount;
                echoCall(wrp, cass.c, true);
                p.sendMessage("§aBought " + finalamount + "x " + ChatColor.stripColor(wrp.handle.getItemMeta().getDisplayName()) +
                        " for $" + FriendlyFormat.format(price) + ".");
                p.getInventory().addItem(new ItemStack[] { new ItemStack(wrp.handle.getType(), finalamount, wrp.handle.getDurability()) });
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                MenuPage mp = InventoryManager.createCatMap(p, cass.c);
                updatePage(p, mp, cass.pageholder);
            }

            private void changeLore(ItemStack ch, double price, boolean buy) {
                ItemMeta meta = ch.getItemMeta();
                meta.setLore(Arrays.asList(new String[] { " ", buy ? ("§a▌ §7Price: §a§l$§a"+ FriendlyFormat.format(price)) : (
                        "§c▌ §7Sell Price: §c§l$§c"+ FriendlyFormat.format(price)) }));
                        ch.setItemMeta(meta);
  }

                private int howMany(Player p, ItemStack it) {
                    int am = 0;
                    for (int i = 0; i < p.getInventory().getSize(); i++) {
                        ItemStack item = p.getInventory().getItem(i);
                        if (item != null &&
                                !item.getItemMeta().hasDisplayName() &&
                                item.getType() == it.getType() && it.getDurability() == item.getDurability())
                            am += 64 - item.getAmount();
                    }
                    return am;
                }

                private int howManyInv(Player p, ItemStack it) {
                    int am = 0;
                    for (int i = 0; i < p.getInventory().getSize(); i++) {
                        ItemStack item = p.getInventory().getItem(i);
                        if (item != null &&
                                !item.getItemMeta().hasDisplayName() &&
                                item.getType() == it.getType() && it.getDurability() == item.getDurability())
                            am += item.getAmount();
                    }
                    return am;
                }

                private int removeAll(Player p, ItemStack it, int reach) {
                    int am = 0;
                    int target = reach;
                    for (int i = 0; i < p.getInventory().getSize() &&
                            target > 0; i++) {
                        ItemStack item = p.getInventory().getItem(i);
                        if (item != null &&
                                !item.getItemMeta().hasDisplayName() &&
                                item.getType() == it.getType() && item.getDurability() == it.getDurability()) {
                            if (item.getAmount() > target) {
                                am += item.getAmount() - target;
                                item.setAmount(item.getAmount() - target);
                                return target;
                            }
                            target -= item.getAmount();
                            am += item.getAmount();
                            p.getInventory().setItem(i, null);
                        }
                    }
                    return am;
                }

                @EventHandler
                public void onDrag(InventoryDragEvent event) {
                    if (event.getView().getTitle().equals(InventoryManager.getTitle()))
                        event.setCancelled(true);
                }

                public static Category getCategory(String name) {
                    for (Category c : ConfigManager.cat) {
                        if (c.display.getItemMeta().getDisplayName().equals(name))
                            return c;
                    }
                    return null;
                }

                @EventHandler
                public void onClose(InventoryCloseEvent event) {
                    PlayerManager.cases.remove(event.getPlayer().getUniqueId());
                    PlayerManager.pages.remove(event.getPlayer().getUniqueId());
                }
            }
