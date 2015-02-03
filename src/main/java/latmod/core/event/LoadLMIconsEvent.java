package latmod.core.event;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LoadLMIconsEvent extends EventLM
{
	private TextureMap register;
	
	public LoadLMIconsEvent(TextureMap r)
	{ register = r; }
	
	public IIcon load(String s)
	{ return register.registerIcon("lmicons:" + s); }
}