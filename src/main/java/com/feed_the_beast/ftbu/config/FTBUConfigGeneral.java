package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.HashMap;
import java.util.Map;

public class FTBUConfigGeneral
{
    public static final PropertyBool AUTO_RESTART = new PropertyBool(false);
    public static final PropertyDouble RESTART_TIMER = new PropertyDouble(12D).setMin(0D).setMax(720);
    public static final PropertyBool SERVER_INFO_DIFFICULTY = new PropertyBool(true);
    public static final PropertyBool SERVER_INFO_MODE = new PropertyBool(true);

    private static final Map<String, ITextComponent> DEF_CHAT_SUB_MAP = new HashMap<>();

    static
    {
        DEF_CHAT_SUB_MAP.put("shrug", new TextComponentString("¯\\_(ツ)_/¯"));
    }

    public static final PropertyString CHAT_SUBSTITUTE_PREFIX = new PropertyString("!");
    public static final PropertyChatSubstituteList CHAT_SUBSTITUTES = new PropertyChatSubstituteList(DEF_CHAT_SUB_MAP);
    public static final PropertyBool ENABLE_LINKS = new PropertyBool(true);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfig(FTBUFinals.MOD_ID, "general.auto_restart", AUTO_RESTART);
        reg.addConfig(FTBUFinals.MOD_ID, "general.restart_timer", RESTART_TIMER).setInfo("Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "4 - 1 Day", "168 - 1 Week", "720 - 1 Month");
        reg.addConfig(FTBUFinals.MOD_ID, "general.server_info_difficulty", SERVER_INFO_DIFFICULTY);
        reg.addConfig(FTBUFinals.MOD_ID, "general.server_info_mode", SERVER_INFO_MODE);
        reg.addConfig(FTBUFinals.MOD_ID, "general.chat.substitute_prefix", CHAT_SUBSTITUTE_PREFIX);
        reg.addConfig(FTBUFinals.MOD_ID, "general.chat.substitutes", CHAT_SUBSTITUTES);
        reg.addConfig(FTBUFinals.MOD_ID, "general.chat.enable_links", ENABLE_LINKS);
    }
}