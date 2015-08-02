package latmod.ftbu.core;

import net.minecraft.util.Facing;

/** Still WIP */
public enum SidedDirection
{
	BOTTOM(2, 3, 0, 0, 0, 0),
	TOP   (3, 2, 1, 1, 1, 1),
	BACK  (1, 0, 3, 2, 5, 4),
	FRONT (0, 1, 2, 3, 4, 5),
	LEFT  (4, 5, 5, 4, 2, 3),
	RIGHT (5, 4, 4, 5, 3, 2),
	NONE  (6, 6, 6, 6, 6, 6);
	
	public final int[] directions;
	public final int ID;
	
	SidedDirection(int... i)
	{
		directions = i;
		ID = ordinal();
	}
	
	// Static //
	
	public static final SidedDirection[] VALUES = new SidedDirection[] { BOTTOM, TOP, BACK, FRONT, LEFT, RIGHT };
	
	public static SidedDirection getSide(int rot, int side)
	{
		if(rot < 0 || rot >= 6 || side < 0 || side >= 6) return NONE;
		for(int i = 0; i < VALUES.length; i++)
		if(VALUES[i].directions[rot] == side) return VALUES[i];
		return NONE;
	}
	
	public static SidedDirection get(int f, int r3, int r2)
	{
		if(f == r3) return FRONT;
		if(f == Facing.oppositeSide[r3]) return BACK;
		
		if(r3 == 0)
		{
			if(f == 2 || f == 3 || f == 4 || f == 5)
			{
				if(r2 == f) return TOP;
				else if(r2 == Facing.oppositeSide[f]) return BOTTOM;
			}
			
			return getSide(r2, f);
		}
		else if(r3 == 1)
		{
			if(f == 2 || f == 3 || f == 4 || f == 5)
			{
				if(r2 == f) return BOTTOM;
				else if(r2 == Facing.oppositeSide[f]) return TOP;
			}
			
			return getSide(r2, f);
		}
		else
		{
			if(f == 0) return BOTTOM;
			else if(f == 1) return TOP;
			return getSide(r3, f);
		}
	}
}