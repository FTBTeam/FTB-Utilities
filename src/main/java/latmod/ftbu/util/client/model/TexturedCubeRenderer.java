package latmod.ftbu.util.client.model;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class TexturedCubeRenderer extends CubeRenderer
{
	protected final float[] uMap = new float[24];
	protected final float[] vMap = new float[24];
	public final boolean isInterpolated;
	
	public TexturedCubeRenderer(boolean b)
	{
		isInterpolated = b;
		setUV(-1, 0F, 0F, 1F, 1F);
	}
	
	protected void setUV0(int i, float u, float v)
	{ uMap[i] = u; vMap[i] = v; }
	
	public void setUV(int side, float minU, float minV, float maxU, float maxV)
	{
		if(side < 0 || side >= 6)
		{
			for(int i = 0; i < 6; i++)
				setUV(i, minU, minV, maxU, maxV);
		}
		else
		{
			if(side == 0)
			{
				setUV0(0, minU, minV);
				setUV0(1, maxU, minV);
				setUV0(2, maxU, maxV);
				setUV0(3, minU, maxV);
			}
			else if(side == 1)
			{
				setUV0(4, minU, minV);
				setUV0(5, minU, maxV);
				setUV0(6, maxU, maxV);
				setUV0(7, maxU, minV);
			}
			else if(side == 2)
			{
				setUV0(8, minU, maxV);
				setUV0(9, maxU, maxV);
				setUV0(10, maxU, minV);
				setUV0(11, minU, minV);
			}
			else if(side == 3)
			{
				setUV0(12, maxU, maxV);
				setUV0(13, maxU, minV);
				setUV0(14, minU, minV);
				setUV0(15, minU, maxV);
			}
			else if(side == 4)
			{
				setUV0(16, minU, maxV);
				setUV0(17, maxU, maxV);
				setUV0(18, maxU, minV);
				setUV0(19, minU, minV);
			}
			else if(side == 5)
			{
				setUV0(20, maxU, maxV);
				setUV0(21, maxU, minV);
				setUV0(22, minU, minV);
				setUV0(23, minU, maxV);
			}
		}
	}
	
	public void setUVD(int side, double minU, double minV, double maxU, double maxV)
	{ setUV(side, (float)minU, (float)minV, (float)maxU, (float)maxV); }
	
	public void setUV(int s, IIcon icon)
	{ setUV(s, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV()); }
	
	public void setUV(Block b, int m)
	{ for(int i = 0; i < 6; i++) setUV(i, b.getIcon(i, m)); }
	
	protected void vertex(int i)
	{ GL11.glTexCoord2f(uMap[currentSide * 4 + i], vMap[currentSide * 4 + i]); super.vertex(i); }
}