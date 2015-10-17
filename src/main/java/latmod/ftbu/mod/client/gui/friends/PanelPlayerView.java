package latmod.ftbu.mod.client.gui.friends;

import org.lwjgl.opengl.GL11;

import latmod.ftbu.mod.client.FTBUClient;
import latmod.ftbu.world.LMWorldClient;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

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
		int playerX = getAX() + width / 2;
		
		int size = 130;
		int playerY = height / 2 + size;
		gui.setTexture(selectedPlayer.getLocationSkin());
		GL11.glTranslatef(0F, 0F, 100F);
		GuiInventory.func_147046_a(playerX, playerY, size, playerX - gui.mouseX, playerY - (size + (size / 1.625F)) - gui.mouseY, selectedPlayer);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}