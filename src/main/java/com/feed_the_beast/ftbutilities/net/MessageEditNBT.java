package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.gui.GuiEditNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageEditNBT extends MessageToClient
{
	private NBTTagCompound info, mainNbt;

	public MessageEditNBT()
	{
	}

	public MessageEditNBT(NBTTagCompound i, NBTTagCompound nbt)
	{
		info = i;
		mainNbt = nbt;

		if (FTBLibConfig.debugging.log_config_editing)
		{
			FTBUtilities.LOGGER.info("Editing NBT: " + mainNbt);
		}
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.FILES;
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
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		new GuiEditNBT(info, mainNbt).openGui();
	}
}