package latmod.core.mod;
import java.io.File;

import latmod.core.*;
import latmod.core.mod.cmd.*;
import latmod.core.net.MessageLM;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod(modid = LC.MOD_ID, name = "LatCoreMC", version = LC.VERSION, dependencies = "required-after:Forge@[10.13.2.1291,)", guiFactory = "latmod.core.mod.client.LCGuiFactory")
public class LC
{
	protected static final String MOD_ID = "LatCoreMC";
	public static final String VERSION = "@VERSION@";
	
	@Mod.Instance(LC.MOD_ID)
	public static LC inst;
	
	@SidedProxy(clientSide = "latmod.core.mod.client.LCClient", serverSide = "latmod.core.mod.LCCommon")
	public static LCCommon proxy;
	
	public static LMMod mod;
	
	public LC() { LatCoreMC.addEventHandler(LCEventHandler.instance, true, true, true); }
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		if(LatCoreMC.isDevEnv)
			LatCoreMC.logger.info("Loading LatCoreMC, Dev Build");
		else
			LatCoreMC.logger.info("Loading LatCoreMC, Build #" + VERSION);
		
		LatCoreMC.latmodFolder = new File(e.getModConfigurationDirectory().getParentFile(), "latmod/");
		if(!LatCoreMC.latmodFolder.exists()) LatCoreMC.latmodFolder.mkdirs();
		
		mod = new LMMod(MOD_ID, new LCConfig(e), null);
		mod.logger = LatCoreMC.logger;
		
		ODItems.preInit();
		
		mod.onPostLoaded();
		
		LatCoreMC.addGuiHandler(this, proxy);
		
		proxy.preInit(e);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		MessageLM.init();
		proxy.init(e);
		
		FMLInterModComms.sendMessage("Waila", "register", "latmod.core.event.RegisterWailaEvent.registerHandlers");
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		ODItems.postInit();
		mod.loadRecipes();
		LCConfig.Recipes.loadRecipes();
		proxy.postInit(e);
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdLatCore());
		e.registerServerCommand(new CmdLatCoreAdmin());
		e.registerServerCommand(new CmdRealNick());
		
		if(!LCConfig.General.disableCommandOverrides)
		{
			e.registerServerCommand(new CmdTpOverride());
			e.registerServerCommand(new CmdListOverride());
			e.registerServerCommand(new CmdGamemodeOverride());
			e.registerServerCommand(new CmdGameruleOverride());
		}
	}
	
	@Mod.EventHandler
	public void shuttingDown(FMLServerStoppingEvent e)
	{
		if(LatCoreMC.hasOnlinePlayers()) for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers().values)
			LCEventHandler.instance.playerLoggedOut(new cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent(ep));
	}
}