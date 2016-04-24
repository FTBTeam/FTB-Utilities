package ftb.utils.world;

import ftb.utils.world.ranks.Rank;
import ftb.utils.world.ranks.Ranks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

public class LMFakeServerPlayer extends LMPlayerServer
{
	public final FakePlayer fakePlayer;
	
	public LMFakeServerPlayer(FakePlayer fp)
	{
		super(fp.getGameProfile());
		fakePlayer = fp;
	}
	
	@Override
	public boolean isOnline()
	{ return false; }
	
	@Override
	public EntityPlayerMP getPlayer()
	{ return fakePlayer; }
	
	@Override
	public boolean isFake()
	{ return true; }
	
	@Override
	public void sendUpdate() { }
	
	@Override
	public boolean isOP()
	{ return false; }
	
	@Override
	public void getInfo(LMPlayerServer owner, List<IChatComponent> info) { }
	
	@Override
	public void refreshStats() { }
	
	@Override
	public void onPostLoaded() { }
	
	@Override
	public void checkNewFriends() { }
	
	@Override
	public Rank getRank()
	{ return Ranks.PLAYER; }
	
	@Override
	public void claimChunk(int dim, int cx, int cz) { }
	
	@Override
	public void unclaimChunk(int dim, int cx, int cz) { }
	
	@Override
	public void unclaimAllChunks(Integer dim) { }
	
	@Override
	public int getClaimedChunks()
	{ return 0; }
	
	@Override
	public int getLoadedChunks(boolean forced)
	{ return 0; }
	
	@Override
	public void setLoaded(int dim, int cx, int cz, boolean flag) { }
}