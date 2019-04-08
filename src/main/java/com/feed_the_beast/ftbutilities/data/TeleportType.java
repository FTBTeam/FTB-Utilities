package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;

import javax.annotation.Nullable;

public enum TeleportType
{
	HOME(FTBUtilitiesPermissions.HOMES_BACK, FTBUtilitiesPermissions.HOMES_WARMUP, FTBUtilitiesPermissions.HOMES_COOLDOWN),
	WARP(FTBUtilitiesPermissions.WARPS_BACK, FTBUtilitiesPermissions.WARPS_WARMUP, FTBUtilitiesPermissions.WARPS_COOLDOWN),
	BACK(FTBUtilitiesPermissions.BACK_BACK, FTBUtilitiesPermissions.BACK_WARMUP, FTBUtilitiesPermissions.BACK_COOLDOWN),
	SPAWN(FTBUtilitiesPermissions.SPAWN_BACK, FTBUtilitiesPermissions.SPAWN_WARMUP, FTBUtilitiesPermissions.SPAWN_COOLDOWN),
	TPA(FTBUtilitiesPermissions.TPA_BACK, FTBUtilitiesPermissions.TPA_WARMUP, FTBUtilitiesPermissions.TPA_COOLDOWN),
	RTP(FTBUtilitiesPermissions.RTP_BACK, FTBUtilitiesPermissions.RTP_WARMUP, FTBUtilitiesPermissions.RTP_COOLDOWN),
	RESPAWN(FTBUtilitiesPermissions.RESPAWN_BACK, null, null);

	private String permission;
	private Node warmup;
	private Node cooldown;

	TeleportType(String node, @Nullable Node warmup, @Nullable Node cooldown)
	{
		this.permission = node;
		this.warmup = warmup;
		this.cooldown = cooldown;
	}

	public String getPermission()
	{
		return this.permission;
	}

	public Node getWarmupPermission()
	{
		return this.warmup;
	}

	public Node getCooldownPermission()
	{
		return this.cooldown;
	}
}
