package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonObject;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LatvianModder on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public class OnlineGuide extends Guide
{
    private final String url;
    private final int priority;
    private ResourceLocation icon;

    public OnlineGuide(JsonObject o, String u)
    {
        super(o.get("id").getAsString(), GuideType.getFromString(o.get("type").getAsString()));
        url = u;
        priority = o.has("priority") ? o.get("priority").getAsInt() : 0;
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
    public IResourceProvider getResourceProvider()
    {
        return s -> new LMConnection(RequestMethod.FILE, url + '/' + s);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getIcon()
    {
        if(icon == null)
        {
            icon = new ResourceLocation("ftbu_guide/" + getName() + ".png");
            FTBLibClient.getDownloadImage(icon, url + "/icon.png", new ResourceLocation("textures/misc/unknown_pack.png"), null);
        }

        return icon;
    }
}