package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.gui.GuiViewCrash;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MessageViewCrash extends MessageToClient<MessageViewCrash>
{
	private String name;
	private Collection<String> text;

	public MessageViewCrash()
	{
	}

	public MessageViewCrash(String n, List<String> l)
	{
		name = n;
		text = l;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.VIEW_CRASH;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeString(name);
		data.writeCollection(text, DataOut.STRING);
	}

	@Override
	public void readData(DataIn data)
	{
		name = data.readString();
		text = data.readCollection(DataIn.STRING);
	}

	@Override
	public void onMessage(MessageViewCrash m, EntityPlayer player)
	{
		new GuiViewCrash(m.name, m.text).openGui();
	}
}