package latmod.ftbu.core.world;

import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.NameType;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import cpw.mods.fml.relauncher.*;

public abstract class LMWorld
{
	public static LMWorldServer server;
	
	@SideOnly(Side.CLIENT)
	public static LMWorldClient client;
	
	public static LMWorld getWorld()
	{
		if(!LatCoreMC.isServer())
			return FTBU.proxy.getClientWorldLM();
		return server;
	}
	
	public final Side side;
	public final UUID worldID;
	public final String worldIDS;
	
	public LMWorld(Side s, UUID id)
	{
		side = s;
		worldID = id;
		worldIDS = LatCoreMC.toShortUUID(worldID);
	}
	
	public abstract FastMap<Integer, ? extends LMPlayer> getPlayers();
	public abstract LMPlayer getPlayer(Object o);
	
	public int getPlayerID(Object o)
	{
		if(o == null) return 0;
		LMPlayer p = getPlayer(o);
		return (p == null) ? 0 : p.playerID;
	}
	
	public String[] getAllNames(NameType type)
	{
		if(type == null || type == NameType.NONE) return new String[0];
		
		FastList<String> allOn = new FastList<String>();
		FastList<String> allOff = new FastList<String>();
		
		for(LMPlayer p : getPlayers())
		{
			String s = LatCoreMC.removeFormatting(p.getName());
			
			if(p.isOnline()) allOn.add(s);
			else if(!type.isOnline()) allOff.add(s);
		}
		
		allOn.sort(null);
		
		if(!type.isOnline())
		{
			allOff.sort(null);
			
			for(int i = 0; i < allOff.size(); i++)
			{
				String s = allOff.get(i);
				if(!allOn.contains(s)) allOn.add(s);
			}
		}
		
		return allOn.toArray(new String[0]);
	}
}