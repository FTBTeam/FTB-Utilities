package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import com.feed_the_beast.ftbu.ranks.CmdOverride;
import com.feed_the_beast.ftbu.ranks.FTBUPermissionHandler;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.util.backups.Backups;
import net.minecraft.command.ICommand;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = FTBUFinals.MOD_ID, name = FTBUFinals.MOD_NAME, version = FTBUFinals.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.10,)", dependencies = "required-after:ftbl")
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU INST;

	@SidedProxy(serverSide = "com.feed_the_beast.ftbu.FTBUCommon", clientSide = "com.feed_the_beast.ftbu.client.FTBUClient")
	public static FTBUCommon PROXY;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		FTBUConfig.sync();

		if (FTBUConfig.ranks.enabled)
		{
			PermissionAPI.setPermissionHandler(FTBUPermissionHandler.INSTANCE);
		}

		PROXY.preInit();
	}

	@Mod.EventHandler
	public void onInit(FMLInitializationEvent event)
	{
		PROXY.init();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		PROXY.postInit();
	}

	@Mod.EventHandler
	public void onServerStarted(FMLServerStartedEvent event)
	{
		Backups.INSTANCE.init();
		Ranks.CMD_PERMISSION_NODES.clear();

		if (FTBUConfig.ranks.override_commands)
		{
			ServerCommandManager manager = (ServerCommandManager) ServerUtils.getServer().getCommandManager();
			List<ICommand> commands = new ArrayList<>(manager.getCommands().values());
			ServerUtils.getCommandSet(manager).clear();
			manager.getCommands().clear();

			for (ICommand command : commands)
			{
				manager.registerCommand(new CmdOverride(command, "command." + command.getName()));
			}

			FTBUFinals.LOGGER.info("Overridden " + manager.getCommands().size() + " commands");
		}

		Ranks.generateExampleFiles();
	}
}