package latmod.core.cmd.mod;

import java.io.*;

import latmod.core.*;
import latmod.core.LMGamerules.RuleID;
import latmod.core.MathHelper;
import latmod.core.cmd.CommandLevel;
import latmod.core.mod.LCEventHandler;
import latmod.core.net.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.*;
import net.minecraft.event.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import baubles.api.BaublesApi;

public class CmdLatCoreAdmin extends CommandBaseLC
{
	public CmdLatCoreAdmin(CommandLevel l)
	{ super("latcoreadmin", l); }
	
	public void printHelp(ICommandSender ics)
	{
	}
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "killblock", "gamerule", "player", "reloadPD", "updateSkins" }; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		
		if(i == 2 && isArg(args, 0, "player"))
			return new String[] { "uuid", "delete", "saveinv", "loadinv", "nick", "skin" };
		
		if(isArg(args, 0, "gamerule"))
		{
			if(i == 1 || i == 2)
			{
				FastList<String> l = new FastList<String>();
				
				for(int j = 0; j < LMGamerules.rules.keys.size(); j++)
				{
					String s = i == 1 ? LMGamerules.rules.keys.get(j).group : LMGamerules.rules.keys.get(j).key;
					if(!l.contains(s)) l.add(s);
				}
				
				return l.toArray(new String[0]);
			}
		}
		
		return super.getTabStrings(ics, args, i);
	}
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 1 && isArg(args, 0, "player"))
			return NameType.MC;
		return NameType.NONE;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return "Subcommands: " + LatCore.strip(getTabStrings(ics, args, 0));
		
		if(args[0].equals("player"))
		{
			if(args.length < 2) return "Missing arguments!";
			
			LMPlayer p = getLMPlayer(args[1]);
			
			if(args[2].equals("uuid"))
			{
				IChatComponent toPrint = new ChatComponentText(p.getDisplayName() + "'s UUID: ");
				IChatComponent uuid = new ChatComponentText(p.uuid.toString());
				uuid.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy to chat")));
				uuid.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, p.uuid.toString()));
				uuid.getChatStyle().setColor(EnumChatFormatting.GOLD);
				toPrint.appendSibling(uuid);
				ics.addChatMessage(uuid);
				return null;
			}
			else if(args[2].equals("delete"))
			{
				if(p.isOnline()) return "The player must be offline!";
				LMPlayer.list.remove(p.uuid);
				return FINE + "Player removed!";
			}
			else if(args[2].equals("saveinv"))
			{
				if(!p.isOnline()) return "The player must be online!";
				
				try
				{
					EntityPlayer ep = p.getPlayer();
					NBTTagCompound tag = new NBTTagCompound();
					writeItemsToNBT(ep.inventory, tag, "Inventory");
					
					if(LatCoreMC.isModInstalled("Baubles"))
					{
						IInventory inv = BaublesApi.getBaubles(ep);
						if(inv != null) writeItemsToNBT(inv, tag, "Baubles");
					}
					
					NBTHelper.writeMap(new FileOutputStream(LatCore.newFile(new File(LatCoreMC.latmodFolder, "playerinvs/" + ep.getCommandSenderName() + ".dat"))), tag);
				}
				catch(Exception e)
				{
					if(LatCoreMC.isDevEnv) e.printStackTrace();
					return "Failed to save inventory!";
				}
				
				return FINE + "Inventory saved!";
			}
			else if(args[2].equals("loadinv"))
			{
				if(!p.isOnline()) return "The player must be online!";
				
				try
				{
					EntityPlayer ep = p.getPlayer();
					NBTTagCompound tag = NBTHelper.readMap(new FileInputStream(new File(LatCoreMC.latmodFolder, "playerinvs/" + ep.getCommandSenderName() + ".dat")));
					
					readItemsFromNBT(ep.inventory, tag, "Inventory");
					
					if(LatCoreMC.isModInstalled("Baubles"))
					{
						IInventory inv = BaublesApi.getBaubles(ep);
						if(inv != null) readItemsFromNBT(inv, tag, "Baubles");
					}
				}
				catch(Exception e)
				{
					if(LatCoreMC.isDevEnv) e.printStackTrace();
					return "Failed to load inventory!";
				}
				
				return FINE + "Inventory loaded!";
			}
			else if(args[2].equals("nick"))
			{
				if(args.length != 4) return "Missing arguments!";
				p.setCustom(LMPlayer.Custom.NAME, args[3].trim());
				return FINE + "Custom nickname changed to " + p.getDisplayName() + " for " + p.username;
			}
			else if(args[2].equals("skin"))
			{
				if(args.length != 4) return "Missing arguments!";
				p.setCustom(LMPlayer.Custom.SKIN, args[3].trim());
				return FINE + "Custom skin changed to " + p.getCustom(LMPlayer.Custom.SKIN) + " for " + p.username;
			}
		}
		else if(args[0].equals("killblock"))
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			
			try
			{
				MovingObjectPosition mop = MathHelper.rayTrace(ep);
				
				//ep.worldObj.setTileEntity(mop.blockX, mop.blockY, mop.blockZ, null);
				ep.worldObj.setBlockToAir(mop.blockX, mop.blockY, mop.blockZ);
				
				return FINE + "Block destroyed!";
			}
			catch(Exception e)
			{ return "Failed to destroy the block!"; }
		}
		else if(args[0].equals("gamerule"))
		{
			if(args.length >= 3)
			{
				RuleID id = new RuleID(args[1], args[2]);
				
				if(args.length >= 4)
				{
					LMGamerules.set(id, args[3]);
					return FINE + "LMGamerule '" + id + "' set to " + args[3];
				}
				else
				{
					LMGamerules.Rule r = LMGamerules.get(id);
					if(r == null) return FINE + "LMGamerule '" + id + "' does not exist";
					return FINE + "LMGamrule '" + id + "' is '" + r.value + "'";
				}
			}
		}
		else if(args[0].equals("reloadPD"))
		{
			LMNetHandler.INSTANCE.sendToAll(new MessageCustomServerAction(LCEventHandler.ACTION_RELOAD_PD, null));
			return FINE + "Reloaded Player Decorators";
		}
		else if(args[0].equals("updateSkins"))
		{
			for(EntityPlayer ep : LatCoreMC.getAllOnlinePlayers())
			{
				NBTTagCompound data1 = new NBTTagCompound();
				data1.setString("UUID", ep.getUniqueID().toString());
				LMNetHandler.INSTANCE.sendToAll(new MessageCustomServerAction(LMPlayer.Custom.SKIN.key, data1));
			}
			
			return FINE + "Skins updated";
		}
		
		return onCommand(ics, null);
	}
	
	private static void writeItemsToNBT(IInventory inv, NBTTagCompound tag, String s)
	{
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack is = inv.getStackInSlot(i);
			
			if(is != null)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				tag1.setShort("S", (short)i);
				tag1.setString("ID", LatCoreMC.getRegName(is.getItem()));
		        tag1.setByte("C", (byte)is.stackSize);
		        tag1.setShort("D", (short)is.getItemDamage());
		        if (is.stackTagCompound != null) tag1.setTag("T", is.stackTagCompound);
				list.appendTag(tag1);
			}
			
		}
		
		if(list.tagCount() > 0) tag.setTag(s, list);
	}
	
	private static void readItemsFromNBT(IInventory inv, NBTTagCompound tag, String s)
	{
		for(int i = 0; i < inv.getSizeInventory(); i++)
			inv.setInventorySlotContents(i, null);
		
		if(tag.hasKey(s))
		{
			NBTTagList list = tag.getTagList(s, NBTHelper.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				Item item = LatCoreMC.getItemFromRegName(tag1.getString("ID"));
		        
		        if(item != null)
		        {
		        	int slot = tag1.getShort("S");
		        	int size = tag1.getByte("C");
		        	int dmg = Math.max(0, tag1.getShort("D"));
		        	ItemStack is = new ItemStack(item, size, dmg);
		        	if(tag1.hasKey("T", 10)) is.setTagCompound(tag1.getCompoundTag("T"));
		        	inv.setInventorySlotContents(slot, is);
		        }
		        
				if(i >= inv.getSizeInventory()) break;
			}
		}
		
		inv.markDirty();
	}
}