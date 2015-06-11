package latmod.ftbu.core.client;

import java.util.UUID;

import latmod.ftbu.mod.FTBU;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.*;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.*;

/** Made by LatvianModder */
public final class LatCoreMCClient // LatCoreMC
{
	public static IIcon blockNullIcon, unknownItemIcon;
	private static float lastBrightnessX, lastBrightnessY;
	
	public static Minecraft getMinecraft()
	{ return FMLClientHandler.instance().getClient(); }
	
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
	
	public static void spawnPart(EntityFX e)
	{ Minecraft.getMinecraft().effectRenderer.addEffect(e); }
	
	public static KeyBinding addKeyBinding(String name, int key, String cat)
	{ KeyBinding k = new KeyBinding(name, key, cat); ClientRegistry.registerKeyBinding(k); return k; }
	
	public static void pushMaxBrightness()
	{
		lastBrightnessX = OpenGlHelper.lastBrightnessX;
		lastBrightnessY = OpenGlHelper.lastBrightnessY;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
	}
	
	public static void popMaxBrightness()
	{ OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY); }

	public static EntityPlayerSP getPlayerSP(UUID uuid)
	{
		World w = FTBU.proxy.getClientWorld();
		
		if(w != null)
		{
			EntityPlayer ep = w.func_152378_a(uuid);
			if(ep != null && ep instanceof EntityPlayerSP)
				return (EntityPlayerSP)ep;
		}
		
		return null;
	}
}