package latmod.core.mod.client;
import latmod.core.*;
import latmod.core.client.LatCoreMCClient;
import latmod.core.client.playerdeco.*;
import latmod.core.event.*;
import latmod.core.gui.GuiLM;
import latmod.core.mod.*;
import latmod.core.net.*;
import latmod.core.tile.IPaintable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClientEventHandler
{
	public static final LCClientEventHandler instance = new LCClientEventHandler();
	
	public final FastMap<String, FastList<PlayerDecorator>> playerDecorators = new FastMap<String, FastList<PlayerDecorator>>();
	public final FastList<GuiNotification> messages = new FastList<GuiNotification>();
	
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
		
		if(e.showAdvancedItemTooltips || !LCConfig.Client.onlyAdvanced)
		{
			if(LCConfig.Client.addRegistryNames)
			{
				e.toolTip.add(LatCoreMC.getRegName(e.itemStack));
			}
			
			if(LCConfig.Client.addOreNames)
			{
				FastList<String> ores = ODItems.getOreNames(e.itemStack);
				
				if(ores != null && !ores.isEmpty())
				{
					e.toolTip.add("Ore Dictionary names:");
					for(String or : ores)
					e.toolTip.add("> " + or);
				}
			}
			
			if(LCConfig.Client.addFluidContainerNames)
			{
				FluidStack fs = LatCoreMC.getFluid(e.itemStack);
				
				if(fs != null && fs.amount > 0)
				{
					e.toolTip.add("Stored FluidID:");
					e.toolTip.add(FluidRegistry.getFluidName(fs.fluidID));
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void preTexturesLoaded(TextureStitchEvent.Pre e)
	{
		if(e.map.getTextureType() == 0)
			LatCoreMCClient.blockNullIcon = e.map.registerIcon(LC.mod.assets + "nullIcon");
	}
	
	@SubscribeEvent
	public void onCustomAction(CustomActionEvent e)
	{
		if(e.action.equals(LCEventHandler.ACTION_OPEN_FRIENDS_GUI))
			Minecraft.getMinecraft().displayGuiScreen(new GuiFriends(e.player));
	}
	
	@SubscribeEvent
	public void playerJoined(LMPlayerEvent.LoggedIn e)
	{
		if(e.side.isClient() && e.entityPlayer != null)
		{
			FastList<PlayerDecorator> l = playerDecorators.get(e.entityPlayer.getCommandSenderName());
			
			if(l != null) for(int i = 0; i < l.size(); i++)
			{
				if(l.get(i) instanceof PDLatMod)
					LatCoreMC.printChat(e.entityPlayer, EnumChatFormatting.BLUE + "Hello, LatMod member!");
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(LCConfig.Client.enablePlayerDecorators && !e.entityPlayer.isInvisible())
		{
			FastList<PlayerDecorator> l = playerDecorators.get(e.entityPlayer.getCommandSenderName());
			
			if(l != null && l.size() > 0)
			{
				for(int i = 0; i < l.size(); i++)
					l.get(i).onPlayerRender(e);
			}
		}
	}
	
	@SubscribeEvent
	public void onReload(ReloadEvent r)
	{
		if(r.side.isClient())
		{
			ThreadCheckPlayerDecorators.init();
		}
	}
	
	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent e)
	{
		if(LCClient.key.isPressed() && LC.proxy.inGameHasFocus())
		{
			EntityPlayer ep = LC.proxy.getClientPlayer();
			
			if (ep != null && ep.worldObj.isRemote)
			{
				FastList<Key> l = Key.toList(null);
				new LMKeyEvent(Side.CLIENT, l, ep).post();
				MessageLM.NET.sendToServer(new MessageLMKeyPressed(l));
			}
		}
	}
	
	@SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if(mc.theWorld != null && event.phase == TickEvent.Phase.END && !messages.isEmpty())
        {
        	GuiNotification m = messages.get(0);
        	m.render(mc); if(m.isDead()) messages.remove(0);
        }
    }
	
	@SubscribeEvent
	public void onIconsLoaded(TextureStitchEvent.Pre e)
	{
		if(e.map.getTextureType() == LCClient.iconsTextureMap.getTextureType())
		{
			LoadLMIconsEvent ev = new LoadLMIconsEvent(e.map);
			GuiLM.Icons.load(ev);
			ev.post();
			LatCoreMC.logger.info("Loaded " + ev.texturesLoaded() + " LMIcons");
		}
	}
}