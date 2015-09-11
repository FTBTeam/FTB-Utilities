package latmod.ftbu.core.client;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public abstract class TileRenderer<T extends TileEntity> extends TileEntitySpecialRenderer
{
	@SuppressWarnings("unchecked")
	public final void renderTileEntityAt(TileEntity te, double rx, double ry, double rz, float pt)
	{ if(te != null && !te.isInvalid()) renderTile((T)te, rx, ry, rz, pt); }
	
	public abstract void renderTile(T t, double rx, double ry, double rz, float pt);
	
	public final void register(Class<? extends T> c)
	{ LatCoreMCClient.addTileRenderer(c, this); }
	
	public final void registerItemRenderer(Block b)
	{ if(this instanceof IItemRenderer) LatCoreMCClient.addItemRenderer(b, (IItemRenderer)this); }
}