package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBU;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class MessageEditNBTRequest extends MessageToClient<MessageEditNBTRequest>
{
	public MessageEditNBTRequest()
	{
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NBTEDIT;
	}

	@Override
	public boolean hasData()
	{
		return false;
	}

	@Override
	public void onMessage(MessageEditNBTRequest m, EntityPlayer player)
	{
		FTBU.PROXY.editNBT();
	}
}