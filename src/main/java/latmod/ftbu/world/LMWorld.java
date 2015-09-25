package latmod.ftbu.world;

import java.util.*;

import cpw.mods.fml.relauncher.Side;
import latmod.core.util.*;
import latmod.ftbu.cmd.NameType;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LatCoreMC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;

public abstract class LMWorld<P extends LMPlayer>
{
	public static LMWorld<? extends LMPlayer> getWorld(Side s)
	{ if(s.isServer()) return LMWorldServer.inst; return FTBU.proxy.getClientWorldLM(); }
	
	public static LMWorld<? extends LMPlayer> getWorld()
	{ return getWorld((LatCoreMC.isServer() && LatCoreMC.getServer() != null) ? Side.SERVER : Side.CLIENT); }
	
	public final Side side;
	public final UUID worldID;
	public final String worldIDS;
	public final FastList<P> players;
	
	public LMWorld(Side s, UUID id, String ids)
	{
		side = s;
		worldID = id;
		worldIDS = ids;
		players = new FastList<P>();
	}
	
	public P getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		else if(o instanceof Number || o instanceof LMPlayer)
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
			
			return getPlayer(LMStringUtils.fromString(s));
		}
		
		return null;
	}
	
	public FastList<P> getAllOnlinePlayers()
	{
		FastList<P> l = new FastList<P>();
		
		for(int i = 0; i < players.size(); i++)
		{
			P p = players.get(i);
			if(p.isOnline()) l.add(p);
		}
		
		return l;
	}
	
	public int getPlayerID(Object o)
	{
		if(o == null) return 0;
		LMPlayer p = getPlayer(o);
		return (p == null) ? 0 : p.playerID;
	}
	
	public String[] getAllPlayerNames(NameType type)
	{
		if(type == null || type == NameType.NONE)
			return new String[0];
		FastList<P> list = (type == NameType.ON) ? getAllOnlinePlayers() : players;
		
		list.sort(new Comparator<P>()
		{
			public int compare(P o1, P o2)
			{
				if(o1.isOnline() == o2.isOnline())
					return o1.getName().compareToIgnoreCase(o2.getName());
				return Boolean.compare(o2.isOnline(), o1.isOnline());
			}
		});
		
		FastList<String> l = new FastList<String>();
		
		for(int i = 0; i < list.size(); i++)
		{
			String s = list.get(i).getName();
			if(!l.contains(s)) l.add(s);
		}
		
		return l.toArray(new String[l.size()]);
	}
	
	public int[] getAllPlayerIDs()
	{
		int[] ai = new int[players.size()];
		for(int i = 0; i < players.size(); i++)
			ai[i] = players.get(i).playerID;
		return ai;
	}
}