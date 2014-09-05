package latmod.core.mod.block;
import latmod.core.mod.*;
import latmod.core.mod.tile.TileLM;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockFacade extends BlockLC
{
	public BlockFacade(String s)
	{
		super(s, Material.rock);
		setHardness(0.3F);
		isBlockContainer = false;
		setBlockBounds(0F, 0F, 0F, 1F / 8F, 1F, 1F);
	}
	
	public boolean canPlaceBlockAt(World w, int x, int y, int z)
	{ return false; }
	
	public boolean canPlaceBlockOnSide(World w, int x, int y, int z, int side)
	{ return false; }
	
	public void loadRecipes()
	{
		LC.mod.recipes().addRecipe(new ItemStack(this, 8), "W",
				'W', LCItems.b_paintable);
		
		LC.mod.recipes().addRecipe(new ItemStack(LCItems.b_paintable), "WWW", "W W", "WWW",
				'W', this);
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return null; }
}