package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideRepoList;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;

import java.util.List;

/**
 * Created by LatvianModder on 17.07.2016.
 */
public class OnlineGuideRepoList extends GuideRepoList
{
    static final OnlineGuideRepoList INSTANCE = new OnlineGuideRepoList();
    private static final String URL = "http://guides.latmod.com/repos.json";

    private OnlineGuideRepoList()
    {
    }

    @Override
    protected void onReload(List<Guide> guides) throws Exception
    {
        for(JsonElement e : new LMConnection(RequestMethod.SIMPLE_GET, URL).connect().asJson().getAsJsonArray())
        {
            try
            {
                JsonObject o = e.getAsJsonObject();
                String url = o.get("url").getAsString();
                JsonElement json = new LMConnection(RequestMethod.SIMPLE_GET, url + "/info.json").connect().asJson().getAsJsonObject();
                Guide g = new OnlineGuide(o.get("id").getAsString(), GuideType.getFromString(o.get("type").getAsString()), url);
                g.fromJson(json);
                guides.add(g);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        System.out.println("Online Guides loaded: " + guides);
    }
}