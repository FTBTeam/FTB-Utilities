package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import latmod.lib.Bits;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public abstract class FTBUPlayerData implements ICapabilityProvider
{
	public static final byte RENDER_BADGE = 1;
	public static final byte CHAT_LINKS = 2;
	public static final byte EXPLOSIONS = 3;
	public static final byte FAKE_PLAYERS = 4;
	
	public final ForgePlayer player;
	protected byte flags = 0;
	public PrivacyLevel blocks;
	
	public FTBUPlayerData(ForgePlayer p)
	{
		player = p;
		blocks = PrivacyLevel.FRIENDS;
	}
	
	public boolean getFlag(byte f)
	{ return Bits.getBit(flags, f); }
	
	public void setFlag(byte f, boolean b)
	{ flags = Bits.setBit(flags, f, b); }
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == FTBUCapabilities.FTBU_PLAYER_DATA;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return (T) this;
	}
	
	public void writeSyncData(NBTTagCompound tag, boolean self)
	{
	}
	
	public void readSyncData(NBTTagCompound tag, boolean self)
	{
	}
}