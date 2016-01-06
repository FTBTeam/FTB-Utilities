package latmod.ftbu.world;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import ftb.lib.*;
import latmod.ftbu.api.EventLMPlayerServer;
import latmod.ftbu.net.MessageLMWorldUpdate;
import latmod.ftbu.world.claims.*;
import latmod.lib.*;
import latmod.lib.config.ConfigGroup;
import latmod.lib.util.Phase;
import net.minecraft.nbt.*;
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
	private final LMFakeServerPlayer fakePlayer;
	public final ConfigGroup customServerData;
	public int lastMailID = 0;
	
	public LMWorldServer(File f)
	{
		super(Side.SERVER);
		latmodFolder = f;
		playerMap = new HashMap<>();
		warps = new Warps();
		claimedChunks = new ClaimedChunks();
		fakePlayer = new LMFakeServerPlayer(this);
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
	}
	
	public LMPlayerServer getPlayer(Object o)
	{
		if(o instanceof FakePlayer) return fakePlayer;
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerMP();
	}

	public void load(NBTTagCompound tag)
	{
		warps.readFromNBT(tag, "Warps");
		settings.readFromNBT(tag.getCompoundTag("Settings"));
		lastMailID = tag.getInteger("LastMailID");
		claimedChunks.load(tag);
	}
	
	public void load(JsonObject group, Phase p)
	{
		if(p.isPre())
		{
			warps.readFromJson(group, "warps");
			customServerData.setJson(group.get(customServerData.ID));
			customCommonData.setJson(group.get(customCommonData.ID));
			settings.readFromJson(group.get("settings").getAsJsonObject());
			lastMailID = group.has("last_mail_id") ? group.get("last_mail_id").getAsInt() : 0;
		}
	}
	
	public void save(JsonObject group, Phase p)
	{
		if(p.isPre())
		{
			warps.writeToJson(group, "warps");
			group.add(customServerData.ID, customServerData.getJson());
			group.add(customCommonData.ID, customCommonData.getJson());
			JsonObject settingsGroup = new JsonObject();
			settings.writeToJson(settingsGroup);
			group.add("settings", settingsGroup);
			if(lastMailID > 0) group.add("last_mail_id", new JsonPrimitive(lastMailID));
		}
	}
	
	public void writeDataToNet(ByteIOStream io, int selfID)
	{
		if(selfID > 0)
		{
			IntList onlinePlayers = new IntList();
			
			io.writeInt(playerMap.size());

			for(LMPlayerServer p : playerMap.values())
			{
				io.writeInt(p.playerID);
				io.writeUUID(p.getUUID());
				io.writeUTF(p.getName());
				
				if(p.isOnline()) onlinePlayers.add(p.playerID);
			}
			
			io.writeIntArray(onlinePlayers.toArray(), ByteCount.INT);
			
			for(int i = 0; i < onlinePlayers.size(); i++)
			{
				LMPlayerServer p = playerMap.get(onlinePlayers.get(i));
				p.writeToNet(io, p.playerID == selfID);
			}
		}
		
		settings.writeToNet(io);

		try { customCommonData.write(io); }
		catch(Exception ex) { }
	}
	
	public void writePlayersToServer(NBTTagCompound tag)
	{
		for(LMPlayerServer p : LMMapUtils.values(playerMap, null))
		{
			NBTTagCompound tag1 = new NBTTagCompound();

			p.writeToServer(tag1);
			new EventLMPlayerServer.DataSaved(p).post();
			tag1.setString("UUID", p.getStringUUID());
			tag1.setString("Name", p.getName());
			tag.setTag(Integer.toString(p.playerID), tag1);
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
			LMPlayerServer p = new LMPlayerServer(this, id, new GameProfile(LMStringUtils.fromString(tag1.getString("UUID")), tag1.getString("Name")));
			p.readFromServer(tag1);
			
			//TODO: Remove me after few updates
			if(tag1.hasKey("Claims"))
			{
				NBTTagCompound tagClaims = tag1.getCompoundTag("Claims");
				NBTTagList listClaims = tagClaims.getTagList("Chunks", LMNBTUtils.INT_ARRAY);
				
				if(listClaims != null && listClaims.tagCount() > 0)
				for(int j = 0; j < listClaims.tagCount(); j++)
				{
					int[] ai = listClaims.func_150306_c(j);
					claimedChunks.put(new ClaimedChunk(p.playerID, ai[0], ai[1], ai[2]));
				}
			}

			playerMap.put(p.playerID, p);
		}

		for(LMPlayerServer p : playerMap.values())
			p.onPostLoaded();
	}
	
	public void update()
	{ new MessageLMWorldUpdate(this).sendTo(null); }

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
					return o1.getName().compareToIgnoreCase(o2.getName());
				return Boolean.compare(o2.isOnline(), o1.isOnline());
			}
		});

		return LMListUtils.toStringArray(list);
	}
}