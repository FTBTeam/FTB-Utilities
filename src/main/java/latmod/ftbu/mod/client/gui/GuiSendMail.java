package latmod.ftbu.mod.client.gui;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.net.MessageMailSend;
import latmod.ftbu.util.gui.*;
import latmod.ftbu.world.Mail;
import latmod.lib.FastList;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class GuiSendMail extends GuiLM
{
	public final Mail mail;
	
	public final WidgetLM playerHead;
	public final ButtonLM buttonItems, buttonSend;
	
	public GuiSendMail(Mail m)
	{
		super(null, FTBU.mod.getLocation("textures/gui/mail.png"));
		mail = m;
		xSize = 176;
		ySize = 137;
		
		playerHead = new WidgetLM(this, 8, 8, 16, 16);
		
		playerHead.title = FTBU.mod.translateClient("label.mail_to", mail.receiver.getName());
		
		buttonItems = new ItemButtonLM(this, 129, 8, 16, 16, new ItemStack(Blocks.chest))
		{
			public void onButtonPressed(int b)
			{
				//FIXME: Mail items
			}
		};
		
		buttonItems.title = FTBU.mod.translateClient("button.mail_items");
		
		buttonSend = new ButtonLM(this, 152, 8, 16, 16)
		{
			public void onButtonPressed(int b)
			{
				FastList<String> text = new FastList<String>();
				text.add("Hi!");
				new MessageMailSend(mail.mailID, text);
				mc.thePlayer.closeScreen();
			}
		};
		
		buttonSend.title = FTBU.mod.translateClient("button.mail_send");
	}
	
	public void addWidgets()
	{
		mainPanel.add(playerHead);
		mainPanel.add(buttonItems);
		mainPanel.add(buttonSend);
	}
	
	public void drawBackground()
	{
		super.drawBackground();
		
		drawPlayerHead(mail.receiver.getName(), playerHead.getAX(), playerHead.getAY(), 16, 16, zLevel);
		buttonItems.renderWidget();
		buttonSend.render(GuiIcons.right);
	}
}