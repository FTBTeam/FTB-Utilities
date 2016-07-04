package com.feed_the_beast.ftbu.gui.guide;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.FinalIDObject;
import com.latmod.lib.IIDObject;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import com.latmod.lib.util.LMUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LatvianModder on 06.05.2016.
 */
public class GuideInfo extends FinalIDObject
{
    public static class Version implements IIDObject
    {
        public final int index;
        public final String ID;
        public final String url;
        public final List<String> changelog;

        public Version(int idx, JsonArray a)
        {
            index = idx;
            ID = a.get(0).getAsString();
            url = a.get(1).getAsString();

            List<String> l = new ArrayList<>();

            for(JsonElement e : a.get(2).getAsJsonArray())
            {
                l.add(e.getAsString());
            }

            changelog = l;
        }

        @Override
        public int hashCode()
        {
            return index;
        }

        @Override
        public String toString()
        {
            return ID;
        }

        @Override
        public boolean equals(Object o)
        {
            if(o instanceof Version)
            {
                return o.hashCode() == index && o.toString().equals(ID);
            }

            return LMUtils.getID(o).equals(ID);
        }

        @Nonnull
        @Override
        public String getID()
        {
            return ID;
        }
    }

    public final boolean isLocal;
    public final String name;
    public final List<String> authors;
    public final Map<String, Version> versions;
    public final Version latest_version;
    public final String base_url;
    public final String logo;
    public final String guide_url;

    public GuideInfo(String id, boolean local, String url) throws Exception
    {
        super(id);

        JsonObject o = new LMConnection(RequestMethod.SIMPLE_GET, url).connect().asJson().getAsJsonObject();

        isLocal = local;
        name = o.get("name").getAsString();

        List<String> l = new ArrayList<>();

        for(JsonElement e : o.get("authors").getAsJsonArray())
        {
            l.add(e.getAsString());
        }

        authors = Collections.unmodifiableList(l);

        Map<String, Version> l1 = new LinkedHashMap<>();

        JsonArray a = o.get("versions").getAsJsonArray();
        for(int i = 0; i < a.size(); i++)
        {
            Version v = new Version(i, a.get(i).getAsJsonArray());
            l1.put(v.getID(), v);
        }

        versions = Collections.unmodifiableMap(l1);

        latest_version = versions.get(o.get("version").getAsString());

        base_url = url.indexOf('/') > 0 ? url.substring(0, url.lastIndexOf('/')) : null;

        logo = o.get("logo").getAsString();
        guide_url = o.get("guide_url").getAsString();
    }
}