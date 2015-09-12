package latmod.ftbu.core;
import latmod.ftbu.mod.FTBU;
import net.minecraft.init.Items;
import net.minecraft.item.*;

public enum EnumDyeColor // ItemDye
{
	BLACK("Black", 0xFF3F3F3F),
	RED("Red", 0xFFFF0000),
	GREEN("Green", 0xFF009B0E),
	BROWN("Brown", 0xFFA35C2D),
	BLUE("Blue", 0xFF004CC4),
	PURPLE("Purple", 0xFF9A41E2),
	CYAN("Cyan", 0xFF00D8C6),
	LIGHT_GRAY("LightGray", 0xFFBCBCBC),
	GRAY("Gray", 0xFF636363),
	PINK("Pink", 0xFFFF95A3),
	LIME("Lime", 0xFF00FF2E),
	YELLOW("Yellow", 0xFFFFD500),
	LIGHT_BLUE("LightBlue", 0xFF63BEFF),
	MAGENTA("Magenta", 0xFFFF006E),
	ORANGE("Orange", 0xFFFF9500),
	WHITE("White", 0xFFFFFFFF);
	
	public static final EnumDyeColor[] VALUES = values();
	
	public final int ID;
	public final String lang;
	public final String name;
	public final int color;
	public final int colorBright;
	public final String dyeName;
	public final String glassName;
	public final String paneName;

	EnumDyeColor(String s, int c)
	{
		ID = ordinal();
		name = ItemDye.field_150921_b[ID];
		lang = "color." + name;
		color = ItemDye.field_150922_c[ID];
		colorBright = c;
		
		dyeName = "dye" + s;
		glassName = "blockGlass" + s;
		paneName = "paneGlass" + s;
	}

	public String toString()
	{ return FTBU.mod.translateClient(lang); }
	
	public ItemStack getDye()
	{ return new ItemStack(Items.dye, 1, ID); }
}