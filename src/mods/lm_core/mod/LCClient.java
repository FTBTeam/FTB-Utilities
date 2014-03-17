package mods.lm_core.mod;
import org.lwjgl.input.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public void preInit() { }
	public void init() { }
	
	public void postInit()
	{
	}
	
	public void printChat(String s)
	{
		try { Minecraft.getMinecraft().thePlayer.addChatMessage(s); }
		catch(Exception e) { System.out.println(s); }
	}
	
	public int getKeyID(String s) { return Keyboard.getKeyIndex(s); }
	public boolean isKeyDown(int id) { return Keyboard.isKeyDown(id); }
	
	public MovingObjectPosition rayTrace(EntityPlayer ep, double d)
	{ return ep.rayTrace(d, 1F); }
}