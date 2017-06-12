package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.EntityDimPos;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;

public class CmdTplast extends CmdBase
{
	public CmdTplast()
	{
		super("tpl", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(args, 1, "(<x> <y> <z>) | ([who] <player>)");

		if (args.length >= 3)
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
			double x = parseDouble(ep.posX, args[0], -30000000, 30000000, true);
			double y = parseDouble(ep.posY, args[1], -30000000, 30000000, true);
			double z = parseDouble(ep.posZ, args[2], -30000000, 30000000, true);
			ServerUtils.teleportPlayer(ep, new Vec3d(x, y, z), ep.dimension);
			return;
		}

		EntityPlayerMP who;
		IForgePlayer to;

		if (args.length == 1)
		{
			who = getCommandSenderAsPlayer(sender);
			to = getForgePlayer(args[0]);
		}
		else
		{
			who = getPlayer(server, sender, args[0]);
			to = getForgePlayer(args[1]);
		}

		BlockDimPos p;

		if (to.isOnline())
		{
			p = new EntityDimPos(to.getPlayer()).toBlockDimPos();
		}
		else
		{
			NBTTagCompound nbt = to.getPlayerNBT();
			NBTTagList posList = nbt.getTagList("Pos", Constants.NBT.TAG_DOUBLE);
			p = new EntityDimPos(new Vec3d(posList.getDoubleAt(0), posList.getDoubleAt(1), posList.getDoubleAt(2)), nbt.getInteger("Dimension")).toBlockDimPos();
		}

		ServerUtils.teleportPlayer(who, p);
	}
}