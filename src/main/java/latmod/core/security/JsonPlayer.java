package latmod.core.security;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.gson.annotations.Expose;

public class JsonPlayer implements Comparable<JsonPlayer>
{
	@Expose public String displayName;
	@Expose public String uuid;
	@Expose public List<String> whitelist;
	@Expose public List<String> blacklist;
	
	private UUID uuid0 = null;
	
	public UUID getUUID()
	{
		if(uuid0 == null)
			uuid0 = UUID.fromString(uuid);
		return uuid0;
	}
	
	public EntityPlayer getPlayer(World w)
	{ return w.func_152378_a(getUUID()); }

	public int compareTo(JsonPlayer o)
	{
		if(displayName == null || o.displayName == null)
			return 0;
		
		return displayName.compareTo(o.displayName);
	}
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(o instanceof String) return ((String)o).equalsIgnoreCase(displayName);
		if(o instanceof UUID) return ((UUID)o).equals(getUUID());
		if(o instanceof EntityPlayer) return ((EntityPlayer)o).getUniqueID().equals(getUUID());
		return false;
	}
}