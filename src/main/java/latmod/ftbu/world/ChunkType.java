package latmod.ftbu.world;

import cpw.mods.fml.relauncher.*;
import ftb.lib.LMDimUtils;
import latmod.ftbu.mod.FTBU;
import latmod.lib.MathHelperLM;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public enum ChunkType
{
	UNLOADED("unloaded", EnumChatFormatting.DARK_GRAY, 0xFF000000),
	SPAWN("spawn", EnumChatFormatting.AQUA, 0xFF00EFDF),
	WORLD_BORDER("world_border", EnumChatFormatting.RED, 0xFFFF0000),
	WILDERNESS("wilderness", EnumChatFormatting.DARK_GREEN, 0xFF2F9E00),
	CLAIMED_OTHER("claimed", EnumChatFormatting.BLUE, 0xFF0094FF),
	CLAIMED_SELF("claimed", EnumChatFormatting.GREEN, 0xFF8CBF00),
	CLAIMED_FRIEND("claimed", EnumChatFormatting.GREEN, 0xFF8CBF00),
	
	; public static final ChunkType[] VALUES = values();
	
	public final int ID;
	public final String lang;
	public final EnumChatFormatting chatColor;
	public final int areaColor;
	
	ChunkType(String s, EnumChatFormatting c, int col)
	{
		ID = ordinal();
		lang = "chunktype." + s;
		chatColor = c;
		areaColor = col;
	}
	
	public boolean isFriendly()
	{ return this == WILDERNESS || this == CLAIMED_SELF || this == CLAIMED_FRIEND; }
	
	public boolean isClaimed()
	{ return this == CLAIMED_OTHER || this == CLAIMED_SELF || this == CLAIMED_FRIEND; }
	
	public boolean canClaim(boolean admin)
	{ return this == WILDERNESS || (admin && (this == SPAWN || isClaimed())); }
	
	public boolean drawGrid()
	{ return this != WILDERNESS && this != UNLOADED; }
	
	@SideOnly(Side.CLIENT)
	public String getIDS()
	{ return FTBU.mod.translateClient(lang); }
	
	public static ChunkType get(int dim, int cx, int cz, LMPlayerServer p)
	{
		World w = LMDimUtils.getWorld(dim);
		if(w == null || !w.getChunkProvider().chunkExists(cx, cz)) return UNLOADED;
		if(Claims.isInSpawn(dim, cx, cz)) return SPAWN;
		if(LMWorldServer.inst.settings.isOutside(dim, cx, cz)) return WORLD_BORDER;
		ClaimedChunk c = Claims.get(dim, cx, cz);
		if(c == null) return WILDERNESS;
		if(p == null) return CLAIMED_OTHER;
		else if(c.claims.owner.equalsPlayer(p)) return CLAIMED_SELF;
		else if(c.claims.owner.isFriend(p)) return CLAIMED_FRIEND;
		else return CLAIMED_OTHER;
	}
	
	public static ChunkType getD(int dim, double x, double z, LMPlayerServer p)
	{ return get(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z), p); }
	
	public static int getChunkTypeI(int dim, int cx, int cz, LMPlayerServer p)
	{
		ClaimedChunk c = Claims.get(dim, cx, cz);
		if(c != null) return c.claims.owner.playerID;
		ChunkType type = ChunkType.get(dim, cx, cz, p);
		return -type.ordinal();
	}
	
	public static ChunkType getChunkTypeFromI(int i, LMPlayer o)
	{
		if(i <= 0) return ChunkType.VALUES[-i];
		LMPlayer p = LMWorld.getWorld().getPlayer(i);
		
		if(o != null)
		{
			if(p == null) return CLAIMED_OTHER;
			else if(o.equalsPlayer(p)) return CLAIMED_SELF;
			else if(o.isFriend(p)) return CLAIMED_FRIEND;
			else return CLAIMED_OTHER;
		}
		
		return WILDERNESS;
	}
}