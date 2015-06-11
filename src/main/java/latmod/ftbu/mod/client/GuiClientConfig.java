package latmod.ftbu.mod.client;

import latmod.ftbu.core.client.ClientConfig;
import net.minecraft.client.gui.*;
import scala.actors.threadpool.Arrays;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class GuiClientConfig extends GuiScreen
{
	@SuppressWarnings("unchecked")
	public void initGui()
	{
		String[] keys = ClientConfig.Registry.map.keys.toArray(new String[0]);
		Arrays.sort(keys);
		
		for(int i = 0; i < keys.length; i++)
			buttonList.add(new GuiButton(i, width / 2 - 100, i * 28 + 32, 200, 20, keys[i]));
		
		buttonList.add(new GuiButton(255, width / 2 - 100, height - 40, 200, 20, "Back"));
	}
	
	public void actionPerformed(GuiButton b)
	{
		if(b.id == 255) mc.displayGuiScreen(null);
		else
		{
			ClientConfig c = ClientConfig.Registry.map.get(b.displayString);
			if(c != null) mc.displayGuiScreen(new GuiClientConfigTab(c));
		}
	}
	
	public void onGuiClosed()
	{
	}
	
	public void drawScreen(int mx, int my, float pt)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, "Client Config", width / 2, 15, 16777215);
		super.drawScreen(mx, my, pt);
	}
	
	public static class GuiClientConfigTab extends GuiScreen
	{
		public final ClientConfig clientConfig;
		
		public GuiClientConfigTab(ClientConfig c)
		{
			clientConfig = c;
		}
		
		@SuppressWarnings("unchecked")
		public void initGui()
		{
			ClientConfig.Property[] props = clientConfig.map.values.toArray(new ClientConfig.Property[0]);
			Arrays.sort(props);
			
			for(int i = 0; i < props.length; i++)
				buttonList.add(new PropButton(i, props[i]));
			
			buttonList.add(new GuiButton(255, width / 2 - 100, height - 40, 200, 20, "Back"));
		}
		
		public void actionPerformed(GuiButton b)
		{
			if(b.id == 255) mc.displayGuiScreen(new GuiClientConfig());
			else if(b instanceof PropButton)
			{
				ClientConfig.Property p = ((PropButton)b).property;
				p.incValue(true);
				b.displayString = p.toString();
			}
		}
		
		public void drawScreen(int mx, int my, float pt)
		{
			drawDefaultBackground();
			drawCenteredString(fontRendererObj, clientConfig.id, width / 2, 15, 16777215);
			super.drawScreen(mx, my, pt);
		}
		
		public void onGuiClosed()
		{
			ClientConfig.Registry.save();
		}
		
		private class PropButton extends GuiButton
		{
			public final ClientConfig.Property property;
			
			public PropButton(int id, ClientConfig.Property p)
			{
				super(id, GuiClientConfigTab.this.width / 2 - 100, id * 28 + 32, 200, 20, p.toString());
				property = p;
			}
		}
	}
}