package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigRanks
{
    public static final PropertyBool ENABLED = new PropertyBool(false);
    public static final PropertyBool OVERRIDE_CHAT = new PropertyBool(true);
    public static final PropertyBool OVERRIDE_COMMANDS = new PropertyBool(true);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfig(FTBUFinals.MOD_ID, "ranks.enabled", ENABLED);
        reg.addConfig(FTBUFinals.MOD_ID, "ranks.override_chat", OVERRIDE_CHAT);
        reg.addConfig(FTBUFinals.MOD_ID, "ranks.override_commands", OVERRIDE_COMMANDS);
    }
}