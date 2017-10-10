package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class MessageRequestServerInfo extends MessageToServer<MessageRequestServerInfo>
{
	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.GENERAL;
	}

	@Override
	public boolean hasData()
	{
		return false;
	}

	@Override
	public void onMessage(MessageRequestServerInfo m, EntityPlayer player)
	{
		new MessageServerInfo((EntityPlayerMP) player).sendTo(player);
	}
}