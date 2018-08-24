package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;

/**
 * @author LatvianModder
 */
public class MessageViewCrash extends MessageToServer
{
	private String id;

	public MessageViewCrash()
	{
	}

	public MessageViewCrash(String s)
	{
		id = s;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.FILES;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeString(id);
	}

	@Override
	public void readData(DataIn data)
	{
		id = data.readString();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.CRASH_REPORTS_VIEW))
		{
			try
			{
				File file = new File(player.server.getDataDirectory(), "crash-reports/crash-" + id + ".txt");

				if (file.exists())
				{
					new MessageViewCrashResponse(file.getName(), DataReader.get(file).stringList()).sendTo(player);
				}
			}
			catch (Exception ex)
			{
				if (FTBLibConfig.debugging.print_more_errors)
				{
					ex.printStackTrace();
				}
			}
		}
	}
}