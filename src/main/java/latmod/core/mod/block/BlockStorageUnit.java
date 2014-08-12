package latmod.core.mod.block;
import latmod.core.ODItems;
import latmod.core.mod.LC;
import latmod.core.mod.tile.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockStorageUnit extends BlockLC
{
	public BlockStorageUnit(String s)
	{
		super(s, Material.wood);
		setHardness(0.7F);
		isBlockContainer = true;
		mod.addTile(TileStorageUnit.class, s);
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return new TileStorageUnit(); }
	
	public void loadRecipes()
	{
		LC.recipes.addRecipe(new ItemStack(this), "SSS", "S S", "SSS",
				'S', ODItems.STICK);
	}
}