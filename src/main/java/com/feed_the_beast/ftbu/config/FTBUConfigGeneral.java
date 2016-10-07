package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigFileProvider;
import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.api.config.IConfigFileProvider;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FTBUConfigGeneral
{
    @ConfigFileProvider(FTBUFinals.MOD_ID)
    public static final IConfigFileProvider FILE = () -> new File(LMUtils.folderLocal, "ftbu/config.json");

    @ConfigValue(id = "general.auto_restart", file = FTBUFinals.MOD_ID)
    public static final PropertyBool AUTO_RESTART = new PropertyBool(false);

    @ConfigValue(id = "general.restart_timer", file = FTBUFinals.MOD_ID, info = {"Server will automatically shut down after X hours", "0 - Disabled", "0.5 - 30 minutes", "1 - 1 Hour", "4 - 1 Day", "168 - 1 Week", "720 - 1 Month"})
    public static final PropertyDouble RESTART_TIMER = new PropertyDouble(12D).setMin(0D).setMax(720);

    @ConfigValue(id = "general.ranks.enabled", file = FTBUFinals.MOD_ID)
    public static final PropertyBool RANKS_ENABLED = new PropertyBool(false);

    //@ConfigValue(id = "general.ranks.override_chat", file = FTBUFinals.MOD_ID)
    //public static final PropertyBool RANKS_OVERRIDE_CHAT = new PropertyBool(true);

    //@ConfigValue(id = "general.ranks.override_commands", file = FTBUFinals.MOD_ID)
    //public static final PropertyBool RANKS_OVERRIDE_COMMANDS = new PropertyBool(true);

    @ConfigValue(id = "general.server_info.difficulty", file = FTBUFinals.MOD_ID)
    public static final PropertyBool SERVER_INFO_DIFFICULTY = new PropertyBool(true);

    @ConfigValue(id = "general.server_info.mode", file = FTBUFinals.MOD_ID)
    public static final PropertyBool SERVER_INFO_MODE = new PropertyBool(true);

    private static final Map<String, ITextComponent> DEF_CHAT_SUB_MAP = new HashMap<>();

    static
    {
        DEF_CHAT_SUB_MAP.put("shrug", new TextComponentString("¯\\_(ツ)_/¯"));
    }

    @ConfigValue(id = "general.chat.substitute_prefix", file = FTBUFinals.MOD_ID)
    public static final PropertyString CHAT_SUBSTITUTE_PREFIX = new PropertyString("!");

    @ConfigValue(id = "general.chat.substitutes", file = FTBUFinals.MOD_ID)
    public static final PropertyChatSubstituteList CHAT_SUBSTITUTES = new PropertyChatSubstituteList(DEF_CHAT_SUB_MAP);

    @ConfigValue(id = "general.chat.enable_links", file = FTBUFinals.MOD_ID)
    public static final PropertyBool ENABLE_LINKS = new PropertyBool(true);
}