package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.ATHelper;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.SidedUtils;
import com.feed_the_beast.ftbutilities.command.FTBUtilitiesCommands;
import com.feed_the_beast.ftbutilities.ranks.CommandOverride;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.mods.aurora.Aurora;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod(
		modid = FTBUtilities.MOD_ID,
		name = FTBUtilities.MOD_NAME,
		version = FTBUtilities.VERSION,
		acceptableRemoteVersions = "*",
		dependencies = FTBLib.THIS_DEP + ";before:" + KubeJS.MOD_ID + ";after:" + Aurora.MOD_ID
)
public class FTBUtilities
{
	public static final String MOD_ID = "ftbutilities";
	public static final String MOD_NAME = "FTB Utilities";
	public static final String VERSION = "0.0.0.ftbutilities";
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
	public void onIMC(FMLInterModComms.IMCEvent event)
	{
		for (FMLInterModComms.IMCMessage message : event.getMessages())
		{
			PROXY.imc(message);
		}
	}

	@Mod.EventHandler
	public void onServerStarted(FMLServerStartedEvent event)
	{
		if (Ranks.isActive())
		{
			Ranks.INSTANCE.commands.clear();
			FileUtils.deleteSafe(new File(Ranks.INSTANCE.universe.server.getDataDirectory(), "local/ftbutilities/all_permissions.html"));
			FileUtils.deleteSafe(new File(Ranks.INSTANCE.universe.server.getDataDirectory(), "local/ftbutilities/all_permissions_full_list.txt"));

			boolean spongeLoaded = Loader.isModLoaded("spongeforge");

			if (spongeLoaded)
			{
				LOGGER.warn("Sponge detected, command overriding has been disabled. If there are any issues with FTB Utilities ranks or permissions, please test them without Sponge!");
			}

			if (!FTBUtilitiesConfig.ranks.override_commands || spongeLoaded)
			{
				return;
			}

			ServerCommandManager manager = (ServerCommandManager) Ranks.INSTANCE.universe.server.getCommandManager();
			List<ICommand> commands = new ArrayList<>(manager.getCommands().values());
			ATHelper.getCommandSet(manager).clear();
			manager.getCommands().clear();

			for (ICommand command : commands)
			{
				ModContainer container = CommonUtils.getModContainerForClass(command.getClass());
				manager.registerCommand(CommandOverride.create(command, container == null ? Rank.NODE_COMMAND : (Rank.NODE_COMMAND + '.' + container.getModId()), container));
			}

			List<CommandOverride> ocommands = new ArrayList<>(Ranks.INSTANCE.commands.values());
			ocommands.sort((o1, o2) -> {
				int i = Boolean.compare(o1.modContainer != null, o2.modContainer != null);
				return i == 0 ? o1.node.compareTo(o2.node) : i;
			});

			for (CommandOverride c : ocommands)
			{
				Ranks.INSTANCE.commands.put(c.node, c);
			}

			LOGGER.info("Overridden " + manager.getCommands().size() + " commands");
		}
	}
}