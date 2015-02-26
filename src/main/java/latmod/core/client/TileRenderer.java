package latmod.core.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class TileRenderer<T extends TileEntity> extends TileEntitySpecialRenderer
{
	@SuppressWarnings("unchecked")
	public final void renderTileEntityAt(TileEntity te, double rx, double ry, double rz, float f)
	{ if(te != null && !te.isInvalid()) renderTile((T)te, rx, ry, rz, f); }
	
	public abstract void renderTile(T t, double rx, double ry, double rz, float f);
	
	public final void register(Class<? extends T> c)
	{ LatCoreMCClient.addTileRenderer(c, this); }
}