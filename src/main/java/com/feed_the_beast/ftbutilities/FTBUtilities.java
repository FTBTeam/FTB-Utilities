package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.ATHelper;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import com.feed_the_beast.ftbutilities.ranks.CommandOverride;
import net.minecraft.command.ICommand;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(
		modid = FTBUtilities.MOD_ID,
		name = FTBUtilities.MOD_NAME,
		version = FTBUtilities.VERSION,
		acceptableRemoteVersions = "*",
		acceptedMinecraftVersions = "[1.12,)",
		dependencies = "required-after:" + FTBLib.MOD_ID
)
public class FTBUtilities
{
	public static final String MOD_ID = "ftbutilities";
	public static final String MOD_NAME = "FTB Utilities";
	public static final String VERSION = "@VERSION@";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@Mod.Instance(MOD_ID)
	public static FTBUtilities INST;

	@SidedProxy(serverSide = "com.feed_the_beast.ftbutilities.FTBUtilitiesCommon", clientSide = "com.feed_the_beast.ftbutilities.client.FTBUtilitiesClient")
	public static FTBUtilitiesCommon PROXY;

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

		if (FTBUtilitiesConfig.ranks.enabled)
		{
			ServerCommandManager manager = (ServerCommandManager) Universe.get().server.getCommandManager();
			List<ICommand> commands = new ArrayList<>(manager.getCommands().values());
			ATHelper.getCommandSet(manager).clear();
			manager.getCommands().clear();

			for (ICommand command : commands)
			{
				manager.registerCommand(CommandOverride.create(command, Node.COMMAND));
			}

			LOGGER.info("Overridden " + manager.getCommands().size() + " commands");
		}
	}
}