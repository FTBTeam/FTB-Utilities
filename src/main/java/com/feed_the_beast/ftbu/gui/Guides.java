package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.events.ClientGuideEvent;
import com.feed_the_beast.ftbl.api.guide.GuideFormat;
import com.feed_the_beast.ftbl.api.guide.GuideType;
import com.feed_the_beast.ftbl.api.guide.IGuideTextLine;
import com.feed_the_beast.ftbl.api.guide.SpecialGuideButton;
import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.client.ImageProvider;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiGuide;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftbl.lib.guide.GuideContentsLine;
import com.feed_the_beast.ftbl.lib.guide.GuideHrLine;
import com.feed_the_beast.ftbl.lib.guide.GuideListLine;
import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbl.lib.guide.GuideTextLineString;
import com.feed_the_beast.ftbl.lib.guide.GuideTitlePage;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum Guides implements IResourceManagerReloadListener
{
    INSTANCE;

    private static final GuidePage INFO_PAGE = new GuidePage("guides").addSpecialButton(new SpecialGuideButton(GuiLang.BUTTON_REFRESH.textComponent(), GuiIcons.REFRESH, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftbc refresh_guide")));

    private static boolean isReloading = false;
    private static Thread reloadingThread = null;
    private static GuiGuide cachedGui = null;
    public static final Map<String, String> SUBSTITUTE_CACHE = new HashMap<>();

    public static void setShouldReload()
    {
        cachedGui = null;
    }

    public static void refresh()
    {
        GuiHelper.playClickSound();
        setShouldReload();
        openGui();
    }

    public static void openGui()
    {
        if(cachedGui == null)
        {
            if(!isReloading)
            {
                isReloading = true;

                new GuiLoading()
                {
                    @Override
                    public void startLoading()
                    {
                        reloadingThread = new Thread(() ->
                        {
                            INSTANCE.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
                            isReloading = false;
                        });
                        reloadingThread.start();
                    }

                    @Override
                    public boolean isLoading()
                    {
                        return isReloading;
                    }

                    @Override
                    public void finishLoading()
                    {
                        reloadingThread = null;
                        cachedGui = new GuiGuide(INFO_PAGE);
                        cachedGui.openGui();
                    }
                }.openGui();
            }
        }
        else if(!isReloading)
        {
            cachedGui.openGui();
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        FTBUFinals.LOGGER.info("Reloading guides...");
        INFO_PAGE.clear();
        INFO_PAGE.setTitle(new TextComponentString("Guides")); //TODO: Lang

        List<GuideTitlePage> guides = new ArrayList<>();
        SUBSTITUTE_CACHE.clear();

        for(String domain : resourceManager.getResourceDomains())
        {
            try
            {
                IResource resource = resourceManager.getResource(new ResourceLocation(domain, "guide.json"));
                JsonElement infoFile = JsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

                if(infoFile.isJsonObject())
                {
                    GuideTitlePage guide = new GuideTitlePage(domain, infoFile.getAsJsonObject());
                    GuideFormat format = guide.getFormat();

                    if(format == GuideFormat.UNSUPPORTED)
                    {
                        guide.println("Unsupported format!"); //TODO: Lang
                        guide.println("Please update FTBUtilities or contact mod author!"); //TODO: Lang
                    }
                    else
                    {
                        loadTree(resourceManager, domain, guide, format, "guide");

                        if(guide.childPages.size() > 0)
                        {
                            guide.println(new GuideHrLine(1, Color4I.NONE));
                            guide.println(new GuideContentsLine(guide));
                        }
                    }

                    guides.add(guide);
                }
            }
            catch(Exception ex)
            {
                //LMUtils.DEV_LOGGER.info("Error while loading guide from domain '" + domain + "'");

                if(!(ex instanceof FileNotFoundException))
                {
                    ex.printStackTrace();
                }
            }
        }

        Map<String, GuideTitlePage> eventMap = new HashMap<>();

        MinecraftForge.EVENT_BUS.post(new ClientGuideEvent(eventMap, resourceManager, modid ->
        {
            ModContainer mod = Loader.instance().getIndexedModList().get(modid);
            if(mod == null)
            {
                return new GuideTitlePage(modid, GuideType.MOD, Collections.emptyList(), Collections.singleton("Autogenerated"));
            }
            else
            {
                return new GuideTitlePage(mod);
            }
        }));

        guides.addAll(eventMap.values());

        for(GuideTitlePage guide : guides)
        {
            INFO_PAGE.addSub(guide);
        }

        INFO_PAGE.cleanup();
        INFO_PAGE.sort(false);
    }

    private static boolean validChar(char c)
    {
        return c == '_' || c == '.' || c == '{' || c == '}' || StringUtils.isTextChar(c, true);
    }

    private static void loadTree(IResourceManager resourceManager, String domain, GuidePage page, GuideFormat format, String parentDir) throws Exception
    {
        List<String> text = Collections.emptyList();
        try
        {
            switch(format)
            {
                case JSON:
                    text = Collections.singletonList(replaceSubstitutes(StringUtils.readString(resourceManager.getResource(new ResourceLocation(domain, parentDir + "/index.json")).getInputStream())).replace("\\$", "$"));
                    for(JsonElement e : JsonUtils.fromJson(text.get(0)).getAsJsonArray())
                    {
                        page.println(page.createLine(e));
                    }
                    break;
                case MD:
                    text = StringUtils.readStringList(resourceManager.getResource(new ResourceLocation(domain, parentDir + "/README.md")).getInputStream());
                    for(String s : text)
                    {
                        //FIXME: Support more than just text
                        page.println(s);
                    }
                    break;
            }
        }
        catch(Exception ex)
        {
            if(!(ex instanceof FileNotFoundException))
            {
                page.getTitle().getStyle().setColor(TextFormatting.RED);
                page.println("Error:");
                page.println(ex);

                if(!text.isEmpty())
                {
                    page.println(null);
                    page.println("Source:");
                    List<IGuideTextLine> lines = new ArrayList<>();

                    for(String s : text)
                    {
                        for(String s1 : s.split("\\r?\\n"))
                        {
                            lines.add(new GuideTextLineString(s1));
                        }
                    }

                    page.println(new GuideListLine(lines, GuideListLine.Type.CODE, GuideListLine.Ordering.NUMBER, 0));
                }
            }
        }

        try
        {
            for(JsonElement e : JsonUtils.fromJson(new InputStreamReader(resourceManager.getResource(new ResourceLocation(domain, parentDir + "/pages.json")).getInputStream())).getAsJsonArray())
            {
                GuidePage page1;

                if(e.isJsonObject())
                {
                    JsonObject o = e.getAsJsonObject();
                    page1 = page.getSub(o.get("id").getAsString());

                    if(o.has("icon"))
                    {
                        page1.setIcon(ImageProvider.get(o.get("icon")));
                    }
                    if(o.has("button"))
                    {
                        page1.addSpecialButton(new SpecialGuideButton(o.get("button").getAsJsonObject()));
                    }
                    if(o.has("lang"))
                    {
                        page1.setTitle(new TextComponentTranslation(o.get("lang").getAsString()));
                    }
                    else
                    {
                        page1.setTitle(new TextComponentTranslation(domain + '.' + parentDir.replace('/', '.') + "." + page1.getName()));
                    }
                }
                else
                {
                    page1 = page.getSub(e.getAsString());
                    page1.setTitle(new TextComponentTranslation(domain + '.' + parentDir.replace('/', '.') + "." + page1.getName()));
                }

                loadTree(resourceManager, domain, page1, format, parentDir + "/" + page1.getName());
            }
        }
        catch(Exception ex)
        {
            if(LMUtils.DEV_ENV && !(ex instanceof FileNotFoundException))
            {
                ex.printStackTrace();
            }
        }
    }

    private static String replaceSubstitutes(CharSequence text)
    {
        StringBuilder builder = new StringBuilder();
        StringBuilder keyBuilder = new StringBuilder();

        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);

            if(keyBuilder.length() > 0)
            {
                if(validChar(c))
                {
                    keyBuilder.append(c);
                }
                else
                {
                    String value = SUBSTITUTE_CACHE.computeIfAbsent(keyBuilder.substring(1), k -> replaceSubstitutes(StringUtils.translate(k).replace('\u201C', '\"').replace('\u201D', '\"').replace("\"", "\\\"")));

                    //TODO: Add special values

                    builder.append(value);

                    if(c != '$')
                    {
                        builder.append(c);
                    }

                    keyBuilder.setLength(0);
                }
            }
            else if(c == '$' && (i < 1 || text.charAt(i - 1) != '\\'))
            {
                keyBuilder.append(c);
            }
            else
            {
                builder.append(c);
            }
        }

        return builder.toString().replace("\t", "  ");
    }
}