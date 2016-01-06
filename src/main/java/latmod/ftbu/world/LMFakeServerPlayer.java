package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;
import ftb.lib.FTBLib;
import latmod.ftbu.world.ranks.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;

import java.util.*;

public class LMFakeServerPlayer extends LMPlayerServer
{
	public static final GameProfile fakeGameProfile = new GameProfile(UUID.nameUUIDFromBytes("FTBU:FakePlayer".getBytes()), "[FakePlayer]");
	private FakePlayer fakePlayer;

	public LMFakeServerPlayer(LMWorldServer w)
	{ super(w, Integer.MAX_VALUE, fakeGameProfile); }

	public boolean isOnline()
	{ return false; }

	public EntityPlayerMP getPlayer()
	{
		if(fakePlayer == null && FTBLib.getServerWorld() != null)
			fakePlayer = new FakePlayer(FTBLib.getServerWorld(), fakeGameProfile);
		return fakePlayer;
	}

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