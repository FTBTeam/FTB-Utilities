package latmod.ftbu.world;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.FakePlayer;

public class LMFakeServerPlayer extends LMPlayerServer
{
	public static final GameProfile fakeGameProfile = new GameProfile(UUID.nameUUIDFromBytes("FTBU:FakePlayer".getBytes()), "[FakePlayer]");
	
	public LMFakeServerPlayer(LMWorldServer w)
	{
		super(w, Integer.MAX_VALUE, fakeGameProfile);
		setPlayer(new FakePlayer(w.worldObj, fakeGameProfile));
	}
}