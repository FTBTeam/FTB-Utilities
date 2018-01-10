package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.gui.GuiClaimedChunks;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class MessageOpenClaimedChunksGui extends MessageToClient<MessageOpenClaimedChunksGui>
{
	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.CLAIMS;
	}

	@Override
	public boolean hasData()
	{
		return false;
	}

	@Override
	public void onMessage(MessageOpenClaimedChunksGui m, EntityPlayer player)
	{
		GuiClaimedChunks.instance = new GuiClaimedChunks();
		GuiClaimedChunks.instance.openGuiLater();
	}
}