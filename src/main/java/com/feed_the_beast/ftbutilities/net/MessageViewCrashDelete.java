package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;

/**
 * @author LatvianModder
 */
public class MessageViewCrashDelete extends MessageToServer
{
	private String id;

	public MessageViewCrashDelete()
	{
	}

	public MessageViewCrashDelete(String s)
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
			File folder = new File(player.server.getDataDirectory(), "crash-reports");

			if (PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.CRASH_REPORTS_DELETE))
			{
				try
				{
					File file = new File(folder, id);

					if (file.exists() && file.getParentFile().equals(folder))
					{
						file.delete();
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

			new MessageViewCrashList(folder).sendTo(player);
		}
	}
}