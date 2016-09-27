package com.feed_the_beast.ftbu.gui.guide.local;

import com.feed_the_beast.ftbl.gui.GuiInfo;
import com.feed_the_beast.ftbl.lib.info.ButtonInfoPage;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.util.LMJsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.gui.guide.GuideRepoList;
import com.feed_the_beast.ftbu.gui.guide.GuideType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 17.07.2016.
 */
@SideOnly(Side.CLIENT)
public class LocalGuideRepoList extends GuideRepoList
{
    static final LocalGuideRepoList INSTANCE = new LocalGuideRepoList();

    private LocalGuideRepoList()
    {
    }

    @Override
    protected void onReload(InfoPage infoPage) throws Exception
    {
        List<LocalGuide> guides = new ArrayList<>();

        LMUtils.DEV_LOGGER.info("Reloading guides...");

        for(String domain : Minecraft.getMinecraft().getResourceManager().getResourceDomains())
        {
            try
            {
                IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(domain, "guide/info.json"));
                JsonElement infoFile = LMJsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

                if(infoFile.isJsonObject())
                {
                    JsonObject o = infoFile.getAsJsonObject();
                    LocalGuide g = new LocalGuide(domain, o.has("type") ? GuideType.getFromString(o.get("type").getAsString()) : GuideType.OTHER);
                    g.fromJson(infoFile);
                    guides.add(g);
                }

                LMUtils.DEV_LOGGER.info("Guide found in domain '" + domain + "'");
            }
            catch(Exception ex)
            {
            }
        }

        for(LocalGuide guide : guides)
        {
            InfoPage page = new InfoPage(guide.getName())
            {
                @Override
                @SideOnly(Side.CLIENT)
                public ButtonInfoPage createButton(GuiInfo gui)
                {
                    return new ButtonInfoPage(gui, this, guide.getIcon());
                }
            };
            page.setTitle(new TextComponentString(guide.getDisplayName()));
            page.println("Yo");
            infoPage.addSub(page);
        }
    }
}