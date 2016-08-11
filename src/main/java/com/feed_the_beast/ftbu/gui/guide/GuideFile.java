package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.info.IResourceProvider;
import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.api.info.impl.InfoPageHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.FinalIDObject;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import com.latmod.lib.json.LMJsonUtils;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 06.05.2016.
 */
public class GuideFile extends FinalIDObject implements IResourceProvider
{
    public final Guide info;
    public final String resource_path;
    public final Map<String, LMConnection> resources;
    public final Map<Item, String> item_map;
    public final InfoPage guide;

    public GuideFile(Guide i, JsonObject o)
    {
        super(i.getID());
        info = i;
        resource_path = o.has("resource_path") ? o.get("resource_path").getAsString() : "";
        resources = new HashMap<>();

        if(o.has("resources"))
        {
            for(JsonElement e : o.get("resources").getAsJsonArray())
            {
                JsonObject o1 = e.getAsJsonObject();
            }
        }

        item_map = new HashMap<>();

        if(o.has("item_map"))
        {
            for(Map.Entry<String, JsonElement> e : o.get("item_map").getAsJsonObject().entrySet())
            {
                Item item = Item.REGISTRY.getObject(new ResourceLocation(e.getKey()));

                if(item != null)
                {
                    item_map.put(item, e.getValue().getAsString());
                }
            }
        }

        guide = createPage(o.get("guide").getAsJsonObject());
        guide.cleanup();
        guide.resourceProvider = this;
    }

    private InfoPage createPage(JsonObject o)
    {
        InfoPage page = new InfoPage();

        if(o.has("name"))
        {
            page.setTitle(LMJsonUtils.deserializeTextComponent(o.get("name")));
        }

        if(o.has("text"))
        {
            for(JsonElement e : o.get("text").getAsJsonArray())
            {
                page.println(InfoPageHelper.createLine(page, e));
            }
        }

        if(o.has("pages"))
        {
            for(Map.Entry<String, JsonElement> e : o.get("pages").getAsJsonObject().entrySet())
            {
                page.addSub(e.getKey(), createPage(e.getValue().getAsJsonObject()));
            }
        }

        return page;
    }

    @Override
    public LMConnection getConnection(String s)
    {
        if(s.startsWith("/"))
        {
            return resources.get(s);
        }

        return new LMConnection(RequestMethod.SIMPLE_GET, s);
    }
}