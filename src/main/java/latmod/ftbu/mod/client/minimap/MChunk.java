package latmod.ftbu.mod.client.minimap;

import java.util.Arrays;

import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.player.ChunkType;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class MChunk
{
	public final MArea area;
	public final int posX, posY, rposX, rposY;
	public final int[] pixels;
	public ChunkType type;
	public LMPlayerClient owner;
	
	public MChunk(MArea a, int x, int y)
	{
		area = a;
		posX = x;
		posY = y;
		
		x = (posX % MArea.size_c);
		if(x < 0) x += MArea.size_c;
		rposX = x;
		
		y = (posY % MArea.size_c);
		if(y < 0) y += MArea.size_c;
		rposY = y;
		
		pixels = new int[256];
		Arrays.fill(pixels, 0xFF000000);
		type = ChunkType.UNLOADED;
		owner = null;
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
	
	public int getTypeID()
	{
		if(owner != null)
			return owner.playerID;
		return -type.ID;
	}
	
	public void setPixel(int x, int y, int col)
	{
		x = (x % 16);
		if(x < 0) x += 16;
		
		y = (y % 16);
		if(y < 0) y += 16;
		
		col = LMColorUtils.getRGBA(col, 255);
		if(pixels[x + y * 16] != col) area.isDirty = true;
		pixels[x + y * 16] = col;
	}
	
	public static short getIndexC(int cx, int cy)
	{
		cx = (cx % MArea.size_c);
		if(cx < 0) cx += MArea.size_c;
		
		cy = (cy % MArea.size_c);
		if(cy < 0) cy += MArea.size_c;
		
		return Bits.byteToShort(cx, cy);
	}
}