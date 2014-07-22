package latmod.core;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class ODItems
{
	public static final String WOOD = "logWood";
	public static final String PLANKS = "planksWood";
	public static final String STICK = "stickWood";
	public static final String GLASS = "glass";
	public static final String SLIMEBALL = "slimeball";
	public static final String MEAT_RAW = "meatCooked";
	public static final String MEAT_COOKED = "meatCooked";
	public static final String REDSTONE = "dustRedstone";
	
	public static final String IRON = "ingotIron";
	public static final String GOLD = "ingotGold";
	public static final String DIAMOND = "gemDiamond";
	public static final String TIN = "ingotTin";
	public static final String COPPER = "ingotCopper";
	public static final String LEAD = "ingotLead";
	public static final String BRONZE = "ingotBronze";
	public static final String SILVER = "ingotSilver";
	
	public static final String RUBY = "gemRuby";
	public static final String SAPPHIRE = "gemSapphire";
	public static final String PERIDOT = "gemPeridot";
	
	public static final void register()
	{
		LatCore.addOreDictionary(GLASS, new ItemStack(Blocks.glass));
		LatCore.addOreDictionary(SLIMEBALL, new ItemStack(Items.slime_ball));
		
		LatCore.addOreDictionary(IRON, new ItemStack(Items.iron_ingot));
		LatCore.addOreDictionary(GOLD, new ItemStack(Items.gold_ingot));
		
		LatCore.addOreDictionary(MEAT_RAW, new ItemStack(Items.beef));
		LatCore.addOreDictionary(MEAT_RAW, new ItemStack(Items.porkchop));
		LatCore.addOreDictionary(MEAT_RAW, new ItemStack(Items.chicken));
		
		LatCore.addOreDictionary(MEAT_COOKED, new ItemStack(Items.cooked_beef));
		LatCore.addOreDictionary(MEAT_COOKED, new ItemStack(Items.cooked_porkchop));
		LatCore.addOreDictionary(MEAT_COOKED, new ItemStack(Items.cooked_chicken));
	}
}