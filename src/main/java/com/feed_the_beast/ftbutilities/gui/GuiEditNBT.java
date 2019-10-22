package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.config.ConfigDouble;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.gui.Button;
import com.feed_the_beast.ftblib.lib.gui.GuiBase;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.feed_the_beast.ftblib.lib.gui.Panel;
import com.feed_the_beast.ftblib.lib.gui.PanelScrollBar;
import com.feed_the_beast.ftblib.lib.gui.SimpleButton;
import com.feed_the_beast.ftblib.lib.gui.Theme;
import com.feed_the_beast.ftblib.lib.gui.Widget;
import com.feed_the_beast.ftblib.lib.gui.WidgetLayout;
import com.feed_the_beast.ftblib.lib.gui.WrappedIngredient;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfig;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfigValue;
import com.feed_the_beast.ftblib.lib.gui.misc.IConfigValueEditCallback;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.IconWithBorder;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.item.ItemEntryWithCount;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.net.MessageEditNBTResponse;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class GuiEditNBT extends GuiBase
{
	private static Icon getIcon(String name)
	{
		return Icon.getIcon("ftbutilities:textures/gui/nbt/" + name + ".png");
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

		public ButtonNBT(Panel panel, @Nullable ButtonNBTCollection b, String k)
		{
			super(panel);
			setPosAndSize(b == null ? 0 : b.posX + 10, 0, 10, 10);
			parent = b;
			key = k;
			setTitle(key);
		}

		public abstract NBTTagCompound copy();

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
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			if (selected == this)
			{
				Color4I.WHITE.withAlpha(33).draw(x, y, w, h);
			}

			IconWithBorder.BUTTON_ROUND_GRAY.draw(x + 1, y + 1, 8, 8);
			drawIcon(theme, x + 1, y + 1, 8, 8);
			theme.drawString(getTitle(), x + 11, y + 1);
		}
	}

	public class ButtonNBTPrimitive extends ButtonNBT implements IConfigValueEditCallback
	{
		private NBTBase nbt;

		public ButtonNBTPrimitive(Panel panel, ButtonNBTCollection b, String k, NBTBase n)
		{
			super(panel, b, k);
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
			setWidth(12 + getTheme().getStringWidth(key + ": " + title));
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
					new GuiEditConfigValue(key, new ConfigInt(((NBTPrimitive) nbt).getInt()), this).openGui();
					break;
				case Constants.NBT.TAG_LONG:
					new GuiEditConfigValue(key, new ConfigString(Long.toString(((NBTPrimitive) nbt).getLong())), this).openGui();
					break;
				case Constants.NBT.TAG_FLOAT:
				case Constants.NBT.TAG_DOUBLE:
				case Constants.NBT.TAG_ANY_NUMERIC:
					new GuiEditConfigValue(key, new ConfigDouble(((NBTPrimitive) nbt).getDouble()), this).openGui();
					break;
				case Constants.NBT.TAG_STRING:
					new GuiEditConfigValue(key, new ConfigString(((NBTTagString) nbt).getString()), this).openGui();
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

		@Override
		public NBTTagCompound copy()
		{
			NBTTagCompound n = new NBTTagCompound();
			n.setTag(key, nbt);
			return n;
		}
	}

	public abstract class ButtonNBTCollection extends ButtonNBT
	{
		public boolean collapsed;
		public final Map<String, ButtonNBT> children;
		public final Icon iconOpen, iconClosed;

		public ButtonNBTCollection(Panel panel, @Nullable ButtonNBTCollection b, String key, Icon open, Icon closed)
		{
			super(panel, b, key);
			iconOpen = open;
			iconClosed = closed;
			setCollapsed(false);
			setWidth(width + 2 + getTheme().getStringWidth(key));
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
			if (getMouseX() <= getX() + height)
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

		public void setCollapsedTree(boolean c)
		{
			setCollapsed(c);

			for (ButtonNBT button : children.values())
			{
				if (button instanceof ButtonNBTCollection)
				{
					((ButtonNBTCollection) button).setCollapsedTree(c);
				}
			}
		}

		public abstract NBTBase getTag(String k);

		public abstract void setTag(String k, @Nullable NBTBase base);
	}

	public class ButtonNBTMap extends ButtonNBTCollection
	{
		private NBTTagCompound map;
		private Icon hoverIcon = Icon.EMPTY;

		public ButtonNBTMap(Panel panel, @Nullable ButtonNBTCollection b, String key, NBTTagCompound m)
		{
			super(panel, b, key, NBT_MAP_OPEN, NBT_MAP_CLOSED);
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
			ItemEntryWithCount entry = new ItemEntryWithCount(map.copy());

			if (!entry.isEmpty())
			{
				hoverIcon = ItemIcon.getItemIcon(entry.getStack(false));
			}
			else
			{
				hoverIcon = Icon.EMPTY;
			}

			setWidth(12 + getTheme().getStringWidth(getTitle()) + (hoverIcon.isEmpty() ? 0 : 10));
		}

		@Override
		public void addMouseOverText(List<String> list)
		{
			if (this == buttonNBTRoot)
			{
				NBTTagList infoList = info.getTagList("text", Constants.NBT.TAG_STRING);

				if (infoList.tagCount() > 0)
				{
					list.add(I18n.format("gui.info") + ":");

					for (int i = 0; i < infoList.tagCount(); i++)
					{
						ITextComponent component = ITextComponent.Serializer.jsonToComponent(infoList.getStringTagAt(i));

						if (component != null)
						{
							list.add(component.getFormattedText());
						}
					}
				}
			}
		}

		@Override
		public void draw(Theme theme, int x, int y, int w, int h)
		{
			super.draw(theme, x, y, w, h);

			if (!hoverIcon.isEmpty())
			{
				hoverIcon.draw(x + 12 + theme.getStringWidth(getTitle()), y + 1, 8, 8);
			}
		}

		@Override
		@Nullable
		public Object getIngredientUnderMouse()
		{
			return new WrappedIngredient(hoverIcon.getIngredient()).tooltip();
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

		@Override
		public NBTTagCompound copy()
		{
			NBTTagCompound nbt = map.copy();

			if (this == buttonNBTRoot)
			{
				NBTTagList infoList1 = new NBTTagList();
				NBTTagList infoList0 = info.getTagList("text", Constants.NBT.TAG_STRING);

				if (infoList0.tagCount() > 0)
				{
					for (int i = 0; i < infoList0.tagCount(); i++)
					{
						ITextComponent component = ITextComponent.Serializer.jsonToComponent(infoList0.getStringTagAt(i));

						if (component != null)
						{
							infoList1.appendTag(new NBTTagString(component.getUnformattedText()));
						}
					}

					nbt.setTag("_", infoList1);
				}
			}

			return nbt;
		}
	}

	public class ButtonNBTList extends ButtonNBTCollection
	{
		private NBTTagList list;

		public ButtonNBTList(Panel panel, ButtonNBTCollection p, String key, NBTTagList l)
		{
			super(panel, p, key, NBT_LIST_OPEN, NBT_LIST_CLOSED);
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
			return list.isEmpty() || list.getTagType() == id;
		}

		@Override
		public NBTTagCompound copy()
		{
			NBTTagCompound n = new NBTTagCompound();
			n.setTag(key, list);
			return n;
		}
	}

	public class ButtonNBTByteArray extends ButtonNBTCollection
	{
		private ByteArrayList list;

		public ButtonNBTByteArray(Panel panel, ButtonNBTCollection p, String key, NBTTagByteArray l)
		{
			super(panel, p, key, NBT_BYTE_ARRAY_OPEN, NBT_BYTE_ARRAY_CLOSED);
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

		@Override
		public NBTTagCompound copy()
		{
			NBTTagCompound n = new NBTTagCompound();
			n.setTag(key, new NBTTagByteArray(list.toByteArray()));
			return n;
		}
	}

	public class ButtonNBTIntArray extends ButtonNBTCollection
	{
		private IntArrayList list;

		public ButtonNBTIntArray(Panel panel, ButtonNBTCollection p, String key, NBTTagIntArray l)
		{
			super(panel, p, key, NBT_INT_ARRAY_OPEN, NBT_INT_ARRAY_CLOSED);
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

		@Override
		public NBTTagCompound copy()
		{
			NBTTagCompound n = new NBTTagCompound();
			n.setTag(key, new NBTTagIntArray(list.toIntArray()));
			return n;
		}
	}

	private ButtonNBT getFrom(ButtonNBTCollection b, String key)
	{
		NBTBase nbt = b.getTag(key);

		switch (nbt.getId())
		{
			case Constants.NBT.TAG_COMPOUND:
				return new ButtonNBTMap(panelNbt, b, key, (NBTTagCompound) nbt);
			case Constants.NBT.TAG_LIST:
				return new ButtonNBTList(panelNbt, b, key, (NBTTagList) nbt);
			case Constants.NBT.TAG_BYTE_ARRAY:
				return new ButtonNBTByteArray(panelNbt, b, key, (NBTTagByteArray) nbt);
			case Constants.NBT.TAG_INT_ARRAY:
				return new ButtonNBTIntArray(panelNbt, b, key, (NBTTagIntArray) nbt);
			default:
				return new ButtonNBTPrimitive(panelNbt, b, key, nbt);
		}
	}

	public SimpleButton newTag(Panel panel, String t, Icon icon, Supplier<NBTBase> supplier)
	{
		return new SimpleButton(panel, t, icon, (gui, button) ->
		{
			if (selected instanceof ButtonNBTMap)
			{
				new GuiEditConfigValue("value", new ConfigString("", Pattern.compile("^.+$")), (value, set) ->
				{
					if (set && !value.getString().isEmpty())
					{
						((ButtonNBTCollection) selected).setTag(value.getString(), supplier.get());
						selected.updateChildren(false);
						panelNbt.refreshWidgets();
					}

					GuiEditNBT.this.openGui();
				}).openGui();
			}
			else if (selected instanceof ButtonNBTCollection)
			{
				((ButtonNBTCollection) selected).setTag("-1", supplier.get());
				selected.updateChildren(false);
				panelNbt.refreshWidgets();
			}
		})
		{
			@Override
			public void drawBackground(Theme theme, int x, int y, int w, int h)
			{
				IconWithBorder.BUTTON_ROUND_GRAY.draw(x, y, w, h);
			}
		};
	}

	private final NBTTagCompound info;
	private final ButtonNBTMap buttonNBTRoot;
	private ButtonNBT selected;
	public final Panel panelTopLeft, panelTopRight, panelNbt;
	public final PanelScrollBar scroll;
	private int shouldClose = 0;

	public GuiEditNBT(NBTTagCompound i, NBTTagCompound nbt)
	{
		info = i;

		panelTopLeft = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				add(new SimpleButton(this, I18n.format("selectServer.delete"), selected == buttonNBTRoot ? GuiIcons.REMOVE_GRAY : GuiIcons.REMOVE, (widget, button) ->
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

				add(new SimpleButton(this, I18n.format("gui.rename"), canRename ? GuiIcons.INFO : GuiIcons.INFO_GRAY, (gui, button) ->
				{
					if (canRename)
					{
						new GuiEditConfigValue(selected.key, new ConfigString(selected.key), (value, set) ->
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

							getGui().openGui();
						}).openGui();
					}
				}));

				if (selected instanceof ButtonNBTPrimitive)
				{
					add(new SimpleButton(this, I18n.format("selectServer.edit"), GuiIcons.FEATHER, (widget, button) -> ((ButtonNBTPrimitive) selected).edit()));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_COMPOUND))
				{
					add(newTag(this, "Compound", NBT_MAP, NBTTagCompound::new));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_LIST))
				{
					add(newTag(this, "List", NBT_LIST, NBTTagList::new));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_STRING))
				{
					add(newTag(this, "String", NBT_STRING, () -> new NBTTagString("")));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_BYTE))
				{
					add(newTag(this, "Byte", NBT_BYTE, () -> new NBTTagByte((byte) 0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_SHORT))
				{
					add(newTag(this, "Short", NBT_SHORT, () -> new NBTTagShort((short) 0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_INT))
				{
					add(newTag(this, "Int", NBT_INT, () -> new NBTTagInt(0)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_LONG))
				{
					add(newTag(this, "Long", NBT_LONG, () -> new NBTTagLong(0L)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_FLOAT))
				{
					add(newTag(this, "Float", NBT_FLOAT, () -> new NBTTagFloat(0F)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_DOUBLE))
				{
					add(newTag(this, "Double", NBT_DOUBLE, () -> new NBTTagDouble(0D)));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_BYTE_ARRAY))
				{
					add(newTag(this, "Byte Array", NBT_BYTE_ARRAY, () -> new NBTTagByteArray(new byte[0])));
				}

				if (selected.canCreateNew(Constants.NBT.TAG_INT_ARRAY))
				{
					add(newTag(this, "Int Array", NBT_INT_ARRAY, () -> new NBTTagIntArray(new int[0])));
				}
			}

			@Override
			public void alignWidgets()
			{
				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelTopLeft.setPosAndSize(0, 2, 0, 16);

		panelTopRight = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				add(new SimpleButton(this, I18n.format("gui.copy"), ItemIcon.getItemIcon(Items.PAPER), (widget, button) -> setClipboardString(selected.copy().toString())));

				add(new SimpleButton(this, I18n.format("gui.collapse_all"), GuiIcons.REMOVE, (widget, button) ->
				{
					for (Widget w : panelNbt.widgets)
					{
						if (w instanceof ButtonNBTCollection)
						{
							((ButtonNBTCollection) w).setCollapsed(true);
						}
					}

					scroll.setValue(0);
					panelNbt.refreshWidgets();
				}));

				add(new SimpleButton(this, I18n.format("gui.expand_all"), GuiIcons.ADD, (widget, button) ->
				{
					for (Widget w : panelNbt.widgets)
					{
						if (w instanceof ButtonNBTCollection)
						{
							((ButtonNBTCollection) w).setCollapsed(false);
						}
					}

					scroll.setValue(0);
					panelNbt.refreshWidgets();
				}));

				add(new SimpleButton(this, I18n.format("gui.cancel"), GuiIcons.CANCEL, (widget, button) ->
				{
					shouldClose = 2;
					widget.getGui().closeGui();
				}));

				add(new SimpleButton(this, I18n.format("gui.accept"), GuiIcons.ACCEPT, (widget, button) ->
				{
					shouldClose = 1;
					widget.getGui().closeGui();
				}));
			}

			@Override
			public void alignWidgets()
			{
				setWidth(align(new WidgetLayout.Horizontal(2, 4, 2)));
			}
		};

		panelNbt = new Panel(this)
		{
			@Override
			public void addWidgets()
			{
				add(buttonNBTRoot);
				buttonNBTRoot.addChildren();
			}

			@Override
			public void alignWidgets()
			{
				scroll.setMaxValue(align(WidgetLayout.VERTICAL) + 2);
			}
		};

		buttonNBTRoot = new ButtonNBTMap(panelNbt, null, info.hasKey("title") ? ITextComponent.Serializer.jsonToComponent(info.getString("title")).getFormattedText() : "ROOT", nbt);
		buttonNBTRoot.updateChildren(true);
		buttonNBTRoot.setCollapsedTree(true);
		buttonNBTRoot.setCollapsed(false);
		selected = buttonNBTRoot;

		scroll = new PanelScrollBar(this, panelNbt);
	}

	@Override
	public void addWidgets()
	{
		add(panelTopLeft);
		add(panelTopRight);
		add(panelNbt);
		add(scroll);
	}

	@Override
	public void alignWidgets()
	{
		panelTopRight.setPosAndSize(width - panelTopRight.width, 2, 0, 16);
		panelTopRight.alignWidgets();
		panelNbt.setPosAndSize(0, 21, width - scroll.width, height - 20);
		panelNbt.alignWidgets();
		scroll.setPosAndSize(width - scroll.width, 20, 16, panelNbt.height);
	}

	@Override
	public boolean onInit()
	{
		return setFullscreen();
	}

	@Override
	public void onClosed()
	{
		super.onClosed();

		if (shouldClose == 1)
		{
			if (NBTUtils.getSizeInBytes(buttonNBTRoot.map, false) >= 30000L)
			{
				FTBUtilities.LOGGER.error("NBT too large to send!");
			}
			else
			{
				new MessageEditNBTResponse(info, buttonNBTRoot.map).sendToServer();
			}
		}
	}

	@Override
	public void drawBackground(Theme theme, int x, int y, int w, int h)
	{
		GuiEditConfig.COLOR_BACKGROUND.draw(0, 0, w, 20);
	}

	@Override
	public Theme getTheme()
	{
		return GuiEditConfig.THEME;
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}
}