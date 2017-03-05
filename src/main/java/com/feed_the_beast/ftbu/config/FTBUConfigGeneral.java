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
        reg.addConfig(FTBUFinals.MOD_ID, "general.auto_restart", AUTO_RESTART);
        reg.addConfig(FTBUFinals.MOD_ID, "general.restart_timer", RESTART_TIMER).setInfo("Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "4 - 1 Day", "168 - 1 Week", "720 - 1 Month");
        reg.addConfig(FTBUFinals.MOD_ID, "general.server_info.difficulty", SERVER_INFO_DIFFICULTY);
        reg.addConfig(FTBUFinals.MOD_ID, "general.server_info.mode", SERVER_INFO_MODE);
        reg.addConfig(FTBUFinals.MOD_ID, "general.server_info.admin_quick_access", SERVER_INFO_ADMIN_QUICK_ACCESS);
        reg.addConfig(FTBUFinals.MOD_ID, "general.chat.substitute_prefix", CHAT_SUBSTITUTE_PREFIX);
        reg.addConfig(FTBUFinals.MOD_ID, "general.chat.substitute_list", CHAT_SUBSTITUTES);
    }
}