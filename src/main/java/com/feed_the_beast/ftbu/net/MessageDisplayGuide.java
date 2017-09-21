package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.FTBU;
import com.google.gson.JsonElement;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class MessageDisplayGuide extends MessageToClient<MessageDisplayGuide>
{
	private String id;
	private JsonElement json;

	public MessageDisplayGuide()
	{
	}

	public MessageDisplayGuide(GuidePage page)
	{
		id = page.getName();
		json = page.toJson();
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeString(id);
		data.writeJson(json);
	}

	@Override
	public void readData(DataIn data)
	{
		id = data.readString();
		json = data.readJson();
	}

	@Override
	public void onMessage(MessageDisplayGuide m, EntityPlayer player)
	{
		FTBU.PROXY.displayGuide(new GuidePage(m.id, null, m.json));
	}
}