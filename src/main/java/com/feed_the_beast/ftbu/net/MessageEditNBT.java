package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftbu.gui.GuiEditNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

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

		if (CommonUtils.DEV_ENV)
		{
			CommonUtils.DEV_LOGGER.info("Editing NBT: " + mainNbt);
		}
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUNetHandler.NBTEDIT;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeNBT(info);
		data.writeNBT(mainNbt);
	}

	@Override
	public void readData(DataIn data)
	{
		info = data.readNBT();
		mainNbt = data.readNBT();
	}

	@Override
	public void onMessage(MessageEditNBT m, EntityPlayer player)
	{
		new GuiEditNBT(m.info, m.mainNbt).openGuiLater();
	}
}