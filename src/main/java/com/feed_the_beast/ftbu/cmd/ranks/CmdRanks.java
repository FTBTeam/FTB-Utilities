package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.lib.cmd.CmdTreeBase;

/**
 * @author LatvianModder
 */
public class CmdRanks extends CmdTreeBase
{
    public CmdRanks()
    {
        super("ranks");
        addSubcommand(new CmdAdd());
        addSubcommand(new CmdGet());
        addSubcommand(new CmdSet());
        //addSubcommand(new CmdEdit());
        addSubcommand(new CmdCheckPerm());
    }
}