/**
 * @author Marcelo Malagutti
 * Class for Regular Expressions processing on Strings.
 *
 */
package br.org.vegetius.ContentAnalysis;

public class RegexRule
{
	public enum RegexTypes 
	{    
		TRANSFORMATION("TRANSFORMATION", 1), CODING("CODING", 2);

		private final String name;
		private final int type;
		
		RegexTypes(String name, int type)
		{
			this.name = name;
			this.type = type;
		}
		
		public String getName()
		{
			return name;
		}
		
		public int getType()
		{
			return type;
		}
	}
	
	private RegexTypes 	type;				// Regular Expression (regex) to be processed
	private String			regex;			// Regular Expression (regex) to be processed
	private String			replacement;	// Replacement of the occurrence of regex found 
	private String			comment;			// Comment on the regex rule
	private Coding			coding = null;	// 'Coding' of the regex rule

	/*
	 * Constructor.
	 * 
	 * @param regex the regex to be stored.
	 * @param replacement the text to replace occurrences of regex found in the base codedText.
	 * @param comment a comment on the regex rule.
	 * @param mask the 'coding' mask of the regex rule.
	 * @param category the 'coding' category of the regex rule.
	 * @param subcategory the 'coding' subcategory of the regex rule.
	 * @param group the 'coding' group of the regex rule.
	 */
	public RegexRule(String type, String regex, String replacement, String comment, 
			String mask, String category, String subcategory, String group)
	{
		setType(type.toUpperCase());
		setRegex(regex);
		setReplacement(replacement);
		setComment(comment);
		if (this.type == RegexTypes.CODING)
			setCoding(new Coding(mask, category, subcategory, group));
		else
			setCoding(null);
	}

	/*
	 * Returns the rule as a formatted string.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		if (this.type.equals(RegexTypes.TRANSFORMATION))
			return String.format("%-15s %-30s %-20s '%s'", "'" + this.type.getName() + "'", "'" + this.regex + "'", "'" + this.replacement + "'", this.comment);
		else
			return String.format("%-15s %-30s %-20s '%s'", "'" + this.type.getName() + "'", "'" + this.regex + "'", "'" + this.getCoding().getCode("") + "'", this.comment);
	}

	/*
	 * Sets the type value of the rule.
	 * 
	 * @param type String with the type to be used in this rule.
	 */
	public void setType(String name)
	{
		this.type = null;
		
		for (RegexTypes r : RegexTypes.values())
			if (name.toUpperCase().equals(r.getName()))
				this.type = r;
	}

	/*
	 * Returns the type value of the rule.
	 */
	public RegexTypes getType()
	{
		return this.type;
	}

	/*
	 * Sets the regex value of the rule.
	 * 
	 * @param regex String with the regex to be used in this rule.
	 */
	public void setRegex(String regex)
	{
		this.regex = regex;
	}

	/*
	 * Returns the regex value of the rule.
	 */
	public String getRegex()
	{
		return this.regex;
	}

	/*
	 * Sets the replacement value of the rule, to be used when a
	 * corresponding regex element is found.
	 * 
	 * @param replacement String with the 'category' part of the codedText to be used
	 * in this rule.
	 */
	public void setReplacement(String replacement)
	{
		this.replacement = replacement;
	}

	/*
	 * Returns the replacement value of the rule.
	 */
	public String getReplacement()
	{
		return this.replacement;
	}

	/*
	 * Sets the comment value of the rule.
	 * 
	 * @param comment String with the comment/description of this rule.
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}

	/*
	 * Returns the comment value of the rule.
	 */
	public String getComment()
	{
		return this.comment;
	}

	/*
	 * Sets the coding value of the rule.
	 * 
	 * @param comment Coding of this rule.
	 */
	public void setCoding(Coding coding)
	{
		this.coding = coding;
	}

	/*
	 * Returns the Coding object of the rule.
	 */
	public Coding getCoding()
	{
		return this.coding;
	}
}
