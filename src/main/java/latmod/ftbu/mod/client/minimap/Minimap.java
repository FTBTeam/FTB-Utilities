package latmod.ftbu.mod.client.minimap;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.net.ClientAction;
import latmod.ftbu.world.ChunkType;
import latmod.lib.*;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class Minimap
{
	public static MOptions mapOptions = new MOptions();
	
	public final int dim;
	public final FastMap<Long, MArea> areas;
	
	public Minimap(int d)
	{
		dim = d;
		areas = new FastMap<Long, MArea>();
	}
	
	public int hashCode()
	{ return dim; }
	
	public MChunk getChunk(int cx, int cy)
	{
		MArea a = areas.get(MArea.getIndexC(cx, cy));
		if(a == null) return null;
		return a.chunks.get(MChunk.getIndexC(cx, cy));
	}
	
	public MArea loadArea(int cx, int cz)
	{
		long apos = MArea.getIndexC(cx, cz);
		MArea a = areas.get(apos);
		if(a == null) areas.put(apos, a = new MArea(this, Bits.intFromLongA(apos), Bits.intFromLongB(apos)));
		return a;
	}
	
	public MChunk loadChunk(int cx, int cy)
	{
		MArea a = loadArea(cx, cy);
		short s = MChunk.getIndexC(cx, cy);
		MChunk c = a.chunks.get(s);
		if(c == null) a.chunks.put(s, c = new MChunk(a, cx, cy));
		return c;
	}
	
	public void loadChunkTypes(int cx, int cz, int sizeX, int sizeZ, int[] types)
	{
		for(int z = 0; z < sizeZ; z++) for(int x = 0; x < sizeX; x++)
			loadChunk(cx + x, cz + z).setType(types[x + z * sizeX]);
	}
	
	public ChunkType getChunkType(int cx, int cz)
	{ MChunk c = getChunk(cx, cz); return (c == null) ? ChunkType.UNLOADED : c.type; }
	
	public void reloadArea(World world, int x, int y, int w, int h)
	{
		for(int cy = y; cy < y + h; cy++)
		for(int cx = x; cx < x + w; cx++)
			loadChunk(cx, cy).reload(world);
		if(w == h) requestArea(w + 2);
	}
	
	public void requestArea(int s)
	{ ClientAction.ACTION_AREA_REQUEST.send(s); }
	
	// Static //
	
	public static final FastMap<Integer, Minimap> minimaps = new FastMap<Integer, Minimap>();
	
	public static Minimap get(int dim)
	{
		Minimap m = minimaps.get(Integer.valueOf(dim));
		if(m == null) minimaps.put(dim, m = new Minimap(dim));
		return m;
	}
	
	public static boolean load()
	{
		minimaps.clear();
		return true;
	}
	
	public static boolean save()
	{
		for(Minimap m : minimaps.values)
			for(MArea a : m.areas.values)
				a.save();
		return true;
	}
}