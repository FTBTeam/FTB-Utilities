package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyList;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraft.util.text.TextComponentString;

public class FTBUConfigGeneral
{
    public static final PropertyBool AUTO_RESTART = new PropertyBool(false);
    public static final PropertyDouble RESTART_TIMER = new PropertyDouble(12D).setMin(0D).setMax(720);
    public static final PropertyBool SERVER_INFO_DIFFICULTY = new PropertyBool(true);
    public static final PropertyBool SERVER_INFO_MODE = new PropertyBool(true);
    public static final PropertyBool SERVER_INFO_ADMIN_QUICK_ACCESS = new PropertyBool(true);
    public static final PropertyString CHAT_SUBSTITUTE_PREFIX = new PropertyString("!");
    public static final PropertyList CHAT_SUBSTITUTES = new PropertyList(PropertyChatSubstitute.ID);

    static
    {
        CHAT_SUBSTITUTES.add(new PropertyChatSubstitute("shrug", new TextComponentString("\u00AF\\_(\u30C4)_/\u00AF")));
    }

    public static void init(IFTBLibRegistry reg)
    {
        String id = FTBUFinals.MOD_ID + ".general";
        reg.addConfig(id, "auto_restart", AUTO_RESTART);
        reg.addConfig(id, "restart_timer", RESTART_TIMER);
        id = FTBUFinals.MOD_ID + ".general.server_info";
        reg.addConfig(id, "difficulty", SERVER_INFO_DIFFICULTY);
        reg.addConfig(id, "mode", SERVER_INFO_MODE);
        reg.addConfig(id, "admin_quick_access", SERVER_INFO_ADMIN_QUICK_ACCESS);
        id = FTBUFinals.MOD_ID + ".general.chat";
        reg.addConfig(id, "substitute_prefix", CHAT_SUBSTITUTE_PREFIX);
        reg.addConfig(id, "substitute_list", CHAT_SUBSTITUTES);
    }
}