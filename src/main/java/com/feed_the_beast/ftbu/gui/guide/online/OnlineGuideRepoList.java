package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbu.gui.guide.Guide;
import com.feed_the_beast.ftbu.gui.guide.GuideRepoList;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by PC on 17.07.2016.
 */
public class OnlineGuideRepoList extends GuideRepoList
{
    public static final String URL = "http://guides.latmod.com/repos.json";
    public static final OnlineGuideRepoList INSTANCE = new OnlineGuideRepoList();

    @Override
    protected void onReload(Map<GuideType, List<Guide>> m) throws Exception
    {
        JsonObject o = new LMConnection(RequestMethod.SIMPLE_GET, URL).connect().asJson().getAsJsonObject();

        for(GuideType t : GuideType.values())
        {
            if(o.has(t.group))
            {
                List<Guide> list = new ArrayList<>();

                for(Map.Entry<String, JsonElement> entry : o.get(t.group).getAsJsonObject().entrySet())
                {
                    try
                    {
                        String url = entry.getValue().getAsString();
                        JsonObject o1 = new LMConnection(RequestMethod.SIMPLE_GET, url + "/info.json").connect().asJson().getAsJsonObject();
                        list.add(new OnlineGuide(t, url, o1));
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }

                if(!list.isEmpty())
                {
                    m.put(t, list);
                }
            }
        }
    }
}