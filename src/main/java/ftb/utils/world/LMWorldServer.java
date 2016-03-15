package ftb.utils.world;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import ftb.lib.*;
import ftb.utils.api.EventLMPlayerServer;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.net.MessageLMWorldUpdate;
import ftb.utils.world.claims.ClaimedChunks;
import latmod.lib.*;
import latmod.lib.config.ConfigGroup;
import latmod.lib.json.UUIDTypeAdapterLM;
import latmod.lib.util.Phase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import java.io.File;
import java.util.*;

public class LMWorldServer extends LMWorld // LMWorldClient
{
	public static LMWorldServer inst = null;
	
	public final File latmodFolder;
	public final HashMap<Integer, LMPlayerServer> playerMap;
	public final Warps warps;
	public final ClaimedChunks claimedChunks;
	public final ConfigGroup customServerData;
	
	public LMWorldServer(File f)
	{
		super(Side.SERVER);
		latmodFolder = f;
		playerMap = new HashMap<>();
		warps = new Warps();
		claimedChunks = new ClaimedChunks();
		customServerData = new ConfigGroup("custom_server_data");
	}
	
	public HashMap<Integer, ? extends LMPlayer> playerMap()
	{ return playerMap; }
	
	public World getMCWorld()
	{ return FTBLib.getServerWorld(); }
	
	public LMWorldServer getServerWorld()
	{ return this; }
	
	public void close()
	{
		playerMap.clear();
		claimedChunks.chunks.clear();
		FTBUChunkEventHandler.instance.clear();
	}
	
	public LMPlayerServer getPlayer(Object o)
	{
		if(o instanceof FakePlayer) return new LMFakeServerPlayer(this, (FakePlayer) o);
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerMP();
	}
	
	public void load(JsonObject group, Phase p)
	{
		if(p.isPre())
		{
			warps.readFromJson(group, "warps");
			customServerData.setJson(group.get(customServerData.getID()));
			customCommonData.setJson(group.get(customCommonData.getID()));
			settings.readFromJson(group.get("settings").getAsJsonObject());
		}
	}
	
	public void save(JsonObject group, Phase p)
	{
		if(p.isPre())
		{
			warps.writeToJson(group, "warps");
			group.add(customServerData.getID(), customServerData.getJson());
			group.add(customCommonData.getID(), customCommonData.getJson());
			JsonObject settingsGroup = new JsonObject();
			settings.writeToJson(settingsGroup);
			group.add("settings", settingsGroup);
		}
	}
	
	public void writeDataToNet(ByteIOStream io, LMPlayerServer self, boolean first)
	{
		if(first)
		{
			IntList onlinePlayers = new IntList();
			
			io.writeInt(playerMap.size());
			
			for(LMPlayerServer p : playerMap.values())
			{
				io.writeInt(p.getPlayerID());
				io.writeUUID(p.getProfile().getId());
				io.writeUTF(p.getProfile().getName());
				
				if(p.isOnline() && p.getPlayerID() != self.getPlayerID()) onlinePlayers.add(p.getPlayerID());
			}
			
			io.writeIntArray(onlinePlayers.toArray(), ByteCount.INT);
			
			for(int i = 0; i < onlinePlayers.size(); i++)
			{
				LMPlayerServer p = playerMap.get(onlinePlayers.get(i));
				p.writeToNet(io, false);
			}
			
			self.writeToNet(io, true);
		}
		
		customCommonData.write(io);
		settings.writeToNet(io);
	}
	
	public void writePlayersToServer(NBTTagCompound tag)
	{
		for(LMPlayerServer p : LMMapUtils.values(playerMap, null))
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			p.writeToServer(tag1);
			new EventLMPlayerServer.DataSaved(p).post();
			tag1.setString("UUID", p.getStringUUID());
			tag1.setString("Name", p.getProfile().getName());
			tag.setTag(Integer.toString(p.getPlayerID()), tag1);
		}
	}
	
	public void readPlayersFromServer(NBTTagCompound tag)
	{
		playerMap.clear();
		
		Map<String, NBTTagCompound> map = LMNBTUtils.toMapWithType(tag);
		
		for(Map.Entry<String, NBTTagCompound> e : map.entrySet())
		{
			int id = Integer.parseInt(e.getKey());
			NBTTagCompound tag1 = e.getValue();
			LMPlayerServer p = new LMPlayerServer(this, id, new GameProfile(UUIDTypeAdapterLM.getUUID(tag1.getString("UUID")), tag1.getString("Name")));
			p.readFromServer(tag1);
			playerMap.put(p.getPlayerID(), p);
		}
		
		for(LMPlayerServer p : playerMap.values())
			p.onPostLoaded();
	}
	
	public void update(LMPlayerServer self)
	{ new MessageLMWorldUpdate(this, self).sendTo(null); }
	
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