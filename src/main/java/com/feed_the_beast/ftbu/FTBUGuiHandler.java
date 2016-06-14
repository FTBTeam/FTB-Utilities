package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.client.gui.ContainerEmpty;
import com.feed_the_beast.ftbl.api.client.gui.LMGuiHandler;
import com.feed_the_beast.ftbu.client.gui.GuiClaimChunks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class FTBUGuiHandler extends LMGuiHandler
{
    public static final FTBUGuiHandler instance = new FTBUGuiHandler(FTBUFinals.MOD_ID);

    public static final int ADMIN_CLAIMS = 1;

    public FTBUGuiHandler(String s)
    {
        super(s);
    }

    @Override
    public Container getContainer(@Nonnull EntityPlayer ep, int id, NBTTagCompound data)
    {
        return new ContainerEmpty(ep, null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(@Nonnull EntityPlayer ep, int id, NBTTagCompound data)
    {
        if(id == ADMIN_CLAIMS)
        {
            return new GuiClaimChunks(data.getLong("T")).getWrapper();
        }

        return null;
    }
}