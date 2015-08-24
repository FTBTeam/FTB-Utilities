package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.player.ChunkType;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Minimap
{
	public static final ClientConfig clientConfig = new ClientConfig("minimap");
	public static final ClientConfig.Property renderIngame = new ClientConfig.Property("render_ingame", 0, "disabled", "right", "left");
	public static final ClientConfig.Property renderPlayers = new ClientConfig.Property("render_players", false);
	public static final ClientConfig.Property renderWaypoints = new ClientConfig.Property("render_waypoints", false);
	public static final ClientConfig.Property calcHeight = new ClientConfig.Property("calc_height", true);
	public static final ClientConfig.Property blur = new ClientConfig.Property("blur", false)
	{
		public void onClicked()
		{
			super.onClicked();
			
			for(Minimap m : minimaps.values)
				for(MArea a : m.areas.values)
					a.isDirty = true;
		}
	};
	
	public static final int[] zoomA = { 3, 5, 7, 9, 11, 13 };
	public static final ClientConfig.Property zoom = new ClientConfig.Property("zoom", 2, getZoomAValues()).setRawValues();
	private static final String[] getZoomAValues()
	{
		String[] s = new String[zoomA.length];
		for(int i = 0; i < zoomA.length; i++)
			s[i] = zoomA[i] + "x";
		return s;
	}
	
	public static final int[] sizeA = { 64, 96, 128, 160 };
	public static final ClientConfig.Property size = new ClientConfig.Property("size_ingame", 2, getSizeAValues()).setRawValues();
	private static final String[] getSizeAValues()
	{
		String[] s = new String[sizeA.length];
		for(int i = 0; i < sizeA.length; i++)
			s[i] = sizeA[i] + "px";
		return s;
	}
	
	public static final ClientConfig.Property renderGrid = new ClientConfig.Property("render_grid", true);
	public static final ClientConfig.Property renderClaimedChunks = new ClientConfig.Property("render_claimed_chunks", true);
	
	public static void init()
	{
		clientConfig.add(renderIngame);
		//clientConfig.add(renderPlayers);
		//clientConfig.add(renderWaypoints);
		clientConfig.add(calcHeight);
		clientConfig.add(blur);
		clientConfig.add(zoom);
		clientConfig.add(size);
		clientConfig.add(renderGrid);
		clientConfig.add(renderClaimedChunks);
		ClientConfig.Registry.add(clientConfig);
		get(0);
	}
	
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
	
	public void loadChunkTypes(int cx, int cz, int size, int[] types)
	{
		for(int z = 0; z < size; z++) for(int x = 0; x < size; x++)
			loadChunk(cx + x, cz + z).setType(types[x + z * size]);
	}
	
	public ChunkType getChunkType(int cx, int cz)
	{ MChunk c = getChunk(cx, cz); return (c == null) ? ChunkType.UNLOADED : c.type; }
	
	public void reloadArea(World world, int x, int y, int w, int h)
	{
		for(int cy = y; cy < y + h; cy++)
		for(int cx = x; cx < x + w; cx++)
			loadChunk(cx, cy).reload(world);
		if(w == h) LMNetHelper.sendToServer(new MessageAreaRequest(x - 1, y - 1, world.provider.dimensionId, w + 2));
	}
	
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