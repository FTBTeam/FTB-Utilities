package latmod.core.mod.gui;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class PlayerHead extends ButtonLM
{
	public String playerName = null;
	public ResourceLocation texture;
	public ThreadDownloadImageData thread;
	public final int ID;
	
	public PlayerHead(GuiLM g, int id, int x, int y, int w, int h)
	{
		super(g, x, y, w, h);
		ID = id;
		setUsername(null);
	}
	
	public void setUsername(String s)
	{
		playerName = s;
		
		texture = AbstractClientPlayer.locationStevePng;
		
		if(s != null)
		{
			texture = AbstractClientPlayer.locationStevePng;
			texture = AbstractClientPlayer.getLocationSkin(s);
			AbstractClientPlayer.getDownloadImageSkin(texture, s);
		}
	}
	
	public void render(int ox, int oy, double w, double h)
	{
		ox += gui.getPosX();
		oy += gui.getPosY();
		
		if(playerName == null) return;
		
		gui.drawPlayerHead(playerName, ox, oy, width, height);
	}
}