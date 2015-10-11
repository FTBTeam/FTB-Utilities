package latmod.ftbu.mod.client;
import java.util.UUID;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.EventLMWorldClient;
import latmod.ftbu.inv.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.gui.friends.GuiFriendsGuiSmall;
import latmod.ftbu.paint.IPainterItem;
import latmod.ftbu.util.*;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.FastList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.item.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.*;

@SideOnly(Side.CLIENT)
public class FTBUClientEventHandler
{
	public static final FTBUClientEventHandler instance = new FTBUClientEventHandler();
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.itemStack == null || e.itemStack.getItem() == null) return;
		
		Item item = e.itemStack.getItem();
		
		if(item instanceof IPainterItem)
		{
			ItemStack paint = ((IPainterItem)item).getPaintItem(e.itemStack);
			if(paint != null) e.toolTip.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.BOLD + paint.getDisplayName());
		}
		
		if(FTBUClient.addRegistryNames.getB())
		{
			e.toolTip.add(LMInvUtils.getRegName(e.itemStack));
		}
		
		if(FTBUClient.addOreNames.getB())
		{
			FastList<String> ores = ODItems.getOreNames(e.itemStack);
			
			if(ores != null && !ores.isEmpty())
			{
				e.toolTip.add("Ore Dictionary names:");
				for(String or : ores)
				e.toolTip.add("> " + or);
			}
		}
	}
	
	@SubscribeEvent
	public void preTexturesLoaded(TextureStitchEvent.Pre e)
	{
		if(e.map.getTextureType() == 0)
		{
			LatCoreMCClient.blockNullIcon = e.map.registerIcon(FTBU.mod.assets + "empty_block");
			LatCoreMCClient.clearCachedData();
		}
		else if(e.map.getTextureType() == 1)
			LatCoreMCClient.unknownItemIcon = e.map.registerIcon(FTBU.mod.assets + "unknown");
	}
	
	@SubscribeEvent
	public void onDrawDebugText(RenderGameOverlayEvent.Text e)
	{
		// Some ideas around this //
		if(!LatCoreMCClient.mc.gameSettings.showDebugInfo)
		{
			if(FTBUClient.displayDebugInfo.getB())
				e.left.add(LatCoreMCClient.mc.debug);
			
			if(FTBUFinals.DEV)
				e.left.add("[MC " + EnumChatFormatting.GOLD + FTBUFinals.MC_VERSION + EnumChatFormatting.WHITE + " DevEnv]");
		}
		
		if(LatCoreMCClient.mc.gameSettings.showDebugInfo)
			e.left.add("r: " + MathHelperMC.get2DRotation(LatCoreMCClient.mc.thePlayer));
	}
	
	@SubscribeEvent
	public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent e)
	{
		ServerData sd = LatCoreMCClient.mc.func_147104_D();
		String s = (sd == null || sd.serverIP.isEmpty()) ? "localhost" : sd.serverIP.replace('.', '_');
		LMWorldClient.inst = new LMWorldClient(new UUID(0L, 0L), s, 0);
		LatCoreMC.logger.info("Connecting to world...");
	}
	
	@SubscribeEvent
	public void onDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
	{
		FTBUClient.onWorldClosed();
		new EventLMWorldClient.Closed(LMWorldClient.inst).post();
		LMWorldClient.inst = null;
	}
	
	@SubscribeEvent
	public void onEntityRightClick(EntityInteractEvent e)
	{
		if(e.entity.worldObj.isRemote && LatCoreMCClient.isPlaying() && e.entityPlayer.getUniqueID().equals(LatCoreMCClient.mc.thePlayer.getUniqueID()) && e.entityPlayer.getHeldItem() == null)// && e.target instanceof EntityPlayer)
		{
			LMPlayerClient p = LMWorldClient.inst.getPlayer(e.target);
			if(p != null) LatCoreMCClient.mc.displayGuiScreen(new GuiFriendsGuiSmall(p));
		}
	}
}