package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.ServerInfoPage;
import com.feed_the_beast.ftbu.gui.guide.Guides;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class MessageServerInfo extends MessageToClient<MessageServerInfo>
{
	private JsonObject json;

	public MessageServerInfo()
	{
	}

	public MessageServerInfo(EntityPlayerMP player)
	{
		json = ServerInfoPage.getPage(player);
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeJson(json);
	}

	@Override
	public void readData(DataIn data)
	{
		json = data.readJson().getAsJsonObject();
	}

	@Override
	public void onMessage(MessageServerInfo m, EntityPlayer player)
	{
		Guides.SERVER_INFO_PAGE.fromJson(m.json);
		Guides.refresh();
	}
}