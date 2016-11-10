package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.events.RegisterFTBCommandsEvent;
import com.feed_the_beast.ftbu.cmd.chunks.CmdChunks;
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
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;

/**
 * Created by LatvianModder on 09.11.2016.
 */
public class FTBUCommands
{
    public static void register(RegisterFTBCommandsEvent event)
    {
        if(event.isDedicatedServer())
        {
            event.add(new CmdRestart());
        }

        if(FTBUConfigCommands.INV.getBoolean())
        {
            event.add(new CmdInv());
        }

        if(FTBUConfigCommands.WARP.getBoolean())
        {
            event.add(new CmdWarp());
            event.add(new CmdSetWarp());
            event.add(new CmdDelWarp());
        }

        if(FTBUConfigBackups.ENABLED.getBoolean())
        {
            event.add(new CmdBackup());
        }

        if(FTBUConfigCommands.HOME.getBoolean())
        {
            event.add(new CmdAdminHome());
            event.add(new CmdHome());
            event.add(new CmdSetHome());
            event.add(new CmdDelHome());
        }

        if(FTBUConfigCommands.SERVER_INFO.getBoolean())
        {
            event.add(new CmdServerInfo());
        }

        if(FTBUConfigCommands.TPL.getBoolean())
        {
            event.add(new CmdTplast());
        }

        if(FTBUConfigCommands.TRASH_CAN.getBoolean())
        {
            event.add(new CmdTrashCan());
        }

        if(FTBUConfigCommands.BACK.getBoolean())
        {
            event.add(new CmdBack());
        }

        if(FTBUConfigCommands.SPAWN.getBoolean())
        {
            event.add(new CmdSpawn());
        }

        if(FTBUConfigCommands.CHUNKS.getBoolean())
        {
            event.add(new CmdChunks());
        }

        if(FTBUConfigGeneral.RANKS_ENABLED.getBoolean())
        {
            event.add(new CmdGetRank());
            event.add(new CmdSetRank());
            event.add(new CmdEditRanks());
        }

        if(FTBUConfigCommands.JS.getBoolean())
        {
            event.add(new CmdJS());
        }

        if(FTBUConfigCommands.KICKME.getBoolean())
        {
            event.add(new CmdKickme());
        }
    }
}