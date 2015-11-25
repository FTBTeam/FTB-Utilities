package latmod.ftbu.mod.client;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.*;
import ftb.lib.MathHelperMC;
import ftb.lib.client.FTBLibClient;
import ftb.lib.item.*;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.api.client.EventFTBUKey;
import latmod.ftbu.api.paint.IPainterItem;
import latmod.ftbu.mod.client.gui.friends.GuiFriendsGuiSmall;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
		
		//if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
		//	e.toolTip.add(EnumChatFormatting.RED + "Banned item");
	}
	
	@SubscribeEvent
	public void onDrawDebugText(RenderGameOverlayEvent.Text e)
	{
		// Some ideas around this //
		if(!FTBLibClient.mc.gameSettings.showDebugInfo)
		{
			if(FTBUClient.displayDebugInfo.getB())
				e.left.add(FTBLibClient.mc.debug);
			
			if(FTBLibFinals.DEV)
				e.left.add("[MC " + EnumChatFormatting.GOLD + Loader.MC_VERSION + EnumChatFormatting.WHITE + " DevEnv]");
		}
		else
		{
			e.left.add("r: " + MathHelperMC.get2DRotation(FTBLibClient.mc.thePlayer));
			
			if(FTBUClient.displayDebugInfo.getB())
			{
				e.right.add("r: " + MathHelperMC.get2DRotation(FTBLibClient.mc.thePlayer));
				
				MovingObjectPosition mop = FTBLibClient.mc.objectMouseOver;
				
				if(mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
				{
					e.right.add(null);
					e.right.add("Block: " + LMStringUtils.stripI(mop.blockX, mop.blockY, mop.blockZ) + ", Side: " + mop.sideHit);
					e.right.add(LMInvUtils.getRegName(FTBLibClient.mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ)) + " :: " + FTBLibClient.mc.theWorld.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityRightClick(EntityInteractEvent e)
	{
		if(e.entity.worldObj.isRemote && LatCoreMCClient.isPlaying() && FTBUClient.playerOptionsShortcut.getB() && e.entityPlayer.getUniqueID().equals(FTBLibClient.mc.thePlayer.getUniqueID()))
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