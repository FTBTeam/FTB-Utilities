package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.config.ConfigDouble;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.GuiLang;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.SimpleButton;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiSelectors;
import com.feed_the_beast.ftblib.lib.gui.misc.IGuiFieldCallback;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.IconWithOutline;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftbutilities.FTBUFinals;
import com.feed_the_beast.ftbutilities.net.MessageEditNBTResponse;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class GuiEditNBT extends GuiBase
{
	private static final Color4I COLOR_BACKGROUND = Color4I.rgba(0x99333333);

	private static Icon getIcon(String name)
	{
		return IconWithOutline.BUTTON_ROUND_GRAY.combineWith(Icon.getIcon(FTBUFinals.MOD_ID + ":textures/gui/nbt/" + name + ".png"));
	}

	public static final Icon NBT_BYTE = getIcon("byte");
	public static final Icon NBT_SHORT = getIcon("short");
	public static final Icon NBT_INT = getIcon("int");
	public static final Icon NBT_LONG = getIcon("long");
	public static final Icon NBT_FLOAT = getIcon("float");
	public static final Icon NBT_DOUBLE = getIcon("double");
	public static final Icon NBT_STRING = getIcon("string");
	public static final Icon NBT_LIST = getIcon("list");
	public static final Icon NBT_LIST_CLOSED = getIcon("list_closed");
	public static final Icon NBT_LIST_OPEN = getIcon("list_open");
	public static final Icon NBT_MAP = getIcon("map");
	public static final Icon NBT_MAP_CLOSED = getIcon("map_closed");
	public static final Icon NBT_MAP_OPEN = getIcon("map_open");
	public static final Icon NBT_BYTE_ARRAY = getIcon("byte_array");
	public static final Icon NBT_BYTE_ARRAY_CLOSED = getIcon("byte_array_closed");
	public static final Icon NBT_BYTE_ARRAY_OPEN = getIcon("byte_array_open");
	public static final Icon NBT_INT_ARRAY = getIcon("int_array");
	public static final Icon NBT_INT_ARRAY_CLOSED = getIcon("int_array_closed");
	public static final Icon NBT_INT_ARRAY_OPEN = getIcon("int_array_open");

	public abstract class ButtonNBT extends Button
	{
		public final ButtonNBTCollection parent;
		public String key;

		public ButtonNBT(GuiBase gui, @Nullable ButtonNBTCollection b, String k)
		{
			super(gui, b == null ? 0 : b.posX + 10, 0, 10, 10);
			parent = b;
			key = k;
			setTitle(key);
		}

		public void updateChildren(boolean first)
		{
		}

		public void addChildren()
		{
		}

		public boolean canCreateNew(int id)
		{
			return false;
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
		}

		@Override
		public void draw()
		{
			int ax = getAX();
			int ay = getAY();

			if (selected == this)
			{
				Color4I.WHITE_A[33].draw(ax, ay, width, height);
			}

			getIcon().draw(ax + 1, ay + 1, 8, 8);
			gui.drawString(getTitle(), ax + 11, ay + 1);
		}
	}

	public class ButtonNBTPrimitive extends ButtonNBT implements IGuiFieldCallback
	{
		private NBTBase nbt;

		public ButtonNBTPrimitive(GuiBase gui, ButtonNBTCollection b, String k, NBTBase n)
		{
			super(gui, b, k);
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
					title = ((NBTPrimitive) nbt).getInt();
					break;
				case Constants.NBT.TAG_LONG:
					title = ((NBTPrimitive) nbt).getLong();
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
			setWidth(12 + getStringWidth(key + ": " + title));
		}

		@Override
		public void onClicked(MouseButton button)
		{
			selected = this;
			panelTopLeft.refreshWidgets();

			if (button.isRight())
			{
				edit();
			}
		}

		public void edit()
		{
			switch (nbt.getId())
			{
				case Constants.NBT.TAG_BYTE:
				case Constants.NBT.TAG_SHORT:
				case Constants.NBT.TAG_INT:
					GuiSelectors.selectJson(new ConfigInt(((NBTPrimitive) nbt).getInt()), this);
					break;
				case Constants.NBT.TAG_LONG:
					GuiSelectors.selectJson(new ConfigString(Long.toString(((NBTPrimitive) nbt).getLong())), this);
					break;
				case Constants.NBT.TAG_FLOAT:
				case Constants.NBT.TAG_DOUBLE:
				case Constants.NBT.TAG_ANY_NUMERIC:
					GuiSelectors.selectJson(new ConfigDouble(((NBTPrimitive) nbt).getDouble()), this);
					break;
				case Constants.NBT.TAG_STRING:
					GuiSelectors.selectJson(new ConfigString(((NBTTagString) nbt).getString()), this);
					break;
			}
		}

		@Override
		public void onCallback(ConfigValue value, boolean set)
		{
			if (set)
			{
				switch (nbt.getId())
				{
					case Constants.NBT.TAG_BYTE:
					case Constants.NBT.TAG_SHORT:
					case Constants.NBT.TAG_INT:
						nbt = new NBTTagInt(value.getInt());
						break;
					case Constants.NBT.TAG_LONG:
						nbt = new NBTTagLong(Long.parseLong(value.getString()));
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
		public final Map<String, ButtonNBT> children;
		public final Icon iconOpen, iconClosed;

		public ButtonNBTCollection(GuiBase gui, @Nullable ButtonNBTCollection b, String key, Icon open, Icon closed)
		{
			super(gui, b, key);
			iconOpen = open;
			iconClosed = closed;
			setCollapsed(false);
			setWidth(width + 2 + getStringWidth(key));
			children = new LinkedHashMap<>();
		}

		@Override
		public void addChildren()
		{
			if (!collapsed)
			{
				for (ButtonNBT button : children.values())
				{
					panelNbt.add(button);
					button.addChildren();
				}
			}
		}

		@Override
		public void onClicked(MouseButton button)
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
		private Icon hoverIcon = Icon.EMPTY;

		public ButtonNBTMap(GuiBase gui, @Nullable ButtonNBTCollection b, String key, NBTTagCompound m)
		{
			super(gui, b, key, NBT_MAP_OPEN, NBT_MAP_CLOSED);
			map = m;
		}

		@Override
		public void updateChildren(boolean first)
		{
			children.clear();
			List<String> list = new ArrayList<>(map.getKeySet());
			list.sort(StringUtils.IGNORE_CASE_COMPARATOR);

			for (String s : list)
			{
				ButtonNBT nbt = getFrom(this, s);
				children.put(s, nbt);
				nbt.updateChildren(first);
			}

			updateHoverIcon();

			if (first && !hoverIcon.isEmpty())
			{
				setCollapsed(true);
			}
		}

		private void updateHoverIcon()
		{
			hoverIcon = Icon.EMPTY;
			ItemStack stack = (map.hasKey("id", Constants.NBT.TAG_STRING) && map.hasKey("Count") && map.hasKey("Damage")) ? new ItemStack(map) : ItemStack.EMPTY;

			if (!stack.isEmpty())
			{
				hoverIcon = ItemIcon.getItemIcon(stack);
			}

			setWidth(12 + getStringWidth(getTitle()) + (hoverIcon.isEmpty() ? 0 : 10));
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
			if (!hoverIcon.isEmpty())
			{
				if (hoverIcon instanceof ItemIcon)
				{
					list.add(((ItemIcon) hoverIcon).getStack().getDisplayName());
				}
			}
		}

		@Override
		public void draw()
		{
			super.draw();

			if (!hoverIcon.isEmpty())
			{
				int ax = getAX();
				int ay = getAY();
				hoverIcon.draw(ax + 12 + getStringWidth(getTitle()), ay + 1, 8, 8);
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

			updateHoverIcon();

			if (parent != null)
			{
				parent.setTag(key, map);
			}
		}

		@Override
		public boolean canCreateNew(int id)
		{
			return true;
		}
	}

	public class ButtonNBTList extends ButtonNBTCollection
	{
		private NBTTagList list;

		public ButtonNBTList(GuiBase gui, ButtonNBTCollection p, String key, NBTTagList l)
		{
			super(gui, p, key, NBT_LIST_OPEN, NBT_LIST_CLOSED);
			list = l;
		}

		@Override
		public void updateChildren(boolean first)
		{
			children.clear();
			for (int i = 0; i < list.tagCount(); i++)
			{
				String s = Integer.toString(i);
				ButtonNBT nbt = getFrom(this, s);
				children.put(s, nbt);
				nbt.updateChildren(first);
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

		@Override
		public boolean canCreateNew(int id)
		{
			return list.hasNoTags() || list.getTagType() == id;
		}
	}

	public class ButtonNBTByteArray extends ButtonNBTCollection
	{
		private ByteArrayList list;

		public ButtonNBTByteArray(GuiBase gui, ButtonNBTCollection p, String key, NBTTagByteArray l)
		{
			super(gui, p, key, NBT_BYTE_ARRAY_OPEN, NBT_BYTE_ARRAY_CLOSED);
			list = new ByteArrayList(l.getByteArray());
		}

		@Override
		public void updateChildren(boolean first)
		{
			children.clear();
			for (int i = 0; i < list.size(); i++)
			{
				String s = Integer.toString(i);
				ButtonNBT nbt = getFrom(this, s);
				children.put(s, nbt);
				nbt.updateChildren(first);
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return new NBTTagByte(list.getByte(Integer.parseInt(k)));
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			int id = Integer.parseInt(k);

			if (id == -1)
			{
				if (base != null)
				{
					list.add(((NBTPrimitive) base).getByte());
				}
			}
			else if (base != null)
			{
				list.set(id, ((NBTPrimitive) base).getByte());
			}
			else
			{
				list.rem(id);
			}

			if (parent != null)
			{
				parent.setTag(key, new NBTTagByteArray(list.toByteArray()));
			}
		}

		@Override
		public boolean canCreateNew(int id)
		{
			return id == Constants.NBT.TAG_BYTE;
		}
	}

	public class ButtonNBTIntArray extends ButtonNBTCollection
	{
		private IntArrayList list;

		public ButtonNBTIntArray(GuiBase gui, ButtonNBTCollection p, String key, NBTTagIntArray l)
		{
			super(gui, p, key, NBT_INT_ARRAY_OPEN, NBT_INT_ARRAY_CLOSED);
			list = new IntArrayList(l.getIntArray());
		}

		@Override
		public void updateChildren(boolean first)
		{
			children.clear();
			for (int i = 0; i < list.size(); i++)
			{
				String s = Integer.toString(i);
				ButtonNBT nbt = getFrom(this, s);
				children.put(s, nbt);
				nbt.updateChildren(first);
			}
		}

		@Override
		public NBTBase getTag(String k)
		{
			return new NBTTagInt(list.getInt(Integer.parseInt(k)));
		}

		@Override
		public void setTag(String k, @Nullable NBTBase base)
		{
			int id = Integer.parseInt(k);

			if (id == -1)
			{
				if (base != null)
				{
					list.add(((NBTPrimitive) base).getInt());
				}
			}
			else if (base != null)
			{
				list.set(id, ((NBTPrimitive) base).getInt());
			}
			else
			{
				list.rem(id);
			}

			if (parent != null)
			{
				parent.setTag(key, new NBTTagIntArray(list.toIntArray()));
			}
		}

		@Override
		public boolean canCreateNew(int id)
		{
			return id == Constants.NBT.TAG_INT;
		}
	}

	private ButtonNBT getFrom(ButtonNBTCollection b, String key)
	{
		NBTBase nbt = b.getTag(key);

		switch (nbt.getId())
		{
			case Constants.NBT.TAG_COMPOUND:
				return new ButtonNBTMap(this, b, key, (NBTTagCompound) nbt);
			case Constants.NBT.TAG_LIST:
				return new ButtonNBTList(this, b, key, (NBTTagList) nbt);
			case Constants.NBT.TAG_BYTE_ARRAY:
				return new ButtonNBTByteArray(this, b, key, (NBTTagByteArray) nbt);
			case Constants.NBT.TAG_INT_ARRAY:
				return new ButtonNBTIntArray(this, b, key, (NBTTagIntArray) nbt);
			default:
				return new ButtonNBTPrimitive(this, b, key, nbt);
		}
	}

	public SimpleButton newTag(String t, Icon icon, Supplier<NBTBase> supplier)
	{
		return new SimpleButton(this, t, icon, (gui, button) ->
		{
			if (selected instanceof ButtonNBTMap)
			{
				GuiSelectors.selectJson(new ConfigString("_unnamed"), (value, set) ->
				{
					if (set && !value.getString().isEmpty())
					{
						((ButtonNBTCollection) selected).setTag(value.getString(), supplier.get());
						selected.updateChildren(false);
						panelNbt.refreshWidgets();
					}

					GuiEditNBT.this.openGui();
				});
			}
			else if (selected instanceof ButtonNBTCollection)
			{
				((ButtonNBTCollection) selected).setTag("-1", supplier.get());
				selected.updateChildren(false);
				panelNbt.refreshWidgets();
			}
		});
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
		buttonNBTRoot = new ButtonNBTMap(this, null, "ROOT", nbt);
		buttonNBTRoot.updateChildren(true);
		selected = buttonNBTRoot;

		panelTopLeft = new Panel(this, 0, 2, 0, 16)
		{
			@Override
			public void addWidgets()
			{
				add(new SimpleButton(gui, GuiLang.DELETE, selected == buttonNBTRoot ? GuiIcons.REMOVE_GRAY : GuiIcons.REMOVE, (gui, button) ->
				{
					if (selected != buttonNBTRoot)
					{
						selected.parent.setTag(selected.key, null);
						selected.parent.updateChildren(false);
						selected = selected.parent;
						panelNbt.refreshWidgets();
						panelTopLeft.refreshWidgets();
					}
				}));

				boolean canRename = selected.parent instanceof ButtonNBTMap;

				add(new SimpleButton(gui, "Rename", canRename ? GuiIcons.INFO : GuiIcons.INFO_GRAY, (gui, button) ->
				{
					if (canRename)
					{
						GuiSelectors.selectJson(new ConfigString(selected.key), (value, set) ->
						{
							if (set)
							{
								String s = value.getString();

								if (!s.isEmpty())
								{
									ButtonNBTCollection parent = selected.parent;
									String s0 = selected.key;
									NBTBase nbt = parent.getTag(s0);
									parent.setTag(s0, null);
									parent.setTag(s, nbt);
									parent.updateChildren(false);
									selected = parent.children.get(s);
									panelNbt.refreshWidgets();
								}
							}

							gui.openGui();
						});
					}
				}));

				if (selected instanceof ButtonNBTPrimitive)
				{
					add(new SimpleButton(gui, GuiLang.EDIT, GuiIcons.FEATHER, (gui, button) -> ((ButtonNBTPrimitive) selected).edit()));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_COMPOUND))
				{
					add(newTag("Compound", NBT_MAP, NBTTagCompound::new));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_LIST))
				{
					add(newTag("List", NBT_LIST, NBTTagList::new));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_STRING))
				{
					add(newTag("String", NBT_STRING, () -> new NBTTagString("")));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_BYTE))
				{
					add(newTag("Byte", NBT_BYTE, () -> new NBTTagByte((byte) 0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_SHORT))
				{
					add(newTag("Short", NBT_SHORT, () -> new NBTTagShort((short) 0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_INT))
				{
					add(newTag("Int", NBT_INT, () -> new NBTTagInt(0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_LONG))
				{
					add(newTag("Long", NBT_LONG, () -> new NBTTagLong(0L)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_FLOAT))
				{
					add(newTag("Float", NBT_FLOAT, () -> new NBTTagFloat(0F)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_DOUBLE))
				{
					add(newTag("Double", NBT_DOUBLE, () -> new NBTTagDouble(0D)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_BYTE_ARRAY))
				{
					add(newTag("Byte Array", NBT_BYTE_ARRAY, () -> new NBTTagByteArray(new byte[0])));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_INT_ARRAY))
				{
					add(newTag("Int Array", NBT_INT_ARRAY, () -> new NBTTagIntArray(new int[0])));
				}

				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelTopLeft.addFlags(Panel.DEFAULTS);

		panelTopRight = new Panel(this, 0, 2, 0, 16)
		{
			@Override
			public void addWidgets()
			{
				add(new SimpleButton(gui, GuiLang.COLLAPSE_ALL, GuiIcons.REMOVE, (gui, button) ->
				{
					for (Widget w : panelNbt.widgets)
					{
						if (w instanceof ButtonNBTCollection)
						{
							((ButtonNBTCollection) w).setCollapsed(true);
						}
					}

					scroll.setValue(0D);
					panelNbt.refreshWidgets();
				}));

				add(new SimpleButton(gui, GuiLang.EXPAND_ALL, GuiIcons.ADD, (gui, button) ->
				{
					for (Widget w : panelNbt.widgets)
					{
						if (w instanceof ButtonNBTCollection)
						{
							((ButtonNBTCollection) w).setCollapsed(false);
						}
					}

					scroll.setValue(0D);
					panelNbt.refreshWidgets();
				}));

				add(new SimpleButton(gui, GuiLang.CANCEL, GuiIcons.CANCEL, (gui, button) ->
				{
					shouldClose = 2;
					gui.closeGui();
				}));

				add(new SimpleButton(gui, GuiLang.ACCEPT, GuiIcons.ACCEPT, (gui, button) ->
				{
					shouldClose = 1;
					gui.closeGui();
				}));

				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelTopRight.addFlags(Panel.DEFAULTS);

		panelNbt = new Panel(this, 0, 21, 0, 0)
		{
			@Override
			public void addWidgets()
			{
				add(buttonNBTRoot);
				buttonNBTRoot.addChildren();
				scroll.setElementSize(align(WidgetLayout.VERTICAL) + 2);
			}
		};

		panelNbt.addFlags(Panel.DEFAULTS);

		scroll = new PanelScrollBar(this, 0, 20, 16, 0, 0, panelNbt)
		{
			@Override
			public boolean shouldDraw()
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
	public boolean onInit()
	{
		return setFullscreen();
	}

	@Override
	public void onPostInit()
	{
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
		COLOR_BACKGROUND.draw(0, 0, width, 20);
	}
}