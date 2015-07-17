package latmod.ftbu.mod.client;
import latmod.ftbu.core.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.client.badges.ThreadLoadBadges;
import latmod.ftbu.core.event.ReloadEvent;
import latmod.ftbu.core.inv.*;
import latmod.ftbu.core.tile.IPaintable;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.claims.ChunkType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.event.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBUClientEventHandler
{
	public static final FTBUClientEventHandler instance = new FTBUClientEventHandler();
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.itemStack == null || e.itemStack.getItem() == null) return;
		
		Item item = e.itemStack.getItem();
		
		if(item instanceof IPaintable.IPainterItem)
		{
			ItemStack paint = ((IPaintable.IPainterItem)item).getPaintItem(e.itemStack);
			if(paint != null) e.toolTip.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.BOLD + paint.getDisplayName());
		}
		
		if(FTBUClient.addRegistryNames.getB())
		{
			e.toolTip.add(InvUtils.getRegName(e.itemStack));
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
	
	@SubscribeEvent
	public void onReload(ReloadEvent r)
	{
		if(r.side.isClient())
		{
			ThreadLoadBadges.init();
		}
	}
	
	@SubscribeEvent
	public void onChatEvent(net.minecraftforge.client.event.ClientChatReceivedEvent e)
	{
		int chatLinks = FTBUClient.chatLinks.getI();
		if(chatLinks == 0) return;
		else if(chatLinks == 1)
		{
			String[] msg = e.message.getUnformattedText().split(" ");
			
			FastList<String> links = new FastList<String>();
			
			for(String s : msg)
			{
				if(s.startsWith("http://") || s.startsWith("https://"))
					links.add(s);
			}
			
			if(!links.isEmpty())
			{
				final IChatComponent line = new ChatComponentText("");
				boolean oneLink = links.size() == 1;
				
				for(int i = 0; i < links.size(); i++)
				{
					String link = links.get(i);
					IChatComponent c = new ChatComponentText(oneLink ? "[Link]" : ("[Link #" + (i + 1) + "]"));
					c.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(link)));
					c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
					line.appendSibling(c);
					if(!oneLink) line.appendSibling(new ChatComponentText(" "));
				}
				
				line.getChatStyle().setColor(EnumChatFormatting.GOLD);
				
				Thread thread = new Thread()
				{
					public void run()
					{
						try { Thread.sleep(50L); }
						catch(Exception e) { }
						LatCoreMC.printChat(null, line);
					}
				};
				
				thread.setDaemon(true);
				thread.start();
			}
		}
		else if(chatLinks == 2)
		{
			/*String[] msg = e.message.getFormattedText().split(" ");
			
			IChatComponent line = new ChatComponentText("");
			//e.message.getSiblings();
			for(int i = 0; i < msg.length; i++)
			{
				if(msg[i].contains("http://") || msg[i].contains("https://"))
				{
					String link = LatCoreMC.removeFormatting(msg[i]);
					IChatComponent c = new ChatComponentText("[Link]");
					c.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(link)));
					c.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
					c.getChatStyle().setColor(EnumChatFormatting.GOLD);
					line.appendSibling(c);
				}
				else
				{
					line.appendSibling(new ChatComponentText(msg[i]));
				}
				
				if(i != msg.length - 1) line.appendSibling(new ChatComponentText(" "));
			}
			
			e.message = line;
			*/
		}
	}
	
	@SubscribeEvent
	public void onDrawDebugText(RenderGameOverlayEvent.Text e)
	{
		boolean shift = FTBU.proxy.isShiftDown();
		Minecraft mc = LatCoreMCClient.getMinecraft();
		
		// Some ideas around this //
		if(!mc.gameSettings.showDebugInfo)
		{
			if(LatCoreMC.isDevEnv)
				e.left.add("[MC " + EnumChatFormatting.GOLD + LatCoreMC.MC_VERSION + EnumChatFormatting.WHITE + " DevEnv]");
			
			if(FTBUClient.displayDebugInfo.getB())
				e.right.add(mc.debug);
		}
		
		if(LMWorldClient.inst != null)
		{
			LMPlayerClient p = LMWorldClient.inst.getClientPlayer();
			if(p != null) ChunkType.getMessage(mc.theWorld.provider.dimensionId, MathHelperLM.chunk(mc.thePlayer.posX), MathHelperLM.chunk(mc.thePlayer.posZ), p, e.right, shift);
		}
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
		LMWorldClient.inst = null;
	}
}