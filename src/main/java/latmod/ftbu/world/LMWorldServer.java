package latmod.ftbu.world;

import java.io.File;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import ftb.lib.*;
import ftb.lib.api.MessageLM;
import latmod.ftbu.api.EventLMPlayerServer;
import latmod.ftbu.net.MessageLMWorldUpdate;
import latmod.ftbu.world.claims.*;
import latmod.lib.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class LMWorldServer extends LMWorld // LMWorldClient
{
	public static LMWorldServer inst = null;
	
	public final WorldServer worldObj;
	public final File latmodFolder;
	public final Warps warps;
	public final ClaimedChunks claimedChunks;
	public NBTTagCompound customServerData;
	public int lastMailID = 0;
	
	public LMWorldServer(WorldServer w, File f)
	{
		super(Side.SERVER);
		worldObj = w;
		latmodFolder = f;
		warps = new Warps();
		claimedChunks = new ClaimedChunks();
		customServerData = new NBTTagCompound();
	}
	
	public World getMCWorld()
	{ return FTBLib.getServerWorld(); }
	
	public LMWorldServer getServerWorld()
	{ return this; }
	
	public LMPlayerServer getPlayer(Object o)
	{
		LMPlayer p = super.getPlayer(o);
		return (p == null) ? null : p.toPlayerMP();
	}
	
	public void load(NBTTagCompound tag)
	{
		warps.readFromNBT(tag, "Warps");
		customServerData = tag.getCompoundTag("CustomServer");
		customCommonData = tag.getCompoundTag("CustomCommon");
		settings.readFromNBT(tag.getCompoundTag("Settings"), true);
		lastMailID = tag.getInteger("LastMailID");
		claimedChunks.load(tag);
	}
	
	public void save(NBTTagCompound tag)
	{
		warps.writeToNBT(tag, "Warps");
		tag.setTag("CustomServer", customServerData);
		tag.setTag("CustomCommon", customCommonData);
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToNBT(settingsTag, true);
		tag.setTag("Settings", settingsTag);
		tag.setInteger("LastMailID", lastMailID);
		claimedChunks.save(tag);
	}
	
	public void writeDataToNet(ByteIOStream io, int selfID)
	{
		if(selfID > 0)
		{
			io.writeInt(players.size());
			for(int i = 0; i < players.size(); i++)
			{
				LMPlayer p = players.get(i);
				io.writeInt(p.playerID);
				io.writeUUID(p.getUUID());
				io.writeString(p.getName());
			}
		}
		
		NBTTagCompound tag = new NBTTagCompound();
		
		if(selfID > 0)
		{
			NBTTagList list = new NBTTagList();
			
			for(int i = 0; i < players.size(); i++)
			{
				LMPlayerServer p = players.get(i).toPlayerMP();
				
				if(p.isOnline())
				{
					NBTTagCompound tag1 = new NBTTagCompound();
					p.writeToNet(tag1, p.playerID == selfID);
					new EventLMPlayerServer.DataSaved(p).post();
					tag1.setInteger("PID", p.playerID);
					list.appendTag(tag1);
				}
			}
			
			tag.setTag("P", list);
		}
		
		if(!customCommonData.hasNoTags()) tag.setTag("C", customCommonData);
		
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToNBT(settingsTag, false);
		tag.setTag("S", settingsTag);
		
		MessageLM.writeTag(io, tag);
	}
	
	public void writePlayersToServer(NBTTagCompound tag)
	{
		players.sort(null);
		
		for(int i = 0; i < players.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			
			LMPlayerServer p = players.get(i).toPlayerMP();
			p.writeToServer(tag1);
			new EventLMPlayerServer.DataSaved(p).post();
			tag1.setString("UUID", p.uuidString);
			tag1.setString("Name", p.getName());
			tag.setTag(Integer.toString(p.playerID), tag1);
		}
	}
	
	public void readPlayersFromServer(NBTTagCompound tag)
	{
		players.clear();
		
		FastMap<String, NBTTagCompound> map = LMNBTUtils.toFastMapWithType(tag);
		
		for(int i = 0; i < map.size(); i++)
		{
			int id = Integer.parseInt(map.keys.get(i));
			NBTTagCompound tag1 = map.values.get(i);
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
			
			players.add(p);
		}
		
		for(int i = 0; i < players.size(); i++)
			players.get(i).toPlayerMP().onPostLoaded();
	}
	
	public void update()
	{ new MessageLMWorldUpdate(this).sendTo(null); }
}