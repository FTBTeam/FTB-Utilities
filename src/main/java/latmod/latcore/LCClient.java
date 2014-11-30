package latmod.latcore;
import latmod.core.tile.IGuiTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class LCClient extends LCCommon
{
	public int getKeyID(String s) { return Keyboard.getKeyIndex(s); }
	public boolean isKeyDown(int id) { return Keyboard.isKeyDown(id); }
	public boolean isShiftDown() { return GuiScreen.isShiftKeyDown(); }
	public boolean isCtrlDown() { return GuiScreen.isCtrlKeyDown(); }
	
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IGuiTile)
			return ((IGuiTile)te).getGui(player, ID);
		return null;
	}
	
	public EntityPlayer getClientPlayer()
	{ return Minecraft.getMinecraft().thePlayer; }
	
	public World getClientWorld()
	{ return Minecraft.getMinecraft().theWorld; }
	
	public double getReachDist(EntityPlayer ep)
	{ return Minecraft.getMinecraft().playerController.getBlockReachDistance(); }
	
	public void onClientPlayerJoined(EntityPlayer ep)
	{
		/*
		LatCoreMC.printChat(ep, "Looking for custom skin...");
		
		if(ep instanceof AbstractClientPlayer)
		{
			AbstractClientPlayer p = (AbstractClientPlayer)ep;
			
			//if(p.getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID()))
			//	p = Minecraft.getMinecraft().thePlayer;
			
			ResourceLocation customSkinLocation = LC.mod.getLocation("custom/skin/" + p.getUniqueID() + ".png");
			
			TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
	        Object object = texturemanager.getTexture(customSkinLocation);

	        if (object == null)
	        {
	            object = new ThreadDownloadImageData((File)null, "http://i.imgur.com/yFSexm0.png", AbstractClientPlayer.locationStevePng, new ImageBufferDownload());
	            texturemanager.loadTexture(customSkinLocation, (ITextureObject)object);
	        }
	        
			//AbstractClientPlayer.getDownloadImageSkin(customSkinLocation, "");
			p.func_152121_a(MinecraftProfileTexture.Type.SKIN, customSkinLocation);
			LatCoreMC.printChat(ep, "Set skin to " + p.getLocationSkin());
		}
		
		*/
	}
	
	public void openClientGui(EntityPlayer ep, IGuiTile i, int ID)
	{ Minecraft.getMinecraft().displayGuiScreen(i.getGui(ep, ID)); }
}