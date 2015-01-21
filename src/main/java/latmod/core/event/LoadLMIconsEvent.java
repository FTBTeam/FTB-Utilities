package latmod.core.event;

import latmod.core.LMMod;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LoadLMIconsEvent extends EventLM
{
	private TextureMap register;
	private int texturesLoaded = 0;
	
	public LoadLMIconsEvent(TextureMap r)
	{ register = r; }
	
	public IIcon load(String s)
	{
		if(register.getTextureExtry(s) == null)
			texturesLoaded++;
		return register.registerIcon(s);
	}
	
	public IIcon load(LMMod m, String s)
	{ return load(m.assets + "textures/" + s); }
	
	public int texturesLoaded()
	{ return texturesLoaded; }
}