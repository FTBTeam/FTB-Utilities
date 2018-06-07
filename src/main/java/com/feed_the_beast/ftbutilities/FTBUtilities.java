package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.ATHelper;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.SidedUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.command.FTBUtilitiesCommands;
import com.feed_the_beast.ftbutilities.data.backups.Backups;
import com.feed_the_beast.ftbutilities.ranks.CommandOverride;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod(
		modid = FTBUtilities.MOD_ID,
		name = FTBUtilities.MOD_NAME,
		version = FTBUtilities.VERSION,
		acceptableRemoteVersions = "*",
		acceptedMinecraftVersions = "[1.12,)",
		dependencies = "required-after:ftblib"
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

	public static ITextComponent lang(@Nullable ICommandSender sender, String key, Object... args)
	{
		return SidedUtils.lang(sender, MOD_ID, key, args);
	}

	public static CommandException error(@Nullable ICommandSender sender, String key, Object... args)
	{
		return CommandUtils.error(lang(sender, key, args));
	}

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
	public void onServerStarting(FMLServerStartingEvent event)
	{
		FTBUtilitiesCommands.registerCommands(event);
	}

	@Mod.EventHandler
	public void onServerStarted(FMLServerStartedEvent event)
	{
		Backups.INSTANCE.init();

		if (Ranks.isActive())
		{
			Ranks.INSTANCE.commands.clear();
			ServerCommandManager manager = (ServerCommandManager) Ranks.INSTANCE.universe.server.getCommandManager();
			List<ICommand> commands = new ArrayList<>(manager.getCommands().values());
			ATHelper.getCommandSet(manager).clear();
			manager.getCommands().clear();

			for (ICommand command : commands)
			{
				ModContainer container = CommonUtils.getModContainerForClass(command.getClass());
				manager.registerCommand(CommandOverride.create(command, container == null ? Node.COMMAND : Node.COMMAND.append(container.getModId())));
			}

			List<CommandOverride> ocommands = new ArrayList<>(Ranks.INSTANCE.commands.values());
			ocommands.sort(null);
			Ranks.INSTANCE.commands.clear();

			for (CommandOverride c : ocommands)
			{
				Ranks.INSTANCE.commands.put(c.node, c);
			}

			LOGGER.info("Overridden " + manager.getCommands().size() + " commands");
			Ranks.INSTANCE.generateExampleFiles();
		}
	}
}