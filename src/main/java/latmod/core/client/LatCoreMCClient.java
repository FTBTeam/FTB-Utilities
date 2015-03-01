package latmod.core.client;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.*;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LatCoreMCClient
{
	public static final int FRONT = ForgeDirection.SOUTH.ordinal();
	
	public static IIcon blockNullIcon = null;
	
	public static final void addEntityRenderer(Class<? extends Entity> c, Render r)
	{ RenderingRegistry.registerEntityRenderingHandler(c, r); }
	
	public static final void addTileRenderer(Class<? extends TileEntity> c, TileEntitySpecialRenderer r)
	{ ClientRegistry.bindTileEntitySpecialRenderer(c, r); }
	
	public static final int getNewArmorID(String s)
	{ return RenderingRegistry.addNewArmourRendererPrefix(s); }
	
	public static final int getNewBlockRenderID()
	{ return RenderingRegistry.getNextAvailableRenderId(); }
	
	public static final void addBlockRenderer(int i, ISimpleBlockRenderingHandler r)
	{ RenderingRegistry.registerBlockHandler(i, r); }

	public static final void addItemRenderer(Item item, IItemRenderer i)
	{ MinecraftForgeClient.registerItemRenderer(item, i); }
	
	public static final void addItemRenderer(Block block, IItemRenderer i)
	{ MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), i); }
	
	public static void spawnPart(EntityFX e)
	{ Minecraft.getMinecraft().effectRenderer.addEffect(e); }

	public static KeyBinding addKeyBinding(String name, int key, String cat)
	{ KeyBinding k = new KeyBinding(name, key, cat); ClientRegistry.registerKeyBinding(k); return k; }
}