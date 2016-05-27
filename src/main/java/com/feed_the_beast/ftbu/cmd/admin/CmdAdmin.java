package com.feed_the_beast.ftbu.cmd.admin;

import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbl.api.cmd.CommandSubLM;
import com.feed_the_beast.ftbu.config.FTBUConfigCmd;

public class CmdAdmin extends CommandSubLM
{
    public CmdAdmin()
    {
        super(FTBUConfigCmd.name_admin.getAsString(), CommandLevel.OP);
        add(new CmdRestart());
        add(new CmdInvsee());
        add(new CmdSetWarp());
        add(new CmdDelWarp());
        add(new CmdUnclaim());
        add(new CmdUnclaimAll());
        add(new CmdBackup());
        add(new CmdUnloadAll());
        add(new CmdAdminHome());
        add(new CmdServerInfo());
    }
}