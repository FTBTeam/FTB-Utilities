package latmod.ftbu.mod.client.minimap;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.zip.*;

import javax.imageio.ImageIO;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.client.ClientConfig;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.util.Bits;
import latmod.ftbu.core.world.LMWorldClient;
import latmod.ftbu.mod.client.gui.GuiMinimap;
import latmod.ftbu.mod.player.ChunkType;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Minimap
{
	public static final ClientConfig clientConfig = new ClientConfig("minimap");
	public static final ClientConfig.Property renderIngame = new ClientConfig.Property("render_ingame", 0, "disabled", "right", "left");
	public static final ClientConfig.Property renderPlayers = new ClientConfig.Property("render_players", true);
	public static final ClientConfig.Property renderWaypoints = new ClientConfig.Property("render_waypoints", true);
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
	
	public static void init()
	{
		clientConfig.add(renderIngame);
		clientConfig.add(renderPlayers);
		clientConfig.add(renderWaypoints);
		clientConfig.add(calcHeight);
		clientConfig.add(blur);
		clientConfig.add(zoom);
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
		GuiMinimap.shouldRedraw = true;
	}
	
	public ChunkType getChunkType(int cx, int cz)
	{ MChunk c = getChunk(cx, cz); return (c == null) ? ChunkType.UNLOADED : c.type; }
	
	public File exportImage()
	{
		File f = new File(LatCoreMC.latmodFolder, "client/" + LMWorldClient.inst.worldIDS + "/minimap_" + dim + ".png");
		
		try
		{
			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;
			
			for(MArea a : areas.values) for(MChunk c : a.chunks.values)
			{
				boolean isOK = false;
				for(int i = 0; i < c.pixels.length; i++)
				{
					if(c.pixels[i] != 0xFF000000 && c.pixels[i] != 0)
					{ isOK = true; break; }
				}
				
				if(isOK)
				{
					if(c.posX < minX) minX = c.posX;
					if(c.posY < minY) minY = c.posY;
					if(c.posX > maxX) maxX = c.posX;
					if(c.posY > maxY) maxY = c.posY;
				}
			}
			
			if(minX == Integer.MAX_VALUE || minY == Integer.MAX_VALUE || maxX == Integer.MIN_VALUE || maxY == Integer.MIN_VALUE)
				return null;
			
			BufferedImage image = new BufferedImage((maxX - minX) * 16, (maxY - minY) * 16, BufferedImage.TYPE_INT_RGB);
			int w = image.getWidth();
			int h = image.getHeight();
			
			int background[] = new int[w * h];
			Arrays.fill(background, 0xFF000000);
			image.setRGB(0, 0, w, h, background, 0, w);
			background = null;
			
			LatCoreMC.logger.info("Exporting " + w + "x" + h);
			
			int offsetX = -minX;
			int offsetY = -minY;
			
			for(MArea a : areas.values) for(MChunk c : a.chunks.values)
			{
				int x = (c.posX + offsetX) * 16;
				int y = (c.posY + offsetY) * 16;
				
				if(x >= 0 && y >= 0 && x < w && y < h)
				{
					image.setRGB(x, y, 16, 16, c.pixels, 0, 16);
					//image.setRGB(x, y, 0xFFFF0000);
				}
				LatCoreMC.logger.info(c.posX + " : " + c.posY + " | " + x + " : " + y + " | " + offsetX + " : " + offsetY);
			}
			
			ImageIO.write(image, "PNG", f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return f;
	}
	
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
			int by = getTopY(w, bx, bz);
			
			Block b = w.getBlock(bx, by, bz);
			if(!b.isAir(w, bx, by, bz))
			{
				int col = BlockColors.getBlockColor(b, w.getBlockMetadata(bx, by, bz)).colorValue;
				int red = LMColorUtils.getRed(col);
				int green = LMColorUtils.getGreen(col);
				int blue = LMColorUtils.getBlue(col);
				
				if(calcHeight.getB())
				{
					int d = 0;
					
					if(getTopY(w, bx - 1, bz) < by || getTopY(w, bx, bz + 1) < by)
						d = 20;
					
					if(getTopY(w, bx + 1, bz) < by || getTopY(w, bx, bz - 1) < by)
						d = -20;
					
					red = MathHelperLM.clampInt(red + d, 0, 255);
					green = MathHelperLM.clampInt(green + d, 0, 255);
					blue = MathHelperLM.clampInt(blue + d, 0, 255);
				}
				
				return LMColorUtils.getRGBA(red, green, blue, 255);
			}
		}
		
		return 0xFF000000;
	}
	
	private static int getTopY(World w, int bx, int bz)
	{
		for(int by = 255; by > 0; by--)
		{
			Block b = w.getBlock(bx, by, bz);
			if(!b.isAir(w, bx, by, bz))
				return by;
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
	
	public static boolean load()
	{
		minimaps.clear();
		
		File f = new File(LatCoreMC.latmodFolder, "client/" + LMWorldClient.inst.worldIDS + "/minimap.data");
		
		if(f.exists()) try
		{
			long totalChunks = 0L;
			
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(f))));
			
			int s = dis.readShort();
			int i = 0;
			
			for(i = 0; i < s; i++)
			{
				int d = dis.readInt();
				long chunks = dis.readLong();
				
				Minimap m = get(d);
				
				for(long i1 = 0L; i1 < chunks; i1++)
				{
					int cx = dis.readInt();
					int cy = dis.readInt();
					int type = dis.readInt();
					int[] pixels = new int[256];
					
					for(i = 0; i < 16; i++)
					{
						int r = dis.readByte() & 0xFF;
						int g = dis.readByte() & 0xFF;
						int b = dis.readByte() & 0xFF;
						pixels[i] = LMColorUtils.getRGBA(r, g, b, 255);
					}
					
					MChunk c = m.loadChunk(cx, cy);
					System.arraycopy(pixels, 0, c.pixels, 0, 256);
					c.setType(type);
				}
				
				totalChunks += chunks;
			}
			
			dis.close();
			
			if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("Loaded " + totalChunks + " chunks from " + f.getName());
			return true;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("Loaded 0 chunks from " + f.getName());
		return false;
	}
	
	public static boolean save()
	{
		File f = LMFileUtils.newFile(new File(LatCoreMC.latmodFolder, "client/" + LMWorldClient.inst.worldIDS + "/minimap.data"));
		
		try
		{
			long totalChunks = 0L;
			
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(f))));
			
			dos.writeShort(minimaps.size());
			
			for(Minimap m : minimaps.values)
			{
				long chunks = 0L;
				
				for(MArea a : m.areas.values)
					chunks += a.chunks.size();
				
				totalChunks += chunks;
				
				dos.writeInt(m.dim);
				dos.writeLong(chunks);
				
				for(MArea a : m.areas.values) for(MChunk c : a.chunks.values)
				{
					dos.writeInt(c.posX);
					dos.writeInt(c.posY);
					dos.writeInt(c.getTypeID());
					
					for(int i = 0; i < c.pixels.length; i++)
					{
						dos.writeByte((byte)LMColorUtils.getRed(c.pixels[i]));
						dos.writeByte((byte)LMColorUtils.getGreen(c.pixels[i]));
						dos.writeByte((byte)LMColorUtils.getBlue(c.pixels[i]));
					}
				}
			}
			
			dos.flush();
			dos.close();
			
			if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("Saved " + totalChunks + " chunks");
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return false;
	}
}