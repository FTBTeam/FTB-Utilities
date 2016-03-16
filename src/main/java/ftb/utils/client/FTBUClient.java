package ftb.utils.client;

import ftb.lib.api.ForgeWorldSP;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.config.ClientConfigRegistry;
import ftb.lib.api.events.*;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.utils.*;
import ftb.utils.badges.BadgeRenderer;
import ftb.utils.cmd.CmdMath;
import ftb.utils.journeymap.IJMPluginHandler;
import ftb.utils.net.MessageButtonPressed;
import ftb.utils.world.*;
import latmod.lib.config.*;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.*;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final ConfigEntryBool render_badges = new ConfigEntryBool("render_badges", true);
	
	@Excluded
	public static final ConfigEntryBool render_my_badge = new ConfigEntryBool("render_my_badge", true)
	{
		public boolean get()
		{ return FTBUWorldDataSP.exists() && FTBUPlayerDataSP.get(ForgeWorldSP.inst.clientPlayer).getFlag(FTBUPlayerData.RENDER_BADGE); }
		
		public void set(boolean b)
		{
			if(FTBUWorldDataSP.exists())
			{
				new MessageButtonPressed(MessageButtonPressed.RENDER_BADGE, b ? 1 : 0).sendToServer();
			}
		}
	};
	
	@Excluded
	public static final ConfigEntryBool chat_links = new ConfigEntryBool("chat_links", true)
	{
		public boolean get()
		{ return FTBUWorldDataSP.exists() && FTBUPlayerDataSP.get(ForgeWorldSP.inst.clientPlayer).getFlag(FTBUPlayerData.CHAT_LINKS); }
		
		public void set(boolean b)
		{
			if(FTBUWorldDataSP.exists())
			{
				new MessageButtonPressed(MessageButtonPressed.CHAT_LINKS, b ? 1 : 0).sendToServer();
			}
		}
	};
	
	public static final ConfigEntryBool loaded_chunks_space_key = new ConfigEntryBool("loaded_chunks_space_key", false);
	public static final ConfigEntryBool guide_unicode = new ConfigEntryBool("guide_unicode", true);
	
	public static IJMPluginHandler journeyMapHandler = null;
	
	public void preInit()
	{
		ClientConfigRegistry.add(new ConfigGroup("ftbu").addAll(FTBUClient.class, null, false));
		ClientCommandHandler.instance.registerCommand(new CmdMath());
		FTBUActions.init();
	}
	
	public void postInit()
	{
		LMGuiHandlerRegistry.add(FTBUGuiHandler.instance);
		
		Map<String, RenderPlayer> skinMap = FTBLibClient.mc.getRenderManager().getSkinMap();
		RenderPlayer render = skinMap.get("default");
		render.addLayer(BadgeRenderer.instance);
		render = skinMap.get("slim");
		render.addLayer(BadgeRenderer.instance);
	}
	
	public void addWorldData(ForgeWorldDataEvent event)
	{
		if(event.world.side.isClient())
		{
			event.add(new FTBUWorldDataSP(event.world.toWorldSP()));
		}
		else super.addWorldData(event);
	}
	
	public void addPlayerData(ForgePlayerDataEvent event)
	{
		if(event.player.getSide().isClient())
		{
			event.add(new FTBUPlayerDataSP(event.player.toPlayerSP()));
		}
		else super.addPlayerData(event);
	}
}