package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUTeamData;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Created by LatvianModder on 15.05.2016.
 */
public class FTBUCapabilities
{
    @CapabilityInject(FTBUPlayerData.class)
    public static Capability<FTBUPlayerData> FTBU_PLAYER_DATA = null;

    @CapabilityInject(FTBUWorldData.class)
    public static Capability<FTBUWorldData> FTBU_WORLD_DATA = null;

    @CapabilityInject(FTBUTeamData.class)
    public static Capability<FTBUTeamData> FTBU_TEAM_DATA = null;

    private static boolean enabled = false;

    public static void enable()
    {
        if(enabled)
        {
            return;
        }

        enabled = true;

        CapabilityManager.INSTANCE.register(FTBUPlayerData.class, new Capability.IStorage<FTBUPlayerData>()
        {
            @Override
            public NBTBase writeNBT(Capability<FTBUPlayerData> capability, FTBUPlayerData instance, EnumFacing side)
            {
                return instance.toMP().serializeNBT();
            }

            @Override
            public void readNBT(Capability<FTBUPlayerData> capability, FTBUPlayerData instance, EnumFacing side, NBTBase base)
            {
                instance.toMP().deserializeNBT((NBTTagCompound) base);
            }
        }, () -> new FTBUPlayerData() { });

        CapabilityManager.INSTANCE.register(FTBUWorldData.class, new Capability.IStorage<FTBUWorldData>()
        {
            @Override
            public NBTBase writeNBT(Capability<FTBUWorldData> capability, FTBUWorldData instance, EnumFacing side)
            {
                return instance.toMP().serializeNBT();
            }

            @Override
            public void readNBT(Capability<FTBUWorldData> capability, FTBUWorldData instance, EnumFacing side, NBTBase base)
            {
                instance.toMP().deserializeNBT((NBTTagCompound) base);
            }
        }, () -> new FTBUWorldData() { });

        CapabilityManager.INSTANCE.register(FTBUTeamData.class, new Capability.IStorage<FTBUTeamData>()
        {
            @Override
            public NBTBase writeNBT(Capability<FTBUTeamData> capability, FTBUTeamData instance, EnumFacing side)
            {
                return instance.toMP().serializeNBT();
            }

            @Override
            public void readNBT(Capability<FTBUTeamData> capability, FTBUTeamData instance, EnumFacing side, NBTBase base)
            {
                instance.toMP().deserializeNBT((NBTTagCompound) base);
            }
        }, () -> new FTBUTeamData() { });
    }
}