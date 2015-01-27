package latmod.core;
import java.awt.Color;

import latmod.core.mod.LC;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.StatCollector;

public enum EnumDyeColor // ItemDye
{
	BLACK("Black"),
	RED("Red"),
	GREEN("Green"),
	BROWN("Brown"),
	BLUE("Blue"),
	PURPLE("Purple"),
	CYAN("Cyan"),
	LIGHT_GRAY("LightGray"),
	GRAY("Gray"),
	PINK("Pink"),
	LIME("Lime"),
	YELLOW("Yellow"),
	LIGHT_BLUE("LightBlue"),
	MAGENTA("Magenta"),
	ORANGE("Orange"),
	WHITE("White");
	
	public static final EnumDyeColor[] VALUES = values();
	
	public final int ID;
	public final String name;
	public final String lang;
	public final Color color;
	public final String dyeName;
	public final String glassName;
	public final String paneName;

	EnumDyeColor(String s)
	{
		ID = ordinal();
		name = ItemDye.field_150921_b[ID];
		lang = LC.mod.assets + "color." + name;
		color = new Color(ItemDye.field_150922_c[ID]);
		
		dyeName = "dye" + s;
		glassName = "blockGlass" + s;
		paneName = "paneGlass" + s;
	}

	public String toString()
	{ return StatCollector.translateToLocal(lang); }
	
	public ItemStack getDye()
	{ return new ItemStack(Items.dye, 1, ID); }
}