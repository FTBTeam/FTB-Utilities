package ftb.utils.world.claims;

import ftb.lib.LMSecurity;
import ftb.lib.api.LangKey;
import ftb.utils.world.LMPlayer;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.PersonalSettings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.UUID;

public class ChunkType
{
	public static final ChunkType UNLOADED = new ChunkType(0, "unloaded", EnumChatFormatting.DARK_GRAY, 0xFF000000);
	public static final ChunkType SPAWN = new ChunkType(1, "spawn", EnumChatFormatting.AQUA, 0xFF00EFDF);
	public static final ChunkType WORLD_BORDER = new ChunkType(2, "world_border", EnumChatFormatting.RED, 0xFFFF0000);
	public static final ChunkType WILDERNESS = new ChunkType(3, "wilderness", EnumChatFormatting.DARK_GREEN, 0xFF2F9E00);
	
	public static final ChunkType[] VALUES = new ChunkType[] {UNLOADED, SPAWN, WORLD_BORDER, WILDERNESS};
	
	public static final class PlayerClaimed extends ChunkType
	{
		public ClaimedChunk chunk;
		
		public PlayerClaimed(ClaimedChunk c)
		{
			super(5, "claimed", null, 0);
			chunk = c;
		}
		
		public boolean isFriendly(LMPlayer p)
		{ return isChunkOwner(p) || p.getWorld().getPlayer(chunk.ownerID).isFriend(p); }
		
		@Override
		public boolean isClaimed()
		{ return chunk != null; }
		
		@Override
		public boolean isChunkOwner(LMPlayer p)
		{ return p != null && p.getProfile().getId().equals(chunk.ownerID); }
		
		@Override
		public EnumChatFormatting getChatColor(LMPlayer p)
		{ return isFriendly(p) ? EnumChatFormatting.GREEN : EnumChatFormatting.BLUE; }
		
		@Override
		public int getAreaColor(LMPlayer p)
		{ return isFriendly(p) ? 0xFF00FF21 : 0xFF0094FF; }
		
		@Override
		public boolean canInteract(LMPlayerServer p, boolean leftClick)
		{
			if(isChunkOwner(p)) return true;
			LMPlayerServer chunkOwner = chunk.getOwnerS();
			if(p.isFake()) return chunkOwner.getSettings().get(PersonalSettings.FAKE_PLAYERS);
			
			LMSecurity s = new LMSecurity(chunkOwner);
			s.level = p.getRank().config.forced_chunk_security.get();
			if(s.level == null) s.level = chunkOwner.getSettings().blocks;
			return s.canInteract(p);
		}
		
		@Override
		public void writeToNBT(NBTTagCompound tag, LMPlayerServer to)
		{
			tag.setLong("OM", chunk.ownerID.getMostSignificantBits());
			tag.setLong("OL", chunk.ownerID.getLeastSignificantBits());
			
			if(chunk.isChunkloaded && isFriendly(to))
			{
				tag.setBoolean("L", chunk.isChunkloaded);
			}
		}
		
		@Override
		public void readFromNBT(NBTTagCompound tag, int dim, ChunkCoordIntPair pos)
		{
			chunk = new ClaimedChunk(new UUID(tag.getLong("OM"), tag.getLong("OL")), dim, pos.chunkXPos, pos.chunkZPos);
			chunk.isChunkloaded = tag.getBoolean("L");
		}
		
		@Override
		public IChatComponent getChatComponent()
		{ return new ChatComponentText(chunk.getOwnerS().getProfile().getName()); }
	}
	
	public final int ID;
	public final LangKey langKey;
	private final EnumChatFormatting chatColor;
	private final int areaColor;
	
	public ChunkType(int id, String s, EnumChatFormatting c, int col)
	{
		ID = id;
		langKey = new LangKey("ftbu.chunktype." + s);
		chatColor = c;
		areaColor = col;
	}
	
	public boolean isClaimed()
	{ return false; }
	
	public boolean isChunkOwner(LMPlayer p)
	{ return this == WILDERNESS; }
	
	public boolean drawGrid()
	{ return this != WILDERNESS && this != UNLOADED; }
	
	@Override
	public int hashCode()
	{ return ID; }
	
	@Override
	public boolean equals(Object o)
	{ return hashCode() == o.hashCode(); }
	
	public EnumChatFormatting getChatColor(LMPlayer p)
	{ return chatColor; }
	
	public int getAreaColor(LMPlayer p)
	{ return areaColor; }
	
	public boolean canInteract(LMPlayerServer p, boolean leftClick)
	{ return this == WILDERNESS || this == SPAWN; }
	
	public void writeToNBT(NBTTagCompound tag, LMPlayerServer to)
	{
	}
	
	public void readFromNBT(NBTTagCompound tag, int dim, ChunkCoordIntPair pos)
	{
	}
	
	public IChatComponent getChatComponent()
	{ return langKey.chatComponent(); }
}