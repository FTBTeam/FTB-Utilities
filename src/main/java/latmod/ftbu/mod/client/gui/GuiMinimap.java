package latmod.ftbu.mod.client.gui;

import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiMinimap extends GuiLM
{
	public static final ResourceLocation tex = FTBU.mod.getLocation("textures/gui/minimap.png");
	public static final TextureCoords tex_mouse_over = new TextureCoords(tex, 124, 0, 16, 16);
	public static final TextureCoords tex_pixel = new TextureCoords(tex, 140, 0, 1, 1);
	
	private static ThreadMinimap thread = null;
	public static final int SIZE_CHUNKS = 7;
	public static final int SIZE = SIZE_CHUNKS * 16;
	private static int[] pixelData = new int[SIZE * SIZE];
	private static int GL_ID = 0;
	private static boolean generated = false;
	
	public final ChunkButton[] chunkButtons = new ChunkButton[SIZE_CHUNKS * SIZE_CHUNKS];
	
	public GuiMinimap()
	{
		super(new ContainerEmpty.ClientGui(), tex);
		xSize = 124;
		ySize = 153;
		
		if(GL_ID == 0) GL_ID = GL11.glGenLists(1);
		
		thread = new ThreadMinimap(mc.theWorld, (MathHelperLM.chunk(mc.thePlayer.posX) - 3) * 16, (MathHelperLM.chunk(mc.thePlayer.posZ) - 3) * 16);
		thread.start();
		
		for(int y = 0; y < SIZE_CHUNKS; y++) for(int x = 0; x < SIZE_CHUNKS; x++)
			chunkButtons[x + y * SIZE_CHUNKS] = new ChunkButton(this, 6 + x * 16, 26 + y * 16);
	}
	
	public void addWidgets(FastList<WidgetLM> l)
	{
		l.addAll(chunkButtons);
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		if(generated)
		{
			generated = false;
			
			GL11.glNewList(GL_ID, GL11.GL_COMPILE);
			
			for(int y = 0; y < SIZE; y++) for(int x = 0; x < SIZE; x++)
			{
				LatCore.Colors.setGLColor(pixelData[x + y * SIZE], 255);
				tex_pixel.render(this, x + 6, y + 26);
			}
			
			GL11.glEndList();
		}
		
		GL11.glCallList(GL_ID);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		for(ChunkButton b : chunkButtons)
		{
			if(b.mouseOver())
			{
				GL11.glColor4f(0.1F, 1F, 0.7F, 0.8F);
				b.render(tex_mouse_over);
			}
		}
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
	
	public void onGuiClosed()
	{
		thread = null;
	}
	
	public static class ChunkButton extends ButtonLM
	{
		public ChunkButton(GuiLM g, int x, int y)
		{
			super(g, x, y, 16, 16);
		}
		
		public void onButtonPressed(int b)
		{
		}
	}
	
	public static class ThreadMinimap extends Thread
	{
		public final World worldObj;
		public final int startX, startY;
		
		public ThreadMinimap(World w, int x, int y)
		{
			worldObj = w;
			startX = x;
			startY = y;
		}
		
		public void run()
		{
			try
			{
				generated = false;
				
				for(int z = 0; z < SIZE; z++)
				for(int x = 0; x < SIZE; x++)
				{
					int bx = startX + x;
					int bz = startY + z;
					pixelData[x + z * SIZE] = 0;
					
					if(worldObj.getChunkProvider().chunkExists(MathHelperLM.chunk(bx), MathHelperLM.chunk(bz)))
					{
						for(int by = 255; by > 0; by--)
						{
							Block b = worldObj.getBlock(bx, by, bz);
							if(!b.isAir(worldObj, bx, by, bz))
							{
								int col = b.getMapColor(worldObj.getBlockMetadata(bx, by, bz)).colorValue;
								int red = LatCore.Colors.getRed(col);
								int green = LatCore.Colors.getGreen(col);
								int blue = LatCore.Colors.getBlue(col);
								
								int d = MathHelperLM.clampInt((by - 64) * 5, -60, 60);
								
								red = MathHelperLM.clampInt(red + d, 0, 255);
								green = MathHelperLM.clampInt(green + d, 0, 255);
								blue = MathHelperLM.clampInt(blue + d, 0, 255);
								
								pixelData[x + z * SIZE] = LatCore.Colors.getRGBA(red, green, blue, 255);
								break;
							}
						}
					}
				}
				
				generated = true;
			}
			catch(Exception e)
			{ e.printStackTrace(); }
			
			thread = null;
		}
	}
}