package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerSettingsEvent;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 11.02.2016.
 */
public class FTBUPlayerData implements INBTSerializable<NBTBase>
{
    public final PropertyBool renderBadge = new PropertyBool(true);
    public final PropertyBool chatLinks = new PropertyBool(true);
    public final PropertyBool disableGlobalBadge = new PropertyBool(false);

    public BlockDimPos lastDeath, lastSafePos;
    public IForgePlayer lastChunkOwner;
    private Map<String, BlockDimPos> homes;

    @Nullable
    public static FTBUPlayerData get(IForgePlayer p)
    {
        return (FTBUPlayerData) p.getData(FTBLibIntegration.FTBU_DATA);
    }

    @Override
    public NBTBase serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setBoolean("RenderBadge", renderBadge.getBoolean());
        nbt.setBoolean("ChatLinks", chatLinks.getBoolean());
        nbt.setBoolean("DisableGlobalBadges", disableGlobalBadge.getBoolean());

        if(homes != null && !homes.isEmpty())
        {
            NBTTagCompound tag1 = new NBTTagCompound();

            for(Map.Entry<String, BlockDimPos> e : homes.entrySet())
            {
                tag1.setIntArray(e.getKey(), e.getValue().toIntArray());
            }

            nbt.setTag("Homes", tag1);
        }

        if(lastDeath != null)
        {
            nbt.setIntArray("LastDeath", lastDeath.toIntArray());
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTBase nbt0)
    {
        NBTTagCompound nbt = (NBTTagCompound) nbt0;

        if(nbt.hasKey("Homes"))
        {
            homes = new HashMap<>();

            NBTTagCompound tag1 = (NBTTagCompound) nbt.getTag("Homes");

            if(tag1 != null && !tag1.hasNoTags())
            {
                for(String s1 : tag1.getKeySet())
                {
                    setHome(s1.toLowerCase(), new BlockDimPos(tag1.getIntArray(s1)));
                }
            }
        }
        else
        {
            homes = null;
        }

        lastDeath = null;
        if(nbt.hasKey("LastDeath"))
        {
            int[] ai = nbt.getIntArray("LastDeath");
            lastDeath = (ai.length == 4) ? new BlockDimPos(ai) : null;
        }
    }

    public Collection<String> listHomes()
    {
        if(homes == null || homes.isEmpty())
        {
            return Collections.emptySet();
        }

        return homes.keySet();
    }

    @Nullable
    public BlockDimPos getHome(String s)
    {
        return homes == null ? null : homes.get(s.toLowerCase());
    }

    public boolean setHome(String s, @Nullable BlockDimPos pos)
    {
        if(pos == null)
        {
            return homes != null && homes.remove(s) != null;
        }

        if(homes == null)
        {
            homes = new HashMap<>();
        }

        return homes.put(s, pos.copy()) == null;
    }

    public int homesSize()
    {
        return homes == null ? 0 : homes.size();
    }

    public void addConfig(ForgePlayerSettingsEvent event)
    {
        String group = FTBUFinals.MOD_ID;
        event.add(group, "render_badge", renderBadge);
        event.add(group, "chat_links", chatLinks);
        event.add(group, "disable_global_badge", disableGlobalBadge);
    }
}