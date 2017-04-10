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
        String id = FTBUFinals.MOD_ID + ".login";
        reg.addConfig(id, "enable_motd", ENABLE_MOTD);
        reg.addConfig(id, "enable_starting_items", ENABLE_STARTING_ITEMS);
        reg.addConfig(id, "motd", MOTD);
        reg.addConfig(id, "starting_items", STARTING_ITEMS);
    }
}