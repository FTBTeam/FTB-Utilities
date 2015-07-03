package latmod.ftbu.core.gui;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public interface IClientActionGui
{
	public void onClientAction(String action);
}