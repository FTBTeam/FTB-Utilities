package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ftb.lib.BlockDimPos;
import ftb.lib.EntityPos;
import ftb.lib.FTBLib;
import ftb.lib.LMDimUtils;
import ftb.lib.api.item.StringIDInvLoader;
import ftb.lib.api.notification.ClickAction;
import ftb.lib.api.notification.MouseAction;
import ftb.lib.api.notification.Notification;
import ftb.utils.api.EventLMPlayerServer;
import ftb.utils.mod.FTBULang;
import ftb.utils.mod.client.FTBUClickAction;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.net.MessageAreaUpdate;
import ftb.utils.net.MessageLMPlayerUpdate;
import ftb.utils.world.claims.ChunkType;
import ftb.utils.world.claims.ClaimedChunk;
import ftb.utils.world.ranks.Rank;
import ftb.utils.world.ranks.RankConfig;
import ftb.utils.world.ranks.Ranks;
import latmod.lib.Bits;
import latmod.lib.IntMap;
import latmod.lib.LMUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LMPlayerServer extends LMPlayer // LMPlayerClient
{
	public static Map<Integer, UUID> tempPlayerIDMap;
	
	private final PersonalSettings settings;
	private NBTTagCompound serverData = null;
	public BlockDimPos lastPos, lastDeath;
	public final LMPlayerStats stats;
	private EntityPlayerMP entityPlayer = null;
	public int lastChunkType = -99;
	public final Warps homes;
	
	public static LMPlayerServer get(Object o) throws CommandException
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(o);
		if(p == null || p.isFake()) throw new PlayerNotFoundException();
		return p;
	}
	
	public LMPlayerServer(GameProfile gp)
	{
		super(gp);
		settings = new PersonalSettings();
		stats = new LMPlayerStats();
		homes = new Warps();
	}
	
	@Override
	public LMWorldServer getWorld()
	{ return LMWorldServer.inst; }
	
	@Override
	public Side getSide()
	{ return Side.SERVER; }
	
	@Override
	public boolean isOnline()
	{ return entityPlayer != null; }
	
	@Override
	public LMPlayerServer toPlayerMP()
	{ return this; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public LMPlayerClient toPlayerSP()
	{ return null; }
	
	@Override
	public EntityPlayerMP getPlayer()
	{ return entityPlayer; }
	
	public void setPlayer(EntityPlayerMP ep)
	{ entityPlayer = ep; }
	
	@Override
	public PersonalSettings getSettings()
	{ return settings; }
	
	public boolean isFake()
	{ return getPlayer() instanceof FakePlayer; }
	
	public void sendUpdate()
	{
		new EventLMPlayerServer.UpdateSent(this).post();
		
		if(isOnline())
		{
			new MessageLMPlayerUpdate(this, true).sendTo(getPlayer());
		}
		
		for(EntityPlayerMP ep : FTBLib.getAllOnlinePlayers(getPlayer()))
		{
			new MessageLMPlayerUpdate(this, false).sendTo(ep);
		}
	}
	
	public boolean isOP()
	{ return FTBLib.isOP(getProfile()); }
	
	public BlockDimPos getPos()
	{
		EntityPlayerMP ep = getPlayer();
		if(ep != null) lastPos = new EntityPos(ep).toLinkedPos();
		return lastPos.copy();
	}
	
	// Reading / Writing //
	
	public void getInfo(LMPlayerServer owner, List<IChatComponent> info)
	{
		refreshStats();
		long ms = LMUtils.millis();
		
		if(!equalsPlayer(owner))
		{
			boolean raw1 = isFriendRaw(owner);
			boolean raw2 = owner.isFriendRaw(this);
			
			if(raw1 && raw2)
			{
				IChatComponent c = FTBULang.label_friend.chatComponent();
				c.getChatStyle().setColor(EnumChatFormatting.GREEN);
				info.add(c);
			}
			else if(raw1 || raw2)
			{
				IChatComponent c = FTBULang.label_pfriend.chatComponent();
				c.getChatStyle().setColor(raw1 ? EnumChatFormatting.GOLD : EnumChatFormatting.BLUE);
				info.add(c);
			}
		}
		
		new EventLMPlayerServer.CustomInfo(this, info).post();
		
		if(owner.getRank().config.show_rank.getAsBoolean())
		{
			Rank rank = getRank();
			IChatComponent rankC = new ChatComponentText("[" + rank.getID() + "]");
			rankC.getChatStyle().setColor(rank.color.get());
			info.add(rankC);
		}
		
		stats.getInfo(this, info, ms);
	}
	
	public void refreshStats()
	{
		if(isOnline())
		{
			stats.refresh(this, false);
			getPos();
		}
	}
	
	public void readFromServer(NBTTagCompound tag)
	{
		friendsList.clear();
		
		if(tag.func_150299_b("Friends") == Constants.NBT.TAG_INT_ARRAY)
		{
			if(tempPlayerIDMap != null)
			{
				for(int id : tag.getIntArray("Friends"))
				{
					UUID uuid = tempPlayerIDMap.get(id);
					
					if(uuid != null)
					{
						friendsList.add(uuid);
					}
				}
			}
		}
		else
		{
			NBTTagList list = tag.getTagList("Friends", Constants.NBT.TAG_STRING);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				UUID id = LMUtils.fromString(list.getStringTagAt(i));
				
				if(id != null)
				{
					friendsList.add(id);
				}
			}
		}
		
		commonPublicData = tag.getCompoundTag("CustomData");
		commonPrivateData = tag.getCompoundTag("CustomPrivateData");
		
		StringIDInvLoader.readItemsFromNBT(lastArmor, tag, "LastItems");
		
		stats.readFromNBT(tag.getCompoundTag("Stats"));
		
		serverData = tag.hasKey("ServerData") ? tag.getCompoundTag("ServerData") : null;
		
		if(tag.hasKey("LastPos"))
		{
			if(tag.func_150299_b("LastPos") == Constants.NBT.TAG_INT_ARRAY)
			{
				lastPos = new BlockDimPos(tag.getIntArray("LastPos"));
			}
			else
			{
				double x = tag.getDouble("X");
				double y = tag.getDouble("Y");
				double z = tag.getDouble("Z");
				int dim = tag.getInteger("D");
				lastPos = new EntityPos(x, y, z, dim).toLinkedPos();
			}
		}
		else lastPos = null;
		
		if(tag.hasKey("LastDeath"))
		{
			if(tag.func_150299_b("LastDeath") == Constants.NBT.TAG_INT_ARRAY)
			{
				lastDeath = new BlockDimPos(tag.getIntArray("LastDeath"));
			}
			else
			{
				double x = tag.getDouble("X");
				double y = tag.getDouble("Y");
				double z = tag.getDouble("Z");
				int dim = tag.getInteger("D");
				lastDeath = new EntityPos(x, y, z, dim).toLinkedPos();
			}
		}
		else lastDeath = null;
		
		
		NBTTagCompound settingsTag = tag.getCompoundTag("Settings");
		settings.readFromServer(settingsTag);
		renderBadge = !settingsTag.hasKey("Badge") || settingsTag.getBoolean("Badge");
		
		homes.readFromNBT(tag, "Homes");
	}
	
	public void writeToServer(NBTTagCompound tag)
	{
		refreshStats();
		
		if(!friendsList.isEmpty())
		{
			NBTTagList list = new NBTTagList();
			
			for(UUID id : friendsList)
			{
				list.appendTag(new NBTTagString(LMUtils.fromUUID(id)));
			}
			
			tag.setTag("Friends", list);
		}
		
		if(commonPublicData != null && !commonPublicData.hasNoTags()) tag.setTag("CustomData", commonPublicData);
		if(commonPrivateData != null && !commonPrivateData.hasNoTags())
			tag.setTag("CustomPrivateData", commonPrivateData);
		
		StringIDInvLoader.writeItemsToNBT(lastArmor, tag, "LastItems");
		
		if(serverData != null && !serverData.hasNoTags()) tag.setTag("ServerData", serverData);
		
		if(lastPos != null) tag.setIntArray("LastPos", lastPos.toIntArray());
		if(lastDeath != null) tag.setIntArray("LastDeath", lastDeath.toIntArray());
		
		NBTTagCompound statsTag = new NBTTagCompound();
		stats.writeToNBT(statsTag);
		tag.setTag("Stats", statsTag);
		
		NBTTagCompound settingsTag = new NBTTagCompound();
		settings.writeToServer(settingsTag);
		settingsTag.setBoolean("Badge", renderBadge);
		tag.setTag("Settings", settingsTag);
		
		homes.writeToNBT(tag, "Homes");
	}
	
	public void writeToNet(NBTTagCompound tag, boolean self)
	{
		refreshStats();
		new EventLMPlayerServer.DataSaved(this).post();
		Rank rank = getRank();
		
		IntMap map = new IntMap();
		
		map.put(0, isOnline() ? 1 : 0);
		map.put(1, renderBadge ? 1 : 0);
		
		tag.setIntArray("S", map.toArray());
		
		if(!friendsList.isEmpty())
		{
			byte[] ba = new byte[friendsList.size() * 16];
			int idx = 0;
			
			for(UUID id : friendsList)
			{
				Bits.fromUUID(ba, idx * 16, id);
				idx++;
			}
			
			tag.setByteArray("F", ba);
		}
		
		List<UUID> otherFriends = new ArrayList<>();
		
		for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
		{
			if(p.friendsList.contains(getProfile().getId()))
			{
				otherFriends.add(p.getProfile().getId());
			}
		}
		
		if(!otherFriends.isEmpty())
		{
			byte[] ba = new byte[otherFriends.size() * 16];
			int idx = 0;
			
			for(UUID id : otherFriends)
			{
				Bits.fromUUID(ba, idx * 16, id);
				idx++;
			}
			
			tag.setByteArray("OF", ba);
		}
		
		tag.setTag("CPUD", commonPublicData);
		
		if(self)
		{
			map = new IntMap();
			
			map.put(0, settings.flags);
			map.put(1, settings.blocks.ID);
			
			map.put(2, getClaimedChunks());
			map.put(3, getLoadedChunks(true));
			map.put(4, rank.config.max_claims.getAsInt());
			map.put(5, rank.config.max_loaded_chunks.getAsInt());
			
			tag.setIntArray("SP", map.toArray());
			
			tag.setTag("CPRD", commonPrivateData);
		}
	}
	
	public void onPostLoaded()
	{ new EventLMPlayerServer.DataLoaded(this).post(); }
	
	public void checkNewFriends()
	{
		if(isOnline())
		{
			ArrayList<String> requests = new ArrayList<>();
			
			for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
			{
				if(p.isFriendRaw(this) && !isFriendRaw(p)) requests.add(p.getProfile().getName());
			}
			
			if(requests.size() > 0)
			{
				IChatComponent cc = FTBULang.label_new_friends.chatComponent();
				cc.getChatStyle().setColor(EnumChatFormatting.GREEN);
				Notification n = new Notification("new_friend_requests", cc, 6000);
				n.setDesc(FTBULang.label_new_friends_click.chatComponent());
				
				MouseAction mouse = new MouseAction();
				mouse.click = new ClickAction(FTBUClickAction.FRIEND_ADD_ALL, null);
				Collections.sort(requests, null);
				
				for(String s : requests) mouse.hover.add(new ChatComponentText(s));
				n.setMouseAction(mouse);
				
				FTBLib.notifyPlayer(getPlayer(), n);
			}
		}
	}
	
	public Rank getRank()
	{ return Ranks.getRankFor(this); }
	
	public void claimChunk(int dim, int cx, int cz)
	{
		RankConfig c = getRank().config;
		if(c.dimension_blacklist.getAsIntList().contains(dim)) return;
		int max = c.max_claims.getAsInt();
		if(max == 0) return;
		if(getClaimedChunks() >= max) return;
		
		ChunkType t = LMWorldServer.inst.claimedChunks.getType(dim, cx, cz);
		if(!t.isClaimed() && t.isChunkOwner(this) && LMWorldServer.inst.claimedChunks.put(new ClaimedChunk(getProfile().getId(), dim, cx, cz)))
			sendUpdate();
	}
	
	public void unclaimChunk(int dim, int cx, int cz)
	{
		if(LMWorldServer.inst.claimedChunks.getType(dim, cx, cz).isChunkOwner(this))
		{
			setLoaded(dim, cx, cz, false);
			LMWorldServer.inst.claimedChunks.remove(dim, cx, cz);
			sendUpdate();
		}
	}
	
	public void unclaimAllChunks(Integer dim)
	{
		List<ClaimedChunk> list = LMWorldServer.inst.claimedChunks.getChunks(this, dim);
		int size0 = list.size();
		if(size0 == 0) return;
		
		for(int i = 0; i < size0; i++)
		{
			ClaimedChunk c = list.get(i);
			setLoaded(c.dim, c.posX, c.posZ, false);
			LMWorldServer.inst.claimedChunks.remove(c.dim, c.posX, c.posZ);
		}
		
		sendUpdate();
	}
	
	public int getClaimedChunks()
	{ return LMWorldServer.inst.claimedChunks.getChunks(this, null).size(); }
	
	public int getLoadedChunks(boolean forced)
	{
		int loaded = 0;
		for(ClaimedChunk c : LMWorldServer.inst.claimedChunks.getChunks(this, null))
		{ if(c.isChunkloaded && (!forced || c.isForced)) loaded++; }
		return loaded;
	}
	
	public NBTTagCompound getServerData()
	{
		if(serverData == null) serverData = new NBTTagCompound();
		return serverData;
	}
	
	public void setLoaded(int dim, int cx, int cz, boolean flag)
	{
		ClaimedChunk chunk = LMWorldServer.inst.claimedChunks.getChunk(dim, cx, cz);
		if(chunk == null) return;
		
		if(flag != chunk.isChunkloaded && equalsPlayer(chunk.getOwnerS()))
		{
			if(flag)
			{
				RankConfig c = getRank().config;
				if(c.dimension_blacklist.getAsIntList().contains(dim)) return;
				int max = c.max_loaded_chunks.getAsInt();
				if(max == 0) return;
				if(getLoadedChunks(false) >= max) return;
			}
			
			chunk.isChunkloaded = flag;
			FTBUChunkEventHandler.instance.markDirty(LMDimUtils.getWorld(dim));
			
			if(getPlayer() != null) new MessageAreaUpdate(this, cx, cz, dim, 1, 1).sendTo(getPlayer());
			sendUpdate();
		}
	}
	
	@Override
	public boolean allowInteractSecure()
	{ return getPlayer() != null && isOP(); }
	
	public StatisticsFile getStatFile(boolean force)
	{
		if(isOnline()) return getPlayer().func_147099_x();
		return force ? FTBLib.getServer().getConfigurationManager().func_152602_a(new FakePlayer(FTBLib.getServerWorld(), getProfile())) : null;
	}
}