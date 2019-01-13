package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.SimpleButton;
import com.feed_the_beast.ftblib.lib.gui.TextField;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.io.HttpDataReader;
import com.feed_the_beast.ftblib.lib.io.RequestMethod;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.net.MessageViewCrashDelete;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.io.File;
import java.net.URL;
import java.util.Collection;

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
				File urlFile = new File(Minecraft.getMinecraft().gameDir, "local/ftbutilities/uploaded_crash_reports/crash-" + name.text[0] + ".txt");
				String url = DataReader.get(urlFile).safeString();

				if (url.isEmpty())
				{
					URL hastebinURL = new URL("https://hastebin.com/documents");
					String outText = StringUtils.unformatted(StringJoiner.with('\n').joinStrings(text.text));
					JsonElement json = DataReader.get(hastebinURL, RequestMethod.POST, DataReader.TEXT, new HttpDataReader.HttpDataOutput.StringOutput(outText), Minecraft.getMinecraft().getProxy()).json();

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
					Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("ftbutilities.lang.uploaded_crash", link));
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private final TextField name;
	private final TextField text;
	private final Panel textPanel;
	private final PanelScrollBar scrollH, scrollV;
	private final Button close, upload, delete, reset;

	public GuiViewCrash(String n, Collection<String> l)
	{
		name = new TextField(this).setText(n);
		name.setPos(8, 12);

		textPanel = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				add(text);
			}

			@Override
			public void alignWidgets()
			{
				scrollH.setMaxValue(text.width + 4);
				scrollV.setMaxValue(text.height);
			}

			@Override
			public void drawBackground(Theme theme, int x, int y, int w, int h)
			{
				theme.drawContainerSlot(x, y, w, h);
			}
		};

		text = new TextField(textPanel);
		text.setX(2);
		text.addFlags(Theme.UNICODE);
		text.setText(StringUtils.fixTabs(String.join("\n", l), 2));

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

		close = new SimpleButton(this, I18n.format("gui.close"), GuiIcons.CLOSE, (widget, button) -> widget.getGui().closeGui());

		upload = new SimpleButton(this, I18n.format("ftbutilities.lang.upload_crash"), GuiIcons.UP, (widget, button) ->
		{
			new ThreadUploadCrash().start();
			widget.getGui().closeGui(false);
		});

		delete = new SimpleButton(this, I18n.format("selectServer.delete"), GuiIcons.REMOVE, (widget, button) -> openYesNo(I18n.format("delete_item", name.text[0]), "", () -> new MessageViewCrashDelete(name.text[0]).sendToServer()));

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
		close.setPosAndSize(width - 24, 8, 20, 20);
		upload.setPosAndSize(width - 48, 8, 20, 20);
		delete.setPosAndSize(width - 72, 8, 20, 20);
		reset.setPos(width - 24, height - 24);
		scrollH.setPosAndSize(8, height - 24, width - 32, 16);
		scrollV.setPosAndSize(width - 24, 32, 16, height - 56);
		textPanel.setSize(scrollH.width - 2, scrollV.height - 2);
		textPanel.alignWidgets();
	}
}