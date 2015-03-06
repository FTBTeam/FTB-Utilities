package latmod.core.client;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public interface IResourceReloader
{
	public void reloadResources();
}