package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.io.File;

public class CmdInv extends CmdTreeBase
{
	public static class CmdView extends CmdBase
	{
		public CmdView()
		{
			super("view", Level.OP);
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index)
		{
			return index == 0;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			checkArgs(sender, args, 1);
			EntityPlayerMP self = getCommandSenderAsPlayer(sender);
			EntityPlayerMP other = CommandUtils.getForgePlayer(sender, args[0]).getCommandPlayer(sender);
			self.displayGUIChest(new InvSeeInventory(other));
		}
	}

	public static class CmdSave extends CmdBase
	{
		public CmdSave()
		{
			super("save", Level.OP);
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index)
		{
			return index == 0;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			checkArgs(sender, args, 2);
			EntityPlayerMP player = CommandUtils.getForgePlayer(sender, args[0]).getCommandPlayer(sender);
			File file = new File(CommonUtils.folderLocal, "ftbutilities/playerinvs/" + StringUtils.fromUUID(player.getGameProfile().getId()) + "_" + args[1].toLowerCase() + ".dat");

			try
			{
				NBTTagCompound tag = new NBTTagCompound();
				writeItemsToNBT(player.inventory, tag, "Inventory");
				/*IInventory baubles = InvUtils.getBaubles(ep);

				if (baubles != null)
				{
					writeItemsToNBT(baubles, tag, "Baubles");
				}*/

				FileUtils.writeNBT(file, tag);
			}
			catch (Exception ex)
			{
				if (FTBLibConfig.debugging.print_more_errors)
				{
					ex.printStackTrace();
				}

				throw FTBLib.error(sender, "error", ex.toString());
			}
		}

		private static void writeItemsToNBT(IInventory inv, NBTTagCompound compound, String s)
		{
			NBTTagList nbttaglist = new NBTTagList();

			for (int i = 0; i < inv.getSizeInventory(); ++i)
			{
				ItemStack is = inv.getStackInSlot(i);

				if (!is.isEmpty())
				{
					NBTTagCompound nbttagcompound = is.serializeNBT();
					nbttagcompound.setInteger("Slot", i);
					nbttaglist.appendTag(nbttagcompound);
				}
			}

			compound.setTag(s, nbttaglist);
		}
	}

	public static class CmdLoad extends CmdBase
	{
		public CmdLoad()
		{
			super("load", Level.OP);
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index)
		{
			return index == 0;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			checkArgs(sender, args, 2);
			EntityPlayerMP player = CommandUtils.getForgePlayer(sender, args[0]).getCommandPlayer(sender);
			File file = new File(CommonUtils.folderLocal, "ftbutilities/playerinvs/" + StringUtils.fromUUID(player.getGameProfile().getId()) + "_" + args[1].toLowerCase() + ".dat");

			try
			{
				NBTTagCompound tag = FileUtils.readNBT(file);
				readItemsFromNBT(player.inventory, tag, "Inventory");
				/*IInventory baubles = InvUtils.getBaubles(ep);

				if (baubles != null)
				{
					readItemsFromNBT(baubles, tag, "Baubles");
				}*/
			}
			catch (Exception ex)
			{
				if (FTBLibConfig.debugging.print_more_errors)
				{
					ex.printStackTrace();
				}

				throw FTBLib.error(sender, "error", ex.toString());
			}
		}

		private static void readItemsFromNBT(@Nullable IInventory inv, NBTTagCompound compound, String s)
		{
			if (inv == null)
			{
				return;
			}

			NBTTagList nbttaglist = compound.getTagList(s, 10);

			for (int i = 0; i < nbttaglist.tagCount(); ++i)
			{
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound.getInteger("Slot");

				if (j >= 0 && j < inv.getSizeInventory())
				{
					inv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
				}
			}
		}
	}

	public static class CmdList extends CmdBase
	{
		public CmdList()
		{
			super("list", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
		}
	}

	public CmdInv()
	{
		super("inv");
		addSubcommand(new CmdView());
		addSubcommand(new CmdSave());
		addSubcommand(new CmdLoad());
		//addSubcommand(new CmdList());
		addSubcommand(new CmdTreeHelp(this));
	}
}