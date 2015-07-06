package latmod.ftbu.mod.client.minimap;

import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.claims.ChunkType;
import latmod.ftbu.mod.client.gui.GuiMinimap;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Minimap
{
	public static final ClientConfig config = new ClientConfig("minimap");
	public static final ClientConfig.Property renderIngame = new ClientConfig.Property("render_ingame", 0, "disabled", "right", "left");
	public static final ClientConfig.Property renderPlayers = new ClientConfig.Property("render_players", true);
	public static final ClientConfig.Property renderWaypoints = new ClientConfig.Property("render_waypoints", true);
	
	public static void init()
	{
		config.add(renderIngame);
		config.add(renderPlayers);
		config.add(renderWaypoints);
		ClientConfig.Registry.add(config);
		get(0);
	}
	
	//
	
	public final int dim;
	public final FastMap<Pos2D, MArea> areas;
	
	public Minimap(int d)
	{
		dim = d;
		areas = new FastMap<Pos2D, MArea>();
	}
	
	public int hashCode()
	{ return dim; }
	
	public int[] getChunkPixels(int cx, int cz)
	{
		MChunk c = getChunk(cx, cz);
		if(c != null) return c.pixels;
		return new int[16 * 16];
	}
	
	public void setChunkPixels(int cx, int cz, int[] col)
	{
		if(col.length != 16 * 16) return;
		boolean hasColor = false;
		for(int i = 0; i < col.length; i++)
			if(col[i] != 0) hasColor = true;
		
		if(hasColor) loadChunk(cx, cz).setPixels(col);
	}
	
	public MChunk getChunk(int cx, int cz)
	{
		Pos2D apos = MArea.getPos(cx, cz);
		MArea a = areas.get(apos);
		if(a == null) return null;
		return a.chunks.get(new Pos2D(cx, cz));
	}
	
	public MChunk loadChunk(int cx, int cz)
	{
		Pos2D apos = MArea.getPos(cx, cz);
		MArea a = areas.get(apos);
		if(a == null) areas.put(apos, a = new MArea(dim, apos));
		Pos2D cpos = new Pos2D(cx, cz);
		MChunk c = a.chunks.get(cpos);
		if(c == null) a.chunks.put(cpos, c = new MChunk(cpos));
		return c;
	}
	
	public void loadChunkTypes(int cx, int cz, byte size, byte[] types)
	{
		for(int z = 0; z < size; z++)
		for(int x = 0; x < size; x++)
		{
			ChunkType t = ChunkType.VALUES[types[x + z * size]];
			
			if(t != ChunkType.UNLOADED)
				loadChunk(cx + x, cz + z).type = t;
		}
		
		GuiMinimap.shouldRedraw = true;
	}
	
	public ChunkType getChunkType(int cx, int cz)
	{ MChunk c = getChunk(cx, cz); return (c == null) ? ChunkType.UNLOADED : c.type; }
	
	// Static //
	
	public static final FastMap<Integer, Minimap> minimaps = new FastMap<Integer, Minimap>();
	private static ThreadMinimap thread = null;
	
	public static Minimap get(int dim)
	{
		Minimap m = minimaps.get(Integer.valueOf(dim));
		if(m == null) minimaps.put(dim, m = new Minimap(dim));
		return m;
	}
	
	public static int getBlockColor(World w, int bx, int bz)
	{
		if(w.getChunkProvider().chunkExists(MathHelperLM.chunk(bx), MathHelperLM.chunk(bz)))
		{
			for(int by = 255; by > 0; by--)
			{
				Block b = w.getBlock(bx, by, bz);
				if(!b.isAir(w, bx, by, bz))
				{
					int col = BlockColors.getBlockColor(b, w.getBlockMetadata(bx, by, bz)).colorValue;
					int red = LatCore.Colors.getRed(col);
					int green = LatCore.Colors.getGreen(col);
					int blue = LatCore.Colors.getBlue(col);
					
					int d = MathHelperLM.clampInt((by - 64) * 5, -60, 60);
					
					red = MathHelperLM.clampInt(red + d, 0, 255);
					green = MathHelperLM.clampInt(green + d, 0, 255);
					blue = MathHelperLM.clampInt(blue + d, 0, 255);
					
					return LatCore.Colors.getRGBA(red, green, blue, 255);
				}
			}
		}
		
		return 0;
	}
	
	public static void startThread(ThreadMinimap t)
	{
		stopThread();
		thread = t;
		thread.start();
	}
	
	public static void stopThread()
	{
		//if(thread != null)
		//	thread.stop();
		thread = null;
	}
}