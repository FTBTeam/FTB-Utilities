package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.InvUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesPlayerEventHandler
{
	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event)
	{
		if (event.getEntity() instanceof EntityPlayerMP)
		{
			FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(Universe.get().getPlayer(event.getEntity()));
			data.setLastDeath(new BlockDimPos(event.getEntity()));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChunkChanged(EntityEvent.EnteringChunk event)
	{
		if (event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityPlayerMP) || !Universe.loaded())
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
		player.markPlayerActive();
		ForgePlayer p = Universe.get().getPlayer(player.getGameProfile());

		if (p == null || p.isFake())
		{
			return;
		}

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(p);
		data.setLastSafePos(new BlockDimPos(player));
		FTBUtilitiesNotifications.updateChunkMessage(data, player, new ChunkDimPos(event.getNewChunkX(), event.getNewChunkZ(), player.dimension));
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onEntityAttacked(AttackEntityEvent event)
	{
		if (!ClaimedChunks.canAttackEntity(event.getEntityPlayer(), event.getTarget()))
		{
			InvUtils.forceUpdate(event.getEntityPlayer());
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if (ClaimedChunks.blockBlockInteractions(event.getEntityPlayer(), event.getPos(), null))
		{
			InvUtils.forceUpdate(event.getEntityPlayer());
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if (ClaimedChunks.blockItemUse(event.getEntityPlayer(), event.getHand(), event.getPos()))
		{
			InvUtils.forceUpdate(event.getEntityPlayer());
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockBreak(BlockEvent.BreakEvent event)
	{
		if (ClaimedChunks.blockBlockEditing(event.getPlayer(), event.getPos(), event.getState()))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockPlace(BlockEvent.PlaceEvent event)
	{
		if (ClaimedChunks.blockBlockEditing(event.getPlayer(), event.getPos(), event.getPlacedBlock()))
		{
			InvUtils.forceUpdate(event.getPlayer());
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		if (ClaimedChunks.blockBlockEditing(event.getEntityPlayer(), event.getPos(), null))
		{
			event.setCanceled(true);
		}
	}

    /*
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void onItemPickup(EntityItemPickupEvent event)
    {
    }
    */

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onNameFormat(PlayerEvent.NameFormat event)
	{
		if (FTBUtilitiesConfig.commands.nick && Universe.loaded() && event.getEntityPlayer() instanceof EntityPlayerMP)
		{
			ForgePlayer p = Universe.get().getPlayer(event.getEntityPlayer().getGameProfile());

			if (p != null)
			{
				FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(p);

				if (!data.getNickname().isEmpty() && PermissionAPI.hasPermission(event.getEntityPlayer(), FTBUtilitiesPermissions.CHAT_NICKNAME_SET))
				{
					String name = data.getNickname().replace('&', StringUtils.FORMATTING_CHAR);

					if (!p.hasPermission(FTBUtilitiesPermissions.CHAT_NICKNAME_COLORS))
					{
						name = TextFormatting.getTextWithoutFormattingCodes(name);
					}
					else if (name.indexOf(StringUtils.FORMATTING_CHAR) != -1)
					{
						name += TextFormatting.RESET;
					}

					if (FTBUtilitiesConfig.chat.add_nickname_tilde)
					{
						name = "~" + name;
					}

					event.setDisplayname(name);
				}
			}
		}
	}

	private static String getStateName(IBlockState state)
	{
		if (state == state.getBlock().getDefaultState())
		{
			return state.getBlock().getRegistryName().toString();
		}

		return state.toString();
	}

	private static String getDim(EntityPlayer player)
	{
		return ServerUtils.getDimensionName(player.dimension).getUnformattedText();
	}

	private static String getPos(BlockPos pos)
	{
		return String.format("[%d, %d, %d]", pos.getX(), pos.getY(), pos.getZ());
	}

	@SubscribeEvent
	public static void onBlockBreakLog(BlockEvent.BreakEvent event)
	{
		EntityPlayer player = event.getPlayer();

		if (FTBUtilitiesConfig.world.logging.block_broken && player instanceof EntityPlayerMP && FTBUtilitiesConfig.world.logging.log((EntityPlayerMP) player))
		{
			FTBUtilitiesUniverseData.worldLog(String.format("%s broke %s at %s in %s", player.getName(), getStateName(event.getState()), getPos(event.getPos()), getDim(player)));
		}
	}

	@SubscribeEvent
	public static void onBlockPlaceLog(BlockEvent.PlaceEvent event)
	{
		EntityPlayer player = event.getPlayer();

		if (FTBUtilitiesConfig.world.logging.block_placed && player instanceof EntityPlayerMP && FTBUtilitiesConfig.world.logging.log((EntityPlayerMP) player))
		{
			FTBUtilitiesUniverseData.worldLog(String.format("%s placed %s at %s in %s", player.getName(), getStateName(event.getState()), getPos(event.getPos()), getDim(player)));
		}
	}

	@SubscribeEvent
	public static void onRightClickItemLog(PlayerInteractEvent.RightClickItem event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if (FTBUtilitiesConfig.world.logging.item_clicked_in_air && player instanceof EntityPlayerMP && FTBUtilitiesConfig.world.logging.log((EntityPlayerMP) player))
		{
			FTBUtilitiesUniverseData.worldLog(String.format("%s clicked %s in air at %s in %s", player.getName(), event.getItemStack().getItem().getRegistryName(), getPos(event.getPos()), getDim(player)));
		}
	}
}