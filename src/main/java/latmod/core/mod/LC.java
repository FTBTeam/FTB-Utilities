package latmod.core.mod;
import java.io.File;

import latmod.core.*;
import latmod.core.mod.cmd.*;
import latmod.core.net.LMNetHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;

@Mod(modid = LC.MOD_ID, name = "LatCoreMC", version = LC.VERSION, dependencies = "required-after:Forge@[10.13.2.1231,)", guiFactory = "latmod.core.mod.client.LCGuiFactory")
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
		LMNetHandler.init();
		proxy.init(e);
		
		FMLInterModComms.sendMessage("Waila", "register", "latmod.core.waila.RegisterWailaEvent.registerHandlers");
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
		regCmd(e, new CmdLatCore(LCConfig.Commands.latcore));
		regCmd(e, new CmdLatCoreAdmin(LCConfig.Commands.latcoreadmin));
		regCmd(e, new CmdRealNick(LCConfig.Commands.realnick));
		regCmd(e, new CmdTpOverride(LCConfig.Commands.teleport));
		regCmd(e, new CmdListOverride(LCConfig.Commands.list));
		e.registerServerCommand(new CmdGamemodeOverride(LCConfig.Commands.gamemode));
		e.registerServerCommand(new CmdGameruleOverride(LCConfig.Commands.gamerule));
	}
	
	@Mod.EventHandler
	public void shuttingDown(FMLServerStoppingEvent e)
	{
		if(LatCoreMC.hasOnlinePlayers()) for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers().values)
			LCEventHandler.instance.playerLoggedOut(new cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent(ep));
	}
	
	private static void regCmd(FMLServerStartingEvent e, CommandBaseLC c)
	{ if(c.level.isEnabled()) e.registerServerCommand(c); }
}