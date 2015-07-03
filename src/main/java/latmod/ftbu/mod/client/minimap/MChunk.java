package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.mod.claims.ChunkType;

public class MChunk
{
	public final Pos2D pos;
	public ChunkType type;
	public final int[] pixels;
	
	public MChunk(Pos2D p)
	{
		pos = p;
		type = ChunkType.UNLOADED;
		pixels = new int[16 * 16];
	}
	
	public void setPixels(int[] col)
	{
		if(col.length != pixels.length) return;
		for(int i = 0; i < col.length; i++)
			pixels[i] = col[i];
	}
}