package latmod.core.block;

import net.minecraft.block.Block;

public interface IBlockLM
{
	public Block getBlock();
	public String getBlockID();
	public void onPostLoaded();
	public void loadRecipes();
}