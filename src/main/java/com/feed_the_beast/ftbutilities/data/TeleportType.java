package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;

public enum TeleportType
{
	HOME(FTBUtilitiesPermissions.HOMES_BACK),
	WARP(FTBUtilitiesPermissions.WARPS_BACK),
	BACK(FTBUtilitiesPermissions.BACK_BACK),
	SPAWN(FTBUtilitiesPermissions.SPAWN_BACK),
	TPA(FTBUtilitiesPermissions.TPA_BACK),
	RTP(FTBUtilitiesPermissions.RTP_BACK),
	RESPAWN(FTBUtilitiesPermissions.RESPAWN_BACK);

	private String permission;

	TeleportType(String node) {
		this.permission = node;
	}

	public String getPermission() {
		return this.permission;
	}
}
