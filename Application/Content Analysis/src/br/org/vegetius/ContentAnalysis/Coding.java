/**
 * @author Marcelo Malagutti
 * Class for Content Analysis coding.
 *
 */
package br.org.vegetius.ContentAnalysis;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Coding
{
	public static final String UNCAT = "Uncategorised";
	public static final char   SPACE = '·';
	
	public static final String CATEGORY = "{CAT}";
	public static final String SUBCATEGORY = "{SUB}";
	public static final String GROUP = "{GRP}";
	public static final String TERM = "{TRM}";

	private static final String   STRINGPATTERN = "(([A-Za-zÀ-ÿ0-9·-]+))"; //
	
	private String cat = "";	// Category
	private String sub = "";	// Subcategory 
	private String grp = "";	// Group
	private String trm = "";	// Term
	private String msk = "";	// Mask

	/*
	 * Constructor.
	 * 
	 * @param mask the mask string of the keys.
	 * @param key the key used in this code.
	 */
	
	/*
	 * Constructor.
	 * 
	 * @param mask the mask of the code.
	 * @param category the category of the code.
	 * @param subcategory the subCategory of the code.
	 * @param group the group of the code.
	 */
	public Coding(String mask, String category, String subcategory, String group)
	{
		setMask(mask);
		setCategory(category);
		setSubcategory(subcategory);
		setGroup(group);
	}

	/*
	 * Constructor.
	 * 
	 * @param category the category of the code.
	 * @param subcategory the subCategory of the code.
	 * @param group the group of the code.
	 */
	public Coding(String mask, String term)
	{
		String tmp, cat, sub, grp, trm;
		String[] matches = { UNCAT, UNCAT, UNCAT, UNCAT, UNCAT, UNCAT, UNCAT, UNCAT };
		int[]		pos = { 0, 0, 0, 0 };
		int iCat = -1, iSub = -1, iGrp = -1, iTrm = -1;
		
		iCat = mask.indexOf(CATEGORY);
		iSub = mask.indexOf(SUBCATEGORY);
		iGrp = mask.indexOf(GROUP);
		iTrm = mask.indexOf(TERM);
		
		pos[0] = iCat;
		pos[1] = iSub;
		pos[2] = iGrp;
		pos[3] = iTrm;
		Arrays.sort(pos, 0, 3);
		
		tmp = new String(mask);
		tmp = tmp.replace(CATEGORY, STRINGPATTERN);
		tmp = tmp.replace(SUBCATEGORY, STRINGPATTERN);
		tmp = tmp.replace(GROUP, STRINGPATTERN);
		tmp = tmp.replace(TERM, STRINGPATTERN);

		try
		{
			Pattern p = Pattern.compile(tmp);
			Matcher m = p.matcher(term);
			if (m.find())
			{
				matches[0] = m.group(1); 
				matches[1] = m.group(3); 
				matches[2] = m.group(5); 
				matches[3] = m.group(7); 
			}
		}
		catch (PatternSyntaxException e)
		{
			System.err.println("Exception while processing coding mask " + e);
			e.printStackTrace();
		}
		
		cat = getVal(getPos(iCat, pos), matches);
		sub = getVal(getPos(iSub, pos), matches);
		grp = getVal(getPos(iGrp, pos), matches);
		trm = getVal(getPos(iTrm, pos), matches);

		setMask(mask);
		setCategory(cat);
		setSubcategory(sub);
		setGroup(grp);
		setTerm(trm);
	}
	
	/*
	 * Returns the order of the element on the mask code.
	 * 
	 * @param val the position of the element asked.
	 * @param pos the array of positions.
	 */
	private static int getPos(int val, int[] pos)
	{
		if (val != -1)
			for (int ret = 0; ret < pos.length; ret++)
				if (val == pos[ret])
					return ret;
		return -1;
	}

	/*
	 * Returns the value of the 'pos' element of the code.
	 * 
	 * @param pos the position asked.
	 * @param m the components of the code.
	 */
	private static String getVal(int pos, String[] m)
	{
		if (pos >= 0 && pos < m.length)
			return m[pos];
		return UNCAT;
	}
	
	/*
	 * Returns the code as a formatted string.
	 * 
	 * @param term the term coded.
	 */
	public String getCode(String term)
	{
		String str = new String(this.msk);
		str = str.replace(CATEGORY, this.cat);
		str = str.replace(SUBCATEGORY, this.sub);
		str = str.replace(GROUP, this.grp);
		str = str.replace(TERM, term.toUpperCase().replace(' ', SPACE));
		return str;
	}

	/*
	 * Sets the category value of the code.
	 * 
	 * @param category String with the category used in this code.
	 */
	public void setCategory(String category)
	{
		this.cat = category;
	}

	/*
	 * Returns the category value of the code.
	 */
	public String getCategory()
	{
		return this.cat;
	}

	/*
	 * Sets the subcategory value of the code.
	 * 
	 * @param subcategory String with the subcategory used in this code.
	 */
	public void setSubcategory(String subcategory)
	{
		if (subcategory.equals("COMPUTERS"))
			System.err.println("Achei");
		this.sub = subcategory;
	}

	/*
	 * Returns the subcategory value of the code.
	 */
	public String getSubcategory()
	{
		return this.sub;
	}

	/*
	 * Sets the group value of the code.
	 * 
	 * @param group String with the category used in this code.
	 */
	public void setGroup(String group)
	{
		this.grp = group;
	}

	/*
	 * Returns the group value of the code.
	 */
	public String getGroup()
	{
		return this.grp;
	}

	/*
	 * Sets the term value of the code.
	 * 
	 * @param term String with the term used in this code.
	 */
	public void setTerm(String term)
	{
		this.trm = term;
	}

	/*
	 * Returns the term value of the code.
	 */
	public String getTerm()
	{
		return this.trm;
	}

	/*
	 * Sets the mask value of the code.
	 * 
	 * @param mask String with the term used in this code.
	 */
	public void setMask(String mask)
	{
		this.msk = mask;
	}

	/*
	 * Returns the mask value of the code.
	 */
	public String getMask()
	{
		return this.msk;
	}
}

