package latmod.core;
import latmod.core.mod.*;
import latmod.core.util.LMCommon;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.StatCollector;

public enum EnumDyeColor // ItemDye
{
	BLACK,
	RED,
	GREEN,
	BROWN,
	BLUE,
	PURPLE,
	CYAN,
	LIGHT_GRAY,
	GRAY,
	PINK,
	LIME,
	YELLOW,
	LIGHT_BLUE,
	MAGENTA,
	ORANGE,
	WHITE;
	
	public static final EnumDyeColor[] VALUES = values();
	
	public final int ID;
	public final String name;
	public final String lang;
	public final int color;
	public final String oreName;

	EnumDyeColor()
	{
		ID = ordinal();
		name = ItemDye.field_150921_b[ID];
		lang = LC.mod.assets + "color." + name;
		color = ItemDye.field_150922_c[ID];
		oreName = "dye" + LMCommon.firstUppercase(ItemDye.field_150923_a[ID]);
	}

	public String toString()
	{ return StatCollector.translateToLocal(lang); }
	
	public ItemStack getDye()
	{ return new ItemStack(Items.dye, 1, ID); }
}