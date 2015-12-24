package latmod.ftbu.world;

import com.mojang.authlib.GameProfile;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

public class LMFakeServerPlayer extends LMPlayerServer
{
	public static final GameProfile fakeGameProfile = new GameProfile(UUID.nameUUIDFromBytes("FTBU:FakePlayer".getBytes()), "[FakePlayer]");
	
	public LMFakeServerPlayer(LMWorldServer w)
	{
		super(w, Integer.MAX_VALUE, fakeGameProfile);
		setPlayer(new FakePlayer(w.worldObj, fakeGameProfile));
	}
}