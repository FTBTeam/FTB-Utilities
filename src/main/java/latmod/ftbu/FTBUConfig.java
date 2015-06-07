package latmod.ftbu;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.FastList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class FTBUConfig extends LMConfig implements IServerConfig
{
	public static FTBUConfig instance;
	
	public FTBUConfig(FMLPreInitializationEvent e)
	{
		super(e, "/LatMod/FTBU.cfg");
		instance = this;
		load();
	}
	
	public void load()
	{
		General.load(get("general"));
		Client.load(get("client"));
		Recipes.load(get("recipes"));
		save();
	}
	
	public void readConfig(NBTTagCompound tag)
	{
		General.friendsGuiArmor = tag.getBoolean("A");
	}
	
	public void writeConfig(NBTTagCompound tag)
	{
		tag.setBoolean("A", General.friendsGuiArmor);
	}
	
	public static class General
	{
		public static boolean disableLatCoreCommand;
		public static boolean friendsGuiArmor;
		public static boolean allowCreativeInteractSecure;
		
		public static void load(Category c)
		{
			c.remove("checkUpdates");
			
			disableLatCoreCommand = c.getBool("disableLatCoreCommand", false);
			friendsGuiArmor = c.getBool("friendsGuiArmor", true);
			allowCreativeInteractSecure = c.getBool("allowCreativeInteractSecure", true);
		}
	}
	
	public static class Client
	{
		public static boolean enablePlayerDecorators;
		public static boolean rotateBlocks;
		public static boolean renderHighlights;
		public static boolean addAllColorItems;
		public static boolean addOreNames;
		public static boolean addRegistryNames;
		public static boolean displayDebugInfo;
		
		public static void load(Category c)
		{
			FastList<String> p = new FastList<String>();
			
			enablePlayerDecorators = c.getBool("EnablePlayerDecorators", true); p.add("EnablePlayerDecorators");
			rotateBlocks = c.getBool("RotateBlocks", true); p.add("RotateBlocks");
			renderHighlights = c.getBool("RenderHighlights", true); p.add("RenderHighlights");
			addAllColorItems = c.getBool("AddAllColorItems", true); p.add("AddAllColorItems");
			addOreNames = c.getBool("AddOreNames", false); p.add("AddOreNames");
			addRegistryNames = c.getBool("AddRegistryNames", false); p.add("AddRegistryNames");
			displayDebugInfo = c.getBool("DisplayDebugInfo", false); p.add("DisplayDebugInfo");
			
			FastList<Property> l = new FastList<Property>();
			l.addAll(c.getCategory().values());
			
			for(int i = 0; i < l.size(); i++)
			{
				String p1 = l.get(i).getName();
				if(!p.contains(p1)) c.getCategory().remove(p1);
			}
		}
	}
	
	public static class Recipes
	{
		public static boolean smeltFleshToLeather;
		
		public static void load(Category c)
		{
			smeltFleshToLeather = c.getBool("smeltFleshToLeather", true);
		}
		
		public static void loadRecipes()
		{
			if(smeltFleshToLeather)
				FTBU.mod.recipes.addSmelting(new ItemStack(Items.leather), new ItemStack(Items.rotten_flesh));
		}
	}
}