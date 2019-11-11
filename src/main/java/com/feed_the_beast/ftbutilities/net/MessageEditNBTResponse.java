package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.BlockUtils;
import com.feed_the_beast.ftbutilities.command.CmdEditNBT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class MessageEditNBTResponse extends MessageToServer
{
	private NBTTagCompound info, mainNbt;

	public MessageEditNBTResponse()
	{
	}

	public MessageEditNBTResponse(NBTTagCompound i, NBTTagCompound nbt)
	{
		info = i;
		mainNbt = nbt;
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
	public void onMessage(EntityPlayerMP player)
	{
		if (CmdEditNBT.EDITING.get(player.getGameProfile().getId()).equals(info))
		{
			CmdEditNBT.EDITING.remove(player.getGameProfile().getId());

			switch (info.getString("type"))
			{
				case "player":
				{
					ForgePlayer player1 = Universe.get().getPlayer(info.getUniqueId("id"));

					if (player1 != null)
					{
						player1.setPlayerNBT(mainNbt);
					}

					break;
				}
				case "block":
				{
					BlockPos pos = new BlockPos(info.getInteger("x"), info.getInteger("y"), info.getInteger("z"));

					if (player.world.isBlockLoaded(pos))
					{
						TileEntity tile = player.world.getTileEntity(pos);

						if (tile != null)
						{
							mainNbt.setInteger("x", pos.getX());
							mainNbt.setInteger("y", pos.getY());
							mainNbt.setInteger("z", pos.getZ());
							mainNbt.setString("id", info.getString("id"));
							tile.readFromNBT(mainNbt);
							tile.markDirty();
							BlockUtils.notifyBlockUpdate(tile.getWorld(), pos, null);
						}
					}

					break;
				}
				case "entity":
				{
					Entity entity = player.world.getEntityByID(info.getInteger("id"));

					if (entity != null)
					{
						entity.deserializeNBT(mainNbt);

						if (entity.isEntityAlive())
						{
							player.world.updateEntityWithOptionalForce(entity, true);
						}
					}

					break;
				}
				case "item":
				{
					ItemStack stack = new ItemStack(mainNbt);
					player.setHeldItem(EnumHand.MAIN_HAND, stack.isEmpty() ? ItemStack.EMPTY : stack);
				}
			}
		}
	}
}