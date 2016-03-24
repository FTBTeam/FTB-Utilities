package ftb.utils.mod.client.gui.friends;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.*;
import ftb.utils.api.guide.GuidePage;
import ftb.utils.api.guide.lines.GuideTextLine;
import ftb.utils.mod.client.gui.guide.*;
import ftb.utils.world.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import org.lwjgl.input.Mouse;

/**
 * Created by LatvianModder on 23.03.2016.
 */
@SideOnly(Side.CLIENT)
public class GuidePlayerViewLine extends GuideTextLine
{
	public final LMPlayerClient playerLM;
	
	public GuidePlayerViewLine(GuidePage c, LMPlayerClient p)
	{
		super(c, null);
		playerLM = p;
	}
	
	@SideOnly(Side.CLIENT)
	public ButtonGuideTextLine createWidget(GuiGuide gui)
	{ return new ButtonGuidePlayerView(gui, this); }
	
	public class ButtonGuidePlayerView extends ButtonGuideTextLine
	{
		private Player player;
		
		public ButtonGuidePlayerView(GuiGuide g, GuidePlayerViewLine w)
		{
			super(g, null);
			height = 1;
		}
		
		public void renderWidget()
		{
			int ay = getAY();
			if(ay < -height || ay > guiGuide.mainPanel.height) return;
			int ax = getAX();
			
			if(player == null) player = new Player(LMWorldClient.inst.clientPlayer);
			
			if(mouseOver() && Mouse.isButtonDown(1))
			{
				for(int i = 0; i < player.inventory.armorInventory.length; i++)
				{
					player.inventory.armorInventory[i] = null;
				}
			}
			else
			{
				EntityPlayer ep1 = playerLM.getPlayer();
				
				if(ep1 != null)
				{
					player.inventory.mainInventory[0] = ep1.inventory.mainInventory[ep1.inventory.currentItem];
					System.arraycopy(ep1.inventory.armorInventory, 0, player.inventory.armorInventory, 0, 4);
					player.inventory.currentItem = 0;
				}
				else
				{
					System.arraycopy(playerLM.lastArmor, 0, player.inventory.armorInventory, 0, 4);
					player.inventory.mainInventory[0] = playerLM.lastArmor[4];
					player.inventory.currentItem = 0;
				}
			}
			
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			
			int pheight = 120;
			int pwidth = (int) (pheight / 1.625F);
			
			int playerX = guiGuide.mainPanel.width - pwidth / 2 - 30;
			int playerY = ay + pheight + 10;
			
			pheight = pheight / 2;
			
			FTBLibClient.setTexture(player.getLocationSkin());
			GlStateManager.translate(0F, 0F, 100F);
			GuiInventory.func_147046_a(playerX, playerY, pheight, playerX - gui.mouse().x, playerY - (pheight + (pheight / 1.625F)) - gui.mouse().y, player);
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}
		
		public class Player extends AbstractClientPlayer
		{
			public Player(LMPlayerClient p)
			{
				super(Minecraft.getMinecraft().theWorld, p.getProfile());
			}
			
			public boolean equals(Object o)
			{ return playerLM.equals(o); }
			
			public void addChatMessage(IChatComponent i) { }
			
			public boolean canCommandSenderUseCommand(int i, String s)
			{ return false; }
			
			public ChunkCoordinates getPlayerCoordinates()
			{ return new ChunkCoordinates(0, 0, 0); }
			
			public boolean isInvisibleToPlayer(EntityPlayer ep)
			{ return true; }
			
			public ResourceLocation getLocationSkin()
			{ return playerLM.getSkin(); }
			
			public boolean func_152122_n()
			{ return false; }
			
			public ResourceLocation getLocationCape()
			{ return null; }
		}
	}
}
