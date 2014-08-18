package latmod.core.mod;

import java.util.UUID;

import latmod.core.util.FastList;

public enum EnumLatModTeam
{
	TEAM("Team"),
	FRIENDS("Friends"),
	OTHER("Other");
	
	public static final EnumLatModTeam[] VALUES = values();
	
	public final int ID;
	public final String name;
	
	public final FastList<UUID> uuids;
	public final FastList<String> names;
	
	EnumLatModTeam(String s)
	{
		ID = ordinal();
		name = s;
		
		uuids = new FastList<UUID>();
		names = new FastList<String>();
	}
	
	public static EnumLatModTeam get(String s)
	{
		for(int i = 0; i < VALUES.length; i++)
		{
			if(VALUES[i].name.equalsIgnoreCase(s))
				return VALUES[i];
		}
		
		return null;
	}
}