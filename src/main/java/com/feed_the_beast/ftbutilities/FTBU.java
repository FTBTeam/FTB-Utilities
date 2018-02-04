package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.FTBLibFinals;
import com.feed_the_beast.ftblib.lib.ATHelper;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import com.feed_the_beast.ftbutilities.ranks.CommandOverride;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.ICommand;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = FTBUFinals.MOD_ID, name = FTBUFinals.MOD_NAME, version = FTBUFinals.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.10,)", dependencies = "required-after:" + FTBLibFinals.MOD_ID)
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU INST;

	@SidedProxy(serverSide = "com.feed_the_beast.ftbutilities.FTBUCommon", clientSide = "com.feed_the_beast.ftbutilities.client.FTBUClient")
	public static FTBUCommon PROXY;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
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
			ServerCommandManager manager = (ServerCommandManager) Universe.get().server.getCommandManager();
			List<ICommand> commands = new ArrayList<>(manager.getCommands().values());
			ATHelper.getCommandSet(manager).clear();
			manager.getCommands().clear();

			for (ICommand command : commands)
			{
				/*if (command instanceof ForgeCommand)
				{
					command = new CommandForgeOverride((ForgeCommand) c);
				}*/

				manager.registerCommand(new CommandOverride(command));
			}

			FTBUFinals.LOGGER.info("Overridden " + manager.getCommands().size() + " commands");
		}

		Ranks.generateExampleFiles();
	}
}