package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.config.ConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyItemStackList;
import com.feed_the_beast.ftbl.lib.config.PropertyTextComponentList;
import com.feed_the_beast.ftbu.FTBUFinals;

import java.util.ArrayList;

public class FTBUConfigLogin
{
    @ConfigValue(id = "login.enable_motd", file = FTBUFinals.MOD_ID)
    public static final PropertyBool ENABLE_MOTD = new PropertyBool(true);

    @ConfigValue(id = "login.enable_starting_items", file = FTBUFinals.MOD_ID)
    public static final PropertyBool ENABLE_STARTING_ITEMS = new PropertyBool(true);

    @ConfigValue(id = "login.motd", file = FTBUFinals.MOD_ID, info = "Message of the day. This will be displayed when player joins the server")
    public static final PropertyTextComponentList MOTD = new PropertyTextComponentList(new ArrayList<>());

    @ConfigValue(id = "login.starting_items", file = FTBUFinals.MOD_ID, info = {"Items to give player when he first joins the server", "Format: \"StringID Size Metadata\" or {nbt}"})
    public static final PropertyItemStackList STARTING_ITEMS = new PropertyItemStackList(new ArrayList<>());
}