package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import com.feed_the_beast.ftbu.config.FTBUConfigRanks;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUServerEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUTeamEventHandler;
import com.feed_the_beast.ftbu.handlers.FTBUWorldEventHandler;
import com.feed_the_beast.ftbu.net.FTBUNetHandler;
import com.feed_the_beast.ftbu.ranks.CmdOverride;
import com.feed_the_beast.ftbu.ranks.CmdTreeOverride;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.backups.Backups;
import net.minecraft.command.ICommand;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = FTBUFinals.MOD_ID, name = FTBUFinals.MOD_ID, version = "0.0.0", useMetadata = true, acceptableRemoteVersions = "*", dependencies = "required-after:ftbl")
public class FTBU
{
    @Mod.Instance(FTBUFinals.MOD_ID)
    public static FTBU INST;

    @SidedProxy(serverSide = "com.feed_the_beast.ftbu.FTBUCommon", clientSide = "com.feed_the_beast.ftbu.client.FTBUClient")
    public static FTBUCommon PROXY;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        FTBUtilitiesAPI_Impl.INSTANCE.init(event.getAsmData());
        FTBUNetHandler.init();

        MinecraftForge.EVENT_BUS.register(new FTBUPlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUWorldEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUTeamEventHandler());
        MinecraftForge.EVENT_BUS.register(new FTBUServerEventHandler());

        PROXY.preInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        FTBUPermissions.init();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event)
    {
        PROXY.postInit();
        ForgeChunkManager.setForcedChunkLoadingCallback(INST, LoadedChunkStorage.INSTANCE);
        Ranks.generateExampleFiles();
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event)
    {
        Backups.INSTANCE.init();

        if(LMUtils.DEV_ENV)
        {
            Ranks.generateExampleFiles();
        }

        if(FTBUConfigRanks.OVERRIDE_COMMANDS.getBoolean())
        {
            ServerCommandManager manager = (ServerCommandManager) LMServerUtils.getServer().getCommandManager();
            List<ICommand> commands = new ArrayList<>(LMServerUtils.getCommandSet(manager));
            LMServerUtils.getCommandSet(manager).clear();
            manager.getCommands().clear();

            for(ICommand command : commands)
            {
                if(command instanceof CommandTreeBase)
                {
                    manager.registerCommand(new CmdTreeOverride((CommandTreeBase) command, "command." + command.getCommandName()));
                }
                else
                {
                    manager.registerCommand(new CmdOverride(command, "command." + command.getCommandName()));
                }
            }

            FTBUFinals.LOGGER.info("Overridden " + manager.getCommands().size() + " commands");
        }
    }
}