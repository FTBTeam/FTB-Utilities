package ftb.utils.client;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.config.ClientConfigRegistry;
import ftb.lib.api.config.ConfigEntryBool;
import ftb.lib.api.events.ForgePlayerDataEvent;
import ftb.lib.api.events.ForgeWorldDataEvent;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.utils.FTBUCommon;
import ftb.utils.FTBUGuiHandler;
import ftb.utils.badges.BadgeRenderer;
import ftb.utils.cmd.CmdMath;
import ftb.utils.journeymap.IJMPluginHandler;
import ftb.utils.world.FTBUPlayerDataSP;
import ftb.utils.world.FTBUWorldDataSP;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final ConfigEntryBool render_badges = new ConfigEntryBool("render_badges", true);
	public static final ConfigEntryBool loaded_chunks_space_key = new ConfigEntryBool("loaded_chunks_space_key", false);
	
	public static IJMPluginHandler journeyMapHandler = null;
	
	@Override
	public void preInit()
	{
		ClientConfigRegistry.addGroup("ftbu", FTBUClient.class);
		ClientCommandHandler.instance.registerCommand(new CmdMath());
		FTBUActions.init();
	}
	
	@Override
	public void postInit()
	{
		LMGuiHandlerRegistry.add(FTBUGuiHandler.instance);
		
		Map<String, RenderPlayer> skinMap = FTBLibClient.mc.getRenderManager().getSkinMap();
		RenderPlayer render = skinMap.get("default");
		render.addLayer(BadgeRenderer.instance);
		render = skinMap.get("slim");
		render.addLayer(BadgeRenderer.instance);
		
		//GuideRepoList.refresh();
	}
	
	@Override
	public void addWorldData(ForgeWorldDataEvent event)
	{
		if(event.world.side.isClient())
		{
			event.add(FTBUWorldDataSP.get());
		}
		else { super.addWorldData(event); }
	}
	
	@Override
	public void addPlayerData(ForgePlayerDataEvent event)
	{
		if(event.player.getSide().isClient())
		{
			event.add(new FTBUPlayerDataSP(event.player.toPlayerSP()));
		}
		else { super.addPlayerData(event); }
	}
}