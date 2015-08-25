package latmod.ftbu.mod.client.minimap;

import java.util.Arrays;

import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldClient;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ThreadReloadChunk extends Thread
{
	private static final short defHeight = -1;
	
	public final World worldObj;
	public final MChunk chunk;
	public final boolean calcHeight, customColors;
	public Chunk chunkMC;
	public final short[] heightMap;
	public short maxHeight = 0;
	
	public ThreadReloadChunk(World w, MChunk c)
	{
		super("LMMC_" + c.posX + "_" + c.posY);
		setDaemon(true);
		worldObj = w;
		chunk = c;
		calcHeight = Minimap.calcHeight.getB();
		customColors = Minimap.customMapColors.getB();
		heightMap = calcHeight ? new short[256] : null;
		if(calcHeight) Arrays.fill(heightMap, defHeight);
	}
	
	public void run()
	{
		try
		{
			if(LMWorldClient.inst != null && worldObj.getChunkProvider().chunkExists(chunk.posX, chunk.posY))
			{
				chunkMC = worldObj.getChunkFromChunkCoords(chunk.posX, chunk.posY);
				maxHeight = (short)Math.max(255, chunkMC.getTopFilledSegment() + 15);
				
				int x = chunk.posX * 16;
				int y = chunk.posY * 16;
				
				for(int i = 0; i < 256; i++)
				{
					int bx = x + (i % 16);
					int by = y + (i / 16);
					int col = getBlockColor(bx, by);
					chunk.setPixel(bx, by, col);
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		chunk.thread = null;
	}
	
	public int getBlockColor(int bx, int bz)
	{
		short by = getTopY(bx, bz);
		if(by == defHeight || by > 255) return 0;
		
		Block b = worldObj.getBlock(bx, by, bz);
		
		if(!b.isAir(worldObj, bx, by, bz))
		{
			int col = getBlockColor(bx, by, bz, b);
			int red = LMColorUtils.getRed(col);
			int green = LMColorUtils.getGreen(col);
			int blue = LMColorUtils.getBlue(col);
			
			if(calcHeight)
			{
				short bw = getTopY(bx - 1, bz);
				short be = getTopY(bx + 1, bz);
				short bn = getTopY(bx, bz - 1);
				short bs = getTopY(bx, bz + 1);
				
				if((bw != defHeight && bw < by) || (bn != defHeight && bn < by))
				{
					red = MathHelperLM.clampInt(red + 20, 0, 255);
					green = MathHelperLM.clampInt(green + 20, 0, 255);
					blue = MathHelperLM.clampInt(blue + 20, 0, 255);
				}
				else if((be != defHeight && be < by) || (bs != defHeight && bs < by))
				{
					red = MathHelperLM.clampInt(red - 20, 0, 255);
					green = MathHelperLM.clampInt(green - 20, 0, 255);
					blue = MathHelperLM.clampInt(blue - 20, 0, 255);
				}
			}
			
			return LMColorUtils.getRGBA(red, green, blue, 255);
		}
		
		return 0;
	}
	
	private short getTopY(int bx, int bz)
	{
		int x = MathHelperLM.wrap(bx, 16);
		int z = MathHelperLM.wrap(bz, 16);
		
		Chunk c = chunkMC;
		short max = maxHeight;
		boolean mapValue = false;
		
		if(calcHeight)
		{
			int cx = MathHelperLM.chunk(bx);
			int cz = MathHelperLM.chunk(bz);
			
			if(cx == chunk.posX && cz == chunk.posY)
			{
				mapValue = true;
				if(heightMap[x + z * 16] != defHeight)
					return heightMap[x + z * 16];
			}
			else
			{
				c = worldObj.getChunkFromBlockCoords(bx, bz);
				max = (short)Math.max(255, c.getTopFilledSegment() + 15);
			}
		}
		
		
		for(short y = max; y > 0; --y)
		{
			Block block = c.getBlock(x, y, z);
			if(block != Blocks.tallgrass && !block.isAir(worldObj, bx, y, bz))
			{
				if(mapValue) heightMap[x + z * 16] = y;
				return y;
			}
		}
		
		return defHeight;
	}
	
	private int getBlockColor(int x, int y, int z, Block b)
	{
		if(b == Blocks.sandstone) return MapColor.sandColor.colorValue;
		else if(b == Blocks.fire) return MapColor.redColor.colorValue;
		else if(b == Blocks.yellow_flower) return MapColor.yellowColor.colorValue;
		else if(b == Blocks.lava) return MapColor.adobeColor.colorValue;
		else if(b == Blocks.end_stone) return MapColor.sandColor.colorValue;
		
		int m = worldObj.getBlockMetadata(x, y, z);
		
		if(b == Blocks.red_flower)
		{
			if(m == 0) return MapColor.yellowColor.colorValue;
			else if(m == 1) return MapColor.lightBlueColor.colorValue;
			else if(m == 2) return MapColor.magentaColor.colorValue;
			else if(m == 3) return MapColor.silverColor.colorValue;
			else if(m == 4) return MapColor.redColor.colorValue;
			else if(m == 5) return MapColor.adobeColor.colorValue;
			else if(m == 6) return MapColor.snowColor.colorValue;
			else if(m == 7) return MapColor.pinkColor.colorValue;
			else if(m == 8) return MapColor.silverColor.colorValue;
		}
		
		if(customColors)
		{
			if(b == Blocks.grass && m == 0) return b.colorMultiplier(worldObj, x, y, z);
		}
		
		return b.getMapColor(m).colorValue;
	}
}