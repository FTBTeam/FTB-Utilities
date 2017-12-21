package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.gui.GuiViewCrashList;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public class MessageViewCrashList extends MessageToClient<MessageViewCrashList>
{
	private Collection<String> list;

	public MessageViewCrashList()
	{
	}

	public MessageViewCrashList(File folder)
	{
		list = new ArrayList<>();

		File[] files = folder.listFiles();

		if (files != null)
		{
			for (File f : files)
			{
				if (f.isFile() && f.getName().endsWith(".txt") && f.getName().startsWith("crash-"))
				{
					list.add(f.getName().replace("crash-", "").replace(".txt", ""));
				}
			}
		}
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.VIEW_CRASH;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeCollection(list, DataOut.STRING);
	}

	@Override
	public void readData(DataIn data)
	{
		list = data.readCollection(DataIn.STRING);
	}

	@Override
	public void onMessage(MessageViewCrashList m, EntityPlayer player)
	{
		new GuiViewCrashList(m.list).openGui();
	}
}