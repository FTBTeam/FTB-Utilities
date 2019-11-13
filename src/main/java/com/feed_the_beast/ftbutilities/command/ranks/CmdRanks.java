package com.feed_the_beast.ftbutilities.command.ranks;


import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;

/**
 * @author LatvianModder
 */
public class CmdRanks extends CmdTreeBase
{
	public CmdRanks()
	{
		super("ranks");
		addSubcommand(new CmdInfo());
		addSubcommand(new CmdCreate());
		addSubcommand(new CmdDelete());
		addSubcommand(new CmdAdd());
		addSubcommand(new CmdRemove());
		addSubcommand(new CmdGetPermission());
		addSubcommand(new CmdSetPermission());
		addSubcommand(new CmdTreeHelp(this));
	}
}