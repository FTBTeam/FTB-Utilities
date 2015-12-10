package latmod.ftbu.mod.client;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.*;
import ftb.lib.client.FTBLibClient;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.api.client.EventFTBUKey;
import latmod.ftbu.api.paint.IPainterItem;
import latmod.ftbu.mod.client.gui.friends.GuiFriendsGuiSmall;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import net.minecraft.item.*;
import net.minecraft.util.EnumChatFormatting;
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
		
		//if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
		//	e.toolTip.add(EnumChatFormatting.RED + "Banned item");
	}
	
	@SubscribeEvent
	public void onEntityRightClick(EntityInteractEvent e)
	{
		if(e.entity.worldObj.isRemote && LatCoreMCClient.isPlaying() && FTBUClient.player_options_shortcut.get() && e.entityPlayer.getUniqueID().equals(FTBLibClient.mc.thePlayer.getUniqueID()))
		{
			LMPlayerClient p = LMWorldClient.inst.getPlayer(e.target);
			if(p != null) FTBLibClient.mc.displayGuiScreen(new GuiFriendsGuiSmall(p));
		}
	}
	
	@SubscribeEvent
	public void onKeyEvent(InputEvent.KeyInputEvent e)
	{
		if(FTBLibFinals.DEV)
		{
			int key = Keyboard.getEventKey();
			
			if(key != Keyboard.KEY_NONE && key != Keyboard.KEY_ESCAPE)
			{
				boolean pressed = Keyboard.getEventKeyState();
				new EventFTBUKey(key, pressed).post();
			}
		}
	}
}