package latmod.ftbu.mod.client.gui.friends;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.client.ClientNotifications;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.LMWorldClient;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class ButtonNotification extends ButtonLM
{
	public final ClientNotifications.Perm notification;
	public final int index;
	
	public ButtonNotification(PanelNotifications p, ClientNotifications.Perm n)
	{
		super(p.gui, 0, 0, 0, 24);
		notification = n;
		index = p.notificationButtons.size();
		posY += index * 26;
		title = n.notification.title.getFormattedText();
		width = gui.getFontRenderer().getStringWidth(n.notification.title.getFormattedText());
		if(n.notification.desc != null) width = Math.max(width, gui.getFontRenderer().getStringWidth(n.notification.desc.getFormattedText()));
		if(n.notification.item != null) width += 20;
		width += 8;
	}
	
	public void renderWidget()
	{
		int ax = getAX();
		int ay = getAY();
		
		int tx = 4;
		ItemStack is = notification.notification.item;
		if(is != null)
		{
			tx += 20;
			gui.drawItem(is, ax + 4, ay + 4);
		}
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GuiLM.drawBlankRect(ax, ay, gui.getZLevel(), parentPanel.width, height, mouseOver() ? 0xFF999999 : 0xFF666666);
		gui.getFontRenderer().drawString(title, ax + tx, ay + 4, 0xFFFFFFFF);
		if(notification.notification.desc != null) gui.getFontRenderer().drawString(notification.notification.desc.getFormattedText(), ax + tx, ay + 14, 0xFFFFFFFF);
	}
	
	public void onButtonPressed(int b)
	{
		gui.playClickSound();
		
		if(b == 0) notification.onClicked(LMWorldClient.inst.clientPlayer);
		ClientNotifications.Perm.list.remove(notification);
		
		gui.refreshWidgets();
	}
	
	public void addMouseOverText(FastList<String> l)
	{
		//if(notification.notification.clickEvent != null)
		//	l.add(new String(notification.notification.clickEvent.val));
	}
}