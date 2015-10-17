package latmod.ftbu.world;

import java.util.*;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.cmd.NameType;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.util.LatCoreMC;
import latmod.lib.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public abstract class LMWorld
{
	public static final UUID nullUUID = new UUID(0L, 0L);
	
	public static LMWorld getWorld(Side s)
	{ if(s.isServer()) return LMWorldServer.inst; return FTBU.proxy.getClientWorldLM(); }
	
	public static LMWorld getWorld()
	{ return getWorld(LatCoreMC.isServer() ? Side.SERVER : Side.CLIENT); }
	
	public final Side side;
	public final UUID worldID;
	public final String worldIDS;
	public final FastList<LMPlayer> players;
	public final LMWorldSettings settings;
	public NBTTagCompound customCommonData;
	public LMWorldJsonSettings jsonSettings;
	
	public LMWorld(Side s, UUID id, String ids)
	{
		side = s;
		worldID = id;
		worldIDS = ids;
		players = new FastList<LMPlayer>();
		settings = new LMWorldSettings(this);
		customCommonData = new NBTTagCompound();
		jsonSettings = new LMWorldJsonSettings();
		jsonSettings.loadDefaults();
	}
	
	public World getMCWorld()
	{ return null; }
	
	public LMWorldServer getServerWorld()
	{ return null; }
	
	@SideOnly(Side.CLIENT)
	public LMWorldClient getClientWorld()
	{ return null; }
	
	public LMPlayer getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		else if(o instanceof Number || o instanceof LMPlayer)
		{
			int h = o.hashCode();
			if(h <= 0) return null;
			
			for(int i = 0; i < players.size(); i++)
			{
				LMPlayer p = players.get(i);
				if(p.playerID == h) return p;
			}
			
			return null;
		}
		else if(o.getClass() == UUID.class)
		{
			UUID id = (UUID)o;
			
			for(int i = 0; i < players.size(); i++)
			{
				LMPlayer p = players.get(i);
				if(p.getUUID().equals(id)) return p;
			}
			
			return null;
		}
		else if(o instanceof EntityPlayer)
		{
			for(int i = 0; i < players.size(); i++)
			{
				LMPlayer p = players.get(i);
				if(p.isOnline() && p.getPlayer() == o) return p;
			}
			
			return getPlayer(((EntityPlayer)o).getUniqueID());
		}
		else if(o instanceof CharSequence)
		{
			String s = o.toString();
			
			if(s == null || s.isEmpty()) return null;
			
			for(int i = 0; i < players.size(); i++)
			{
				LMPlayer p = players.get(i);
				if(p.getName().equalsIgnoreCase(s)) return p;
			}
			
			return getPlayer(LMStringUtils.fromString(s));
		}
		
		return null;
	}
	
	public FastList<LMPlayer> getAllOnlinePlayers()
	{
		FastList<LMPlayer> l = new FastList<LMPlayer>();
		
		for(int i = 0; i < players.size(); i++)
		{
			LMPlayer p = players.get(i);
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
		FastList<LMPlayer> list = (type == NameType.ON) ? getAllOnlinePlayers() : players;
		
		list.sort(new Comparator<LMPlayer>()
		{
			public int compare(LMPlayer o1, LMPlayer o2)
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
	
	public void update()
	{
	}
	
	public FastList<LMPlayerServer> getServerPlayers()
	{
		FastList<LMPlayerServer> l = new FastList<LMPlayerServer>();
		for(int i = 0; i < players.size(); i++)
			l.add(players.get(i).toPlayerMP());
		return l;
	}
}