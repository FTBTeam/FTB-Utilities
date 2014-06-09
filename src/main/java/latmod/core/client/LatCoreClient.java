package latmod.core.client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.entity.*;
import net.minecraft.item.Item;
import net.minecraft.tileentity.*;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.*;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LatCoreClient
{
	public static IIcon blockNullIcon = null;
	
	public static final void addEntityRenderer(Class<? extends Entity> c, Render r)
	{ RenderingRegistry.registerEntityRenderingHandler(c, r); }
	
	public static final void addTileRenderer(Class<? extends TileEntity> c, TileEntitySpecialRenderer r)
	{ r.func_147497_a(TileEntityRendererDispatcher.instance); ClientRegistry.bindTileEntitySpecialRenderer(c, r); }
	
	public static final int getNewArmorID(String s)
	{ return RenderingRegistry.addNewArmourRendererPrefix(s); }
	
	public static final int getNewBlockRenderID()
	{ return RenderingRegistry.getNextAvailableRenderId(); }
	
	public static final void addBlockRenderer(int i, ISimpleBlockRenderingHandler r)
	{ RenderingRegistry.registerBlockHandler(i, r); }

	public static final void addItemRenderer(Item item, IItemRenderer i)
	{ MinecraftForgeClient.registerItemRenderer(item, i); }
	
	public static void spawnPart(EntityFX e)
	{ Minecraft.getMinecraft().effectRenderer.addEffect(e); }
}