package latmod.ftbu.core.paint;

import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.*;

public interface ICustomPaintBlockIcon
{
	@SideOnly(Side.CLIENT)
	public IIcon getCustomPaintIcon(int side, Paint p);
}