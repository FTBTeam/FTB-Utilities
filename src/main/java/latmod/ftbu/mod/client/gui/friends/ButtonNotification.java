package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.ButtonLM;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.player.ClientNotifications;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

public class ButtonNotification extends ButtonLM
{
	public final ClientNotifications.PermNotification notification;
	public final int index;
	
	public ButtonNotification(GuiFriends g, ClientNotifications.PermNotification n)
	{
		super(g, -g.getPosX(0), -g.getPosY(0), g.notificationsWidth, 24);
		notification = n;
		index = g.notificationButtons.size();
		posY += index * 26 + 18;
		
		title = notification.notification.title.getFormattedText();
	}
	
	public void render()
	{
		int x = 2;
		int y = index * 26 + 18;
		
		int tx = 4;
		ItemStack is = notification.notification.getItem();
		if(is != null)
		{
			tx += 20;
			gui.drawItem(is, x + 4, y + 4);
		}
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GuiFriends.drawRect(x, y, x + width, y + height, mouseOver() ? 0xFF999999 : 0xFF666666);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		gui.getFontRenderer().drawString(title, x + tx, y + 4, 0xFFFFFFFF);
		if(notification.notification.getDesc() != null) gui.getFontRenderer().drawString(notification.notification.getDesc().getFormattedText(), x + tx, y + 14, 0xFFFFFFFF);
	}
	
	public void onButtonPressed(int b)
	{
		gui.playClickSound();
		
		if(b == 0) notification.onClicked(gui.mc);
		ClientNotifications.perm.remove(notification);
		
		gui.refreshWidgets();
	}
	
	public void addMouseOverText(FastList<String> l)
	{
	}
}