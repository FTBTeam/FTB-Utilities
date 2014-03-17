package mods.lm_core.mod;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

public class LCCommon // LCClient
{
	public void preInit() { }
	public void init() { }
	public void postInit() { }
	
	public void printChat(String s) {}
	
	public int getKeyID(String s) { return 0; }
	public boolean isKeyDown(int id) { return false; }
	
	public boolean isKeyDown(String id)
	{ return isKeyDown(getKeyID(id)); }
	
	public MovingObjectPosition rayTrace(EntityPlayer ep, double d)
	{
		Vec3 pos = ep.worldObj.getWorldVec3Pool().getVecFromPool(ep.posX, ep.posY, ep.posZ);
		Vec3 look = ep.getLook(1F);
		Vec3 vec = pos.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
        return ep.worldObj.clip(pos, vec);
	}
}