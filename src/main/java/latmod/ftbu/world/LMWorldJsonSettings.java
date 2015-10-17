package latmod.ftbu.world;

public class LMWorldJsonSettings
{
	public String gamemode;
	
	public void loadDefaults()
	{
		if(gamemode == null) gamemode = "default";
	}
}