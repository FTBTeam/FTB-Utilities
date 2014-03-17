package mods.lm_core.client;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.client.*;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LatCoreClient
{
	public static final void addEntityRenderer(Class<? extends Entity> c, Render r)
	{ RenderingRegistry.registerEntityRenderingHandler(c, r); }
	
	public static final void addTileRenderer(Class<? extends TileEntity> c, TileEntitySpecialRenderer r)
	{ r.setTileEntityRenderer(TileEntityRenderer.instance); ClientRegistry.bindTileEntitySpecialRenderer(c, r); }
	
	public static final int getNewArmorID(String s)
	{ return RenderingRegistry.addNewArmourRendererPrefix(s); }
	
	public static final int getNewBlockRenderID()
	{ return RenderingRegistry.getNextAvailableRenderId(); }
	
	public static final void addBlockRenderer(int i, ISimpleBlockRenderingHandler r)
	{ RenderingRegistry.registerBlockHandler(i, r); }

	public static final void addItemRenderer(int itemID, IItemRenderer i)
	{ MinecraftForgeClient.registerItemRenderer(itemID, i); }
}