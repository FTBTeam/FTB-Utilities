package com.feed_the_beast.ftbutilities.cmd.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

/**
 * @author LatvianModder
 */
public class CmdGet extends CmdBase
{
	public CmdGet()
	{
		super("get", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 1);
		ForgePlayer p = getForgePlayer(sender, args[0]);
		Rank rank = Ranks.getRank(server, p.getProfile());
		ITextComponent name = new TextComponentString(rank.getName() + " - " + rank.getFormattedName(p.getName()));
		name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + p.getName() + " "));
		name.getStyle().setInsertion(p.getName());
		sender.sendMessage(name);
	}
}