package com.feed_the_beast.ftbutilities.cmd.chunks;

import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftbutilities.FTBULang;
import com.feed_the_beast.ftbutilities.FTBUPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
public class CmdUnloadAll extends CmdBase
{
	public CmdUnloadAll()
	{
		super("unload_all", Level.ALL);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (ClaimedChunks.instance == null)
		{
			throw FTBLibLang.FEATURE_DISABLED.commandError();
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		checkArgs(sender, args, 1);

		ForgePlayer p;

		if (args.length >= 2)
		{
			if (!PermissionAPI.hasPermission(player, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS))
			{
				throw FTBLibLang.COMMAND_PERMISSION.commandError();
			}

			p = getForgePlayer(sender, args[1]);
		}
		else
		{
			p = getForgePlayer(player);
		}

		boolean allDimensions = parseBoolean(args[0]);
		int currentDim = sender.getEntityWorld().provider.getDimension();

		for (ClaimedChunk chunk : ClaimedChunks.instance.getTeamChunks(p.getTeam()))
		{
			if (!allDimensions || currentDim == chunk.getPos().dim)
			{
				chunk.setLoaded(false);
			}
		}

		FTBULang.CHUNKS_UNLOADED_FOR.sendMessage(sender, p.getName());
	}
}