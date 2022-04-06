package me.gamendecat.stocks.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.Containers;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class TitleUpdater {
    private static Method getHandle;

    private static Field activeContainerField;

    private static Field windowIdField;

    private static Field playerConnectionField;

    private static Constructor<?> chatMessageConstructor;

    static {
        try {
            getHandle = CraftPlayer.class.getMethod("getHandle");
            chatMessageConstructor = ChatMessage.class.getConstructor(new Class[] { String.class, Object[].class });
            Class<?> nmsPlayer = EntityHuman.class;
            Class<?> nmsPlayer2 = EntityPlayer.class;
            activeContainerField = nmsPlayer.getDeclaredField("bV");
            Class<?> inv = Container.class;
            windowIdField = inv.getDeclaredField("j");
            playerConnectionField = nmsPlayer2.getDeclaredField("b");
        } catch (NoSuchMethodException|SecurityException|NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void update(Player p, String title) {
        //todo maybe change here idk
        if (p.getOpenInventory().getTitle().equalsIgnoreCase("container.crafting"))
            return;
        try {
            Object handle = getHandle.invoke(p, new Object[0]);
            Object message = chatMessageConstructor.newInstance(new Object[] { title, new Object[0] });
            Object container = activeContainerField.get(handle);
            Object windowId = windowIdField.get(container);
            PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow((int) windowId, Containers.f, (IChatBaseComponent) message);
            Object playerConnection = playerConnectionField.get(handle);
            ((PlayerConnection) playerConnection).a(packet);
        } catch (IllegalArgumentException|IllegalAccessException|InstantiationException|java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
        }
        p.updateInventory();
    }
}
