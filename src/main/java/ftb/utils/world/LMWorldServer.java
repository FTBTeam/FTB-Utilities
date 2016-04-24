package ftb.utils.world;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import ftb.lib.FTBLib;
import ftb.lib.LMNBTUtils;
import ftb.utils.api.EventLMPlayerServer;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.world.claims.ClaimedChunks;
import latmod.lib.LMListUtils;
import latmod.lib.LMMapUtils;
import latmod.lib.LMUtils;
import latmod.lib.util.Phase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LMWorldServer extends LMWorld // LMWorldClient
{
	public static LMWorldServer inst = null;
	
	public final File latmodFolder;
	public final Map<UUID, LMPlayerServer> playerMap;
	public final Warps warps;
	public final ClaimedChunks claimedChunks;
	
	public LMWorldServer(File f)
	{
		super(Side.SERVER);
		latmodFolder = f;
		playerMap = new HashMap<>();
		warps = new Warps();
		claimedChunks = new ClaimedChunks();
	}
	
	@Override
	public Map<UUID, ? extends LMPlayer> playerMap()
	{ return playerMap; }
	
	@Override
	public World getMCWorld()
	{ return FTBLib.getServerWorld(); }
	
	@Override
	public LMWorldServer getServerWorld()
	{ return this; }
	
	public void close()
	{
		playerMap.clear();
		claimedChunks.chunks.clear();
		FTBUChunkEventHandler.instance.clear();
	}
	
	@Override
	public LMPlayerServer getPlayer(Object o)
	{
		if(o instanceof FakePlayer) return new LMFakeServerPlayer((FakePlayer) o);
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerMP();
	}
	
	public void load(JsonObject group, Phase p)
	{
		if(p == Phase.PRE)
		{
			warps.readFromJson(group, "warps");
			settings.readFromJson(group.get("settings").getAsJsonObject());
		}
	}
	
	public void save(JsonObject group, Phase p)
	{
		if(p == Phase.PRE)
		{
			warps.writeToJson(group, "warps");
			JsonObject settingsGroup = new JsonObject();
			settings.writeToJson(settingsGroup);
			group.add("settings", settingsGroup);
		}
	}
	
	public void writeDataToNet(NBTTagCompound tag, LMPlayerServer self, boolean first)
	{
		if(first)
		{
			NBTTagCompound playerMapTag = new NBTTagCompound();
			
			for(LMPlayerServer p : playerMap.values())
			{
				if(p.isOnline())
				{
					NBTTagCompound tag1 = new NBTTagCompound();
					p.writeToNet(tag1, p.equalsPlayer(self));
					tag1.setString("N", p.getProfile().getName());
					playerMapTag.setTag(LMUtils.fromUUID(p.getProfile().getId()), tag1);
				}
				else
				{
					playerMapTag.setString(LMUtils.fromUUID(p.getProfile().getId()), p.getProfile().getName());
				}
			}
			
			tag.setTag("PM", playerMapTag);
		}
		
		settings.writeToNet(tag);
	}
	
	public void writePlayersToServer(NBTTagCompound tag)
	{
		for(LMPlayerServer p : LMMapUtils.values(playerMap, null))
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			p.writeToServer(tag1);
			new EventLMPlayerServer.DataSaved(p).post();
			tag1.setString("Name", p.getProfile().getName());
			tag.setTag(p.getStringUUID(), tag1);
		}
	}
	
	public void readPlayersFromServer(NBTTagCompound tag)
	{
		playerMap.clear();
		LMPlayerServer.tempPlayerIDMap = null;
		
		Map<String, NBTTagCompound> map = LMNBTUtils.toMapWithType(tag);
		
		for(Map.Entry<String, NBTTagCompound> e : map.entrySet())
		{
			NBTTagCompound tag1 = e.getValue();
			
			if(LMPlayerServer.tempPlayerIDMap == null && tag1.hasKey("UUID"))
			{
				LMPlayerServer.tempPlayerIDMap = new HashMap<>();
				
				for(Map.Entry<String, NBTTagCompound> e1 : map.entrySet())
				{
					LMPlayerServer.tempPlayerIDMap.put(Integer.parseInt(e.getKey()), LMUtils.fromString(e1.getValue().getString("UUID")));
				}
				
				FTBLib.dev_logger.info("Old LMPlayers.dat found:" + LMPlayerServer.tempPlayerIDMap);
			}
			
			if(LMPlayerServer.tempPlayerIDMap == null)
			{
				LMPlayerServer p = new LMPlayerServer(new GameProfile(LMUtils.fromString(e.getKey()), tag1.getString("Name")));
				p.readFromServer(tag1);
				playerMap.put(p.getProfile().getId(), p);
			}
			else
			{
				LMPlayerServer p = new LMPlayerServer(new GameProfile(LMUtils.fromString(tag1.getString("UUID")), tag1.getString("Name")));
				p.readFromServer(tag1);
				playerMap.put(p.getProfile().getId(), p);
			}
		}
		
		for(LMPlayerServer p : playerMap.values())
			p.onPostLoaded();
	}
	
	@Override
	public List<LMPlayerServer> getAllOnlinePlayers()
	{
		ArrayList<LMPlayerServer> l = new ArrayList<>();
		for(LMPlayerServer p : playerMap.values())
		{ if(p.isOnline()) l.add(p); }
		return l;
	}
	
	public String[] getAllPlayerNames(Boolean online)
	{
		if(online == null) return new String[0];
		List<LMPlayerServer> list = (online == Boolean.TRUE) ? getAllOnlinePlayers() : LMListUtils.clone(playerMap.values());
		
		Collections.sort(list, new Comparator<LMPlayerServer>()
		{
			@Override
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{
				if(o1.isOnline() == o2.isOnline())
					return o1.getProfile().getName().compareToIgnoreCase(o2.getProfile().getName());
				return Boolean.compare(o2.isOnline(), o1.isOnline());
			}
		});
		
		return LMListUtils.toStringArray(list);
	}
}