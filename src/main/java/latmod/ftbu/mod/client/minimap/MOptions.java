package latmod.ftbu.mod.client.minimap;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class MOptions
{
	public boolean hasBlur()
	{ return false; }
	
	public boolean hasGrid()
	{ return false; }
	
	public boolean calcHeight()
	{ return true; }
	
	public boolean customColors()
	{ return true; }
}