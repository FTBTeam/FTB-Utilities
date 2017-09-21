package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.guide.GuidePage;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api.events.registry.RegisterCustomPermissionPrefixesEvent;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.net.FTBUNetHandler;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.HashSet;

public class FTBUCommon
{
	public static final Collection<NodeEntry> CUSTOM_PERM_PREFIX_REGISTRY = new HashSet<>();

	public void preInit()
	{
		FTBUtilitiesAPI.API = new FTBUtilitiesAPI_Impl();
		FTBUNetHandler.init();

		new RegisterCustomPermissionPrefixesEvent(CUSTOM_PERM_PREFIX_REGISTRY::add).post();
	}

	public void init()
	{
	}

	public void postInit()
	{
		FTBUConfig.sync();
		ClaimedChunks.loadReflection();
	}

	public void onReloadedClient()
	{
	}

	public void openNBTEditorGui(NBTTagCompound info, NBTTagCompound mainNbt)
	{
	}

	public void displayGuide(GuidePage page)
	{
	}
}