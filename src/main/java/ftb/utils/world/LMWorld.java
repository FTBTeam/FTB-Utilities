package ftb.utils.world;

import cpw.mods.fml.relauncher.*;
import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigGroup;
import ftb.utils.mod.FTBU;
import ftb.utils.world.claims.LMWorldSettings;
import latmod.lib.LMUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import java.util.*;

public abstract class LMWorld // FTBWorld
{
	public static LMWorld getWorld(Side s)
	{
		if(s.isServer()) return LMWorldServer.inst;
		return FTBU.proxy.getClientWorldLM();
	}
	
	public static LMWorld getWorld()
	{ return getWorld(FTBLib.getEffectiveSide()); }
	
	public final Side side;
	public final ConfigGroup customCommonData;
	public final LMWorldSettings settings;
	
	public LMWorld(Side s)
	{
		side = s;
		customCommonData = new ConfigGroup("custom_common_data");
		settings = new LMWorldSettings(this);
	}
	
	public abstract Map<Integer, ? extends LMPlayer> playerMap();
	
	public abstract World getMCWorld();
	
	public LMWorldServer getServerWorld()
	{ return null; }
	
	@SideOnly(Side.CLIENT)
	public LMWorldClient getClientWorld()
	{ return null; }
	
	public LMPlayer getPlayer(Object o)
	{
		if(o == null || o instanceof FakePlayer) return null;
		
		Map<Integer, ? extends LMPlayer> playerMap = playerMap();
		
		if(o instanceof Number || o instanceof LMPlayer)
		{
			int h = o.hashCode();
			if(h <= 0) return null;
			return playerMap.get(Integer.valueOf(h));
		}
		else if(o.getClass() == UUID.class)
		{
			UUID id = (UUID) o;
			
			for(LMPlayer p : playerMap.values())
			{ if(p.getProfile().getId().equals(id)) return p; }
			
			return null;
		}
		else if(o instanceof EntityPlayer)
		{
			if(side.isServer())
			{
				for(LMPlayer p : playerMap.values())
				{ if(p.isOnline() && p.getPlayer() == o) return p; }
			}
			
			return getPlayer(((EntityPlayer) o).getGameProfile().getId());
		}
		else if(o instanceof CharSequence)
		{
			String s = o.toString();
			
			if(s == null || s.isEmpty()) return null;
			
			for(LMPlayer p : playerMap.values())
			{ if(p.getProfile().getName().equalsIgnoreCase(s)) return p; }
			
			return getPlayer(LMUtils.fromString(s));
		}
		
		return null;
	}
	
	public List<? extends LMPlayer> getAllOnlinePlayers()
	{
		ArrayList<LMPlayer> l = new ArrayList<>();
		for(LMPlayer p : playerMap().values())
		{ if(p.isOnline()) l.add(p); }
		return l;
	}
	
	public int getPlayerID(Object o)
	{
		if(o == null) return 0;
		LMPlayer p = getPlayer(o);
		return (p == null) ? 0 : p.getPlayerID();
	}
	
	public int[] getAllPlayerIDs()
	{
		int[] ai = new int[playerMap().size()];
		int id = -1;
		for(LMPlayer p : playerMap().values())
			ai[++id] = p.getPlayerID();
		return ai;
	}
	
	public List<LMPlayerServer> getServerPlayers()
	{
		ArrayList<LMPlayerServer> l = new ArrayList<>();
		for(LMPlayer p : playerMap().values())
			l.add(p.toPlayerMP());
		return l;
	}
}