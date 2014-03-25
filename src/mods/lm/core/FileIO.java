package mods.lm.core;
import java.io.*;
import java.util.*;

public class FileIO
{
	public final ArrayList<String> text = new ArrayList<String>();
	public final File file;
	private boolean sort = false;
	private StringBuilder currentText = new StringBuilder();
	
	public FileIO(File f)
	{ file = f; }
	
	public boolean initAndCreate()
	{
		if(file == null) throw new RuntimeException("File not found!");
		
		if(!file.exists())
		{
			File p = file.getParentFile();
			if(!p.exists()) p.mkdirs();
			
			try { file.createNewFile(); return true; } catch(Exception e)
			{ System.err.println("Failed to create '" + file.getAbsolutePath() + "'!"); return false; }
		}
		
		return false;
	}
	
	public boolean load()
	{
		clear();
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			String s = null;
			while((s = br.readLine()) != null)
			{
				String s1 = s.trim();
				
				if(s1.length() > 0)
				text.add(s1);
			}
			
			br.close();
			return true;
		}
		catch(Exception e) { }
		
		return false;
	}
	
	public boolean save()
	{
		try
		{
			String[] s = text.toArray(new String[0]);
			if(sort) Arrays.sort(s);
			
			FileWriter fr = new FileWriter(file);
			for(int i = 0; i < s.length; i++)
			{ fr.append(s[i]); fr.append('\n'); }
			
			fr.close();
			
			return true;
		}
		catch(Exception e) { }
		
		return false;
	}
	
	public void clear()
	{ text.clear(); }
	
	public boolean hasText()
	{ return !text.isEmpty(); }
	
	public void enableSorting(boolean b)
	{ sort = b; }
	
	public void print(String s)
	{ currentText.append(s); }
	
	public void println()
	{ text.add(currentText.toString());
	currentText = new StringBuilder(); }
	
	public void println(String s)
	{ print(s); println(); }
}