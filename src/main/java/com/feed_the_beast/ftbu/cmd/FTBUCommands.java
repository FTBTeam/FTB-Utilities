package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbu.cmd.chunks.CmdChunks;
import com.feed_the_beast.ftbu.cmd.ranks.CmdRanks;
import com.feed_the_beast.ftbu.cmd.tp.CmdAdminHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdBack;
import com.feed_the_beast.ftbu.cmd.tp.CmdDelHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdDelWarp;
import com.feed_the_beast.ftbu.cmd.tp.CmdHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdSetHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdSetWarp;
import com.feed_the_beast.ftbu.cmd.tp.CmdSpawn;
import com.feed_the_beast.ftbu.cmd.tp.CmdTplast;
import com.feed_the_beast.ftbu.cmd.tp.CmdWarp;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigCommands;
import net.minecraftforge.server.command.CommandTreeBase;

/**
 * Created by LatvianModder on 09.11.2016.
 */
public class FTBUCommands
{
    public static void register(CommandTreeBase command, boolean dedi)
    {
        if(dedi)
        {
            command.addSubcommand(new CmdRestart());
        }

        if(FTBUConfigCommands.INV.getBoolean())
        {
            command.addSubcommand(new CmdInv());
        }

        if(FTBUConfigCommands.WARP.getBoolean())
        {
            command.addSubcommand(new CmdWarp());
            command.addSubcommand(new CmdSetWarp());
            command.addSubcommand(new CmdDelWarp());
        }

        if(FTBUConfigBackups.ENABLED.getBoolean())
        {
            command.addSubcommand(new CmdBackup());
        }

        if(FTBUConfigCommands.HOME.getBoolean())
        {
            command.addSubcommand(new CmdAdminHome());
            command.addSubcommand(new CmdHome());
            command.addSubcommand(new CmdSetHome());
            command.addSubcommand(new CmdDelHome());
        }

        if(FTBUConfigCommands.SERVER_INFO.getBoolean())
        {
            command.addSubcommand(new CmdServerInfo());
        }

        if(FTBUConfigCommands.TPL.getBoolean())
        {
            command.addSubcommand(new CmdTplast());
        }

        if(FTBUConfigCommands.TRASH_CAN.getBoolean())
        {
            command.addSubcommand(new CmdTrashCan());
        }

        if(FTBUConfigCommands.BACK.getBoolean())
        {
            command.addSubcommand(new CmdBack());
        }

        if(FTBUConfigCommands.SPAWN.getBoolean())
        {
            command.addSubcommand(new CmdSpawn());
        }

        if(FTBUConfigCommands.CHUNKS.getBoolean())
        {
            command.addSubcommand(new CmdChunks());
        }

        if(FTBUConfigCommands.JS.getBoolean())
        {
            command.addSubcommand(new CmdJS());
        }

        if(FTBUConfigCommands.KICKME.getBoolean())
        {
            command.addSubcommand(new CmdKickme());
        }

        if(FTBUConfigCommands.RANKS.getBoolean())
        {
            command.addSubcommand(new CmdRanks());
        }

        if(FTBUConfigCommands.VIEW_CRASH.getBoolean())
        {
            command.addSubcommand(new CmdViewCrash());
        }
    }
}