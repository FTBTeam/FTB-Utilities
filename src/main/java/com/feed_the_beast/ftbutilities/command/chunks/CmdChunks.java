package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;

/**
 * @author LatvianModder
 */
public class CmdChunks extends CmdTreeBase
{
	public CmdChunks()
	{
		super("chunks");
		addSubcommand(new CmdClaim());
		addSubcommand(new CmdUnclaim());
		addSubcommand(new CmdLoad());
		addSubcommand(new CmdUnload());
		addSubcommand(new CmdUnclaimAll());
		addSubcommand(new CmdUnloadAll());
		addSubcommand(new CmdUnclaimEverything());
		addSubcommand(new CmdUnloadEverything());
		addSubcommand(new CmdInfo());
		addSubcommand(new CmdClaimAs());
		addSubcommand(new CmdTreeHelp(this));
	}
}