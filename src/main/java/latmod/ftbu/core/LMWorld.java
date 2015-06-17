package latmod.ftbu.core;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class LMWorld
{
	private static UUID worldID;
	private static String worldIDS;
	
	public static final UUID getID()
	{ return worldID; }
	
	public static final String getIDS()
	{ return worldIDS; }
	
	public static final void setID(UUID id)
	{
		worldID = id;
		worldIDS = LatCoreMC.toShortUUID(worldID);
	}
	
	public static void load(NBTTagCompound tag)
	{
		if(tag.hasKey("UUID"))
			setID(LatCoreMC.getUUIDFromString(tag.getString("UUID")));
		else
			setID(UUID.randomUUID());
		
		LatCoreMC.logger.info("WorldID: " + getIDS());
	}
	
	public static void save(NBTTagCompound tag)
	{
		tag.setString("UUID", getIDS());
	}
}