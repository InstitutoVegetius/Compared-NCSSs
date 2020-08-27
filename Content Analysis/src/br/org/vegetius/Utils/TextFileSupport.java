package br.org.vegetius.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TextFileSupport
{
	public static String stringFromFile(File inpFile)
	{
		String entireFileText = null;
		
		try 
		{
			entireFileText = new Scanner(inpFile).useDelimiter("\\A").next();
		} 
		catch (IOException e) 
		{
			System.err.println("Exception while trying to read input text file - " + e);
			e.printStackTrace();
		}
		
		return entireFileText;
	}

	
	public static void stringToFile(String str, File output)
	{
		try 
		{
			FileWriter txtFile = new FileWriter(output.getName(), false);
			BufferedWriter bufferedWriter = new BufferedWriter(txtFile);

			bufferedWriter.write(str);
			bufferedWriter.close();
		} 
		catch (IOException e) 
		{
			System.err.println("Exception while trying to write txt file - " + e);
			e.printStackTrace();
		}
	}

	
	public static void stringToFilename(String str, String fileName)
	{
		try 
		{
			FileWriter txtFile = new FileWriter(fileName, false);
			BufferedWriter bufferedWriter = new BufferedWriter(txtFile);

			bufferedWriter.write(str);
			bufferedWriter.close();
		} 
		catch (IOException e) 
		{
			System.err.println("Exception while trying to write txt file - " + e);
			e.printStackTrace();
		}
	}
}
