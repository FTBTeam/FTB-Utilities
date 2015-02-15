package latmod.core;
import java.awt.Color;

import latmod.core.mod.LC;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.StatCollector;

public enum EnumDyeColor // ItemDye
{
	BLACK("Black", 0xFF262626),
	RED("Red", 0xFFE01414),
	GREEN("Green", 0xFF00980E),
	BROWN("Brown", 0xFF934E23),
	BLUE("Blue", 0xFF004CC4),
	PURPLE("Purple", 0xFF9A41E2),
	CYAN("Cyan", 0xFF00AEFF),
	LIGHT_GRAY("LightGray", 0xFFC0C0C0),
	GRAY("Gray", 0xFF636363),
	PINK("Pink", 0xFFFF7F7F),
	LIME("Lime", 0xFF2BE541),
	YELLOW("Yellow", 0xFFFFD500),
	LIGHT_BLUE("LightBlue", 0xFF63BEFF),
	MAGENTA("Magenta", 0xFFFF00DC),
	ORANGE("Orange", 0xFFFF952B),
	WHITE("White", 0xFFEFEFEF);
	
	public static final EnumDyeColor[] VALUES = values();
	
	public final int ID;
	public final String name;
	public final String lang;
	public final Color color;
	public final Color colorBright;
	public final String dyeName;
	public final String glassName;
	public final String paneName;

	EnumDyeColor(String s, int c)
	{
		ID = ordinal();
		name = ItemDye.field_150921_b[ID];
		lang = LC.mod.assets + "color." + name;
		color = new Color(ItemDye.field_150922_c[ID]);
		colorBright = new Color(c);
		
		dyeName = "dye" + s;
		glassName = "blockGlass" + s;
		paneName = "paneGlass" + s;
	}

	public String toString()
	{ return StatCollector.translateToLocal(lang); }
	
	public ItemStack getDye()
	{ return new ItemStack(Items.dye, 1, ID); }
}