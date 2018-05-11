package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public final class BlockDimPosStorage implements INBTSerializable<NBTTagCompound>
{
	private final Map<String, BlockDimPos> map = new HashMap<>();
	private final List<String> names = new ArrayList<>();

	public Collection<String> list()
	{
		return names;
	}

	@Nullable
	public BlockDimPos get(String s)
	{
		return map.get(s);
	}

	public boolean set(String name, @Nullable BlockDimPos pos)
	{
		if (pos == null)
		{
			if (map.remove(name) != null)
			{
				names.remove(name);
				return true;
			}

			return false;
		}

		if (map.put(name, pos.copy()) == null)
		{
			names.add(name);
			names.sort(StringUtils.IGNORE_CASE_COMPARATOR);
		}

		return false;
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public int size()
	{
		return map.size();
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		for (Map.Entry<String, BlockDimPos> entry : map.entrySet())
		{
			nbt.setIntArray(entry.getKey(), entry.getValue().toIntArray());
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		map.clear();
		names.clear();

		for (String name : nbt.getKeySet())
		{
			BlockDimPos pos = BlockDimPos.fromIntArray(nbt.getIntArray(name));

			if (pos != null)
			{
				map.put(name, pos);
			}
		}

		names.addAll(map.keySet());
		names.sort(StringUtils.IGNORE_CASE_COMPARATOR);
	}
}