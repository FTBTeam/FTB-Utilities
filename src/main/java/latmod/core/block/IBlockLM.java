package latmod.core.block;

import latmod.core.item.ItemBlockLM;

public interface IBlockLM
{
	public Class<? extends ItemBlockLM> getItemBlock();
	public String getItemID();
	public void onPostLoaded();
	public void loadRecipes();
}