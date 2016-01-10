package latmod.ftbu.block;

import latmod.ftbu.item.ItemBlockLM;

public interface IBlockLM
{
	public Class<? extends ItemBlockLM> getItemBlock();
	
	public String getItemID();
	
	public void onPostLoaded();
	
	public void loadRecipes();
}