package latmod.core.mod;
import java.io.File;
import java.util.Map;

import latmod.core.*;
import latmod.core.mod.cmd.*;
import latmod.core.net.MessageLM;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = LC.MOD_ID, name = "LatCoreMC", version = LC.VERSION, dependencies = "required-after:Forge@[10.13.2.1291,);after:Baubles;after:Waila", guiFactory = "latmod.core.mod.client.LCGuiFactory")
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
	
	private ModMetadata modMeta;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		if(LatCoreMC.isDevEnv)
			LatCoreMC.logger.info("Loading LatCoreMC, Dev Build");
		else
			LatCoreMC.logger.info("Loading LatCoreMC, Build #" + VERSION);
		
		modMeta = e.getModMetadata();
		
		LatCoreMC.latmodFolder = new File(e.getModConfigurationDirectory().getParentFile(), "latmod/");
		if(!LatCoreMC.latmodFolder.exists()) LatCoreMC.latmodFolder.mkdirs();
		
		mod = new LMMod(e, new LCConfig(e), null);
		mod.logger = LatCoreMC.logger;
		
		ODItems.preInit();
		
		mod.onPostLoaded();
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
		
		boolean addedDesc = false;
		if(modMeta != null) for(int i = 0; i < LMMod.modsMap.size(); i++)
		{
			LMMod m = LMMod.modsMap.values.get(i);
			
			if(!m.modID.equals(mod.modID))
			{
				if(!addedDesc)
				{
					modMeta.description += EnumChatFormatting.GREEN + "\n\nMods using LatCoreMC:";
					addedDesc = true;
				}
				
				modMeta.description += "\n" + m.modID;
			}
		}
		
		for(String s : LCGuiHandler.IDs) LatCoreMC.addLMGuiHandler(s, LCGuiHandler.instance);
	}
	
	@Mod.EventHandler
	public void registerCommands(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdLatCore());
		e.registerServerCommand(new CmdLMFriends());
		e.registerServerCommand(new CmdLatCoreAdmin());
	}
	
	@Mod.EventHandler
	public void shuttingDown(FMLServerStoppingEvent e)
	{
		if(LatCoreMC.hasOnlinePlayers()) for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers().values)
			LCEventHandler.instance.playerLoggedOut(new cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent(ep));
	}
	
	@NetworkCheckHandler
	public boolean checkNetwork(Map<String, String> m, Side side)
	{
		String s = m.get(MOD_ID);
		return s == null || s.equals(VERSION) || VERSION.equals(LatCoreMC.DEV_VERSION);
	}
}