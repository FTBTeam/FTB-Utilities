package latmod.ftbu.mod.client.gui.friends;

import latmod.ftbu.core.gui.ButtonLM;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.mod.player.ClientNotifications;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class ButtonNotification extends ButtonLM
{
	public final ClientNotifications.Perm notification;
	public final int index;
	
	public ButtonNotification(PanelNotifications p, ClientNotifications.Perm n)
	{
		super(p.gui, 0, 0, p.width, 24);
		notification = n;
		index = p.notificationButtons.size();
		posY += index * 26 + 18;
		title = notification.notification.title.getFormattedText();
	}
	
	public void renderWidget()
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
		
		gui.drawBlankRect(x, y, width, height, mouseOver() ? 0xFF999999 : 0xFF666666);
		gui.getFontRenderer().drawString(title, x + tx, y + 4, 0xFFFFFFFF);
		if(notification.notification.getDesc() != null) gui.getFontRenderer().drawString(notification.notification.getDesc().getFormattedText(), x + tx, y + 14, 0xFFFFFFFF);
	}
	
	public void onButtonPressed(int b)
	{
		gui.playClickSound();
		
		if(b == 0) notification.onClicked(gui.mc);
		ClientNotifications.Perm.list.remove(notification);
		
		gui.refreshWidgets();
	}
	
	public void addMouseOverText(FastList<String> l)
	{
	}
}