package latmod.ftbu.world;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import latmod.lib.FastList;
import net.minecraft.stats.StatBase;
import net.minecraft.util.IChatComponent;

public class LMPlayerServerFake extends LMPlayerServer
{
	public static final LMPlayerServerFake inst = new LMPlayerServerFake();
	
	private LMPlayerServerFake()
	{
		super(LMWorldServer.inst, Integer.MAX_VALUE, new GameProfile(new UUID(0L, 0L), "[Server]"));
	}
	
	public void sendUpdate() { }
	
	public boolean isOP() { return true; }
	
	public void getInfo(FastList<IChatComponent> info)
	{
	}
	
	public int getStat(StatBase s)
	{ return 0; }
	
	public void refreshStats()
	{
	}
	
	public void onPostLoaded() { }
	
	public int getMaxClaimPower()
	{ return -1; }
}