package com.feed_the_beast.ftbutilities.command.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdSet extends CmdBase
{
	public CmdSet()
	{
		super("set", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.getRankNames());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (Ranks.INSTANCE == null)
		{
			throw new CommandException("feature_disabled_server");
		}

		checkArgs(sender, args, 2);

		Rank r = Ranks.INSTANCE.getRank(args[1]);

		if (!Ranks.INSTANCE.getRankNames().contains(args[1]))
		{
			throw new CommandException("commands.ranks.not_found", args[1]);
		}

		ForgePlayer p = getForgePlayer(sender, args[0]);
		Ranks.INSTANCE.setRank(p.getId(), r);

		ITextComponent rankText = r == null ? FTBUtilities.lang(sender, "commands.ranks.none") : new TextComponentString(r.getName());
		rankText.getStyle().setColor(r == null ? TextFormatting.DARK_GRAY : TextFormatting.DARK_GREEN);
		sender.sendMessage(FTBUtilities.lang(sender, "commands.ranks.set.set", StringUtils.color(p.getDisplayName(), TextFormatting.BLUE), rankText));
	}
}
