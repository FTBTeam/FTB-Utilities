package latmod.ftbu.util.client;

import cpw.mods.fml.client.registry.*;
import ftb.lib.FTBLib;
import ftb.lib.client.FTBLibClient;
import latmod.ftbu.api.client.callback.ClientTickCallback;
import latmod.ftbu.mod.client.FTBURenderHandler;
import latmod.ftbu.notification.Notification;
import latmod.ftbu.util.gui.IClientActionGui;
import latmod.ftbu.world.LMWorldClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.*;

/** Made by LatvianModder */
public final class LatCoreMCClient // LatCoreMC // FTBLibClient
{
	public static int displayW, displayH;
	
	public static void addEntityRenderer(Class<? extends Entity> c, Render r)
	{ RenderingRegistry.registerEntityRenderingHandler(c, r); }
	
	public static void addTileRenderer(Class<? extends TileEntity> c, TileEntitySpecialRenderer r)
	{ ClientRegistry.bindTileEntitySpecialRenderer(c, r); }
	
	public static int getNewArmorID(String s)
	{ return RenderingRegistry.addNewArmourRendererPrefix(s); }
	
	public static int getNewBlockRenderID()
	{ return RenderingRegistry.getNextAvailableRenderId(); }
	
	public static void addBlockRenderer(int i, ISimpleBlockRenderingHandler r)
	{ RenderingRegistry.registerBlockHandler(i, r); }
	
	public static void addItemRenderer(Item item, IItemRenderer i)
	{ MinecraftForgeClient.registerItemRenderer(item, i); }
	
	public static void addItemRenderer(Block block, IItemRenderer i)
	{ MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), i); }
	
	public static void addClientTickCallback(ClientTickCallback e)
	{ FTBURenderHandler.callbacks.add(e); }
	
	public static void notifyClient(String ID, Object text, int t)
	{ ClientNotifications.add(new Notification(ID, FTBLib.getChatComponent(text), t)); }
	
	public static void onGuiClientAction()
	{
		if(FTBLibClient.mc.currentScreen instanceof IClientActionGui)
			((IClientActionGui)FTBLibClient.mc.currentScreen).onClientDataChanged();
	}
	
	public static boolean isPlaying()
	{
		return FTBLibClient.mc.theWorld != null
		&& FTBLibClient.mc.thePlayer != null
		&& FTBLibClient.mc.thePlayer.worldObj != null
		&& LMWorldClient.inst != null
		&& LMWorldClient.inst.clientPlayer != null;
	}
	
	public static int getDim()
	{ return isPlaying() ? FTBLibClient.mc.thePlayer.worldObj.provider.dimensionId : 0; }
}