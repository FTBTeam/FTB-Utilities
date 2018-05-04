package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.gui.GuiViewCrash;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MessageViewCrashResponse extends MessageToClient
{
	private String name;
	private Collection<String> text;

	public MessageViewCrashResponse()
	{
	}

	public MessageViewCrashResponse(String n, List<String> l)
	{
		name = n;
		text = l;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.FILES;
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
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		new GuiViewCrash(name, text).openGui();
	}
}