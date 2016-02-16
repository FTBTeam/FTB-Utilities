package ftb.utils.mod.handlers.ftbl;

import ftb.lib.*;
import ftb.lib.api.friends.*;
import ftb.lib.api.item.LMInvUtils;
import ftb.utils.badges.ServerBadges;
import ftb.utils.mod.config.FTBUConfigLogin;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.net.MessageAreaUpdate;
import ftb.utils.world.*;
import ftb.utils.world.claims.ClaimedChunk;
import latmod.lib.Bits;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUPlayerData extends ForgePlayerData
{
	public static final byte RENDER_BADGE = 1;
	public static final byte CHAT_LINKS = 2;
	public static final byte EXPLOSIONS = 3;
	public static final byte FAKE_PLAYERS = 4;
	
	public static FTBUPlayerData get(LMPlayer p)
	{ return (FTBUPlayerData) p.getData("ftbu"); }
	
	//Common
	private byte flags = 0;
	public PrivacyLevel blocks;
	
	//Server
	public Warps homes;
	//private Rank rank;
	
	//Client
	private int claimedChunks;
	private int loadedChunks;
	
	public FTBUPlayerData(String id, LMPlayer p)
	{
		super(id, p);
		
		blocks = PrivacyLevel.FRIENDS;
		
		if(p.getSide().isServer())
		{
			homes = new Warps();
		}
		else if(p.isMCPlayer())
		{
		}
	}
	
	public boolean getFlag(byte f)
	{ return Bits.getBit(flags, f); }
	
	public void setFlag(byte f, boolean b)
	{ flags = Bits.setBit(flags, f, b); }
	
	public void readFromServer(NBTTagCompound tag)
	{
		setFlag(RENDER_BADGE, tag.hasKey("Badge") ? tag.getBoolean("Badge") : true);
		setFlag(CHAT_LINKS, tag.hasKey("ChatLinks") ? tag.getBoolean("ChatLinks") : false);
		setFlag(EXPLOSIONS, tag.hasKey("Explosions") ? tag.getBoolean("Explosions") : true);
		setFlag(FAKE_PLAYERS, tag.hasKey("FakePlayers") ? tag.getBoolean("FakePlayers") : true);
		blocks = PrivacyLevel.VALUES_3[tag.getByte("BlockSecurity")];
		
		homes.readFromNBT(tag, "Homes");
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		tag.setBoolean("Badge", getFlag(RENDER_BADGE));
		tag.setBoolean("ChatLinks", getFlag(CHAT_LINKS));
		tag.setBoolean("Explosions", getFlag(EXPLOSIONS));
		tag.setBoolean("FakePlayers", getFlag(FAKE_PLAYERS));
		tag.setByte("BlockSecurity", (byte) blocks.ordinal());
		
		homes.writeToNBT(tag, "Homes");
	}
	
	public void readFromNet(NBTTagCompound tag, boolean self)
	{
		flags = tag.getByte("F");
		blocks = PrivacyLevel.VALUES_3[tag.getByte("B")];
		
		if(self)
		{
			claimedChunks = tag.getShort("CC");
			loadedChunks = tag.getShort("LC");
		}
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		tag.setByte("F", flags);
		tag.setByte("B", (byte) blocks.ordinal());
		
		if(self)
		{
			tag.setInteger("CC", getClaimedChunks());
			tag.setInteger("LC", getLoadedChunks());
		}
	}
	
	public void onLoggedIn(boolean firstTime)
	{
		if(player.getSide().isServer())
		{
			EntityPlayerMP ep = player.toPlayerMP().getPlayer();
			
			if(firstTime)
			{
				for(ItemStack is : FTBUConfigLogin.getStartingItems(player.getProfile().getId()))
				{
					LMInvUtils.giveItem(ep, is);
				}
			}
			
			FTBUConfigLogin.printMotd(ep);
			Backups.hadPlayer = true;
			ServerBadges.sendToPlayer(ep);
			
			new MessageAreaUpdate(player.toPlayerMP(), player.toPlayerMP().getPos(), 3, 3).sendTo(ep);
			FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
	
	public void onLoggedOut()
	{
		//Backups.shouldRun = true;
		FTBUChunkEventHandler.instance.markDirty(null);
	}
	
	public void onDeath()
	{
	}
	
	public int getClaimedChunks()
	{
		if(player.getSide().isServer())
		{
			return 0;
		}
		
		return claimedChunks;
	}
	
	public int getLoadedChunks()
	{
		if(player.getSide().isServer())
		{
			return 0;
		}
		
		return loadedChunks;
	}
	
	public void claimChunk(int dim, int cx, int cz)
	{
		RankConfig c = getRank().config;
		if(c.dimension_blacklist.get().contains(dim)) return;
		int max = c.max_claims.get();
		if(max == 0) return;
		if(getClaimedChunks() >= max) return;
		
		ChunkType t = world.claimedChunks.getType(dim, cx, cz);
		if(!t.isClaimed() && t.isChunkOwner(this) && world.claimedChunks.put(new ClaimedChunk(getPlayerID(), dim, cx, cz)))
			sendUpdate();
	}
	
	public void unclaimChunk(int dim, int cx, int cz)
	{
		if(world.claimedChunks.getType(dim, cx, cz).isChunkOwner(this))
		{
			setLoaded(dim, cx, cz, false);
			world.claimedChunks.remove(dim, cx, cz);
			sendUpdate();
		}
	}
	
	public void unclaimAllChunks(Integer dim)
	{
		List<ClaimedChunk> list = FTBUWorldData.serverInstance.getChunks(player.getProfile().getId(), dim);
		int size0 = list.size();
		if(size0 == 0) return;
		
		for(int i = 0; i < size0; i++)
		{
			ClaimedChunk c = list.get(i);
			setLoaded(c.dim, c.posX, c.posZ, false);
			FTBUWorldData.serverInstance.remove(c.dim, c.posX, c.posZ);
		}
		
		sendUpdate();
	}
	
	public int getClaimedChunks()
	{ return FTBUWorldData.serverInstance.getChunks(player.getProfile().getId(), null).size(); }
	
	public int getLoadedChunks(boolean forced)
	{
		int loaded = 0;
		for(ClaimedChunk c : FTBUWorldData.serverInstance.getChunks(player.getProfile().getId(), null))
		{ if(c.isChunkloaded && (!forced || c.isForced)) loaded++; }
		return loaded;
	}
	
	public void setLoaded(int dim, int cx, int cz, boolean flag)
	{
		ClaimedChunk chunk = FTBUWorldData.serverInstance.getChunk(dim, cx, cz);
		if(chunk == null) return;
		
		if(flag != chunk.isChunkloaded && player.equalsPlayer(chunk.getOwner()))
		{
			if(flag)
			{
				RankConfig c = getRank().config;
				if(c.dimension_blacklist.get().contains(dim)) return;
				int max = c.max_loaded_chunks.get();
				if(max == 0) return;
				if(getLoadedChunks(false) >= max) return;
			}
			
			chunk.isChunkloaded = flag;
			FTBUChunkEventHandler.instance.markDirty(LMDimUtils.getWorld(dim));
			
			if(getPlayer() != null)
			{
				new MessageAreaUpdate(this, cx, cz, dim, 1, 1).sendTo(player.getPlayer());
				sendUpdate();
			}
		}
	}
}