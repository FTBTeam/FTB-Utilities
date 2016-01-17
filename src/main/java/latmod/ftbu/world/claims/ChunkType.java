package latmod.ftbu.world.claims;

import ftb.lib.LMSecurity;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.world.*;
import net.minecraft.util.EnumChatFormatting;

public class ChunkType
{
	public static final ChunkType UNLOADED = new ChunkType(0, "unloaded", EnumChatFormatting.DARK_GRAY, 0xFF000000);
	public static final ChunkType SPAWN = new ChunkType(-1, "spawn", EnumChatFormatting.AQUA, 0xFF00EFDF);
	public static final ChunkType WORLD_BORDER = new ChunkType(-2, "world_border", EnumChatFormatting.RED, 0xFFFF0000);
	public static final ChunkType WILDERNESS = new ChunkType(-3, "wilderness", EnumChatFormatting.DARK_GREEN, 0xFF2F9E00);
	public static final ChunkType LOADED_SELF = new ChunkType(-4, "chunkloaded", EnumChatFormatting.RED, 0xFFFF0000);
	
	public static final ChunkType[] UNCLAIMED_VALUES = new ChunkType[] {UNLOADED, SPAWN, WORLD_BORDER, WILDERNESS, LOADED_SELF,};
	
	public static final class PlayerClaimed extends ChunkType
	{
		public LMPlayer chunkOwner = null;
		
		public PlayerClaimed(LMPlayer o)
		{
			super(o.playerID, "claimed", null, 0);
			chunkOwner = o;
		}
		
		public boolean isFriendly(LMPlayer p)
		{ return chunkOwner.equalsPlayer(p) || chunkOwner.isFriend(p); }
		
		public boolean isClaimed()
		{ return true; }
		
		public boolean isChunkOwner(LMPlayer p)
		{ return chunkOwner.equals(p); }
		
		public EnumChatFormatting getChatColor(LMPlayer p)
		{ return isFriendly(p) ? EnumChatFormatting.GREEN : EnumChatFormatting.BLUE; }
		
		public int getAreaColor(LMPlayer p)
		{ return isFriendly(p) ? 0xFF00FF21 : 0xFF0094FF; }
		
		public boolean canInteract(LMPlayerServer p, boolean leftClick)
		{
			if(chunkOwner.equals(p)) return true;
			else if(p.isFake()) return chunkOwner.getSettings().get(PersonalSettings.FAKE_PLAYERS);
			
			LMSecurity s = new LMSecurity(chunkOwner);
			s.level = p.getRank().config.forced_chunk_security.get();
			if(s.level == null) s.level = chunkOwner.getSettings().blocks;
			return s.canInteract(p);
		}
	}
	
	public final int ID;
	public final String lang;
	private final EnumChatFormatting chatColor;
	private final int areaColor;
	
	public ChunkType(int id, String s, EnumChatFormatting c, int col)
	{
		ID = id;
		lang = "chunktype." + s;
		chatColor = c;
		areaColor = col;
	}
	
	public boolean isClaimed()
	{ return false; }
	
	public boolean isChunkOwner(LMPlayer p)
	{ return this == WILDERNESS; }
	
	public boolean drawGrid()
	{ return this != WILDERNESS && this != UNLOADED; }
	
	public String getIDS()
	{ return FTBU.mod.translate(lang); }
	
	public int hashCode()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return hashCode() == o.hashCode(); }
	
	public EnumChatFormatting getChatColor(LMPlayer p)
	{ return chatColor; }
	
	public int getAreaColor(LMPlayer p)
	{ return areaColor; }
	
	public boolean canInteract(LMPlayerServer p, boolean leftClick)
	{ return this == WILDERNESS || this == SPAWN; }
}