package latmod.ftbu.mod.client;
import java.io.File;
import java.util.UUID;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.client.*;
import latmod.ftbu.core.inv.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.paint.IPainterItem;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldClient;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.minimap.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

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
			LatCoreMCClient.resetTextureMaps();
		}
		else if(e.map.getTextureType() == 1)
			LatCoreMCClient.unknownItemIcon = e.map.registerIcon(FTBU.mod.assets + "unknown");
	}
	
	@SubscribeEvent
	public void onDrawDebugText(RenderGameOverlayEvent.Text e)
	{
		Minecraft mc = LatCoreMCClient.getMinecraft();
		
		// Some ideas around this //
		if(!mc.gameSettings.showDebugInfo)
		{
			if(FTBUClient.displayDebugInfo.getB())
				e.left.add(mc.debug);
			
			if(FTBUFinals.DEV)
				e.left.add("[MC " + EnumChatFormatting.GOLD + FTBUFinals.MC_VERSION + EnumChatFormatting.WHITE + " DevEnv]");
		}
		
		if(mc.gameSettings.showDebugInfo)
			e.left.add("r: " + MathHelperLM.get2DRotation(mc.thePlayer));
	}
	
	@SubscribeEvent
	public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent e)
	{
		ServerData sd = LatCoreMCClient.getMinecraft().func_147104_D();
		String s = (sd == null || sd.serverIP.isEmpty()) ? "localhost" : sd.serverIP.replace('.', '_');
		LMWorldClient.inst = new LMWorldClient(new UUID(0L, 0L), s, 0);
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
		if(e.pass == 0 && Minimap.renderIngame.getB())
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
		if(FTBUFinals.DEV && Keyboard.getEventKeyState())
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
			else if(key == Keyboard.KEY_N)
			{
				File f = Minimap.get(LatCoreMCClient.getMinecraft().thePlayer.dimension).exportImage();
				if(f != null)
				{
					Notification n = new Notification(null, new ChatComponentText("Minimap exported!"), 2000);
					n.setDesc(new ChatComponentText(f.getName()));
					n.setItem(new ItemStack(Items.map));
					n.setClickEvent(new NotificationClick(NotificationClick.FILE, f.getAbsolutePath()));
					ClientNotifications.add(n);
				}
				else
				{
					Notification n = new Notification(null, new ChatComponentText("Minimap failed to export!"), 2000);
					n.setItem(new ItemStack(Items.map));
					ClientNotifications.add(n);
				}
			}
		}
	}
}