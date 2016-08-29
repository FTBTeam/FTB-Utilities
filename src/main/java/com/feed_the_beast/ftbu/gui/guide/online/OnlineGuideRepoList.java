package com.feed_the_beast.ftbu.gui.guide.online;

import com.feed_the_beast.ftbl.api.info.impl.ButtonInfoPage;
import com.feed_the_beast.ftbl.api.info.impl.InfoImageLine;
import com.feed_the_beast.ftbl.api.info.impl.InfoPage;
import com.feed_the_beast.ftbl.gui.GuiInfo;
import com.feed_the_beast.ftbu.gui.guide.GuideRepoList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.lib.io.LMConnection;
import com.latmod.lib.io.RequestMethod;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 17.07.2016.
 */
public class OnlineGuideRepoList extends GuideRepoList
{
    public static final OnlineGuideRepoList INSTANCE = new OnlineGuideRepoList();
    private static final String URL = "http://guides.latmod.com/repos.json";

    private OnlineGuideRepoList()
    {
    }

    @Override
    protected void onReload(InfoPage infoPage) throws Exception
    {
        List<OnlineGuide> guides = new ArrayList<>();

        for(JsonElement e : new LMConnection(RequestMethod.GET, URL).connect().asJson().getAsJsonArray())
        {
            try
            {
                JsonObject o = e.getAsJsonObject();
                String url = o.get("url").getAsString();
                JsonElement json = new LMConnection(RequestMethod.GET, url + "/info.json").connect().asJson().getAsJsonObject();
                OnlineGuide g = new OnlineGuide(o, url);
                g.fromJson(json);
                guides.add(g);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        for(final OnlineGuide guide : guides)
        {
            InfoPage page1 = new InfoPage(guide.getName())
            {
                @Override
                @SideOnly(Side.CLIENT)
                public ButtonInfoPage createButton(GuiInfo gui)
                {
                    return new ButtonInfoPage(gui, this, guide.getIcon());
                }
            };

            infoPage.addSub(page1);

            page1.setTitle(new TextComponentString(guide.getDisplayName()));
            page1.println("ID: " + guide.getName());
            page1.println("Type: " + guide.getType());
            page1.println("Authors: " + guide.getAuthors());

            if(!guide.getGuideAuthors().isEmpty())
            {
                page1.println("Guide Authors: " + guide.getGuideAuthors());
            }

            page1.println(null);
            page1.println(new InfoImageLine(guide.getIcon(), 64, 64));
        }
    }
}