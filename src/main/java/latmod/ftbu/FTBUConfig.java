package latmod.ftbu;

import latmod.ftbu.core.*;
import net.minecraft.nbt.NBTTagCompound;
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
		public static boolean friendsGuiArmor;
		public static boolean allowCreativeInteractSecure;
		
		public static void load(Category c)
		{
			c.remove("checkUpdates");
			
			friendsGuiArmor = c.getBool("friendsGuiArmor", true);
			allowCreativeInteractSecure = c.getBool("allowCreativeInteractSecure", true);
		}
	}
}