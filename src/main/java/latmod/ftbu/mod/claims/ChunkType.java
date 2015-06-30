package latmod.ftbu.mod.claims;

import java.util.List;

import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.util.MathHelperLM;
import net.minecraft.util.EnumChatFormatting;

public enum ChunkType
{
	SPAWN,
	WORLD_BORDER,
	WILDERNESS,
	CLAIMED,
	CLAIMED_SELF,
	CLAIMED_FRIEND;
	
	public boolean isFriendly()
	{ return this == WILDERNESS || this == CLAIMED_SELF || this == CLAIMED_FRIEND; }
	
	public static ChunkType get(int dim, int cx, int cz, LMPlayer p)
	{
		//if(!LatCoreMC.isDedicatedServer()) return WILDERNESS;
		
		if(Claims.isInSpawn(dim, cx, cz)) return SPAWN;
		if(Claims.isOutsideWorldBorder(dim, cx, cz)) return WORLD_BORDER;
		ClaimedChunk c = Claims.get(dim, cx, cz);
		if(c == null) return WILDERNESS;
		if(p == null) return CLAIMED;
		else if(c.owner.equalsPlayer(p)) return CLAIMED_SELF;
		else if(c.owner.isFriend(p)) return CLAIMED_FRIEND;
		else return CLAIMED;
	}
	
	public static ChunkType getD(int dim, double x, double z, LMPlayer p)
	{ return get(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z), p); }
	
	public static void getMessage(int dim, int cx, int cz, LMPlayer p, List<String> l, boolean shift)
	{
		ChunkType t = get(dim, cx, cz, p);
		
		if(t == SPAWN) l.add(EnumChatFormatting.AQUA + "Spawn area");
		else if(t == WILDERNESS) l.add(EnumChatFormatting.DARK_GREEN + "Wilderness");
		else if(t == WORLD_BORDER) l.add(EnumChatFormatting.DARK_RED + "You have reached world border!");
		else if(t == CLAIMED_SELF)
		{
			l.add(EnumChatFormatting.GREEN + "Claimed area");
			if(shift) l.add(p.getName());
		}
		else
		{
			ClaimedChunk c = Claims.get(dim, cx, cz);
			
			if(t == CLAIMED_FRIEND)
			{
				l.add(EnumChatFormatting.GREEN + "Claimed area");
				if(shift) l.add(EnumChatFormatting.GREEN + c.owner.getName());
			}
			else
			{
				l.add(EnumChatFormatting.BLUE + "Claimed area");
				if(shift) l.add(EnumChatFormatting.BLUE + c.owner.getName());
			}
		}
	}
}