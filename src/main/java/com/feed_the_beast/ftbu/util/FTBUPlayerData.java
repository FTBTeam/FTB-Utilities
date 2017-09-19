package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftbl.lib.config.ConfigBoolean;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.handlers.FTBLibIntegration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public class FTBUPlayerData implements INBTSerializable<NBTTagCompound>
{
	public final ConfigBoolean renderBadge = new ConfigBoolean(true);
	public final ConfigBoolean chatLinks = new ConfigBoolean(true);
	public final ConfigBoolean disableGlobalBadge = new ConfigBoolean(false);

	public final IForgePlayer player;
	public BlockDimPos lastDeath, lastSafePos;
	public IForgePlayer lastChunkOwner;
	public final BlockDimPosStorage homes;

	public FTBUPlayerData(IForgePlayer p)
	{
		player = p;
		homes = new BlockDimPosStorage();
	}

	public static FTBUPlayerData get(IForgePlayer player)
	{
		return player.getData().get(FTBLibIntegration.FTBU_DATA);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setBoolean("RenderBadge", renderBadge.getBoolean());
		nbt.setBoolean("ChatLinks", chatLinks.getBoolean());
		nbt.setBoolean("DisableGlobalBadges", disableGlobalBadge.getBoolean());

		if (!homes.isEmpty())
		{
			nbt.setTag("Homes", homes.serializeNBT());
		}

		if (lastDeath != null)
		{
			nbt.setIntArray("LastDeath", lastDeath.toIntArray());
		}

		Collection<ClaimedChunk> claimedChunks = ClaimedChunks.INSTANCE.getChunks(player);

		if (!claimedChunks.isEmpty())
		{
			NBTTagList list = new NBTTagList();

			for (IClaimedChunk chunk : claimedChunks)
			{
				ChunkDimPos pos = chunk.getPos();

				int flags = 0;

				for (IChunkUpgrade upgrade : FTBUCommon.CHUNK_UPGRADES)
				{
					if (upgrade != null && chunk.hasUpgrade(upgrade))
					{
						flags |= (1 << upgrade.getId());
					}
				}

				int ai[] = flags != 0 ? new int[4] : new int[3];
				ai[0] = pos.dim;
				ai[1] = pos.posX;
				ai[2] = pos.posZ;

				if (flags != 0)
				{
					ai[3] = flags;
				}

				list.appendTag(new NBTTagIntArray(ai));
			}

			nbt.setTag("ClaimedChunks", list);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}

		renderBadge.setBoolean(nbt.getBoolean("RenderBadge"));
		chatLinks.setBoolean(nbt.getBoolean("ChatLinks"));
		disableGlobalBadge.setBoolean(nbt.getBoolean("DisableGlobalBadges"));
		homes.deserializeNBT(nbt.getCompoundTag("Homes"));

		lastDeath = null;
		if (nbt.hasKey("LastDeath"))
		{
			int[] ai = nbt.getIntArray("LastDeath");
			lastDeath = (ai.length == 4) ? new BlockDimPos(ai) : null;
		}

		if (nbt.hasKey("ClaimedChunks"))
		{
			NBTTagList list = (NBTTagList) nbt.getTag("ClaimedChunks");

			for (int i = 0; i < list.tagCount(); i++)
			{
				int[] ai = list.getIntArrayAt(i);

				if (ai.length >= 3)
				{
					ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(ai[1], ai[2], ai[0]), player, ai.length >= 4 ? ai[3] : 0);
					ClaimedChunks.INSTANCE.setChunk(chunk.getPos(), chunk);
				}
			}
		}
	}

	public void addConfig(ForgePlayerConfigEvent event)
	{
		String group = FTBUFinals.MOD_ID;
		event.getConfig().setGroupName(group, new TextComponentString(FTBUFinals.MOD_NAME));
		event.getConfig().add(group, "render_badge", renderBadge);
		event.getConfig().add(group, "chat_links", chatLinks);
		event.getConfig().add(group, "disable_global_badge", disableGlobalBadge);
	}
}