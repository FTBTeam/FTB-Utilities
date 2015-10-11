package latmod.ftbu.badges;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class BadgeEmpty extends Badge
{
	public BadgeEmpty()
	{ super("_empty_"); }
	
	public ResourceLocation getTexture()
	{ return null; }
}