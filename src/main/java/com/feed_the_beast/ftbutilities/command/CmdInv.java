package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class CmdInv extends CmdTreeBase
{
	public static class CmdView extends CmdBase
	{
		public CmdView()
		{
			super("view", Level.OP);
		}

		@Override
		public List<String> getAliases()
		{
			return Collections.singletonList("edit");
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
			self.displayGUIChest(new InvSeeInventory(other.inventory, other));
		}
	}

	public static class CmdDisableRightClick extends CmdBase
	{
		public CmdDisableRightClick()
		{
			super("disable_right_click", Level.OP);
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			LinkedHashSet<String> list = new LinkedHashSet<>(Arrays.asList(FTBUtilitiesConfig.world.disabled_right_click_items));
			ItemStack stack = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.MAIN_HAND);
			String s = stack.getItem().getRegistryName() + (stack.getHasSubtypes() ? ("@" + stack.getMetadata()) : "");

			if (list.contains(s))
			{
				list.remove(s);
			}
			else
			{
				list.add(s);
			}

			FTBUtilitiesConfig.world.disabled_right_click_items = list.toArray(new String[0]);
			FTBUtilitiesConfig.sync();
		}
	}

	public CmdInv()
	{
		super("inv");
		addSubcommand(new CmdView());
		addSubcommand(new CmdDisableRightClick());
		addSubcommand(new CmdTreeHelp(this));
	}
}