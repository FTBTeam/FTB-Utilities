package latmod.ftbu.world;

import cpw.mods.fml.relauncher.*;
import ftb.lib.LMDimUtils;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfigClaims;
import latmod.ftbu.util.LMSecurity;
import latmod.lib.MathHelperLM;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ChunkType
{
	public static final ChunkType UNLOADED = new ChunkType(0, "unloaded", EnumChatFormatting.DARK_GRAY, 0xFF000000);
	public static final ChunkType SPAWN = new ChunkType(-1, "spawn", EnumChatFormatting.AQUA, 0xFF00EFDF);
	public static final ChunkType WORLD_BORDER = new ChunkType(-2, "world_border", EnumChatFormatting.RED, 0xFFFF0000);
	public static final ChunkType WILDERNESS = new ChunkType(-3, "wilderness", EnumChatFormatting.DARK_GREEN, 0xFF2F9E00);
	
	public static final ChunkType[] UNCLAIMED_VALUES = new ChunkType[]
	{
		UNLOADED,
		SPAWN,
		WORLD_BORDER,
		WILDERNESS,
	};
	
	private static final class ChunkTypeClaimed extends ChunkType
	{
		public LMPlayer chunkOwner = null;
		
		public ChunkTypeClaimed(LMPlayer o)
		{ super(o.playerID, "claimed", null, 0); chunkOwner = o; }
		
		public boolean isFriendly(LMPlayer p)
		{ return chunkOwner.equalsPlayer(p) || chunkOwner.isFriend(p); }
		
		public boolean isClaimed()
		{ return true; }
		
		public EnumChatFormatting getChatColor(LMPlayer p)
		{ return isFriendly(p) ? EnumChatFormatting.GREEN : EnumChatFormatting.BLUE; }
		
		public int getAreaColor(LMPlayer p)
		{ return isFriendly(p) ? 0xFF00FF21 : 0xFF0094FF; }
		
		public boolean canInteract(LMPlayerServer p, boolean leftClick)
		{
			if(chunkOwner.equals(p)) return true;
			LMSecurity s = new LMSecurity(chunkOwner);
			s.level = FTBUConfigClaims.forcedChunkSecurity.get();
			if(s.level == null) s.level = chunkOwner.settings.blocks;
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
	
	public boolean canClaimChunk(LMPlayer p)
	{ return this == WILDERNESS; }
	
	public boolean drawGrid()
	{ return this != WILDERNESS && this != UNLOADED; }
	
	@SideOnly(Side.CLIENT)
	public String getIDS()
	{ return FTBU.mod.translateClient(lang); }
	
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
	
	public static ChunkType get(int dim, int cx, int cz)
	{
		World w = LMDimUtils.getWorld(dim);
		if(w == null || !w.getChunkProvider().chunkExists(cx, cz)) return UNLOADED;
		if(Claims.isInSpawn(dim, cx, cz)) return SPAWN;
		if(LMWorldServer.inst.settings.isOutside(dim, cx, cz)) return WORLD_BORDER;
		ClaimedChunk c = Claims.get(dim, cx, cz);
		if(c == null) return WILDERNESS;
		return new ChunkTypeClaimed(c.claims.owner);
	}
	
	public static ChunkType getD(int dim, double x, double z)
	{ return get(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public static int getChunkTypeI(int dim, int cx, int cz)
	{ return ChunkType.get(dim, cx, cz).ID; }
	
	public static ChunkType getChunkTypeFromI(int i)
	{
		if(i <= 0) return ChunkType.UNCLAIMED_VALUES[-i];
		LMPlayer p = LMWorld.getWorld().getPlayer(i);
		return (p == null) ? WILDERNESS : new ChunkTypeClaimed(p);
	}
}