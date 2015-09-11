package latmod.ftbu.core.paint;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.IIcon;

public interface ICustomPaintBlockIcon
{
	@SideOnly(Side.CLIENT)
	public IIcon getCustomPaintIcon(int side, Paint p);
}