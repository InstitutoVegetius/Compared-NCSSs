package br.org.vegetius.Utils;

import java.util.Hashtable;
import java.util.ArrayList;

/*
 * Processes arguments supplied in the command line according to a set of established rules.
 */
public class ArgsProcessor
{
	private ArrayList<String> argList;				//stores arguments in informed order
	private Hashtable <String, Argument> table;		//stores processed arguments
	
	/*
	 * Constructor
	 * @param rules informs the rules to be used for parsing as two dimensional array of strings. 
	 * 		Each line of the array contains one possible argument, with the the key and the description of that argument.  
	 */
	public ArgsProcessor (String[][] rules)
	{
		table = new Hashtable <String, Argument>();
		argList = new ArrayList<String>();
		
		parseRules(rules);
	}

	/*
	 * Inner class for storing argument information.
	 */
	private class Argument
	{
		String description;				// the description of the argument
		boolean valueReq;					// indicates if a value is required for the argument
		boolean informed;					// indicates if the argument has been informed on the command line
		ArrayList<String> values;		// values informed in the command line for that argument
		
		/*
		 * Constructor
		 * @param description	describes the argument
		 * @param valueReq	indicates if the argument requires a value informed 
		 */
		Argument(String description, boolean valueReq)
		{
			this.description = description;
			this.valueReq = valueReq;
			this.informed = false; 
			this.values =  new ArrayList<String>(); 
		}
		
		/*
		 * Returns the informed values of the argument
		 * 
		 */
		public ArrayList<String> getValues()
		{
			return values;
		}
		
		/*
		 * Indicates if the argument requires a value when informed on the command line.
		 */
		public boolean isValueRequired()
		{
			return valueReq;
		}

		
		/*
		 * Indicates if the argument has been informed on the command line.
		 */
		public boolean isInformed()
		{
			return informed;
		}
	}

	/*
	 * Returns the values informed on the command line for that argument.
	 * @param name		The name of the desired argument.
	 */
	public ArrayList<String> getValues(String name)
	{
		Argument arg = table.get(name);
		if (arg != null)
			return arg.getValues();
		else
			return null;
	}

	/*
	 * Indicates if the argument was informed on the command line.
	 * @param name		The name of the desired argument.
	 */
	public boolean isInformed(String name)
	{
		Argument arg = table.get(name);
		if (arg != null)
			return arg.isInformed();
		else
			return false;
	}
	
	/*
	 * Indicates if the argument was informed on the command line.
	 * @param name		The name of the desired argument.
	 */
	public boolean isValueRequired(String name)
	{
		Argument arg = table.get(name);
		if (arg != null)
			return arg.isValueRequired();
		else
			return false;
	}
	/*
	 * Parses and stores informed rules.
	 * @param rules informs the rules to be used for parsing as two dimensional array of strings. 
	 * 		Each line of the array contains one possible argument, with the the key and the description of that argument.  
	 */
	public void parseRules(String[][] rules)
	{
	   int cRules = 0;
	   boolean valReq = false;
	   String key, name, desc;
	    
	   while (cRules < rules.length) 
	   {
	   	valReq = false;
	      name = rules[cRules][0];
	      if (name.endsWith(":"))
	      {
	      	key = name.substring(0, name.length()-1);
	      	valReq = true;
	      }
	      else
	      	key = name;
	      desc = rules[cRules][1];
	        
	      Argument arg = new Argument(desc, valReq);
	      argList.add(key);
	      table.put(key, arg);
	      cRules++;
	   }
	}	    

	/*
	 * Returns the informed arguments usage rules.
	 */
	public String usage()
	{
		Argument arg;
		String ret = "Usage:\n", line, key;
		
      for (int i = 0; i < argList.size(); i++) 
	   {
	      key = argList.get(i);
	      arg = table.get(key);
	      line = String.format("%-15s   %s\n", key, arg.description);
	      ret += line;
	   }
	   
	   return ret;
	}

	/*
	 * Parses and stores arguments informed in the command line.
	 */
	public boolean parseArgs(String[] args)
	{
	   int argc = 0;
	   boolean ret = true;
	   String key, value;

	   while (argc < args.length) 
	   {
	       key = args[argc++];
	       Argument arg = table.get(key); 
	       if (arg != null)
	       {
	      	 arg.informed = true;
	      	 if (arg.valueReq)
	      	 {
	      		 value = args[argc++];
	      		 arg.values.add(value);
	      	 }
	       }
	       else
	       {
	      	 System.err.println("Argument '" + key + "' is invalid!");
	      	 ret = false;
	       }
	   }
	    
	   return ret;
	}
	
	/*
	 * Tests if an argument has been informed and lists its values.
	 * @param arg		name of the tested argument.
	 */
	public static void testArgument(ArgsProcessor arguments, String arg)
	{
		// Verifies if argument '-d' has been informed on the command line and list all of the informed values.
		if (arguments.isInformed(arg))
		{
			System.out.print("Argument '"+ arg + "' informed, requires value: '" + arguments.isValueRequired(arg) + "', informed values:");
			System.out.println(arguments.getValues(arg));
		}
		else
			System.out.println("Argument '" + arg + "' has not been informed");
	}

	/*
	 * Main program for class testing.
	 */
	public static void main(String args[])
	{
		String[][] rules = { // set of rules informed for testing. The ':' indicates that a value will be expected when that argument is informed.
				{"-d:",	"Informa o diretorio de trabalho."},
				{"-v",	"Indica 'verbose mode'."},
				{"-r",	"Indica modo recursivo."}, 
				{"-p:",	"Informa o nome do arquivo PDF de entrada."},
				{"-t:",	"Informa o nome do arquivo TXT de saída."}
				};
				
		ArgsProcessor arguments = new ArgsProcessor(rules);
		if (!arguments.parseArgs(args))
		{
			System.err.println(arguments.usage());
			System.exit(-1);
		}
		
		testArgument(arguments, "-d");
		testArgument(arguments, "-p");
		testArgument(arguments, "-v");
		testArgument(arguments, "-r");
		testArgument(arguments, "-t");
		testArgument(arguments, "-i");
	}
}

