package latmod.ftbu.mod.client;

import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.paint.IPainterItem;
import latmod.ftbu.mod.client.gui.friends.GuiFriendsGuiSmall;
import latmod.ftbu.world.*;
import net.minecraft.item.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.*;

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
			ItemStack paint = ((IPainterItem) item).getPaintItem(e.itemStack);
			if(paint != null)
				e.toolTip.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.BOLD + paint.getDisplayName());
		}
		
		//if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
		//	e.toolTip.add(EnumChatFormatting.RED + "Banned item");
	}
	
	@SubscribeEvent
	public void onEntityRightClick(EntityInteractEvent e)
	{
		if(e.entity.worldObj.isRemote && FTBLibClient.isPlayingWithFTBU() && FTBUClient.player_options_shortcut.get() && e.entityPlayer.getUniqueID().equals(FTBLibClient.mc.thePlayer.getUniqueID()))
		{
			LMPlayerClient p = LMWorldClient.inst.getPlayer(e.target);
			if(p != null) FTBLibClient.mc.displayGuiScreen(new GuiFriendsGuiSmall(p));
		}
	}
}