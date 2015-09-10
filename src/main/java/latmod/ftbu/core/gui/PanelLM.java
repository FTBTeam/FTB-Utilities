package latmod.ftbu.core.gui;

import latmod.ftbu.core.util.FastList;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public abstract class PanelLM extends WidgetLM // GuiLM
{
	protected final FastList<WidgetLM> widgets;
	protected final FastList<PanelLM> childPanels;
	
	public PanelLM(GuiLM g, int x, int y, int w, int h)
	{
		super(g, x, y, w, h);
		widgets = new FastList<WidgetLM>();
		childPanels = new FastList<PanelLM>();
	}
	
	public FastList<WidgetLM> getWidgets()
	{ return widgets.clone(); }
	
	public abstract void addWidgets();
	
	public void add(WidgetLM w)
	{
		if(w == null) return;
		w.parentPanel = this;
		widgets.add(w);
		
		if(w instanceof PanelLM)
		{
			childPanels.add((PanelLM)w);
			((PanelLM)w).refreshWidgets();
		}
	}
	
	public void addAll(WidgetLM[] l)
	{
		if(l == null || l.length == 0) return;
		for(int i = 0; i < l.length; i++) add(l[i]);
	}
	
	public void addAll(FastList<? extends WidgetLM> l)
	{
		if(l == null || l.isEmpty()) return;
		for(int i = 0; i < l.size(); i++) add(l.get(i));
	}
	
	public void refreshWidgets()
	{
		widgets.clear();
		addWidgets();
	}
	
	public void addMouseOverText(FastList<String> l)
	{
		if(title != null) l.add(title); 
		
		for(int i = 0; i < widgets.size(); i++)
		{
			WidgetLM w = widgets.get(i);
			if(w.isEnabled() && w.mouseOver())
				w.addMouseOverText(l);
		}
	}
	
	public void mousePressed(int b)
	{
		for(int i = 0; i < widgets.size(); i++)
		{
			WidgetLM w = widgets.get(i);
			if(w.isEnabled()) w.mousePressed(b);
		}
	}
	
	public boolean keyPressed(int key, char keyChar)
	{
		for(int i = 0; i < widgets.size(); i++)
		{
			WidgetLM w = widgets.get(i);
			if(w.isEnabled() && w.keyPressed(key, keyChar))
				return true;
		}
		
		return false;
	}
}