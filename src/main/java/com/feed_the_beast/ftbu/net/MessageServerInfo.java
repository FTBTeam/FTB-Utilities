package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.feed_the_beast.ftbu.ServerInfoPage;
import com.feed_the_beast.ftbu.api.guide.ServerInfoEvent;
import com.feed_the_beast.ftbu.gui.guide.Guides;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * @author LatvianModder
 */
public class MessageServerInfo extends MessageToClient<MessageServerInfo>
{
	public JsonArray mainPage;
	public JsonObject commands;
	public JsonElement serverGuide;

	public MessageServerInfo()
	{
	}

	public MessageServerInfo(EntityPlayerMP player)
	{
		long now = player.getEntityWorld().getTotalWorldTime();
		List<ITextComponent> list = ServerInfoPage.getMainPage(player, now);
		new ServerInfoEvent(player, now, list::add).post();

		mainPage = new JsonArray();

		for (ITextComponent component : list)
		{
			mainPage.add(JsonUtils.serializeTextComponent(component));
		}

		commands = ServerInfoPage.getCommands(player);
		serverGuide = ServerInfoPage.getServerGuide();
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeJson(mainPage);
		data.writeJson(commands);
		data.writeJson(serverGuide);
	}

	@Override
	public void readData(DataIn data)
	{
		mainPage = data.readJson().getAsJsonArray();
		commands = data.readJson().getAsJsonObject();
		serverGuide = data.readJson();
	}

	@Override
	public void onMessage(MessageServerInfo m, EntityPlayer player)
	{
		Guides.readServerInfoPage(m);
	}
}