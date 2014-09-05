package latmod.core.mod.block;
import latmod.core.ODItems;
import latmod.core.mod.LC;
import latmod.core.mod.tile.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockPaintable extends BlockLC
{
	public static int renderID = -1;
	
	public BlockPaintable(String s)
	{
		super(s, Material.rock);
		setHardness(0.3F);
		isBlockContainer = true;
		LC.mod.addTile(TilePaintable.class, s);
	}
	
	public boolean canHarvestBlock(EntityPlayer ep, int meta)
	{ return true; }
	
	public void loadRecipes()
	{
		LC.mod.recipes().addRecipe(new ItemStack(this, 16), "WWW", "WPW", "WWW",
				'W', new ItemStack(Blocks.wool, 1, 0),
				'P', ODItems.PLANKS);
	}
	
	public TileLM createNewTileEntity(World w, int m)
	{ return new TilePaintable(); }
	
	public int getRenderType()
	{ return renderID; }
}