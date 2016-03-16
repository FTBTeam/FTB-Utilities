package ftb.utils.world;

import ftb.lib.PrivacyLevel;
import ftb.lib.api.*;
import ftb.utils.FTBUFinals;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.*;

/**
 * Created by LatvianModder on 23.02.2016.
 */
@SideOnly(Side.CLIENT)
public class FTBUPlayerDataSP extends FTBUPlayerData
{
	public static FTBUPlayerDataSP get(ForgePlayer p)
	{ return (FTBUPlayerDataSP) p.getData(FTBUFinals.MOD_ID); }
	
	public short claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;
	
	public FTBUPlayerDataSP(ForgePlayerSP p)
	{
		super(FTBUFinals.MOD_ID, p);
	}
	
	public void readFromNet(NBTTagCompound tag, boolean self)
	{
		flags = tag.getByte("F");
		blocks = PrivacyLevel.VALUES_3[tag.getByte("B")];
		
		if(self)
		{
			claimedChunks = tag.getShort("CC");
			loadedChunks = tag.getShort("LC");
			maxClaimedChunks = tag.getShort("MCC");
			maxLoadedChunks = tag.getShort("MLC");
		}
	}
}
