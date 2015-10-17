package latmod.ftbu.api.paint;

import net.minecraft.entity.player.EntityPlayer;

public class PaintData
{
	public final EntityPlayer player;
	
	public final int posX;
	public final int posY;
	public final int posZ;
	
	public final float hitX;
	public final float hitY;
	public final float hitZ;
	
	public final int side;
	public final int subHit;
	
	public final Paint paint;
	
	public PaintData(EntityPlayer ep, Paint p, int x, int y, int z, float hx, float hy, float hz, int s, int sh)
	{
		player = ep; paint = p;
		posX = x; posY = y; posZ = z;
		hitX = hx; hitY = hx; hitZ = hx;
		side = s; subHit = sh;
	}
	
	public boolean canReplace(Paint p)
	{
		if(p == null && paint == null) return false;
		if(p == null && paint != null) return true;
		if(p != null && paint == null) return true;
		return p.block != paint.block || (p.block == paint.block && p.meta != paint.meta);
	}
}