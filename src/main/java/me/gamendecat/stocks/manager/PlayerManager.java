package me.gamendecat.stocks.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.gamendecat.stocks.wrappers.BuyCase;
import me.gamendecat.stocks.wrappers.MenuPage;

public class PlayerManager {
    public static Map<UUID, BuyCase> cases = Collections.synchronizedMap(new HashMap<>());

    public static Map<UUID, MenuPage> pages = Collections.synchronizedMap(new HashMap<>());
}
