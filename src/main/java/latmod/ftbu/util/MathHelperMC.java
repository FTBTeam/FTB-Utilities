package latmod.ftbu.util;

import java.util.Random;

import latmod.core.util.*;
import latmod.ftbu.mod.FTBU;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class MathHelperMC
{
	public static int get2DRotation(EntityLivingBase el)
	{
		//int i = floor(el.rotationYaw * 4D / 360D + 0.5D) & 3;
		int i = MathHelperLM.getRotations(el.rotationYaw, 4);
		if(i == 0) return 2;
		else if(i == 1) return 5;
		else if(i == 2) return 3;
		else if(i == 3) return 4;
		return 6;
	}
	
	public static int get3DRotation(World w, int x, int y, int z, EntityLivingBase el)
	{ return BlockPistonBase.determineOrientation(w, x, y, z, el); }
	
	public static VecLM randomAABB(Random r, AxisAlignedBB bb)
	{
		double x = MathHelperLM.randomDouble(r, bb.minX, bb.maxX);
		double y = MathHelperLM.randomDouble(r, bb.minY, bb.maxY);
		double z = MathHelperLM.randomDouble(r, bb.minZ, bb.maxZ);
		return new VecLM(x, y, z);
	}
	
	public static Vec3 getEyePosition(EntityPlayer ep)
	{
		double y = 0D;
		if(!ep.worldObj.isRemote) y = ep.getEyeHeight();
		return Vec3.createVectorHelper(ep.posX, ep.posY + y, ep.posZ);
	}
	
	public static MovingObjectPosition rayTrace(EntityPlayer ep, double d)
	{
		if(ep == null) return null;
		Vec3 pos = getEyePosition(ep);
		Vec3 look = ep.getLookVec();
		Vec3 vec = pos.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
		MovingObjectPosition mop = ep.worldObj.func_147447_a(pos, vec, false, true, false);
		if(mop != null && mop.hitVec == null) mop.hitVec = Vec3.createVectorHelper(0D, 0D, 0D);
		return mop;
	}
	
	public static MovingObjectPosition rayTrace(EntityPlayer ep)
	{ return rayTrace(ep, FTBU.proxy.getReachDist(ep)); }
	
	public static MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 start, Vec3 end, AxisAlignedBB[] boxes)
	{
		if(boxes == null || boxes.length <= 0) return null;
		
		MovingObjectPosition current = null;
		double dist = Double.POSITIVE_INFINITY;
		
		for(int i = 0; i < boxes.length; i++)
		{
			if(boxes[i] != null)
			{
				MovingObjectPosition mop = collisionRayTrace(w, x, y, z, start, end, boxes[i]);
				
				if(mop != null)
				{
					double d1 = mop.hitVec.squareDistanceTo(start);
					if(current == null || d1 < dist)
					{
						current = mop;
						current.subHit = i;
						dist = d1;
					}
				}
			}
		}
		
		return current;
	}
	
	public static MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 start, Vec3 end, FastList<AxisAlignedBB> boxes)
	{
		AxisAlignedBB[] boxesa = new AxisAlignedBB[boxes.size()];
		for(int i = 0; i < boxesa.length; i++) boxesa[i] = boxes.get(i).copy();
		return collisionRayTrace(w, x, y, z, start, end, boxesa);
	}
	
	public static MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 start, Vec3 end, AxisAlignedBB aabb)
	{
		Vec3 pos = start.addVector(-x, -y, -z);
		Vec3 rot = end.addVector(-x, -y, -z);
		
		Vec3 xmin = pos.getIntermediateWithXValue(rot, aabb.minX);
		Vec3 xmax = pos.getIntermediateWithXValue(rot, aabb.maxX);
		Vec3 ymin = pos.getIntermediateWithYValue(rot, aabb.minY);
		Vec3 ymax = pos.getIntermediateWithYValue(rot, aabb.maxY);
		Vec3 zmin = pos.getIntermediateWithZValue(rot, aabb.minZ);
		Vec3 zmax = pos.getIntermediateWithZValue(rot, aabb.maxZ);
		
		if (!isVecInsideYZBounds(xmin, aabb)) xmin = null;
		if (!isVecInsideYZBounds(xmax, aabb)) xmax = null;
		if (!isVecInsideXZBounds(ymin, aabb)) ymin = null;
		if (!isVecInsideXZBounds(ymax, aabb)) ymax = null;
		if (!isVecInsideXYBounds(zmin, aabb)) zmin = null;
		if (!isVecInsideXYBounds(zmax, aabb)) zmax = null;
		Vec3 v = null;
		
		if (xmin != null && (v == null || pos.squareDistanceTo(xmin) < pos.squareDistanceTo(v))) v = xmin;
		if (xmax != null && (v == null || pos.squareDistanceTo(xmax) < pos.squareDistanceTo(v))) v = xmax;
		if (ymin != null && (v == null || pos.squareDistanceTo(ymin) < pos.squareDistanceTo(v))) v = ymin;
		if (ymax != null && (v == null || pos.squareDistanceTo(ymax) < pos.squareDistanceTo(v))) v = ymax;
		if (zmin != null && (v == null || pos.squareDistanceTo(zmin) < pos.squareDistanceTo(v))) v = zmin;
		if (zmax != null && (v == null || pos.squareDistanceTo(zmax) < pos.squareDistanceTo(v))) v = zmax;
		if (v == null) return null; else
		{
			int side = -1;

			if (v == xmin) side = 4;
			if (v == xmax) side = 5;
			if (v == ymin) side = 0;
			if (v == ymax) side = 1;
			if (v == zmin) side = 2;
			if (v == zmax) side = 3;
			
			return new MovingObjectPosition(x, y, z, side, v.addVector(x, y, z));
		}
	}
	
	private static boolean isVecInsideYZBounds(Vec3 v, AxisAlignedBB aabb)
	{ return v == null ? false : v.yCoord >= aabb.minY && v.yCoord <= aabb.maxY && v.zCoord >= aabb.minZ && v.zCoord <= aabb.maxZ; }
	
	private static boolean isVecInsideXZBounds(Vec3 v, AxisAlignedBB aabb)
	{ return v == null ? false : v.xCoord >= aabb.minX && v.xCoord <= aabb.maxX && v.zCoord >= aabb.minZ && v.zCoord <= aabb.maxZ; }
	
	private static boolean isVecInsideXYBounds(Vec3 v, AxisAlignedBB aabb)
	{ return v == null ? false : v.xCoord >= aabb.minX && v.xCoord <= aabb.maxX && v.yCoord >= aabb.minY && v.yCoord <= aabb.maxY; }
	
	public static MovingObjectPosition getMOPFrom(int x, int y, int z, int s, float hitX, float hitY, float hitZ)
	{ return new MovingObjectPosition(x, y, z, s, Vec3.createVectorHelper(x + hitX, y + hitY, z + hitZ)); }
	
	public static AxisAlignedBB getBox(double cx, double y0, double cz, double w, double y1, double d)
	{ return AxisAlignedBB.getBoundingBox(cx - w / 2D, y0, cz - d / 2D, cx + w / 2D, y1, cz + d / 2D); }
	
	public static AxisAlignedBB centerBox(double x, double y, double z, double w, double h, double d)
	{ return getBox(x, y - h / 2D, z, w, y + h / 2D, d); }
	
	public static AxisAlignedBB rotate90BoxV(AxisAlignedBB bb, int dir)
	{
		double x1 = bb.minX;
		double y1 = bb.minY;
		double z1 = bb.minZ;
		
		double x2 = bb.maxX;
		double y2 = bb.maxY;
		double z2 = bb.maxZ;
		
		if(dir < 0 || dir >= 6 || dir == 2 || dir == 3)
			return AxisAlignedBB.getBoundingBox(x1, y1, z1, x2, y2, z2);
		return AxisAlignedBB.getBoundingBox(z1, y1, x1, z2, y2, x2);
	}
}