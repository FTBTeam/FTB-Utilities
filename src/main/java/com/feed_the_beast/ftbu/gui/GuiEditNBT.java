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
import com.feed_the_beast.ftbl.lib.gui.SimpleButton;
import com.feed_the_beast.ftbl.lib.gui.Widget;
import com.feed_the_beast.ftbl.lib.gui.WidgetLayout;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiEditConfig;
import com.feed_the_beast.ftbl.lib.gui.misc.GuiSelectors;
import com.feed_the_beast.ftbl.lib.gui.misc.IGuiFieldCallback;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.net.MessageEditNBTResponse;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TIntArrayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

		public ButtonNBT(@Nullable ButtonNBTCollection b, String k)
		{
			super(b == null ? 0 : b.posX + 10, 0, 10, 10);
			parent = b;
			key = k;
			setTitle(key);
		}

		public void addChildren()
		{
		}

		@Override
		public void onClicked(GuiBase gui, IMouseButton button)
		{
			selected = this;
			panelTopLeft.refreshWidgets();
		}

		@Override
		public void addMouseOverText(GuiBase gui, List<String> list)
		{
		}

		@Override
		public void renderWidget(GuiBase gui)
		{
			int ax = getAX();
			int ay = getAY();

			if (selected == this)
			{
				GuiHelper.drawBlankRect(ax, ay, width, height, Color4I.WHITE_A[33]);
			}

			NBT_BACKGROUND.draw(ax + 1, ay + 1, 8, 8, Color4I.NONE);
			getIcon(gui).draw(ax + 1, ay + 1, 8, 8, Color4I.NONE);
			gui.drawString(getTitle(gui), ax + 11, ay + 1);
		}
	}

	public class ButtonNBTPrimitive extends ButtonNBT implements IGuiFieldCallback
	{
		private NBTBase nbt;

		public ButtonNBTPrimitive(ButtonNBTCollection b, String k, NBTBase n)
		{
			super(b, k);
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
				case Constants.NBT.TAG_STRING:
					setIcon(NBT_STRING);
					break;
			}

			parent.setTag(key, nbt);
			updateTitle();
		}

		public void updateTitle()
		{
			Object title = "";

			switch (nbt.getId())
			{
				case Constants.NBT.TAG_BYTE:
				case Constants.NBT.TAG_SHORT:
				case Constants.NBT.TAG_INT:
				case Constants.NBT.TAG_LONG:
					title = ((NBTPrimitive) nbt).getInt();
					break;
				case Constants.NBT.TAG_FLOAT:
				case Constants.NBT.TAG_DOUBLE:
				case Constants.NBT.TAG_ANY_NUMERIC:
					title = ((NBTPrimitive) nbt).getDouble();
					break;
				case Constants.NBT.TAG_STRING:
					title = ((NBTTagString) nbt).getString();
					break;
			}

			setTitle(key + ": " + title);
			setWidth(12 + getFont().getStringWidth(key + ": " + title));
		}

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
				case Constants.NBT.TAG_STRING:
					GuiSelectors.selectJson(new PropertyString(((NBTTagString) nbt).getString()), this);
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
					case Constants.NBT.TAG_STRING:
						nbt = new NBTTagString(value.getString());
						break;
				}

				parent.setTag(key, nbt);
				updateTitle();
			}

			GuiEditNBT.this.openGui();
		}
	}

	public abstract class ButtonNBTCollection extends ButtonNBT
	{
		public boolean collapsed;
		public final List<ButtonNBT> children;
		public final IDrawableObject iconOpen, iconClosed;

		public ButtonNBTCollection(@Nullable ButtonNBTCollection b, String key, IDrawableObject open, IDrawableObject closed)
		{
			super(b, key);
			iconOpen = open;
			iconClosed = closed;
			setCollapsed(false);
			setWidth(width + 2 + getFont().getStringWidth(key));
			children = new ArrayList<>();
		}

		@Override
		public final void addChildren()
		{
			if (!collapsed)
			{
				for (ButtonNBT button : children)
				{
					panelNbt.add(button);
					button.addChildren();
				}
			}
		}

		@Override
		public void onClicked(GuiBase gui, IMouseButton button)
		{
			if (gui.getMouseX() <= getAX() + height)
			{
				setCollapsed(!collapsed);
				panelNbt.refreshWidgets();
			}
			else
			{
				selected = this;
				panelTopLeft.refreshWidgets();
			}
		}

		public void setCollapsed(boolean c)
		{
			collapsed = c;
			setIcon(collapsed ? iconClosed : iconOpen);
		}

		public abstract NBTBase getTag(String k);

		public abstract void setTag(String k, @Nullable NBTBase base);
	}

	public class ButtonNBTMap extends ButtonNBTCollection
	{
		private NBTTagCompound map;

		public ButtonNBTMap(@Nullable ButtonNBTCollection b, String key, NBTTagCompound m)
		{
			super(b, key, NBT_MAP_OPEN, NBT_MAP_CLOSED);
			map = m;
			List<String> list = new ArrayList<>(map.getKeySet());
			list.sort(StringUtils.IGNORE_CASE_COMPARATOR);

			for (String s : list)
			{
				children.add(getFrom(this, s));
			}

			if (map.hasKey("id") && map.hasKey("Count") && map.hasKey("Damage"))
			{
				setCollapsed(true);
			}
		}

		@Override
		public void addMouseOverText(GuiBase gui, List<String> list)
		{
			if (map.hasKey("id") && map.hasKey("Count") && map.hasKey("Damage"))
			{
				ItemStack stack = new ItemStack(map);

				if (!stack.isEmpty())
				{
					int ax = gui.getMouseX() + 4;
					int ay = gui.getMouseY() - 20;
					GuiHelper.drawBlankRect(ax - 2, ay - 2, 20, 20, Color4I.GRAY);
					GuiHelper.drawItem(stack, ax, ay, true, Color4I.NONE);
				}
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return map.getTag(k);
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			if (base != null)
			{
				map.setTag(k, base);
			}
			else
			{
				map.removeTag(k);
			}

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

			for (int i = 0; i < list.tagCount(); i++)
			{
				children.add(getFrom(this, Integer.toString(i)));
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return list.get(Integer.parseInt(k));
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			int id = Integer.parseInt(k);

			if (id == -1)
			{
				if (base != null)
				{
					list.appendTag(base);
				}
			}
			else if (base != null)
			{
				list.set(id, base);
			}
			else
			{
				list.removeTag(id);
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

			for (int i = 0; i < list.getByteArray().length; i++)
			{
				children.add(getFrom(this, Integer.toString(i)));
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return new NBTTagByte(list.getByteArray()[Integer.parseInt(k)]);
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			int id = Integer.parseInt(k);
			TByteArrayList list1 = new TByteArrayList(list.getByteArray());

			if (id == -1)
			{
				if (base != null)
				{
					list1.add(((NBTPrimitive) base).getByte());
				}
			}
			else if (base != null)
			{
				list1.set(id, ((NBTPrimitive) base).getByte());
			}
			else
			{
				list1.removeAt(id);
			}

			list = new NBTTagByteArray(list1.toArray());

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

			for (int i = 0; i < list.getIntArray().length; i++)
			{
				children.add(getFrom(this, Integer.toString(i)));
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return new NBTTagInt(list.getIntArray()[Integer.parseInt(k)]);
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			int id = Integer.parseInt(k);
			TIntArrayList list1 = new TIntArrayList(list.getIntArray());

			if (id == -1)
			{
				if (base != null)
				{
					list1.add(((NBTPrimitive) base).getInt());
				}
			}
			else if (base != null)
			{
				list1.set(id, ((NBTPrimitive) base).getInt());
			}
			else
			{
				list1.removeAt(id);
			}

			list = new NBTTagIntArray(list1.toArray());

			if (parent != null)
			{
				parent.setTag(key, list);
			}
		}
	}

	private ButtonNBT getFrom(ButtonNBTCollection b, String key)
	{
		NBTBase nbt = b.getTag(key);

		switch (nbt.getId())
		{
			case Constants.NBT.TAG_COMPOUND:
				return new ButtonNBTMap(b, key, (NBTTagCompound) nbt);
			case Constants.NBT.TAG_LIST:
				return new ButtonNBTList(b, key, (NBTTagList) nbt);
			case Constants.NBT.TAG_BYTE_ARRAY:
				return new ButtonNBTByteArray(b, key, (NBTTagByteArray) nbt);
			case Constants.NBT.TAG_INT_ARRAY:
				return new ButtonNBTIntArray(b, key, (NBTTagIntArray) nbt);
			default:
				return new ButtonNBTPrimitive(b, key, nbt);
		}
	}

	private final NBTTagCompound info;
	private final ButtonNBTMap buttonNBTRoot;
	private ButtonNBT selected = null;
	public final Panel panelTopLeft, panelTopRight, panelNbt;
	public final PanelScrollBar scroll;
	private int shouldClose = 0;

	public GuiEditNBT(NBTTagCompound i, NBTTagCompound nbt)
	{
		super(0, 0);
		info = i;
		buttonNBTRoot = new ButtonNBTMap(null, "ROOT", nbt);
		selected = buttonNBTRoot;

		panelTopLeft = new Panel(0, 2, 0, 16)
		{
			@Override
			public void addWidgets()
			{
				add(new SimpleButton(0, 0, 16, 16, GuiLang.BUTTON_DELETE, selected == buttonNBTRoot ? GuiIcons.REMOVE_GRAY : GuiIcons.REMOVE, (gui, button) ->
				{
					if (selected != buttonNBTRoot)
					{
						selected.parent.setTag(selected.key, null);
						selected.parent.children.remove(selected);
						selected = selected.parent;
						panelNbt.refreshWidgets();
					}
				}));

				if (selected.parent instanceof ButtonNBTMap)
				{
					add(new SimpleButton(0, 0, 16, 16, "Rename", GuiIcons.INFO, (gui, button) ->
					{
					}));
				}

				if (selected instanceof ButtonNBTPrimitive)
				{
					add(new SimpleButton(0, 0, 16, 16, GuiLang.BUTTON_EDIT, GuiIcons.FEATHER, (gui, button) -> ((ButtonNBTPrimitive) selected).edit()));
				}
			}

			@Override
			public void updateWidgetPositions()
			{
				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelTopLeft.addFlags(Panel.FLAG_DEFAULTS);

		panelTopRight = new Panel(0, 2, 0, 16)
		{
			@Override
			public void addWidgets()
			{
				add(new SimpleButton(0, 0, 16, 16, GuiLang.BUTTON_COLLAPSE_ALL, GuiIcons.REMOVE, (gui, button) ->
				{
					for (Widget w : panelNbt.widgets)
					{
						if (w instanceof ButtonNBTCollection)
						{
							((ButtonNBTCollection) w).setCollapsed(true);
						}
					}

					scroll.setValue(gui, 0);
					panelNbt.refreshWidgets();
				}));

				add(new SimpleButton(0, 0, 16, 16, GuiLang.BUTTON_EXPAND_ALL, GuiIcons.ADD, (gui, button) ->
				{
					for (Widget w : panelNbt.widgets)
					{
						if (w instanceof ButtonNBTCollection)
						{
							((ButtonNBTCollection) w).setCollapsed(false);
						}
					}

					scroll.setValue(gui, 0);
					panelNbt.refreshWidgets();
				}));

				add(new SimpleButton(0, 0, 16, 16, GuiLang.BUTTON_CANCEL, GuiIcons.CANCEL, (gui, button) ->
				{
					shouldClose = 2;
					gui.closeGui();
				}));

				add(new SimpleButton(0, 0, 16, 16, GuiLang.BUTTON_ACCEPT, GuiIcons.ACCEPT, (gui, button) ->
				{
					shouldClose = 1;
					gui.closeGui();
				}));

				updateWidgetPositions();
			}

			@Override
			public void updateWidgetPositions()
			{
				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelTopRight.addFlags(Panel.FLAG_DEFAULTS);

		panelNbt = new Panel(0, 21, 0, 0)
		{
			@Override
			public void addWidgets()
			{
				add(buttonNBTRoot);
				buttonNBTRoot.addChildren();
				updateWidgetPositions();
			}

			@Override
			public void updateWidgetPositions()
			{
				setHeight(align(WidgetLayout.VERTICAL));
				scroll.setElementSize(widgets.size() * 10 + 2);
			}
		};

		panelNbt.addFlags(Panel.FLAG_DEFAULTS);

		scroll = new PanelScrollBar(0, 20, 16, 0, 10, panelNbt)
		{
			@Override
			public boolean shouldRender(GuiBase gui)
			{
				return true;
			}
		};
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
		panelTopLeft.updateWidgetPositions();
		panelTopRight.updateWidgetPositions();
		panelTopRight.posX = width - panelTopRight.width;

		panelNbt.setHeight(height - 20);
		panelNbt.setWidth(width - scroll.width);

		scroll.posX = width - scroll.width;
		scroll.setHeight(panelNbt.height);
	}

	@Override
	public void onClosed()
	{
		if (shouldClose == 1)
		{
			new MessageEditNBTResponse(info, buttonNBTRoot.map).sendToServer();
		}
	}

	@Override
	public void drawBackground()
	{
		GuiHelper.drawBlankRect(0, 0, width, 20, GuiEditConfig.COLOR_BACKGROUND);
		GlStateManager.color(1F, 1F, 1F, 1F);
	}
}