package latmod.ftbu.mod.client.minimap;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldClient;
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
	public static final ClientConfig.Property customMapColors = new ClientConfig.Property("custom_map_colors", true);
	
	public static void init()
	{
		clientConfig.add(renderIngame);
		clientConfig.add(renderPlayers);
		clientConfig.add(renderWaypoints);
		clientConfig.add(calcHeight);
		clientConfig.add(blur);
		clientConfig.add(zoom);
		clientConfig.add(size);
		clientConfig.add(renderGrid);
		clientConfig.add(renderClaimedChunks);
		clientConfig.add(customMapColors);
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
	
	public File exportImage()
	{
		if(areas.isEmpty()) return null;
		
		try
		{
			int ms = Integer.MAX_VALUE;
			int minX = ms;
			int minY = ms;
			int maxX = -ms;
			int maxY = -ms;
			
			for(MArea a : areas) for(MChunk c : a.chunks)
			{
				for(int i = 0; i < c.pixels.length; i++)
				{
					if(c.pixels[i] != 0)
					{
						if(c.posX < minX) minX = c.posX;
						if(c.posY < minY) minY = c.posY;
						if(c.posX > maxX) maxX = c.posX;
						if(c.posY > maxY) maxY = c.posY;
						continue;
					}
				}
			}
			
			if(minX == ms || minY == ms || maxX == -ms || maxY == -ms)
				return null;
			
			long w = (maxX - minX + 1L) * 16L;
			long h = (maxY - minY + 1L) * 16L;
			
			if(w <= 0L || h <= 0L || w > Integer.MAX_VALUE || h > Integer.MAX_VALUE) return null;
			
			PixelBuffer image = new PixelBuffer((int)w, (int)h);
			
			for(MArea a : areas) for(MChunk c : a.chunks)
			{
				int x = (c.posX - minX) * 16;
				int y = (c.posY - minY) * 16;
				if(x > 0 && y > 0) image.setRGB(x, y, 16, 16, c.pixels, 0, 16);
			}
			
			File file = new File(LMWorldClient.inst.clientDataFolder, "minimap_" + dim + ".png");
			ImageIO.write(image.toImage(BufferedImage.TYPE_INT_RGB), "PNG", file);
			return file;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return null;
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