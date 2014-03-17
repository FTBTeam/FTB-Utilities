package mods.lm_core;
import net.minecraftforge.common.ForgeDirection;
import static net.minecraftforge.common.ForgeDirection.*;

/** Still WIP */
public enum SidedDirection
{
	//Forge:DOWN, 	UP, 	NORTH, 	SOUTH, 	WEST, 	EAST
	Bottom(	NORTH, 	SOUTH, 	DOWN, 	DOWN, 	DOWN, 	DOWN),
	Top(	SOUTH, 	NORTH, 	UP, 	UP, 	UP, 	UP),
	Back(	UP, 	DOWN, 	SOUTH, 	NORTH, 	EAST, 	WEST),
	Front(	DOWN, 	UP, 	NORTH, 	SOUTH, 	WEST, 	EAST),
	Left(	WEST, 	EAST, 	EAST, 	WEST, 	NORTH, 	SOUTH),
	Right(	EAST, 	WEST, 	WEST, 	EAST, 	SOUTH, 	NORTH),
	
	None(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
	
	public final ForgeDirection[] directions;
	public final int index;
	
	SidedDirection(ForgeDirection... f)
	{ directions = f; index = ordinal(); }
	
	public ForgeDirection getDir(ForgeDirection rot)
	{ return getDir(rot.ordinal()); }
	
	public ForgeDirection getDir(int rot)
	{ return directions[rot]; }
	
	// Static //
	
	public static final SidedDirection[] VALUES = new SidedDirection[]{ Bottom, Top, Back, Front, Left, Right };
	
	public static SidedDirection getSide(ForgeDirection rot, ForgeDirection side)
	{
		for(int i = 0; i < VALUES.length; i++)
		if(VALUES[i].getDir(rot) == side) return VALUES[i];
		return None;
	}
}