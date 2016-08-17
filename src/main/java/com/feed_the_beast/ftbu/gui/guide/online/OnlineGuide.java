package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by LatvianModder on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public class OnlineGuide extends Guide
{
    private final String url;
    private ResourceLocation icon;

    public OnlineGuide(String id, GuideType t, String u)
    {
        super(id, t);
        url = u;
    }

    public String getURL()
    {
        return url;
    }

    @Override
    public boolean isLocal()
    {
        return false;
    }

    @Override
    @Nonnull
    public IResourceProvider getResourceProvider()
    {
        return s -> new LMConnection(RequestMethod.FILE, url + '/' + s);
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