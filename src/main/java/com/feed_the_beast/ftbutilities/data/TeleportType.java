package com.feed_the_beast.ftbutilities.data;

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
	private String warmup;
	private String cooldown;

	TeleportType(String node, @Nullable String warmup, @Nullable String cooldown)
	{
		this.permission = node;
		this.warmup = warmup;
		this.cooldown = cooldown;
	}

	public String getPermission()
	{
		return this.permission;
	}

	public String getWarmupPermission()
	{
		return this.warmup;
	}

	public String getCooldownPermission()
	{
		return this.cooldown;
	}
}
