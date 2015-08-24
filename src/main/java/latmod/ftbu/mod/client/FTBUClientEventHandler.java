package latmod.ftbu.mod.client;
import latmod.ftbu.core.*;
import latmod.ftbu.core.api.IFTBUReloadable;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.inv.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.paint.IPainterItem;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldClient;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.badges.ThreadLoadBadges;
import latmod.ftbu.mod.client.minimap.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUClientEventHandler implements IFTBUReloadable
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
			FTBULang.reload();
		}
		else if(e.map.getTextureType() == 1)
			LatCoreMCClient.unknownItemIcon = e.map.registerIcon(FTBU.mod.assets + "unknown");
	}
	
	public void onReloaded(Side s, ICommandSender sender) throws Exception
	{
		if(s.isClient()) ThreadLoadBadges.init();
	}
	
	@SubscribeEvent
	public void onDrawDebugText(RenderGameOverlayEvent.Text e)
	{
		Minecraft mc = LatCoreMCClient.getMinecraft();
		
		// Some ideas around this //
		if(!mc.gameSettings.showDebugInfo)
		{
			if(LatCoreMC.isDevEnv)
				e.left.add("[MC " + EnumChatFormatting.GOLD + LatCoreMC.MC_VERSION + EnumChatFormatting.WHITE + " DevEnv]");
			
			if(FTBUClient.displayDebugInfo.getB())
				e.right.add(mc.debug);
		}
		
		if(mc.gameSettings.showDebugInfo)
			e.left.add("r: " + MathHelperLM.get2DRotation(mc.thePlayer));
	}
	
	@SubscribeEvent
	public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent e)
	{
		ServerData sd = LatCoreMCClient.getMinecraft().func_147104_D();
		String s = (sd == null || sd.serverIP.isEmpty()) ? "localhost" : sd.serverIP.replace('.', '_');
		LMWorldClient.NoServerWorld.worldIDSNoWorld = s;
		LMWorldClient.inst = new LMWorldClient.NoServerWorld();
		LatCoreMC.logger.info("Connecting to world...");
	}
	
	@SubscribeEvent
	public void onDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
	{
		FTBUClient.onWorldClosed();
		LMWorldClient.inst = null;
	}
	
	@SubscribeEvent
	public void renderChunk(RenderWorldEvent.Pre e)
	{
		if(e.pass == 0)
		{
			int cx = MathHelperLM.chunk(e.renderer.posX);
			int cz = MathHelperLM.chunk(e.renderer.posZ);
			World w = LatCoreMCClient.getMinecraft().theWorld;
			MChunk c = Minimap.get(w.provider.dimensionId).loadChunk(cx, cz);
			c.reload(w);
			LMNetHelper.sendToServer(new MessageAreaRequest(cx, cz, w.provider.dimensionId, 1));
		}
	}
	
	@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent e)
	{
		if(LatCoreMC.isDevEnv && Keyboard.getEventKeyState())
		{
			//LatCoreMC.printChat(null, Keyboard.getKeyName(Keyboard.getEventKey()));
			
			int key = Keyboard.getEventKey();
			if(key == Keyboard.KEY_GRAVE)
			{
				Minimap.save();
			}
			else if(key == Keyboard.KEY_MINUS)
			{
				if(GuiScreen.isShiftKeyDown())
				{
					int i = Minimap.size.getI() - 1;
					if(i >= 0 && i < Minimap.size.values.length)
						Minimap.size.setValue(i);
				}
				else
				{
					int i = Minimap.zoom.getI() + 1;
					if(i >= 0 && i < Minimap.zoom.values.length)
						Minimap.zoom.setValue(i);
				}
				
				LatCoreMCClient.playClickSound();
			}
			else if(key == Keyboard.KEY_EQUALS)
			{
				if(GuiScreen.isShiftKeyDown())
				{
					int i = Minimap.size.getI() + 1;
					if(i >= 0 && i < Minimap.size.values.length)
						Minimap.size.setValue(i);
				}
				else
				{
					int i = Minimap.zoom.getI() - 1;
					if(i >= 0 && i < Minimap.zoom.values.length)
						Minimap.zoom.setValue(i);
				}
				
				LatCoreMCClient.playClickSound();
			}
			else if(key == Keyboard.KEY_M)
			{
				Minimap.renderIngame.onClicked();
			}
		}
	}
}