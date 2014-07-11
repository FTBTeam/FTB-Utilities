package latmod.core.mod;
import org.lwjgl.input.*;
import latmod.core.client.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public void preInit()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void init() { }
	public void postInit() { }
	
	@ForgeSubscribe
	public void preTexturesLoaded(TextureStitchEvent.Pre e)
	{
		if(e.map.getTextureType() == 0)
			LatCoreClient.blockNullIcon = e.map.registerIcon(LC.mod.assets + "nullIcon");
	}
	
	public int getKeyID(String s) { return Keyboard.getKeyIndex(s); }
	public boolean isKeyDown(int id) { return Keyboard.isKeyDown(id); }
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
}