package ftb.utils.world;

import ftb.utils.world.ranks.*;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

public class LMFakeServerPlayer extends LMPlayerServer
{
	public LMFakeServerPlayer(LMWorldServer w, FakePlayer fp)
	{
		super(w, Integer.MAX_VALUE, fp.getGameProfile());
		setPlayer(fp);
	}
	
	public boolean isOnline()
	{ return false; }
	
	public boolean isFake()
	{ return true; }
	
	public void sendUpdate() { }
	
	public boolean isOP()
	{ return false; }
	
	public void getInfo(LMPlayerServer owner, List<IChatComponent> info) { }
	
	public void refreshStats() { }
	
	public void onPostLoaded() { }
	
	public void checkNewFriends() { }
	
	public Rank getRank()
	{ return Ranks.PLAYER; }
	
	public void claimChunk(int dim, int cx, int cz) { }
	
	public void unclaimChunk(int dim, int cx, int cz) { }
	
	public void unclaimAllChunks(Integer dim) { }
	
	public int getClaimedChunks()
	{ return 0; }
	
	public int getLoadedChunks(boolean forced)
	{ return 0; }
	
	public void setLoaded(int dim, int cx, int cz, boolean flag) { }
}