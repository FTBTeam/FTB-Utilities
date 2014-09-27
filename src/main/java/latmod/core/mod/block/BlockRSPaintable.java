package latmod.core.mod.block;
import latmod.core.ODItems;
import latmod.core.mod.*;
import latmod.core.mod.tile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRSPaintable extends BlockPaintable
{
	public BlockRSPaintable(String s)
	{
		super(s);
	}
	
	public void loadRecipes()
	{
		LC.mod.recipes().addRecipe(new ItemStack(this, 8), "PPP", "PRP", "PPP",
				'P', LCItems.b_paintable,
				'R', ODItems.REDSTONE);
		
		LC.mod.recipes().addRecipe(new ItemStack(LCItems.b_paintable), "F",
				'F', LCItems.b_rs_paintable);
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return new TileRSPaintable(); }
}