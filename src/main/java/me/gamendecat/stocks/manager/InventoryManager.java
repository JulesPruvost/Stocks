package me.gamendecat.stocks.manager;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.gamendecat.stocks.Stocks;
import me.gamendecat.stocks.config.Category;
import me.gamendecat.stocks.config.ConfigManager;
import me.gamendecat.stocks.utils.FriendlyFormat;
import me.gamendecat.stocks.utils.ib;
import me.gamendecat.stocks.wrappers.ItemWrapper;
import me.gamendecat.stocks.wrappers.MenuPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryManager {
    public static Inventory inv;
    public static String title;

    public static Inventory ex;

    private static int[] a = new int[] {
            0, 1, 7, 8,
            9, 17,
            36, 44,
            45, 46,
            52, 53 };

    private static int[] c = new int[] {
            2, 3, 4, 5, 6,
            18, 26,
            27, 35, 47,
            48, 49, 50, 51 };

    public static String getTitle() {
        return title;
    }

    public static void setup() {
        title = "§lMarket";
        inv = Bukkit.createInventory(null, 54, title);
                ex = Bukkit.createInventory(null, 54, "§a");
        byte b;
        int j, arrayOfInt[];
        for (j = (arrayOfInt = a).length, b = 0; b < j; ) {
            int k = arrayOfInt[b];
            ex.setItem(k, (ItemStack)ib.mat(Material.GRAY_STAINED_GLASS_PANE).name(" "));
            b++;
        }
        for (j = (arrayOfInt = c).length, b = 0; b < j; ) {
            int k = arrayOfInt[b];
            ex.setItem(k, (ItemStack)ib.mat(Material.BLACK_STAINED_GLASS_PANE).name(" "));
            b++;
        }
        List<Category> list = ConfigManager.cat;
        for (Category c : list) {
            inv.setItem(c.slot, c.display);
            c.orderedList = Lists.newArrayList(c.items.values());
            Collections.sort(c.orderedList, new Comparator<ItemWrapper>() {
                public int compare(ItemWrapper o1, ItemWrapper o2) {
                    if (o1.buyprice < o2.buyprice)
                        return -1;
                    if (o1.buyprice == o2.buyprice)
                        return o1.handle.getItemMeta().getDisplayName().compareTo(o2.handle.getItemMeta().getDisplayName());
                    return 1;
                }
            });
        }
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null)
                inv.setItem(i, (ItemStack)ib.mat(Material.GRAY_STAINED_GLASS_PANE).name(" "));
        }
    }

    public static MenuPage createCatMap(Player p, Category c) {
        Inventory inv = Bukkit.createInventory(null, 54, "§n" + ChatColor.stripColor(c.name) + " - 1/" + c.pages);
                inv.setContents(ex.getContents());
        ib ib = me.gamendecat.stocks.utils.ib.mat(Material.PLAYER_HEAD).name("§a§nYour stats").lore(new String[] { "§a▌ §7Wallet: §a§l$§a"+ FriendlyFormat.format(Stocks.econ.getBalance(p)) });
                setHeadSkin((ItemStack)ib, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM2ZTk0ZjZjMzRhMzU0NjVmY2U0YTkwZjJlMjU5NzYzODllYjk3MDlhMTIyNzM1NzRmZjcwZmQ0ZGFhNjg1MiJ9fX0=");
        inv.setItem(8, (ItemStack)ib);
        MenuPage mp = new MenuPage(c, inv);
        if (c.pages > 1)
            inv.setItem(50, (ItemStack) ib.mat(Material.LIME_STAINED_GLASS_PANE).name("§a§lNext Page §7【§fPage 1/" + c.pages + "§7】").lore(new String[] { "§7Click to go to next page" }));
        int j = 10;
        for (int i = 0; i < c.orderedList.size(); i++) {
            ItemWrapper iw = c.orderedList.get(i);
            ItemStack item = iw.handle;
            if (j == 44)
                break;
            if ((j + 1) % 9 == 0)
                j += 2;
            inv.setItem(j, item);
            mp.openID.put(Integer.valueOf(iw.id), Integer.valueOf(j));
            j++;
        }
        p.openInventory(inv);
        PlayerManager.pages.put(p.getUniqueId(), mp);
        return mp;
  }

        /*public static ItemStack setHeadSkin(ItemStack paramItemStack, String paramString) {
            SkullMeta itemMeta = (SkullMeta) paramItemStack.getItemMeta();

            try {
                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), paramString);
                PropertyMap map = gameProfile.getProperties();
                Property property = new Property("textures", paramString);
                property.getClass().getMethod("put", new Class[] { Object.class, Object.class }).invoke(map, new Object[] { "textures", property });
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, gameProfile);

            } catch (NoSuchMethodException|IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchFieldException classNotFoundException) {
                return paramItemStack;
            }
            paramItemStack.setItemMeta(itemMeta);
            return paramItemStack;
        }
         */

    public static ItemStack setHeadSkin(ItemStack itemStack, String url) {

        if (url.isEmpty()) return itemStack;

        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            ex.printStackTrace();
        }

        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}

