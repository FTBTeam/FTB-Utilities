package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamSettingsEvent;
import com.feed_the_beast.ftbl.lib.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyEnum;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class FTBUTeamData implements INBTSerializable<NBTBase>
{
    public final PropertyEnum<EnumTeamPrivacyLevel> editBlocks = new PropertyEnum<>(EnumTeamPrivacyLevel.NAME_MAP, EnumTeamPrivacyLevel.ALLIES);
    public final PropertyEnum<EnumTeamPrivacyLevel> interactWithBlocks = new PropertyEnum<>(EnumTeamPrivacyLevel.NAME_MAP, EnumTeamPrivacyLevel.ALLIES);
    public final PropertyBool explosions = new PropertyBool(false);
    public final PropertyBool fakePlayers = new PropertyBool(true);

    @Nullable
    public static FTBUTeamData get(IForgeTeam t)
    {
        return (FTBUTeamData) t.getData(FTBLibIntegration.FTBU_DATA);
    }

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("Explosions", explosions.getBoolean());
        nbt.setBoolean("FakePlayers", fakePlayers.getBoolean());
        nbt.setString("EditBlocks", editBlocks.getString());
        nbt.setString("InteractWithBlocks", interactWithBlocks.getString());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTBase nbt0)
    {
        if(nbt0 instanceof NBTTagCompound)
        {
            NBTTagCompound nbt = (NBTTagCompound) nbt0;
            explosions.setBoolean(nbt.getBoolean("Explosions"));
            fakePlayers.setBoolean(nbt.getBoolean("FakePlayers"));

            if(nbt.hasKey("EditBlocks"))
            {
                editBlocks.setValueFromString(nbt.getString("EditBlocks"), false);
            }

            if(nbt.hasKey("InteractWithBlocks"))
            {
                interactWithBlocks.setValueFromString(nbt.getString("InteractWithBlocks"), false);
            }
        }
        else
        {
            int flags = ((NBTPrimitive) nbt0).getInt();
            explosions.setBoolean(!Bits.getFlag(flags, 1));
            fakePlayers.setBoolean(Bits.getFlag(flags, 2));
        }
    }

    public void addConfig(ForgeTeamSettingsEvent event)
    {
        String group = FTBUFinals.MOD_ID;
        event.add(group, "explosions", explosions);
        event.add(group, "fake_players", fakePlayers);
        event.add(group, "blocks_edit", editBlocks);
        event.add(group, "blocks_interact", interactWithBlocks);
    }
}