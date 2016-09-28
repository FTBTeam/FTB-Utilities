package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibAddon;
import com.feed_the_beast.ftbl.api.OptionalServerModID;

/**
 * Created by LatvianModder on 20.09.2016.
 */
public class FTBLibIntegration
{
    @FTBLibAddon
    public static FTBLibAPI API;

    @OptionalServerModID
    public static final String MOD_ID = FTBUFinals.MOD_ID;
}