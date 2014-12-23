package latmod.core.mod.client;
import java.util.UUID;

import latmod.core.*;
import latmod.core.client.LatCoreMCClient;
import latmod.core.client.playerdeco.*;
import latmod.core.event.ReloadEvent;
import latmod.core.mod.*;
import latmod.core.net.CustomActionEvent;
import latmod.core.tile.IPaintable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.*;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

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
					
					if(l != null) for(int i = 0; i < l.size(); i++)
					{
						if(l.get(i) instanceof PDLatMod)
							LatCoreMC.printChat(ep, EnumChatFormatting.BLUE + "Hello, LatMod member!");
					}
				}
			}
		}
		else if(e.action.equals(LCEventHandler.ACTION_OPEN_URL))
			LatCore.openURL(e.extraData.getString("URL"));
		else if(e.action.equals(LMPlayer.Custom.SKIN.key))
			updateSkin(UUID.fromString(e.extraData.getString("UUID")));
		else if(e.action.equals(ReloadEvent.ACTION))
			new ReloadEvent(Side.CLIENT).post();
	}
	
	public void updateSkin(UUID id)
	{
		EntityPlayer ep = Minecraft.getMinecraft().theWorld.func_152378_a(id);
		if(ep == null || !(ep instanceof AbstractClientPlayer)) return;
		
		String s = LMPlayer.getPlayer(ep).getCustom(LMPlayer.Custom.SKIN);
		if(s == null) s = "http://skins.minecraft.net/MinecraftSkins/" + ep.getCommandSenderName() + ".png";
		
		ResourceLocation loc = LC.mod.getLocation("custom_skin_" + ep.getUniqueID() + ".png");
		Minecraft.getMinecraft().getTextureManager().loadTexture(loc, new ThreadDownloadImageData(null, s, AbstractClientPlayer.locationStevePng, new CustomSkinBufferDownload()));
		((AbstractClientPlayer)ep).func_152121_a(MinecraftProfileTexture.Type.SKIN, loc);
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
}