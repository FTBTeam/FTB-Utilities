package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.latmod.lib.PixelBuffer;
import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ThreadReloadArea extends Thread
{
    public static final PixelBuffer pixels = new PixelBuffer(GuiClaimChunks.tiles_tex * 16, GuiClaimChunks.tiles_tex * 16);
    private static final Map<IBlockState, Integer> colorCache = new HashMap<>();
    private static BlockPos.MutableBlockPos currentBlockPos = new BlockPos.MutableBlockPos(0, 0, 0);
    public final World worldObj;
    public final GuiClaimChunks gui;

    public ThreadReloadArea(World w, GuiClaimChunks m)
    {
        super("LM_MapReloader");
        setDaemon(true);
        worldObj = w;
        gui = m;
    }

    private static int getBlockColor(IBlockState state)
    {
        Integer col = colorCache.get(state);

        if(col == null)
        {
            col = 0xFF000000 | getBlockColor0(state);
            colorCache.put(state, col);
        }

        return col;
    }

    private static int getBlockColor0(IBlockState state)
    {
        Block b = state.getBlock();

        if(b == Blocks.SANDSTONE)
        {
            return MapColor.SAND.colorValue;
        }
        else if(b == Blocks.FIRE)
        {
            return MapColor.RED.colorValue;
        }
        else if(b == Blocks.YELLOW_FLOWER)
        {
            return MapColor.YELLOW.colorValue;
        }
        else if(b == Blocks.LAVA)
        {
            return MapColor.ADOBE.colorValue;
        }
        else if(b == Blocks.END_STONE)
        {
            return MapColor.SAND.colorValue;
        }
        else if(b == Blocks.OBSIDIAN)
        {
            return 0x150047;
        }
        else if(b == Blocks.GRAVEL)
        {
            return 0x8D979B;
        }
        else if(b == Blocks.GRASS)
        {
            return 0x74BC7C;
        }
        //else if(b.getMaterial(state) == Material.water)
        //	return LMColorUtils.multiply(MapColor.waterColor.colorValue, b.colorMultiplier(worldObj, pos), 200);
        else if(b == Blocks.RED_FLOWER)
        {
            switch(state.getValue(Blocks.RED_FLOWER.getTypeProperty()))
            {
                case DANDELION:
                    return MapColor.YELLOW.colorValue;
                case POPPY:
                    return MapColor.RED.colorValue;
                case BLUE_ORCHID:
                    return MapColor.LIGHT_BLUE.colorValue;
                case ALLIUM:
                    return MapColor.MAGENTA.colorValue;
                case HOUSTONIA:
                    return MapColor.SILVER.colorValue;
                case RED_TULIP:
                    return MapColor.RED.colorValue;
                case ORANGE_TULIP:
                    return MapColor.ADOBE.colorValue;
                case WHITE_TULIP:
                    return MapColor.SNOW.colorValue;
                case PINK_TULIP:
                    return MapColor.PINK.colorValue;
                case OXEYE_DAISY:
                    return MapColor.SILVER.colorValue;
            }
        }
        else if(b == Blocks.PLANKS)
        {
            switch(state.getValue(BlockPlanks.VARIANT))
            {
                case OAK:
                    return 0xC69849;
                case SPRUCE:
                    return 0x7C5E2E;
                case BIRCH:
                    return 0xF2E093;
                case JUNGLE:
                    return 0xC67653;
                case ACACIA:
                    return 0xE07F3E;
                case DARK_OAK:
                    return 0x512D14;
            }
        }

        //if(b == Blocks.leaves || b == Blocks.vine || b == Blocks.waterlily)
        //	return LMColorUtils.addBrightness(b.colorMultiplier(worldObj, pos), -40);
        //else if(b == Blocks.grass && state.getValue(BlockGrass.SNOWY))
        //	return LMColorUtils.addBrightness(b.colorMultiplier(worldObj, pos), -15);

        return state.getMapColor().colorValue;
    }

    @Override
    public void run()
    {
        Arrays.fill(pixels.pixels, 0);
        Chunk chunk;
        int cx, cz, x, z, wx, wz, by, color;

        int startY = Minecraft.getMinecraft().thePlayer.getPosition().getY();

        try
        {
            for(cz = 0; cz < GuiClaimChunks.tiles_gui; cz++)
            {
                for(cx = 0; cx < GuiClaimChunks.tiles_gui; cx++)
                {
                    chunk = worldObj.getChunkProvider().getLoadedChunk(gui.startX + cx, gui.startZ + cz);

                    if(chunk != null)
                    {
                        x = (gui.startX + cx) * 16;
                        z = (gui.startZ + cz) * 16;

                        for(wz = 0; wz < 16; wz++)
                        {
                            for(wx = 0; wx < 16; wx++)
                            {
                                for(by = Math.max(255, chunk.getTopFilledSegment() + 15); by > 0; --by)
                                {
                                    IBlockState state = chunk.getBlockState(wx, by, wz);

                                    currentBlockPos.setPos(x + wx, by, z + wz);

                                    if(state.getBlock() != Blocks.TALLGRASS && !worldObj.isAirBlock(currentBlockPos))
                                    {
                                        color = getBlockColor(state);
                                        color = LMColorUtils.addBrightness(color, MathHelperLM.clampInt(by - startY, -30, 30) * 5);
                                        pixels.setRGB(cx * 16 + wx, cz * 16 + wz, color);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    GuiClaimChunks.pixelBuffer = FTBLibClient.toByteBuffer(pixels.pixels, false);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        GuiClaimChunks.pixelBuffer = FTBLibClient.toByteBuffer(pixels.pixels, false);
    }
}