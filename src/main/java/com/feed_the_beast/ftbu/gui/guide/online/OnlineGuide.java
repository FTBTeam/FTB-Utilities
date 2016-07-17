package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonObject;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by PC on 17.07.2016.
 */
public class OnlineGuide extends Guide
{
    private ResourceLocation icon;

    public OnlineGuide(GuideType t, String u, JsonObject o)
    {
        super(t, u, o);
    }

    @Override
    public boolean isLocal()
    {
        return false;
    }

    @Override
    public IResourceProvider getResourceProvider(String path)
    {
        return s -> new LMConnection(RequestMethod.FILE, url + '/' + path);
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getIcon()
    {
        if(icon == null)
        {
            icon = new ResourceLocation(FTBUFinals.MOD_ID, "textures/guide/icons/" + getID() + ".png");
            FTBLibClient.getDownloadImage(icon, url + "/icon.png", new ResourceLocation("textures/misc/unknown_pack.png"), null);
        }

        return icon;
    }
}
