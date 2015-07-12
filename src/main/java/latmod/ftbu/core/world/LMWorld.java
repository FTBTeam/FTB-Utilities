package latmod.ftbu.core.world;

import java.util.UUID;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.NameType;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.FTBU;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import cpw.mods.fml.relauncher.Side;

public abstract class LMWorld<P extends LMPlayer>
{
	public static LMWorld<?> getWorld()
	{
		if(LatCoreMC.isServer())
			return LMWorldServer.inst;
		return FTBU.proxy.getClientWorldLM();
	}
	
	public final Side side;
	public final UUID worldID;
	public final String worldIDS;
	public final FastList<P> players;
	
	public LMWorld(Side s, UUID id)
	{
		side = s;
		worldID = id;
		worldIDS = getWorldIDS();
		players = new FastList<P>();
	}
	
	protected String getWorldIDS()
	{ return LatCoreMC.toShortUUID(worldID); }
	
	public P getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		else if(o instanceof Integer || o instanceof LMPlayer)
		{
			int h = o.hashCode();
			if(h <= 0) return null;
			
			for(int i = 0; i < players.size(); i++)
			{
				P p = players.get(i);
				if(p.playerID == h) return p;
			}
			
			return null;
		}
		else if(o.getClass() == UUID.class)
		{
			UUID id = (UUID)o;
			
			for(int i = 0; i < players.size(); i++)
			{
				P p = players.get(i);
				if(p.getUUID().equals(id)) return p;
			}
			
			return null;
		}
		else if(o instanceof EntityPlayer)
			return getPlayer(((EntityPlayer)o).getUniqueID());
		else if(o instanceof String)
		{
			String s = o.toString();
			
			if(s == null || s.isEmpty()) return null;
			
			for(int i = 0; i < players.size(); i++)
			{
				P p = players.get(i);
				if(p.getName().equalsIgnoreCase(s)) return p;
			}
			
			return getPlayer(LatCoreMC.getUUIDFromString(s));
		}
		
		return null;
	}
	
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
		
		for(int i = 0; i < players.size(); i++)
		{
			P p = players.get(i);
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
	
	public int[] getAllPlayerIDs()
	{
		int[] ai = new int[players.size()];
		for(int i = 0; i < players.size(); i++)
			ai[i] = players.get(i).playerID;
		return ai;
	}
}