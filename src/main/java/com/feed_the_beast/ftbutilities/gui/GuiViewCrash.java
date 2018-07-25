package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.SimpleButton;
import com.feed_the_beast.ftblib.lib.gui.TextField;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.io.HttpDataReader;
import com.feed_the_beast.ftblib.lib.io.RequestMethod;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.net.MessageViewCrashDelete;
import com.google.gson.JsonElement;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiViewCrash extends GuiBase
{
	public class ThreadUploadCrash extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				File urlFile = new File(CommonUtils.folderLocal, "ftbutilities/uploaded_crash_reports/crash-" + name.text[0] + ".txt");
				String url = DataReader.get(urlFile).safeString();

				if (url.isEmpty())
				{
					URL hastebinURL = new URL("https://hastebin.com/documents");
					JsonElement json = DataReader.get(hastebinURL, RequestMethod.POST, DataReader.TEXT, new HttpDataReader.HttpDataOutput.StringOutput(text), ClientUtils.MC.getProxy()).json();

					if (json.isJsonObject() && json.getAsJsonObject().has("key"))
					{
						url = "https://hastebin.com/" + json.getAsJsonObject().get("key").getAsString() + ".md";
						FileUtils.saveSafe(urlFile, url);
					}
				}

				if (!url.isEmpty())
				{
					ITextComponent link = new TextComponentTranslation("click_here");
					link.getStyle().setColor(TextFormatting.GOLD);
					link.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(url)));
					link.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
					ClientUtils.MC.player.sendMessage(new TextComponentTranslation("ftbutilities.lang.uploaded_crash", link));
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private final List<String> text;
	private final TextField name;
	private final Panel textPanel;
	private final PanelScrollBar scrollH, scrollV;
	private final Button close, upload, delete, reset;

	public GuiViewCrash(String n, Collection<String> l)
	{
		name = new TextField(this, n);
		name.setPosAndSize(8, 12, 0, 20);
		text = new ArrayList<>(l);

		textPanel = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				for (String s : text)
				{
					add(new TextField(this, StringUtils.fixTabs(s, 2), Widget.UNICODE));
				}
			}

			@Override
			public void alignWidgets()
			{
				int wi = 0;

				for (Widget w : widgets)
				{
					w.setX(2);
					wi = Math.max(w.width, wi);
				}

				scrollH.setMaxValue(wi + 4);
				scrollV.setMaxValue(align(WidgetLayout.VERTICAL));
			}

			@Override
			public Icon getIcon()
			{
				return getTheme().getContainerSlot();
			}
		};

		textPanel.setPos(9, 33);
		textPanel.setUnicode(true);

		scrollH = new PanelScrollBar(this, PanelScrollBar.Plane.HORIZONTAL, textPanel);
		scrollH.setCanAlwaysScroll(true);
		scrollH.setCanAlwaysScrollPlane(false);
		scrollH.setScrollStep(30);

		scrollV = new PanelScrollBar(this, textPanel);
		scrollV.setCanAlwaysScroll(true);
		scrollV.setCanAlwaysScrollPlane(false);
		scrollV.setScrollStep(30);

		close = new SimpleButton(this, I18n.format("gui.close"), GuiIcons.CLOSE, (widget, button) -> widget.getGui().closeGui())
		{
			@Override
			public Icon getIcon()
			{
				return getButtonBackground().withBorder(-2).combineWith(super.getIcon());
			}
		};

		upload = new SimpleButton(this, I18n.format("ftbutilities.lang.upload_crash"), GuiIcons.UP, (widget, button) ->
		{
			new ThreadUploadCrash().start();
			widget.getGui().closeGui(false);
		})
		{
			@Override
			public Icon getIcon()
			{
				return getButtonBackground().withBorder(-2).combineWith(super.getIcon());
			}
		};

		delete = new SimpleButton(this, I18n.format("selectServer.delete"), GuiIcons.REMOVE, (widget, button) -> openYesNo(I18n.format("delete_item", name.text[0]), "", () -> new MessageViewCrashDelete(name.text[0]).sendToServer()))
		{
			@Override
			public Icon getIcon()
			{
				return getButtonBackground().withBorder(-2).combineWith(super.getIcon());
			}
		};

		reset = new SimpleButton(this, "", Icon.EMPTY, (widget, button) ->
		{
			scrollH.setValue(0);
			scrollV.setValue(0);
		});
	}

	@Override
	public boolean onInit()
	{
		return setFullscreen();
	}

	@Override
	public void addWidgets()
	{
		add(textPanel);
		add(scrollH);
		add(scrollV);
		add(close);
		add(upload);
		add(delete);
		add(reset);
		add(name);
	}

	@Override
	public void alignWidgets()
	{
		close.setPos(width - 24, 8);
		upload.setPos(width - 48, 8);
		delete.setPos(width - 72, 8);
		reset.setPos(width - 24, height - 24);
		scrollH.setPosAndSize(8, height - 24, width - 32, 16);
		scrollV.setPosAndSize(width - 24, 32, 16, height - 56);
		textPanel.setSize(scrollH.width - 2, scrollV.height - 2);
		textPanel.alignWidgets();
	}
}