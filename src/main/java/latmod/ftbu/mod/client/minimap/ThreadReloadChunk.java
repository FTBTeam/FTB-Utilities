package latmod.ftbu.mod.client.minimap;

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
	public final World worldObj;
	public final MChunk chunk;
	public final boolean calcHeight;
	public Chunk chunkMC;
	
	public ThreadReloadChunk(World w, MChunk c)
	{
		super("LMMC_" + c.posX + "_" + c.posY);
		setDaemon(true);
		worldObj = w;
		chunk = c;
		calcHeight = Minimap.calcHeight.getB();
	}
	
	public void run()
	{
		try
		{
			if(LMWorldClient.inst != null && worldObj.getChunkProvider().chunkExists(chunk.posX, chunk.posY))
			{
				chunkMC = worldObj.getChunkFromChunkCoords(chunk.posX, chunk.posY);
				
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
		int by = getTopY(bx, bz);
		if(by > 255 || by < 0) return 0xFF000000;
		
		Block b = worldObj.getBlock(bx, by, bz);
		
		if(!b.isAir(worldObj, bx, by, bz))
		{
			int col = getBlockColor(bx, by, bz, b);
			int red = LMColorUtils.getRed(col);
			int green = LMColorUtils.getGreen(col);
			int blue = LMColorUtils.getBlue(col);
			
			if(calcHeight)
			{
				int d = 0;
				
				if(getTopY(bx - 1, bz) < by || getTopY(bx, bz + 1) < by)
					d = 20;
				
				if(getTopY(bx + 1, bz) < by || getTopY(bx, bz - 1) < by)
					d = -20;
				
				red = MathHelperLM.clampInt(red + d, 0, 255);
				green = MathHelperLM.clampInt(green + d, 0, 255);
				blue = MathHelperLM.clampInt(blue + d, 0, 255);
			}
			
			return LMColorUtils.getRGBA(red, green, blue, 255);
		}
		
		return 0xFF000000;
	}
	
	private int getTopY(int bx, int bz)
	{
		int x = MathHelperLM.wrap(bx, 16);
		int z = MathHelperLM.wrap(bz, 16);
		
		for(int y = chunkMC.getTopFilledSegment() + 15; y > 0; --y)
		{
			Block block = chunkMC.getBlock(x, y, z);
			if(!block.isAir(worldObj, bx, y, bz))
				return y;
		}

		return -1;
	}
	
	private int getBlockColor(int x, int y, int z, Block b)
	{
		if(b == Blocks.sandstone) return MapColor.sandColor.colorValue;
		else if(b == Blocks.fire) return MapColor.redColor.colorValue;
		
		int m = worldObj.getBlockMetadata(x, y, z);
		
		if(b == Blocks.yellow_flower)
			return MapColor.yellowColor.colorValue;
		else if(b == Blocks.lava)
			return MapColor.adobeColor.colorValue;
		else if(b == Blocks.red_flower)
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
		else if(b == Blocks.grass && m == 0)
			return b.colorMultiplier(worldObj, x, y, z);
		
		return b.getMapColor(m).colorValue;
	}
}