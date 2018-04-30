package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.handlers.FTBUClientEventHandler;
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
		return FTBUNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeInt(current);
		data.writeInt(total);
	}

	@Override
	public void readData(DataIn data)
	{
		current = data.readInt();
		total = data.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		FTBUClientEventHandler.currentBackupFile = current;
		FTBUClientEventHandler.totalBackupFiles = total;
	}
}