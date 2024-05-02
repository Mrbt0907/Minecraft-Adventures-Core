package net.endermanofdoom.mac.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StringUtil
{
	public static String repeat(String original, String input, int amount)
	{
		for (int i = 0; i < amount; i++)
			original = original.concat(input);
		return original;
	}
	
	@SideOnly(Side.CLIENT)
	public static List<String> wordwrap(net.minecraft.client.gui.FontRenderer fontRenderer, String str, int width)
	{
		int length = str.length();
		List<String> strings = new ArrayList<String>();
		String string = "",  word = "";
		char character;
		
		for (int i = 0 ; i < length; i++)
		{
			character = str.charAt(i);
			word += character;
			
			if (fontRenderer.getStringWidth(word) >= width)
			{
				strings.add(string + word);
				string = "";
				word = "";
			}
			else if (character == ' ')
			{
				if (fontRenderer.getStringWidth(string + word) >= width)
				{
					strings.add(string);
					string = word;
					word = "";
				}
				else
				{
					string += word;
					word = "";
				}
			}
		}
		
			if (!string.isEmpty())
				if (fontRenderer.getStringWidth(string + word) >= width)
				{
					strings.add(string);
					if (!word.isEmpty())
						strings.add(word);
				}
				else
					strings.add(string + word);
			else if (!word.isEmpty())
				strings.add(word);
		
		return strings;
	}
}
