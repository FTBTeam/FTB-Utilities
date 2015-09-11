package latmod.ftbu.mod.client.gui.friends;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.LatCoreMCClient;
import latmod.ftbu.core.world.LMWorldClient;
import latmod.ftbu.mod.client.FTBUClient;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public class PanelPlayerView extends PanelFriendsGui
{
	public Player selectedPlayer;
	
	public PanelPlayerView(GuiFriends g)
	{
		super(g);
		selectedPlayer = new Player(LMWorldClient.inst.clientPlayer);
	}
	
	public void addWidgets()
	{
	}
	
	public void renderWidget()
	{
		if(FTBUClient.hideArmorFG.getB())
		{
			for(int i = 0; i < 4; i++)
				selectedPlayer.inventory.armorInventory[i] = null;
		}
		else
		{
			EntityPlayer ep1 = selectedPlayer.playerLM.getPlayer();
			
			if(ep1 != null)
			{
				selectedPlayer.inventory.mainInventory = ep1.inventory.mainInventory.clone();
				selectedPlayer.inventory.armorInventory = ep1.inventory.armorInventory.clone();
				selectedPlayer.inventory.currentItem = ep1.inventory.currentItem;
			}
			else
			{
				for(int i = 0; i < 4; i++)
					selectedPlayer.inventory.armorInventory[i] = selectedPlayer.playerLM.lastArmor[i];
				selectedPlayer.inventory.mainInventory[0] = selectedPlayer.playerLM.lastArmor[4];
				selectedPlayer.inventory.currentItem = 0;
			}
		}
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		//int playerX = guiLeft + 44;
		//int playerY = guiTop + 127;
		int playerX = getAX() + width / 2;
		int playerY = height - 54;
		gui.setTexture(LatCoreMCClient.getSkinTexture(selectedPlayer.getCommandSenderName()));
		GL11.glTranslatef(0F, 0F, 100F);
		GuiInventory.func_147046_a(playerX, playerY, 130, playerX - gui.mouseX, playerY - 210 - gui.mouseY, selectedPlayer);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}