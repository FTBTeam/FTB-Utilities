package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.icon.ItemIcon;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.guide.ClientGuideEvent;
import com.feed_the_beast.ftbu.api.guide.GuideType;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;
import com.feed_the_beast.ftbu.api.guide.IGuideTitlePage;
import com.feed_the_beast.ftbu.api.guide.SpecialGuideButton;
import com.feed_the_beast.ftbu.net.MessageRequestServerInfo;
import com.feed_the_beast.ftbu.net.MessageServerInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum Guides implements IResourceManagerReloadListener
{
	INSTANCE;

	public static final IGuidePage INFO_PAGE = new GuidePage("guides", null)
	{
		@Override
		public String getFullId()
		{
			return "";
		}
	}.addSpecialButton(new SpecialGuideButton(GuiLang.REFRESH.textComponent(null), GuiIcons.REFRESH, new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftbc refresh_guide")));

	public static final IGuidePage SERVER_INFO_PAGE = new GuidePage("server_info", INFO_PAGE)
	{
		@Override
		public GuideType getType()
		{
			return GuideType.SERVER_INFO;
		}
	}.setTitle(new TextComponentTranslation("sidebar_button." + FTBUFinals.MOD_ID + ".server_info")).setIcon(GuiIcons.BOOK_RED);

	public static String pageToOpen = "";
	private static boolean isReloading = false;
	private static Thread reloadingThread = null;
	private static GuiGuide cachedGui = null;

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

	public static void readServerInfoPage(MessageServerInfo m)
	{
		SERVER_INFO_PAGE.clear();

		if (m.serverGuide.isJsonObject())
		{
			SERVER_INFO_PAGE.fromJson(m.serverGuide.getAsJsonObject());
		}

		for (JsonElement element : m.mainPage)
		{
			ITextComponent component = JsonUtils.deserializeTextComponent(element);
			SERVER_INFO_PAGE.println(component);
		}

		IGuidePage commandPage = SERVER_INFO_PAGE.getSub("commands");

		for (MessageServerInfo.CommandInfo info : m.commands.subcommands)
		{
			addCommandTree(commandPage, info);
		}

		commandPage.sort(true);
		commandPage.setTitle(new TextComponentTranslation("commands"));
		commandPage.setIcon(ItemIcon.getItemIcon(new ItemStack(Blocks.COMMAND_BLOCK)));

		if (cachedGui != null && cachedGui.getSelectedPage() == SERVER_INFO_PAGE)
		{
			cachedGui.refreshWidgets();
		}
	}

	private static void addCommandTree(IGuidePage page, MessageServerInfo.CommandInfo info)
	{
		IGuidePage subPage = page.getSub(info.name);
		subPage.setTitle(new TextComponentString("/" + info.name));

		if (!info.info.isEmpty())
		{
			for (ITextComponent component : info.info)
			{
				subPage.println(component);
			}
		}

		if (!info.subcommands.isEmpty())
		{
			for (MessageServerInfo.CommandInfo info1 : info.subcommands)
			{
				addCommandTree(subPage, info1);
			}
		}
	}

	public static final Runnable OPEN_GUI = () ->
	{
		new MessageRequestServerInfo().sendToServer();

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
		FTBUFinals.LOGGER.info("Reloading guides..."); //LANG
		INFO_PAGE.clear();
		INFO_PAGE.setTitle(new TextComponentTranslation("sidebar_button." + FTBUFinals.MOD_ID + ".guide"));

		List<IGuideTitlePage> guides = new ArrayList<>();

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
					GuideTitlePage guide = new GuideTitlePage(domain, type);

					guide.setTitle(new TextComponentTranslation(domain + ".guide"));

					if (!json.has("icon"))
					{
						guide.setIcon(Icon.getIcon(domain + ":textures/icon.png"));
					}

					for (JsonElement element : JsonUtils.toArray(json.get("authors")))
					{
						guide.getAuthors().add(element.getAsString());
					}

					if (json.has("guide_authors"))
					{
						for (JsonElement element : JsonUtils.toArray(json.get("guide_authors")))
						{
							guide.getGuideAuthors().add(element.getAsString());
						}
					}
					else
					{
						guide.getGuideAuthors().addAll(guide.getAuthors());
					}

					loadPage(resourceManager, domain, guide, json, "guide");

					if (guide.getChildren().size() > 0)
					{
						guide.println(new GuideHrLine(1, Icon.EMPTY));
						guide.println(new GuideContentsLine(guide));
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

		Map<String, IGuideTitlePage> eventMap = new HashMap<>();
		new ClientGuideEvent(eventMap, resourceManager, modid ->
		{
			GuideTitlePage page = new GuideTitlePage(modid, GuideType.MOD);
			ModContainer mod = Loader.instance().getIndexedModList().get(modid);

			if (mod != null)
			{
				page.getAuthors().addAll(mod.getMetadata().authorList);

				if (!mod.getMetadata().description.isEmpty())
				{
					for (String s : mod.getMetadata().description.split("\n"))
					{
						page.println(s);
					}
				}

				page.setTitle(new TextComponentString(mod.getName()));
			}

			return page;
		}).post();

		guides.addAll(eventMap.values());

		for (IGuideTitlePage guide : guides)
		{
			INFO_PAGE.addSub(guide);
		}

		INFO_PAGE.cleanup();
		INFO_PAGE.addSub(SERVER_INFO_PAGE);
		INFO_PAGE.sort(false);
	}

	private static void loadPage(IResourceManager resourceManager, String domain, IGuidePage page, JsonObject json, String currentDir) throws Exception
	{
		if (json.has("button"))
		{
			page.addSpecialButton(new SpecialGuideButton(json.get("button").getAsJsonObject()));
		}
		else if (json.has("buttons"))
		{
			for (JsonElement element : JsonUtils.toArray(json.get("buttons")))
			{
				page.addSpecialButton(new SpecialGuideButton(element.getAsJsonObject()));
			}
		}

		if (json.has("name"))
		{
			page.setTitle(JsonUtils.deserializeTextComponent(json.get("name")));
		}
		else
		{
			page.setTitle(new TextComponentTranslation("guide." + page.getFullId()));
		}

		if (json.has("icon"))
		{
			page.setIcon(Icon.getIcon(json.get("icon")));
		}

		if (json.has("pages"))
		{
			for (Map.Entry<String, JsonElement> entry : json.get("pages").getAsJsonObject().entrySet())
			{
				IGuidePage sub = page.getSub(entry.getKey());

				try
				{
					loadPage(resourceManager, domain, sub, entry.getValue().getAsJsonObject(), currentDir + "/" + entry.getKey());
				}
				catch (Exception ex)
				{
					sub.println(StringUtils.color(FTBLibLang.ERROR.textComponent(null).appendText(ex.toString()), TextFormatting.RED));
					ex.printStackTrace();
				}
			}
		}

		for (JsonElement element : JsonUtils.toArray(JsonUtils.fromJson(resourceManager.getResource(new ResourceLocation(domain, currentDir + "/index.json")))))
		{
			try
			{
				page.println(page.createLine(element));
			}
			catch (Exception ex)
			{
				page.println(StringUtils.color(FTBLibLang.ERROR.textComponent(null).appendText(ex.toString()), TextFormatting.RED));
				page.println(StringUtils.color(new TextComponentString(element.toString()), TextFormatting.DARK_RED));
				ex.printStackTrace();
			}
		}
	}
}