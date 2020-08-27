/**
 * @author Marcelo Malagutti
 * Class for storing and processing token counts.
 */

package br.org.vegetius.ContentAnalysis;

 public class TokenCount
{
	public String token;
	public int count;

	public TokenCount(String token, int count)
	{
		setToken(token);
		setCount(count);
	}

	/*
	 * Returns the rule as a formatted string.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return String.format("%-30s %d", "'" + this.token + "'", this.count);
	}

	/*
	 * Sets the regex value of the rule.
	 * 
	 * @param regex String with the regex to be used in this rule.
	 */
	public void setToken(String token)
	{
		this.token = token;
	}

	/*
	 * Returns the regex value of the rule.
	 */
	public String getToken()
	{
		return this.token;
	}

	/*
	 * Sets the count value of the rule.
	 * 
	 * @param count Number of occurrences of this token.
	 */
	public void setCount(int count)
	{
		this.count = count;
	}

	/*
	 * Returns the comment value of the rule.
	 */
	public int getCount()
	{
		return this.count;
	}

	/*
	 * Increments the token counting by 1.
	 */
	public void incCount()
	{
		this.count++;
	}

	/*
	 * Increments the token counting by n.
	 */
	public void incCount(int n)
	{
		this.count += n;
	}
}
