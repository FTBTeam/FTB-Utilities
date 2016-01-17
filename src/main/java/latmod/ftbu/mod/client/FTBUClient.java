package latmod.ftbu.mod.client;

import ftb.lib.*;
import ftb.lib.api.config.ClientConfigRegistry;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.lib.api.tile.TileLM;
import latmod.ftbu.badges.BadgeRenderer;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.cmd.CmdMath;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.*;
import latmod.lib.config.*;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.*;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final ConfigEntryBool render_badges = new ConfigEntryBool("render_badges", true);
	
	public static final ConfigEntryBool render_my_badge = new ConfigEntryBool("render_my_badge", true)
	{
		public boolean get()
		{ return FTBLibClient.isIngameWithFTBU() && LMWorldClient.inst.clientPlayer.renderBadge; }
		
		public void set(boolean b)
		{
			if(FTBLibClient.isIngameWithFTBU()) ClientAction.BUTTON_RENDER_BADGE.send(b ? 1 : 0);
		}
	}.setExcluded();
	
	public static final ConfigEntryBool chat_links = new ConfigEntryBool("chat_links", true)
	{
		public boolean get()
		{ return FTBLibClient.isIngameWithFTBU() && LMWorldClient.inst.clientPlayer.getSettings().get(PersonalSettings.CHAT_LINKS); }
		
		public void set(boolean b)
		{
			if(FTBLibClient.isIngameWithFTBU()) ClientAction.BUTTON_CHAT_LINKS.send(b ? 1 : 0);
		}
	}.setExcluded();
	
	public static final ConfigEntryBool sort_friends_az = new ConfigEntryBool("sort_friends_az", false);
	public static final ConfigEntryBool loaded_chunks_space_key = new ConfigEntryBool("loaded_chunks_space_key", false);
	public static final ConfigEntryBool guide_unicode = new ConfigEntryBool("guide_unicode", true);
	public static final ConfigEntryBool hide_armor_fg = new ConfigEntryBool("hide_armor_fg", false).setHidden();
	
	public void preInit()
	{
		JsonHelper.initClient();
		
		ClientConfigRegistry.add(new ConfigGroup("ftbu").addAll(FTBUClient.class, null, false));
		ClientCommandHandler.instance.registerCommand(new CmdMath());
		FTBUActions.init();
	}
	
	public void postInit()
	{
		LMGuiHandlerRegistry.add(FTBUGuiHandler.instance);
		FTBUClickAction.init();
		
		Map<String, RenderPlayer> skinMap = FTBLibClient.mc.getRenderManager().getSkinMap();
		RenderPlayer render = skinMap.get("default");
		render.addLayer(BadgeRenderer.instance);
		render = skinMap.get("slim");
		render.addLayer(BadgeRenderer.instance);
	}
	
	public LMWorld getClientWorldLM()
	{ return LMWorldClient.inst; }
	
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p)
	{
		NBTTagCompound data = p.getNbtCompound();
		t.readTileData(data);
		t.readTileClientData(data);
		t.onUpdatePacket();
		FTBLibClient.onGuiClientAction();
	}
}