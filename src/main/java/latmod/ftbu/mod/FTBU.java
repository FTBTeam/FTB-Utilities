package latmod.ftbu.mod;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import ftb.lib.*;
import ftb.lib.notification.*;
import latmod.ftbu.mod.cmd.*;
import latmod.ftbu.mod.cmd.admin.CmdAdmin;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.mod.handlers.*;
import latmod.ftbu.mod.handlers.ftbl.*;
import latmod.ftbu.net.FTBUNetHandler;
import latmod.ftbu.util.LMMod;
import latmod.ftbu.world.Backups;
import latmod.lib.LMJsonUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.ForgeChunkManager;

@Mod(modid = FTBUFinals.MOD_ID, version = FTBUFinals.MOD_VERSION, name = FTBUFinals.MOD_NAME, dependencies = FTBUFinals.MOD_DEP)
public class FTBU
{
	@Mod.Instance(FTBUFinals.MOD_ID)
	public static FTBU inst;

	@SidedProxy(clientSide = "latmod.ftbu.mod.client.FTBUClient", serverSide = "latmod.ftbu.mod.FTBUCommon")
	public static FTBUCommon proxy;

	@SidedProxy(clientSide = "latmod.ftbu.mod.handlers.ftbl.FTBLIntegrationClient", serverSide = "latmod.ftbu.mod.handlers.ftbl.FTBLIntegrationCommon")
	public static FTBLIntegrationCommon proxy_ftbl_int;

	@LMMod.Instance(FTBUFinals.MOD_ID)
	public static LMMod mod;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		LMMod.init(this);
		FTBLib.ftbu = new FTBLIntegration();

		LMJsonUtils.register(Notification.class, new Notification.Serializer());
		LMJsonUtils.register(MouseAction.class, new MouseAction.Serializer());
		FTBUConfig.load();

		EventBusHelper.register(new FTBUPlayerEventHandler());
		EventBusHelper.register(new FTBUWorldEventHandler());
		EventBusHelper.register(new FTBUChatEventHandler());
		EventBusHelper.register(FTBUChunkEventHandler.instance);
		FTBUChunkEventHandler.instance.refreshMaxChunksCount();

		FTBUNetHandler.init();
		Backups.init();
		mod.onPostLoaded();
		proxy.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		FMLInterModComms.sendMessage("Waila", "register", "latmod.ftbu.core.api.RegisterWailaEvent.registerHandlers");
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		mod.loadRecipes();
		proxy.postInit();
		ForgeChunkManager.setForcedChunkLoadingCallback(inst, FTBUChunkEventHandler.instance);
	}

	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		FTBUTicks.serverStarted();
		
		e.registerServerCommand(new CmdAdmin());
		e.registerServerCommand(new CmdBack());
		e.registerServerCommand(new CmdHome());
		e.registerServerCommand(new CmdSpawn());
		e.registerServerCommand(new CmdTplast());
		e.registerServerCommand(new CmdWarp());
	}

	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		if(FTBLib.hasOnlinePlayers())
		{
			for(EntityPlayerMP ep : FTBLib.getAllOnlinePlayers(null))
				FTBUPlayerEventHandler.playerLoggedOut(ep);
		}
	}
}