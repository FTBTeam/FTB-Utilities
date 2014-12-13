package latmod.latcore.client;
import java.util.UUID;

import latmod.core.*;
import latmod.core.client.LatCoreMCClient;
import latmod.core.net.CustomActionEvent;
import latmod.core.tile.IPaintable;
import latmod.latcore.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClientEventHandler
{
	public static final LCClientEventHandler instance = new LCClientEventHandler();
	
	public final FastMap<String, FastList<PlayerDecorator>> playerDecorators = new FastMap<String, FastList<PlayerDecorator>>();
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.itemStack == null || e.itemStack.getItem() == null) return;
		
		Item item = e.itemStack.getItem();
		
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
		
		if(item instanceof IPaintable.IPainterItem)
		{
			ItemStack paint = ((IPaintable.IPainterItem)item).getPaintItem(e.itemStack);
			if(paint != null) e.toolTip.add("Paint: " + paint.getDisplayName());
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
		if(e.action.equals(LCEventHandler.ACTION_PLAYER_JOINED))
		{
			LMPlayer p = LMPlayer.getPlayer(UUID.fromString(e.extraData.getString("UUID")));
			if(p != null)
			{
				EntityPlayer ep = p.getPlayer();
				
				if(ep != null)
				{
					FastList<PlayerDecorator> l = playerDecorators.get(ep.getCommandSenderName());
					
					if(l != null && l.size() > 0)
					{
						if(l.contains("latmod"))
							LatCoreMC.printChat(ep, "Hello, LatMod member!");
					}
					
					/*
					LatCoreMC.printChat(ep, "Looking for custom skin...");
					
					if(ep instanceof AbstractClientPlayer)
					{
						AbstractClientPlayer p = (AbstractClientPlayer)ep;
						
						//if(p.getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID()))
						//	p = Minecraft.getMinecraft().thePlayer;
						
						ResourceLocation customSkinLocation = LC.mod.getLocation("custom/skin/" + p.getUniqueID() + ".png");
						
						TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
				        Object object = texturemanager.getTexture(customSkinLocation);

				        if (object == null)
				        {
				            object = new ThreadDownloadImageData((File)null, "http://i.imgur.com/yFSexm0.png", AbstractClientPlayer.locationStevePng, new ImageBufferDownload());
				            texturemanager.loadTexture(customSkinLocation, (ITextureObject)object);
				        }
				        
						//AbstractClientPlayer.getDownloadImageSkin(customSkinLocation, "");
						p.func_152121_a(MinecraftProfileTexture.Type.SKIN, customSkinLocation);
						LatCoreMC.printChat(ep, "Set skin to " + p.getLocationSkin());
					}
					
					*/
				}
			}
		}
		else if(e.action.equals(LCEventHandler.ACTION_OPEN_URL))
			LatCore.openURL(e.extraData.getString("URL"));
		else if(e.action.equals(LCEventHandler.ACTION_RELOAD_PD))
			ThreadCheckPlayerDecorators.init();
	}
	
	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Specials.Post e)
	{
		if(LCConfig.Client.enablePlayerDecorators)
		{
			FastList<PlayerDecorator> l = playerDecorators.get(e.entityPlayer.getCommandSenderName());
			
			if(l != null && l.size() > 0)
			{
				for(int i = 0; i < l.size(); i++)
					l.get(i).onPlayerRender(e);
			}
		}
	}
}