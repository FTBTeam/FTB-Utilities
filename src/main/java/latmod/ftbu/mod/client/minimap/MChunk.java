package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.util.MathHelperLM;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.player.ChunkType;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class MChunk
{
	public final Pos2D pos;
	public final int[] pixels;
	
	public ChunkType type;
	public LMPlayerClient owner;
	
	public MChunk(Pos2D p)
	{
		pos = p;
		type = ChunkType.UNLOADED;
		pixels = new int[16 * 16];
	}
	
	public void setType(int i)
	{
		type = ChunkType.WILDERNESS;
		owner = null;
		
		if(i > 0)
		{
			owner = LMWorldClient.inst.getPlayer(i);
			if(owner == null) type = ChunkType.CLAIMED_OTHER;
			else if(owner.equalsPlayer(LMWorldClient.inst.clientPlayer)) type = ChunkType.CLAIMED_SELF;
			else type = owner.isFriend(LMWorldClient.inst.clientPlayer) ? ChunkType.CLAIMED_FRIEND : ChunkType.CLAIMED_OTHER;
		}
		else type = ChunkType.VALUES[MathHelperLM.clampInt(-i, 0, ChunkType.VALUES.length - 1)];
	}
	
	public void setPixels(int[] col)
	{
		if(col.length != pixels.length) return;
		for(int i = 0; i < col.length; i++)
			pixels[i] = col[i];
	}
}