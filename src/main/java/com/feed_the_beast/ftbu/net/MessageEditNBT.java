package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.MessageToClient;
import com.feed_the_beast.ftbl.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbu.FTBU;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * @author LatvianModder
 */
public class MessageEditNBT extends MessageToClient<MessageEditNBT>
{
	private NBTTagCompound info, mainNbt;

	public MessageEditNBT()
	{
	}

	public MessageEditNBT(NBTTagCompound i, NBTTagCompound nbt)
	{
		info = i;
		mainNbt = nbt;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NET;
	}

	@Override
	public void fromBytes(ByteBuf io)
	{
		info = ByteBufUtils.readTag(io);
		mainNbt = ByteBufUtils.readTag(io);
	}

	@Override
	public void toBytes(ByteBuf io)
	{
		ByteBufUtils.writeTag(io, info);
		ByteBufUtils.writeTag(io, mainNbt);
	}

	@Override
	public void onMessage(MessageEditNBT m, EntityPlayer player)
	{
		FTBU.PROXY.openNBTEditorGui(m.info, m.mainNbt);
	}
}