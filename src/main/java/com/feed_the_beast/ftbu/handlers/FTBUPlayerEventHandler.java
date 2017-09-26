package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftbl.api.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftbl.api.player.ForgePlayerLoggedOutEvent;
import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.InvUtils;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.util.Badges;
import com.feed_the_beast.ftbu.util.FTBUPlayerData;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import com.google.common.base.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUPlayerEventHandler
{
	@SubscribeEvent
	public static void onLoggedIn(ForgePlayerLoggedInEvent event)
	{
		if (event.getPlayer().isFake())
		{
			return;
		}

		EntityPlayerMP ep = event.getPlayer().getPlayer();

		if (event.isFirstLogin())
		{
			if (FTBUConfig.login.enable_starting_items)
			{
				for (ItemStack stack : FTBUConfig.login.getStartingItems())
				{
					InvUtils.giveItem(ep, stack.copy());
				}
			}
		}

		if (FTBUConfig.login.enable_motd)
		{
			for (ITextComponent t : FTBUConfig.login.getMOTD())
			{
				ep.sendMessage(t);
			}
		}

		ClaimedChunks.INSTANCE.markDirty();
	}

	@SubscribeEvent
	public static void onLoggedOut(ForgePlayerLoggedOutEvent event)
	{
		ClaimedChunks.INSTANCE.markDirty();
		Badges.update(event.getPlayer().getId());
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event)
	{
		if (event.getEntity() instanceof EntityPlayerMP)
		{
			FTBUPlayerData.get(FTBLibAPI.API.getUniverse().getPlayer(event.getEntity())).lastDeath = new BlockDimPos(event.getEntity());
		}
	}

	@SubscribeEvent
	public static void getSettings(ForgePlayerConfigEvent event)
	{
		FTBUPlayerData.get(event.getPlayer()).addConfig(event);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onChunkChanged(EntityEvent.EnteringChunk event)
	{
		if (event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
		IForgePlayer p = FTBLibAPI.API.getUniverse().getPlayer(player.getGameProfile());

		if (p == null || p.isFake())
		{
			return;
		}

		FTBUPlayerData.get(p).lastSafePos = new BlockDimPos(player);
		updateChunkMessage(player, new ChunkDimPos(event.getNewChunkX(), event.getNewChunkZ(), player.dimension));
	}

	public static void updateChunkMessage(EntityPlayer player, ChunkDimPos pos)
	{
		ClaimedChunk chunk = ClaimedChunks.INSTANCE.getChunk(pos);
		IForgeTeam team = chunk == null ? null : chunk.getTeam();

		FTBUPlayerData data = FTBUPlayerData.get(FTBLibAPI.API.getUniverse().getPlayer(player));

		if (!Objects.equal(data.lastChunkTeam, team))
		{
			data.lastChunkTeam = team;

			if (team != null)
			{
				Notification notification = Notification.of(FTBUNotifications.WILDERNESS.getId());
				notification.addLine(StringUtils.color(new TextComponentString(team.getTitle()), team.getColor().getTextFormatting()));

				if (!team.getDesc().isEmpty())
				{
					notification.addLine(StringUtils.italic(new TextComponentString(team.getDesc()), true));
				}

				notification.send(player);
			}
			else
			{
				FTBUNotifications.WILDERNESS.send(player);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPlayerAttacked(LivingAttackEvent event)
	{
		if (event.getEntity().world.isRemote || event.getEntity().dimension != 0 || !(event.getEntity() instanceof EntityPlayerMP) || event.getEntity() instanceof FakePlayer)
		{
			return;
		}

		Entity entity = event.getSource().getTrueSource();

		if (entity != null && (entity instanceof EntityPlayerMP || entity instanceof IMob))
		{
			if (entity instanceof FakePlayer)
			{
				return;
			}
			/*else if(entity instanceof EntityPlayerMP && PermissionAPI.hasPermission(((EntityPlayerMP) entity).getGameProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(entity)))
			{
                return;
            }*/

			if ((FTBUConfig.world.safe_spawn && FTBUUniverseData.isInSpawn(new ChunkDimPos(event.getEntity()))))
			{
				event.setCanceled(true);
			}
			/*else
			{
				ClaimedChunk c = Claims.getMode(dim, cx, cz);
				if(c != null && c.claims.settings.isSafe()) event.setCanceled(true);
			}*/
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if (event.getEntityPlayer() instanceof EntityPlayerMP && !ClaimedChunks.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.INTERACT))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if (event.getEntityPlayer() instanceof EntityPlayerMP && !ClaimedChunks.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.ITEM))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockBreak(BlockEvent.BreakEvent event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunks.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getPlayer(), EnumHand.MAIN_HAND, new BlockPosContainer(event.getWorld(), event.getPos(), event.getState()), BlockInteractionType.EDIT))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockPlace(BlockEvent.PlaceEvent event)
	{
		if (event.getPlayer() instanceof EntityPlayerMP && !ClaimedChunks.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getPlayer(), EnumHand.MAIN_HAND, new BlockPosContainer(event.getWorld(), event.getPos(), event.getPlacedBlock()), BlockInteractionType.EDIT))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event)
	{
		if (event.getEntityPlayer() instanceof EntityPlayerMP && !ClaimedChunks.INSTANCE.canPlayerInteract((EntityPlayerMP) event.getEntityPlayer(), event.getHand(), new BlockPosContainer(event), BlockInteractionType.EDIT))
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
}