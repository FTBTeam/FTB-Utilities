package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;

public enum TeleportType
{
	HOME(FTBUtilitiesPermissions.HOMES_BACK),
	WARP(FTBUtilitiesPermissions.WARPS_BACK),
	BACK(null),
	SPAWN(FTBUtilitiesPermissions.SPAWN_BACK),
	TPA(FTBUtilitiesPermissions.TPA_BACK),
	RTP(FTBUtilitiesPermissions.RTP_BACK),
	RESPAWN(FTBUtilitiesPermissions.RESPAWN_BACK);

	private Node permission;

	TeleportType(Node node) {
		this.permission = node;
	}

	public Node getPermissionNode() {
		return this.permission;
	}
}
