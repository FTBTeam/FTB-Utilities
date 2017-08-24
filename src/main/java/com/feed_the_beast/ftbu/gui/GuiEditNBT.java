package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.gui.IDrawableObject;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.lib.Color4I;
import com.feed_the_beast.ftbl.lib.client.ImageProvider;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbl.lib.gui.Button;
import com.feed_the_beast.ftbl.lib.gui.GuiBase;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.Panel;
import com.feed_the_beast.ftbl.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.WidgetLayout;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiSelectors;
import com.feed_the_beast.ftbl.lib.gui.misc.IGuiFieldCallback;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.net.MessageEditNBTResponse;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

/**
 * @author LatvianModder
 */
public class GuiEditNBT extends GuiBase
{
	private static IDrawableObject getIcon(String name)
	{
		return ImageProvider.get(FTBUFinals.MOD_ID + ":textures/gui/nbt/" + name + ".png");
	}

	public static final IDrawableObject NBT_BACKGROUND = getIcon("background");
	public static final IDrawableObject NBT_BYTE = getIcon("byte");
	public static final IDrawableObject NBT_SHORT = getIcon("short");
	public static final IDrawableObject NBT_INT = getIcon("int");
	public static final IDrawableObject NBT_LONG = getIcon("long");
	public static final IDrawableObject NBT_FLOAT = getIcon("float");
	public static final IDrawableObject NBT_DOUBLE = getIcon("double");
	public static final IDrawableObject NBT_STRING = getIcon("string");
	public static final IDrawableObject NBT_LIST = getIcon("list");
	public static final IDrawableObject NBT_LIST_CLOSED = getIcon("list_closed");
	public static final IDrawableObject NBT_LIST_OPEN = getIcon("list_open");
	public static final IDrawableObject NBT_MAP = getIcon("map");
	public static final IDrawableObject NBT_MAP_CLOSED = getIcon("map_closed");
	public static final IDrawableObject NBT_MAP_OPEN = getIcon("map_open");
	public static final IDrawableObject NBT_BYTE_ARRAY = getIcon("byte_array");
	public static final IDrawableObject NBT_BYTE_ARRAY_CLOSED = getIcon("byte_array_closed");
	public static final IDrawableObject NBT_BYTE_ARRAY_OPEN = getIcon("byte_array_open");
	public static final IDrawableObject NBT_INT_ARRAY = getIcon("int_array");
	public static final IDrawableObject NBT_INT_ARRAY_CLOSED = getIcon("int_array_closed");
	public static final IDrawableObject NBT_INT_ARRAY_OPEN = getIcon("int_array_open");

	public class ButtonNBT extends Button
	{
		public final ButtonNBTCollection parent;
		public final String key;

		public ButtonNBT(ButtonNBTCollection p, String k)
		{
			super(p == null ? 0 : p.posX + 10, 0, 10, 10);
			parent = p;
			key = k;
			setTitle(key);
		}

		public void addChildren()
		{
		}

		public boolean canEdit()
		{
			return false;
		}

		public void edit()
		{
		}

		@Override
		public void onClicked(GuiBase gui, IMouseButton button)
		{
			selected = this;
		}

		@Override
		public void renderWidget(GuiBase gui)
		{
			int ax = getAX();
			int ay = getAY();
			NBT_BACKGROUND.draw(ax + 1, ay + 1, 8, 8, Color4I.NONE);
			getIcon(gui).draw(ax + 1, ay + 1, 8, 8, Color4I.NONE);
			gui.drawString(getTitle(gui), ax + 11, ay + 1);
		}
	}

	public class ButtonNBTString extends ButtonNBT implements IGuiFieldCallback
	{
		private NBTTagString nbt;

		public ButtonNBTString(ButtonNBTCollection p, String k, NBTTagString n)
		{
			super(p, k);
			nbt = n;
			setIcon(NBT_STRING);
			String title = key + ": " + nbt.getString();
			setTitle(title);
			setWidth(width + 2 + getFont().getStringWidth(title));
		}

		@Override
		public boolean canEdit()
		{
			return true;
		}

		@Override
		public void edit()
		{
			GuiSelectors.selectJson(new PropertyString(nbt.getString()), this);
		}

		@Override
		public void onCallback(IConfigValue value, boolean set)
		{
			if (set)
			{
				nbt = new NBTTagString(value.getString());
				parent.setTag(key, nbt);
				setTitle(key + ": " + nbt);
			}

			GuiEditNBT.this.openGui();
		}
	}

	public class ButtonNBTPrimitive extends ButtonNBT implements IGuiFieldCallback
	{
		private NBTBase nbt;

		public ButtonNBTPrimitive(ButtonNBTCollection p, String k, NBTBase n)
		{
			super(p, k);
			nbt = n;

			switch (nbt.getId())
			{
				case Constants.NBT.TAG_BYTE:
					setIcon(NBT_BYTE);
					break;
				case Constants.NBT.TAG_SHORT:
					setIcon(NBT_SHORT);
					break;
				case Constants.NBT.TAG_INT:
					setIcon(NBT_INT);
					break;
				case Constants.NBT.TAG_LONG:
					setIcon(NBT_LONG);
					break;
				case Constants.NBT.TAG_FLOAT:
					setIcon(NBT_FLOAT);
					break;
				case Constants.NBT.TAG_DOUBLE:
				case Constants.NBT.TAG_ANY_NUMERIC:
					setIcon(NBT_DOUBLE);
					break;
			}

			parent.setTag(key, nbt);
			updateTitle();
		}

		public void updateTitle()
		{
			String title = key + ": " + nbt;
			setTitle(title);
			setWidth(width + 2 + getFont().getStringWidth(title));
		}

		@Override
		public boolean canEdit()
		{
			return true;
		}

		@Override
		public void edit()
		{
			switch (nbt.getId())
			{
				case Constants.NBT.TAG_BYTE:
				case Constants.NBT.TAG_SHORT:
				case Constants.NBT.TAG_INT:
				case Constants.NBT.TAG_LONG:
					GuiSelectors.selectJson(new PropertyInt(((NBTPrimitive) nbt).getInt()), this);
					break;
				case Constants.NBT.TAG_FLOAT:
				case Constants.NBT.TAG_DOUBLE:
				case Constants.NBT.TAG_ANY_NUMERIC:
					GuiSelectors.selectJson(new PropertyDouble(((NBTPrimitive) nbt).getDouble()), this);
					break;
			}
		}

		@Override
		public void onCallback(IConfigValue value, boolean set)
		{
			if (set)
			{
				switch (nbt.getId())
				{
					case Constants.NBT.TAG_BYTE:
					case Constants.NBT.TAG_SHORT:
					case Constants.NBT.TAG_INT:
					case Constants.NBT.TAG_LONG:
						nbt = new NBTTagInt(value.getInt());
						break;
					case Constants.NBT.TAG_FLOAT:
					case Constants.NBT.TAG_DOUBLE:
					case Constants.NBT.TAG_ANY_NUMERIC:
						nbt = new NBTTagDouble(value.getDouble());
						break;
				}

				updateTitle();
			}

			GuiEditNBT.this.openGui();
		}
	}

	public abstract class ButtonNBTCollection extends ButtonNBT
	{
		public boolean collapsed;
		public final IDrawableObject iconOpen, iconClosed;

		public ButtonNBTCollection(ButtonNBTCollection p, String key, IDrawableObject open, IDrawableObject closed)
		{
			super(p, key);
			iconOpen = open;
			iconClosed = closed;
			setIcon(open);
			setWidth(width + 2 + getFont().getStringWidth(key));
		}

		@Override
		public void onClicked(GuiBase gui, IMouseButton button)
		{
			super.onClicked(gui, button);
			setCollapsed(!collapsed);
		}

		public void setCollapsed(boolean c)
		{
			collapsed = c;
			setIcon(collapsed ? iconClosed : iconOpen);
		}

		public abstract NBTBase getTag(String k);

		public abstract void setTag(String k, NBTBase base);
	}

	public class ButtonNBTMap extends ButtonNBTCollection
	{
		private NBTTagCompound map;

		public ButtonNBTMap(ButtonNBTCollection p, String key, NBTTagCompound m)
		{
			super(p, key, NBT_MAP_OPEN, NBT_MAP_CLOSED);
			map = m;
		}

		@Override
		public void addChildren()
		{
			for (String s : map.getKeySet())
			{
				ButtonNBT buttonNBT = getFrom(this, s);
				panelNbt.add(buttonNBT);
				buttonNBT.addChildren();
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return map.getTag(k);
		}

		@Override
		public void setTag(String k, NBTBase base)
		{
			map.setTag(k, base);

			if (parent != null)
			{
				parent.setTag(key, map);
			}
		}
	}

	public class ButtonNBTList extends ButtonNBTCollection
	{
		private NBTTagList list;

		public ButtonNBTList(ButtonNBTCollection p, String key, NBTTagList l)
		{
			super(p, key, NBT_LIST_OPEN, NBT_LIST_CLOSED);
			list = l;
		}

		@Override
		public void addChildren()
		{
			for (int i = 0; i < list.tagCount(); i++)
			{
				ButtonNBT buttonNBT = getFrom(this, Integer.toString(i));
				panelNbt.add(buttonNBT);
				buttonNBT.addChildren();
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return list.get(Integer.parseInt(k));
		}

		@Override
		public void setTag(String k, NBTBase base)
		{
			int id = Integer.parseInt(k);

			if (id == -1)
			{
				list.appendTag(base);
			}
			else
			{
				list.set(id, base);
			}

			if (parent != null)
			{
				parent.setTag(key, list);
			}
		}
	}

	public class ButtonNBTByteArray extends ButtonNBTCollection
	{
		private NBTTagByteArray list;

		public ButtonNBTByteArray(ButtonNBTCollection p, String key, NBTTagByteArray l)
		{
			super(p, key, NBT_BYTE_ARRAY_OPEN, NBT_BYTE_ARRAY_CLOSED);
			list = l;
		}

		@Override
		public void addChildren()
		{
			for (int i = 0; i < list.getByteArray().length; i++)
			{
				ButtonNBT buttonNBT = getFrom(this, Integer.toString(i));
				panelNbt.add(buttonNBT);
				buttonNBT.addChildren();
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return new NBTTagByte(list.getByteArray()[Integer.parseInt(k)]);
		}

		@Override
		public void setTag(String k, NBTBase base)
		{
			int id = Integer.parseInt(k);

			if (id == -1)
			{
				TByteArrayList list1 = new TByteArrayList(list.getByteArray());
				list1.add(((NBTPrimitive) base).getByte());
				list = new NBTTagByteArray(list1.toArray());
			}
			else
			{
				list.getByteArray()[id] = ((NBTPrimitive) base).getByte();
			}

			if (parent != null)
			{
				parent.setTag(key, list);
			}
		}
	}

	public class ButtonNBTIntArray extends ButtonNBTCollection
	{
		private NBTTagIntArray list;

		public ButtonNBTIntArray(ButtonNBTCollection p, String key, NBTTagIntArray l)
		{
			super(p, key, NBT_INT_ARRAY_OPEN, NBT_INT_ARRAY_CLOSED);
			list = l;
		}

		@Override
		public void addChildren()
		{
			for (int i = 0; i < list.getIntArray().length; i++)
			{
				ButtonNBT buttonNBT = getFrom(this, Integer.toString(i));
				panelNbt.add(buttonNBT);
				buttonNBT.addChildren();
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return new NBTTagInt(list.getIntArray()[Integer.parseInt(k)]);
		}

		@Override
		public void setTag(String k, NBTBase base)
		{
			int id = Integer.parseInt(k);

			if (id == -1)
			{
				TIntArrayList list1 = new TIntArrayList(list.getIntArray());
				list1.add(((NBTPrimitive) base).getInt());
				list = new NBTTagIntArray(list1.toArray());
			}
			else
			{
				list.getIntArray()[id] = ((NBTPrimitive) base).getInt();
			}

			if (parent != null)
			{
				parent.setTag(key, list);
			}
		}
	}

	private ButtonNBT getFrom(ButtonNBTCollection p, String key)
	{
		NBTBase nbt = p.getTag(key);

		switch (nbt.getId())
		{
			case Constants.NBT.TAG_COMPOUND:
				return new ButtonNBTMap(p, key, (NBTTagCompound) nbt);
			case Constants.NBT.TAG_LIST:
				return new ButtonNBTList(p, key, (NBTTagList) nbt);
			case Constants.NBT.TAG_BYTE_ARRAY:
				return new ButtonNBTByteArray(p, key, (NBTTagByteArray) nbt);
			case Constants.NBT.TAG_INT_ARRAY:
				return new ButtonNBTIntArray(p, key, (NBTTagIntArray) nbt);
			case Constants.NBT.TAG_STRING:
				return new ButtonNBTString(p, key, (NBTTagString) nbt);
			default:
				return new ButtonNBTPrimitive(p, key, nbt);
		}
	}

	private final NBTTagCompound info;
	private final NBTTagCompound mainNbt;
	private ButtonNBT selected = null;
	public final Panel panelTopLeft, panelTopRight, panelNbt;
	public final PanelScrollBar scroll;
	private int shouldClose = 0;

	public GuiEditNBT(NBTTagCompound i, NBTTagCompound nbt)
	{
		super(0, 0);
		info = i;
		mainNbt = nbt;

		panelTopLeft = new Panel()
		{
			@Override
			public void addWidgets()
			{
			}
		};

		panelTopLeft.addFlags(Panel.FLAG_DEFAULTS);

		panelTopRight = new Panel(0, 0, 0, 20)
		{
			@Override
			public void addWidgets()
			{
				add(new Button(0, 2, 16, 16, GuiLang.BUTTON_ACCEPT.translate())
				{
					@Override
					public void onClicked(GuiBase gui, IMouseButton button)
					{
						GuiHelper.playClickSound();
						shouldClose = 1;
						gui.closeGui();
					}
				}.setIcon(GuiIcons.ACCEPT));

				add(new Button(0, 2, 16, 16, GuiLang.BUTTON_CANCEL.translate())
				{
					@Override
					public void onClicked(GuiBase gui, IMouseButton button)
					{
						GuiHelper.playClickSound();
						shouldClose = 2;
						gui.closeGui();
					}
				}.setIcon(GuiIcons.CANCEL));

				add(new Button(0, 2, 16, 16, GuiLang.BUTTON_COLLAPSE_ALL.translate())
				{
					@Override
					public void onClicked(GuiBase gui, IMouseButton button)
					{
						GuiHelper.playClickSound();

						for (Widget w : panelNbt.widgets)
						{
							if (w instanceof ButtonNBTCollection)
							{
								((ButtonNBTCollection) w).setCollapsed(true);
							}
						}

						scroll.setValue(gui, 0);
						gui.refreshWidgets();
					}
				}.setIcon(GuiIcons.REMOVE));

				add(new Button(0, 2, 16, 16, GuiLang.BUTTON_EXPAND_ALL.translate())
				{
					@Override
					public void onClicked(GuiBase gui, IMouseButton button)
					{
						GuiHelper.playClickSound();

						for (Widget w : panelNbt.widgets)
						{
							if (w instanceof ButtonNBTCollection)
							{
								((ButtonNBTCollection) w).setCollapsed(false);
							}
						}

						scroll.setValue(gui, 0);
						gui.refreshWidgets();
					}
				}.setIcon(GuiIcons.ADD));

				updateWidgetPositions();
			}

			@Override
			public void updateWidgetPositions()
			{
				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelTopRight.addFlags(Panel.FLAG_DEFAULTS);

		panelNbt = new Panel()
		{
			@Override
			public void addWidgets()
			{
				selected = null;
				ButtonNBTMap map = new ButtonNBTMap(null, "", mainNbt);

				for (String s : mainNbt.getKeySet())
				{
					ButtonNBT buttonNBT = getFrom(map, s);
					add(buttonNBT);
					buttonNBT.addChildren();
				}

				updateWidgetPositions();
			}

			@Override
			public void updateWidgetPositions()
			{
				setHeight(align(WidgetLayout.VERTICAL));
			}
		};

		panelNbt.addFlags(Panel.FLAG_DEFAULTS);

		scroll = new PanelScrollBar(0, 20, 10, 0, 6, panelNbt);
	}

	@Override
	public void addWidgets()
	{
		addAll(panelTopLeft, panelTopRight, panelNbt, scroll);
	}

	@Override
	public boolean isFullscreen()
	{
		return true;
	}

	@Override
	public void updateWidgetPositions()
	{
		panelTopRight.updateWidgetPositions();
		panelTopRight.posX = width - panelTopRight.width;

		panelNbt.setHeight(height - 20);
		panelNbt.posX = 0;
		panelNbt.posY = 20;
		panelNbt.setWidth(width - scroll.width);

		scroll.posX = width - 16;
		scroll.setHeight(panelNbt.height);
	}

	@Override
	public void onClosed()
	{
		if (shouldClose == 1)
		{
			new MessageEditNBTResponse(info, mainNbt).sendToServer();
		}
	}
}