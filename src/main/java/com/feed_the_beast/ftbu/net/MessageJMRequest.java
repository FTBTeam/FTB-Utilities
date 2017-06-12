package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.gui.misc.GuiConfigs;
import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.net.MessageToServer;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import io.netty.buffer.ByteBuf;
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
	public void fromBytes(ByteBuf io)
	{
		centerX = io.readInt();
		centerZ = io.readInt();
	}

	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeInt(centerX);
		io.writeInt(centerZ);
	}

	@Override
	public void onMessage(MessageJMRequest m, EntityPlayer player)
	{
		new MessageJMUpdate(m.centerX - GuiConfigs.CHUNK_SELECTOR_TILES_GUI2, m.centerZ - GuiConfigs.CHUNK_SELECTOR_TILES_GUI2, player).sendTo(player);
	}
}