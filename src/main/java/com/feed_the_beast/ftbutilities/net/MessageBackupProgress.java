package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.handlers.FTBUtilitiesClientEventHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageBackupProgress extends MessageToClient
{
	public int current, total;

	public MessageBackupProgress()
	{
	}

	public MessageBackupProgress(int c, int t)
	{
		current = c;
		total = t;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeVarInt(current);
		data.writeVarInt(total);
	}

	@Override
	public void readData(DataIn data)
	{
		current = data.readVarInt();
		total = data.readVarInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		FTBUtilitiesClientEventHandler.currentBackupFile = current;
		FTBUtilitiesClientEventHandler.totalBackupFiles = total;
	}
}