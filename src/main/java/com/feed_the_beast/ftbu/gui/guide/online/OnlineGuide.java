package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbl.api.info.IImageProvider;
import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbl.lib.info.URLImageProvider;
import com.feed_the_beast.ftbl.lib.io.LMConnection;
import com.feed_the_beast.ftbl.lib.io.RequestMethod;
import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LatvianModder on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public class OnlineGuide extends Guide implements Comparable<OnlineGuide>
{
    private final String url;
    private final int priority;
    private final IImageProvider icon;

    private final IResourceProvider RESOURCE_PROVIDER = new IResourceProvider()
    {
        @Override
        public LMConnection getConnection(String s)
        {
            return new LMConnection(RequestMethod.FILE, url + '/' + s);
        }
    };

    public OnlineGuide(JsonObject o, String u)
    {
        super(o.get("id").getAsString(), GuideType.getFromString(o.get("type").getAsString()));
        url = u;
        priority = o.has("priority") ? o.get("priority").getAsInt() : 0;
        icon = new URLImageProvider(url + "/icon.png");
    }

    public String getURL()
    {
        return url;
    }

    public int getPriority()
    {
        return priority;
    }

    @Override
    public boolean isLocal()
    {
        return false;
    }

    @Override
    public IResourceProvider getResourceProvider()
    {
        return RESOURCE_PROVIDER;
    }

    @Override
    public IImageProvider getIcon()
    {
        return icon;
    }

    @Override
    public int compareTo(OnlineGuide o)
    {
        int i = Integer.compare(o.getPriority(), getPriority());

        if(i == 0)
        {
            i = getDisplayName().compareToIgnoreCase(o.getDisplayName());
        }

        return i;
    }
}