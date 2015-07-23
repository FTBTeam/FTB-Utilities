package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.util.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Pos2D
{
	public final int x, y;
	
	public Pos2D(int px, int py)
	{ x = px; y = py; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || (o.getClass() == Pos2D.class && equalsPos((Pos2D)o))); }
	
	public boolean equalsPos(Pos2D p)
	{ return p != null && p.x == x && p.y == y; }
	
	public int hashCode()
	{ return LatCore.hashCode(x, y); }
	
	public String toString()
	{ return LMStringUtils.stripI(x, y); }
}