package ftb.utils.api.paint;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

public class PaintData
{
	public final EntityPlayer player;
	
	public final BlockPos pos;
	
	public final float hitX;
	public final float hitY;
	public final float hitZ;
	
	public final EnumFacing side;
	public final int subHit;
	
	public final Paint paint;
	
	public PaintData(EntityPlayer ep, Paint p, BlockPos ps, float hx, float hy, float hz, EnumFacing s, int sh)
	{
		player = ep;
		paint = p;
		pos = ps;
		hitX = hx;
		hitY = hx;
		hitZ = hx;
		side = s;
		subHit = sh;
	}
	
	public boolean canReplace(Paint p)
	{
		if(p == null && paint == null) return false;
		if(p == null && paint != null) return true;
		if(p != null && paint == null) return true;
		return p.block != paint.block || (p.block == paint.block && p.meta != paint.meta);
	}
}