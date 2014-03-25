package mods.lm.core;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.Property;

public class LMUtils
{
	public static boolean anyEquals(Object o, Object... o1)
	{
		for(int i = 0; i < o1.length; i++)
		{
			if(o == null && o1[i] == null) return true;
			if(o != null && o1[i] != null)
			{ if(o == o1[i] || o.equals(o1[i])) return true; }
		}
		
		return false;
	}
	
	public static boolean allEquals(Object o, Object... o1)
	{
		for(int i = 0; i < o1.length; i++)
		{
			if((o == null && o1[i] != null) || (o != null && o1[i] == null)) return false;
			if(o != o1[i]) { if(!o.equals(o1[i])) return false; }
		}
		
		return true;
	}
	
	public static ForgeDirection get2DRotation(EntityLivingBase el)
	{
		int i = MathHelper.floor_float(el.rotationYaw * 4F / 360F + 0.5F) & 3;
		if(i == 0) return ForgeDirection.NORTH;
		else if(i == 1) return ForgeDirection.EAST;
		else if(i == 2) return ForgeDirection.SOUTH;
		else if(i == 3) return ForgeDirection.WEST;
		return ForgeDirection.UNKNOWN;
	}
	
	public static ForgeDirection get3DRotation(World w, int x, int y, int z, EntityLivingBase el)
	{ return ForgeDirection.values()[BlockPistonBase.determineOrientation(w, x, y, z, el)]; }
	
	public static String getPath(ResourceLocation res)
	{ return "/assets/" + res.getResourceDomain() + "/" + res.getResourcePath(); }
	
	public static String stripe(Object... i)
	{
		StringBuilder s = new StringBuilder();
		for(int j = 0; j < i.length; j++)
		{ s.append(i[j]);
		if(j != i.length - 1) s.append(", "); }
		return s.toString();
	}

	public static void setPropertyComment(Configuration config, String category, String property, String... comment)
	{
		ConfigCategory cat = config.getCategory(category);
		Property prop = cat.get(property);
		if(prop != null)
		{
			String s = "";
			for(int i = 0; i < comment.length; i++)
			{ s += comment[i]; if(i < comment.length - 1) s += '\n'; }
			prop.comment = s;
		}
	}
}