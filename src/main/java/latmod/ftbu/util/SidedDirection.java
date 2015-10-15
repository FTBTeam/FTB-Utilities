package latmod.ftbu.util;

import net.minecraft.util.Facing;

public enum SidedDirection
{
	BOTTOM(2, 3, 0, 0, 0, 0),
	TOP   (3, 2, 1, 1, 1, 1),
	BACK  (1, 0, 3, 2, 5, 4),
	FRONT (0, 1, 2, 3, 4, 5),
	LEFT  (4, 5, 5, 4, 2, 3),
	RIGHT (5, 4, 4, 5, 3, 2),
	NONE  (6, 6, 6, 6, 6, 6);
	
	/** -Y */ public static final int DOWN = 0;
	/** +Y */ public static final int UP = 1;
	/** -Z */ public static final int NORTH = 2;
	/** +Z */ public static final int SOUTH = 3;
	/** -X */ public static final int WEST = 4;
	/** +X */ public static final int EAST = 5;
	
	public final int[] directions;
	public final int ID;
	
	SidedDirection(int... i)
	{
		directions = i;
		ID = ordinal();
	}
	
	// Static //
	
	public static final SidedDirection[] VALUES = new SidedDirection[] { BOTTOM, TOP, BACK, FRONT, LEFT, RIGHT };
	
	public static SidedDirection getSide(int side, int rot)
	{
		if(rot < 0 || rot >= 6 || side < 0 || side >= 6) return NONE;
		for(int i = 0; i < VALUES.length; i++)
		if(VALUES[i].directions[rot] == side) return VALUES[i];
		return NONE;
	}
	
	public static SidedDirection get(int side, int rot3D, int rot2D)
	{
		if(side == rot3D) return FRONT;
		if(side == Facing.oppositeSide[rot3D]) return BACK;
		
		if(rot3D == 0)
		{
			if(side == 2 || side == 3 || side == 4 || side == 5)
			{
				if(rot2D == side) return TOP;
				else if(rot2D == Facing.oppositeSide[side]) return BOTTOM;
			}
			
			return getSide(side, rot2D);
		}
		else if(rot3D == 1)
		{
			if(side == 2 || side == 3 || side == 4 || side == 5)
			{
				if(rot2D == side) return BOTTOM;
				else if(rot2D == Facing.oppositeSide[side]) return TOP;
			}
			
			return getSide(side, rot2D);
		}
		else
		{
			if(side == 0) return BOTTOM;
			else if(side == 1) return TOP;
			return getSide(side, rot3D);
		}
	}
}