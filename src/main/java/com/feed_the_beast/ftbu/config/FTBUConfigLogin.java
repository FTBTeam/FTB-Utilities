package com.feed_the_beast.ftbu.config;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyItemStack;
import com.feed_the_beast.ftbl.lib.config.PropertyList;
import com.feed_the_beast.ftbl.lib.config.PropertyTextComponent;
import com.feed_the_beast.ftbu.FTBUFinals;

public class FTBUConfigLogin
{
    public static final PropertyBool ENABLE_MOTD = new PropertyBool(true);
    public static final PropertyBool ENABLE_STARTING_ITEMS = new PropertyBool(true);
    public static final PropertyList MOTD = new PropertyList(PropertyTextComponent.ID);
    public static final PropertyList STARTING_ITEMS = new PropertyList(PropertyItemStack.ID);

    public static void init(IFTBLibRegistry reg)
    {
        reg.addConfig(FTBUFinals.MOD_ID, "login.enable_motd", ENABLE_MOTD);
        reg.addConfig(FTBUFinals.MOD_ID, "login.enable_starting_items", ENABLE_STARTING_ITEMS);
        reg.addConfig(FTBUFinals.MOD_ID, "login.motd", MOTD).setInfo("Message of the day. This will be displayed when player joins the server");
        reg.addConfig(FTBUFinals.MOD_ID, "login.starting_items", STARTING_ITEMS).setInfo("Items to give player when he first joins the server", "Format: \"StringID Size Metadata\" or {nbt}");
    }
}