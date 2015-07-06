package latmod.ftbu.core.client.badges;

import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.mod.FTBU;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class BadgeURL extends Badge
{
	public static final ResourceLocation defTex = FTBU.mod.getLocation("textures/badges/failed_url.png");
	
	private ResourceLocation textureURL = null;
	
	public BadgeURL(String id)
	{
		super(id);
	}
	
	public ResourceLocation getTexture()
	{
		if(textureURL == null)
		{
			textureURL = FTBU.mod.getLocation("textures/badges/url/" + ID);
			LatCoreMCClient.getDownloadImage(textureURL, ID, defTex, null);
		}
		
		return textureURL;
	}
}