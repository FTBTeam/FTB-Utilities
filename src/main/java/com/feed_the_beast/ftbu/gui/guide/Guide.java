package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.FinalIDObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LatvianModder on 06.05.2016.
 */
public abstract class Guide extends FinalIDObject
{
    public final GuideType type;
    public final String url;
    public final String name;
    public final List<String> authors;
    public final List<String> guide_authors;
    public final Map<String, InfoPage> modes;

    public Guide(GuideType t, String u, JsonObject o)
    {
        super(o.get("id").getAsString());
        type = t;
        url = u;

        name = o.get("name").getAsString();

        List<String> l = new ArrayList<>();

        for(JsonElement e : o.get("authors").getAsJsonArray())
        {
            l.add(e.getAsString());
        }

        authors = Collections.unmodifiableList(l);

        l = new ArrayList<>();

        for(JsonElement e : o.get("guide_authors").getAsJsonArray())
        {
            l.add(e.getAsString());
        }

        guide_authors = Collections.unmodifiableList(l);

        modes = new HashMap<>();

        for(JsonElement e : o.get("modes").getAsJsonArray())
        {
            modes.put(e.getAsString(), null);
        }
    }

    public abstract boolean isLocal();

    public abstract IResourceProvider getResourceProvider(String path);

    @SideOnly(Side.CLIENT)
    public abstract ResourceLocation getIcon();

    public InfoPage loadPage(String mode) throws Exception
    {
        InfoPage page = modes.get(mode);

        if(mode == null)
        {
            page = new InfoPage();
            generatePage(page);
        }

        return page;
    }

    private void generatePage(InfoPage page)
    {
    }
}