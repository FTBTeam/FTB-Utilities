package com.feed_the_beast.ftbutilities.cmd.chunks;

import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.command.TextComponentHelper;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;

/**
 * @author LatvianModder
 */
public class CmdUnclaimAll extends CmdBase
{
	public CmdUnclaimAll()
	{
		super("unclaim_all", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, LIST_TRUE_FALSE);
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 1;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (ClaimedChunks.instance == null)
		{
			throw FTBLibLang.FEATURE_DISABLED_SERVER.commandError();
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		checkArgs(sender, args, 1);

		ForgePlayer p;

		if (args.length >= 2)
		{
			if (!PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS))
			{
				throw FTBLibLang.COMMAND_PERMISSION.commandError();
			}

			p = getForgePlayer(sender, args[1]);
		}
		else
		{
			p = getForgePlayer(player);
		}

		if (p.hasTeam())
		{
			boolean allDimensions = args.length == 0 || parseBoolean(args[0]);
			ClaimedChunks.instance.unclaimAllChunks(p.team, allDimensions ? OptionalInt.empty() : OptionalInt.of(player.dimension));
			Notification.of(FTBUtilitiesNotifications.UNCLAIMED_ALL, TextComponentHelper.createComponentTranslation(player, FTBUtilities.MOD_ID + ".lang.chunks.unclaimed_all")).send(server, player);
		}
	}
}