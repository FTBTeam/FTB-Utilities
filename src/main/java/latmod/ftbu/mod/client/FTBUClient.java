package latmod.ftbu.mod.client;

import cpw.mods.fml.relauncher.*;
import ftb.lib.*;
import ftb.lib.api.config.ClientConfigRegistry;
import ftb.lib.api.gui.LMGuiHandlerRegistry;
import ftb.lib.client.FTBLibClient;
import ftb.lib.mod.client.FTBLibGuiEventHandler;
import latmod.ftbu.badges.BadgeRenderer;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.cmd.CmdMath;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.tile.TileLM;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.config.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.client.ClientCommandHandler;

@SideOnly(Side.CLIENT)
public class FTBUClient extends FTBUCommon // FTBLibModClient
{
	public static final ConfigEntryBool render_badges = new ConfigEntryBool("render_badges", true);
	
	public static final ConfigEntryBool render_my_badge = new ConfigEntryBool("render_my_badge", true)
	{
		public boolean get()
		{ return LatCoreMCClient.isPlaying() && LMWorldClient.inst.getClientPlayer().renderBadge; }
		
		public void set(boolean b)
		{
			if(LatCoreMCClient.isPlaying()) ClientAction.BUTTON_RENDER_BADGE.send(b ? 1 : 0);
		}
	}.setExcluded();
	
	public static final ConfigEntryBool chat_links = new ConfigEntryBool("chat_links", true)
	{
		public boolean get()
		{ return LatCoreMCClient.isPlaying() && LMWorldClient.inst.getClientPlayer().getSettings().chatLinks; }
		
		public void set(boolean b)
		{
			if(LatCoreMCClient.isPlaying()) ClientAction.BUTTON_CHAT_LINKS.send(b ? 1 : 0);
		}
	}.setExcluded();
	
	public static final ConfigEntryBool player_options_shortcut = new ConfigEntryBool("player_options_shortcut", false);
	public static final ConfigEntryBool sort_friends_az = new ConfigEntryBool("sort_friends_az", false);
	public static final ConfigEntryBool loaded_chunks_space_key = new ConfigEntryBool("loaded_chunks_space_key", false);
	public static final ConfigEntryBool hide_armor_fg = new ConfigEntryBool("hide_armor_fg", false).setHidden();
	
	public void preInit()
	{
		JsonHelper.initClient();
		EventBusHelper.register(FTBUClientEventHandler.instance);
		EventBusHelper.register(FTBURenderHandler.instance);
		EventBusHelper.register(FTBUGuiEventHandler.instance);
		EventBusHelper.register(BadgeRenderer.instance);
		
		ClientConfigRegistry.add(new ConfigGroup("ftbu").addAll(FTBUClient.class, null, false));
		ClientCommandHandler.instance.registerCommand(new CmdMath());
		FTBLibGuiEventHandler.sidebar_buttons_config.addAll(FTBUGuiEventHandler.class, null, false);
	}
	
	public void postInit()
	{
		LMGuiHandlerRegistry.add(FTBUGuiHandler.instance);
		FTBUClickAction.init();
	}
	
	public LMWorld getClientWorldLM()
	{ return LMWorldClient.inst; }
	
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p)
	{
		NBTTagCompound data = p.func_148857_g();
		t.readTileData(data);
		t.readTileClientData(data);
		t.onUpdatePacket();
		FTBLibClient.onGuiClientAction();
	}
}