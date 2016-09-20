package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibAddon;
import com.feed_the_beast.ftbl.api.IFTBLibAddon;
import com.feed_the_beast.ftbu.client.FTBUActions;
import com.feed_the_beast.ftbu.handlers.sync.SyncBadges;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 20.09.2016.
 */
@FTBLibAddon
public class FTBLibIntegration implements IFTBLibAddon
{
    public static FTBLibAPI API;

    @Override
    public void onFTBLibLoaded(FTBLibAPI api)
    {
        API = api;
        API.getRegistries().syncedData().register(new ResourceLocation(FTBUFinals.MOD_ID, "badges"), new SyncBadges());
        //API.getRegistries().syncedData().register(new ResourceLocation(FTBUFinals.MOD_ID, "config"), new SyncConfig());

        FTBUActions.init(api.getRegistries());
    }
}