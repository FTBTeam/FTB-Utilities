package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.Constants;

public class CmdTplast extends CmdBase
{
	public CmdTplast()
	{
		super("tpl", Level.OP);
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

		if (args.length >= 3)
		{
			EntityPlayerMP player = getCommandSenderAsPlayer(sender);
			double x = parseDouble(player.posX, args[0], -30000000, 30000000, true);
			double y = parseDouble(player.posY, args[1], -30000000, 30000000, true);
			double z = parseDouble(player.posZ, args[2], -30000000, 30000000, true);
			TeleporterDimPos.of(x, y, z, player.dimension).teleport(player);
			return;
		}

		EntityPlayerMP who;
		ForgePlayer to;

		if (args.length == 1)
		{
			who = getCommandSenderAsPlayer(sender);
			to = CommandUtils.getForgePlayer(sender, args[0]);
		}
		else
		{
			who = CommandUtils.getForgePlayer(sender, args[0]).getCommandPlayer(sender);
			to = CommandUtils.getForgePlayer(sender, args[1]);
		}

		BlockDimPos p;

		if (to.isOnline())
		{
			p = new BlockDimPos(to.getCommandPlayer(sender));
		}
		else
		{
			NBTTagCompound nbt = to.getPlayerNBT();
			NBTTagList posList = nbt.getTagList("Pos", Constants.NBT.TAG_DOUBLE);
			p = new BlockDimPos(posList.getDoubleAt(0), posList.getDoubleAt(1), posList.getDoubleAt(2), nbt.getInteger("Dimension"));
		}

		p.teleporter().teleport(who);
	}
}