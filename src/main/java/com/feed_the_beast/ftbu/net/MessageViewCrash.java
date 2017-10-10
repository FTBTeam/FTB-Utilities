package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.gui.GuiViewCrash;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MessageViewCrash extends MessageToClient<MessageViewCrash>
{
	private Collection<String> text;

	public MessageViewCrash()
	{
	}

	public MessageViewCrash(List<String> l)
	{
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
		data.writeCollection(text, DataOut.STRING);
	}

	@Override
	public void readData(DataIn data)
	{
		text = data.readCollection(DataIn.STRING);
	}

	@Override
	public void onMessage(MessageViewCrash m, EntityPlayer player)
	{
		new GuiViewCrash(m.text).openGui();
	}
}