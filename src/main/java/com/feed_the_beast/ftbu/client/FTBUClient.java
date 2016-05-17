package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.config.ClientConfigRegistry;
import com.feed_the_beast.ftbl.api.config.ConfigEntryBool;
import com.feed_the_beast.ftbl.api.events.ForgeWorldDataEvent;
import com.feed_the_beast.ftbl.api.gui.LMGuiHandlerRegistry;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.FTBUGuiHandler;
import com.feed_the_beast.ftbu.badges.BadgeRenderer;
import com.feed_the_beast.ftbu.cmd.CmdMath;
import com.feed_the_beast.ftbu.journeymap.IJMPluginHandler;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
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
}