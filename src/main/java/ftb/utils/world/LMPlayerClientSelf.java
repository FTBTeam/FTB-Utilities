package ftb.utils.world;

import com.mojang.authlib.GameProfile;
import ftb.lib.api.friends.PersonalSettings;

public class LMPlayerClientSelf extends LMPlayerClient
{
	public LMPlayerClientSelf(LMWorldClient w, int i, GameProfile gp)
	{
		super(w, i, gp);
		
		settings = new PersonalSettings(this);
		rank = null;
	}
	
	public LMPlayerClientSelf toPlayerSPSelf()
	{ return this; }
	
	
}