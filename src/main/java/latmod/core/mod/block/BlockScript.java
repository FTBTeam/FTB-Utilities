package latmod.core.mod.block;

import latmod.core.mod.LC;
import latmod.core.mod.tile.*;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockScript extends BlockLC
{
	public BlockScript(String s)
	{
		super(s, Material.iron);
		setHardness(-1F);
		setResistance(Float.MAX_VALUE);
		isBlockContainer = true;
		LC.mod.addTile(TileScript.class, s);
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return new TileScript(); }
}