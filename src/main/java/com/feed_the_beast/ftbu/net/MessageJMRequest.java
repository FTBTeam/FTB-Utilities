package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class MessageJMRequest extends MessageToServer<MessageJMRequest>
{
	private int centerX, centerZ;

	public MessageJMRequest()
	{
	}

	public MessageJMRequest(int cx, int cz)
	{
		centerX = cx;
		centerZ = cz;
	}

	public MessageJMRequest(Entity entity)
	{
		this(MathUtils.chunk(entity.posX), MathUtils.chunk(entity.posZ));
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeInt(centerX);
		data.writeInt(centerZ);
	}

	@Override
	public void readData(DataIn data)
	{
		centerX = data.readInt();
		centerZ = data.readInt();
	}

	@Override
	public void onMessage(MessageJMRequest m, EntityPlayer player)
	{
		new MessageJMUpdate(m.centerX - GuiConfigs.CHUNK_SELECTOR_TILES_GUI2, m.centerZ - GuiConfigs.CHUNK_SELECTOR_TILES_GUI2, player).sendTo(player);
	}
}