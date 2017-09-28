package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.guide.ClientGuideEvent;
import com.feed_the_beast.ftbu.api.guide.GuideFormat;
import com.feed_the_beast.ftbu.api.guide.GuideType;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.IGuideTextLine;
import com.feed_the_beast.ftbu.api.guide.SpecialGuideButton;
import com.feed_the_beast.ftbu.gui.guide.GuiGuide;
import com.feed_the_beast.ftbu.gui.guide.GuideContentsLine;
import com.feed_the_beast.ftbu.gui.guide.GuideHrLine;
import com.feed_the_beast.ftbu.gui.guide.GuideListLine;
import com.feed_the_beast.ftbu.gui.guide.GuidePage;
import com.feed_the_beast.ftbu.gui.guide.GuideTextLineString;
import com.feed_the_beast.ftbu.gui.guide.GuideTitlePage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public enum Guides implements IResourceManagerReloadListener
{
	INSTANCE;

	private static final IGuidePage INFO_PAGE = new GuidePage("guides").addSpecialButton(new SpecialGuideButton(GuiLang.REFRESH.textComponent(), GuiIcons.REFRESH, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftbc refresh_guide")));

	private static boolean isReloading = false;
	private static Thread reloadingThread = null;
	private static GuiGuide cachedGui = null;
	public static final Map<String, String> SUBSTITUTE_CACHE = new HashMap<>();
	private static final Function<String, String> SUBSTITUTE_FUNCTION = k -> replaceSubstitutes(StringUtils.translate(k).replace('\u201C', '\"').replace('\u201D', '\"').replace("\"", "\\\""));

	public static void setShouldReload()
	{
		cachedGui = null;
	}

	public static void refresh()
	{
		GuiHelper.playClickSound();
		setShouldReload();
		OPEN_GUI.run();
	}

	public static final Runnable OPEN_GUI = () ->
	{
		if (cachedGui == null)
		{
			if (!isReloading)
			{
				isReloading = true;

				new GuiLoading()
				{
					@Override
					public void startLoading()
					{
						reloadingThread = new Thread(() ->
						{
							INSTANCE.onResourceManagerReload(ClientUtils.MC.getResourceManager());
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
						cachedGui.openGuiLater();
					}
				}.openGuiLater();
			}
		}
		else if (!isReloading)
		{
			cachedGui.openGuiLater();
		}
	};

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		FTBUFinals.LOGGER.info("Reloading guides...");
		INFO_PAGE.clear();
		INFO_PAGE.setTitle(new TextComponentTranslation("sidebar_button.ftbu.guide"));

		List<GuideTitlePage> guides = new ArrayList<>();
		SUBSTITUTE_CACHE.clear();

		for (String domain : resourceManager.getResourceDomains())
		{
			try
			{
				IResource resource = resourceManager.getResource(new ResourceLocation(domain, "guide/guide.json"));
				JsonElement infoFile = JsonUtils.fromJson(new InputStreamReader(resource.getInputStream()));

				if (infoFile.isJsonObject())
				{
					JsonObject json = infoFile.getAsJsonObject();
					GuideType type = json.has("type") ? GuideType.NAME_MAP.get(json.get("type").getAsString()) : GuideType.OTHER;
					GuideFormat format = json.has("format") ? GuideFormat.NAME_MAP.get(json.get("format").getAsString()) : GuideFormat.JSON;

					GuideTitlePage guide = new GuideTitlePage(new GuidePage(domain), type, format);

					if (guide.format == GuideFormat.UNSUPPORTED)
					{
						guide.page.println(FTBULang.GUIDE_UNSUPPORTED_FORMAT.textComponent());
					}
					else
					{
						guide.page.setTitle(new TextComponentTranslation(domain + ".guide"));

						if (json.has("icon"))
						{
							guide.page.setIcon(Icon.getIcon(json.get("icon")));
						}
						else
						{
							guide.page.setIcon(Icon.getIcon(domain + ":textures/icon.png"));
						}
						if (json.has("button"))
						{
							guide.page.addSpecialButton(new SpecialGuideButton(json.get("button").getAsJsonObject()));
						}
						else if (json.has("buttons"))
						{
							for (JsonElement element : json.get("buttons").getAsJsonArray())
							{
								guide.page.addSpecialButton(new SpecialGuideButton(element.getAsJsonObject()));
							}
						}

						for (JsonElement element : json.get("authors").getAsJsonArray())
						{
							guide.authors.add(element.getAsString());
						}

						if (json.has("guide_authors"))
						{
							for (JsonElement element : json.get("guide_authors").getAsJsonArray())
							{
								guide.guideAuthors.add(element.getAsString());
							}
						}
						else
						{
							guide.guideAuthors.addAll(guide.authors);
						}

						loadTree(resourceManager, domain, guide.page, guide.format, "guide");

						if (guide.page.getChildren().size() > 0)
						{
							guide.page.println(new GuideHrLine(1, Color4I.NONE));
							guide.page.println(new GuideContentsLine(guide.page));
						}
					}

					guides.add(guide);
				}
			}
			catch (Exception ex)
			{
				//CommonUtils.DEV_LOGGER.info("Error while loading guide from domain '" + domain + "'");

				if (!(ex instanceof FileNotFoundException))
				{
					ex.printStackTrace();
				}
			}
		}

		Map<String, GuideTitlePage> eventMap = new HashMap<>();
		new ClientGuideEvent(eventMap, resourceManager, modid ->
		{
			GuideTitlePage page = new GuideTitlePage(new GuidePage(modid), GuideType.MOD, GuideFormat.CUSTOM);
			ModContainer mod = Loader.instance().getIndexedModList().get(modid);

			if (mod != null)
			{
				page.authors.addAll(mod.getMetadata().authorList);

				if (!mod.getMetadata().description.isEmpty())
				{
					for (String s : mod.getMetadata().description.split("\n"))
					{
						page.page.println(s);
					}
				}

				page.page.setTitle(new TextComponentString(mod.getName()));
			}

			return page;
		}).post();

		guides.addAll(eventMap.values());

		for (GuideTitlePage guide : guides)
		{
			INFO_PAGE.addSub(guide.page);
		}

		INFO_PAGE.cleanup();
		INFO_PAGE.sort(false);
	}

	private static boolean validChar(char c)
	{
		return c == '_' || c == '.' || c == '{' || c == '}' || StringUtils.isTextChar(c, true);
	}

	private static void loadTree(IResourceManager resourceManager, String domain, IGuidePage page, GuideFormat format, String parentDir) throws Exception
	{
		List<String> text = Collections.emptyList();
		try
		{
			switch (format)
			{
				case JSON:
					text = Collections.singletonList(replaceSubstitutes(StringUtils.readString(resourceManager.getResource(new ResourceLocation(domain, parentDir + "/index.json")).getInputStream())).replace("\\$", "$"));
					for (JsonElement e : JsonUtils.fromJson(text.get(0)).getAsJsonArray())
					{
						page.println(page.createLine(e));
					}
					break;
				case MD:
					text = StringUtils.readStringList(resourceManager.getResource(new ResourceLocation(domain, parentDir + "/README.md")).getInputStream());
					for (String s : text)
					{
						//FIXME: Support more than just text
						page.println(s);
					}
					break;
			}
		}
		catch (Exception ex)
		{
			if (!(ex instanceof FileNotFoundException))
			{
				page.getTitle().getStyle().setColor(TextFormatting.RED);
				page.println("Error:");
				page.println(ex);

				if (!text.isEmpty())
				{
					page.println(null);
					page.println("Source:");
					List<IGuideTextLine> lines = new ArrayList<>();

					for (String s : text)
					{
						for (String s1 : s.split("\\r?\\n"))
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
			for (JsonElement e : JsonUtils.fromJson(new InputStreamReader(resourceManager.getResource(new ResourceLocation(domain, parentDir + "/pages.json")).getInputStream())).getAsJsonArray())
			{
				IGuidePage page1;

				if (e.isJsonObject())
				{
					JsonObject o = e.getAsJsonObject();
					page1 = page.getSub(o.get("id").getAsString());

					if (o.has("icon"))
					{
						page1.setIcon(Icon.getIcon(o.get("icon")));
					}
					if (o.has("button"))
					{
						page1.addSpecialButton(new SpecialGuideButton(o.get("button").getAsJsonObject()));
					}
					if (o.has("lang"))
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
		catch (Exception ex)
		{
			if (CommonUtils.DEV_ENV && !(ex instanceof FileNotFoundException))
			{
				ex.printStackTrace();
			}
		}
	}

	private static String replaceSubstitutes(CharSequence text)
	{
		StringBuilder builder = new StringBuilder();
		StringBuilder keyBuilder = new StringBuilder();

		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);

			if (keyBuilder.length() > 0)
			{
				if (validChar(c))
				{
					keyBuilder.append(c);
				}
				else
				{
					String value = SUBSTITUTE_CACHE.computeIfAbsent(keyBuilder.substring(1), SUBSTITUTE_FUNCTION);

					//TODO: Add special values

					builder.append(value);

					if (c != '$')
					{
						builder.append(c);
					}

					keyBuilder.setLength(0);
				}
			}
			else if (c == '$' && (i == 0 || (text.charAt(i - 1) != '\\' && text.charAt(i - 1) != '/')))
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