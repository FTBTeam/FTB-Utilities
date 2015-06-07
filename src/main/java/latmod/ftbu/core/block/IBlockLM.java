package latmod.ftbu.core.block;

import latmod.ftbu.core.item.ItemBlockLM;

public interface IBlockLM
{
	public Class<? extends ItemBlockLM> getItemBlock();
	public String getItemID();
	public void onPostLoaded();
	public void loadRecipes();
}