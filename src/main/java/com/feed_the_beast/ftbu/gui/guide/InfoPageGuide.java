package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.api.gui.IWidget;
import com.feed_the_beast.ftbl.api.info.IGuiInfoPage;
import com.feed_the_beast.ftbl.gui.GuiInfo;
import com.feed_the_beast.ftbl.lib.client.ITextureCoordsProvider;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.info.ButtonInfoPage;
import com.feed_the_beast.ftbl.lib.info.InfoPage;
import com.feed_the_beast.ftbl.lib.info.WrappedImageProvider;
import com.feed_the_beast.ftbu.api.guide.GuideFormat;
import com.feed_the_beast.ftbu.api.guide.GuideType;
import com.feed_the_beast.ftbu.api.guide.IGuide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LatvianModder on 02.10.2016.
 */
public class InfoPageGuide implements IGuide
{
    private class Page extends InfoPage
    {
        public Page(String id)
        {
            super(id);
        }

        @Override
        public IWidget createButton(GuiInfo gui)
        {
            return new ButtonInfoPage(gui, this, icon);
        }
    }

    private Page page;
    private GuideType type;
    private GuideFormat format;
    private int priority;
    private List<String> authors;
    private List<String> guideAuthors;
    private ITextureCoordsProvider icon;

    public InfoPageGuide(String id)
    {
        page = new Page(id);
        type = GuideType.OTHER;
        priority = 0;
        authors = Collections.emptyList();
        guideAuthors = Collections.emptyList();
        icon = GuiIcons.BOOK;
    }

    public InfoPageGuide(String id, JsonObject o)
    {
        page = new Page(id);
        page.setTitle(new TextComponentTranslation(id + ".guide"));
        type = o.has("type") ? GuideType.getFromString(o.get("type").getAsString()) : GuideType.OTHER;
        format = o.has("format") ? GuideFormat.getFromString(o.get("format").getAsString()) : GuideFormat.JSON;
        priority = o.has("priority") ? o.get("priority").getAsInt() : 0;

        List<String> l = new ArrayList<>();

        for(JsonElement e : o.get("authors").getAsJsonArray())
        {
            l.add(e.getAsString());
        }

        authors = Collections.unmodifiableList(l);

        l = new ArrayList<>();

        if(o.has("guide_authors"))
        {
            for(JsonElement e : o.get("guide_authors").getAsJsonArray())
            {
                l.add(e.getAsString());
            }
        }

        guideAuthors = Collections.unmodifiableList(l);

        icon = new WrappedImageProvider(o.has("icon") ? new ResourceLocation(o.get("icon").getAsString()) : new ResourceLocation(id, "textures/icon.png"));
    }

    @Override
    public GuideType getType()
    {
        return type;
    }

    @Override
    public GuideFormat getFormat()
    {
        return format;
    }

    @Override
    public List<String> getAuthors()
    {
        return authors;
    }

    @Override
    public List<String> getGuideAuthors()
    {
        return guideAuthors;
    }

    @Override
    public int getPriority()
    {
        return priority;
    }

    @Override
    public IGuiInfoPage getPage()
    {
        return page;
    }
}