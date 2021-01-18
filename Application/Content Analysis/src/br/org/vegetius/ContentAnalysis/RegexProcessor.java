/**
 * @author Marcelo Malagutti 
 * Class for Regular Expressions processing over
 *         Strings.
 */
package br.org.vegetius.ContentAnalysis;

import br.org.vegetius.ContentAnalysis.RegexRule.RegexTypes;
import br.org.vegetius.Utils.ArgsProcessor;
import br.org.vegetius.Utils.TextFileSupport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RegexProcessor
{
	private static int PRINTING = 0;	// Indicates printing usage of strings
	private static int REGEX = 1;		// Indicates regex use of strings
	private static String ACCENTS = "·¡‡¿‰ƒ‚¬„√È…Ë»Í ÎÀÌÕÏÃÓŒÔœÛ”Ú“Ù‘ˆ÷ı’˙⁄˘Ÿ˚€¸‹Á«Ò—";

	private String codedText;			// text to be 'coded'
	private String markedText;			// text to be 'marked' with coded original text within {}
	private String codeMask;			// mask of the coding
	private boolean caseIns = false; // indicates if the processing is case insensitive
	private boolean verbose = false; // indicates if the processing is in 'verbose' mode (printing messages)
	private boolean marking = false; // indicates if the processing is in 'marking' mode (marking text replacements)
	private ArrayList<RegexRulesInfo> rulesInfo = new  ArrayList<RegexRulesInfo>(); // rules sets (list)
	
	private class RegexRulesInfo
	{
		private String name; // name of sheet with rule set
		private ArrayList<RegexRule> rulesSet = new ArrayList<RegexRule>(); // rules set

		RegexRulesInfo(String name, ArrayList<RegexRule> rulesSet)
		{
			this.name = name;
			this.rulesSet = rulesSet;
		}

		String getName()
		{
			return name;
		}

		ArrayList<RegexRule> getRules()
		{
			return rulesSet;
		}
	}

	/*
	 * Constructor
	 * 
	 * @param text text where to apply the regexes.
	 */
	public RegexProcessor(String text)
	{
		this.codedText = new String(text);
		this.markedText = new String(text);
	}

	/*
	 * Set regexes to be processed as Case Insensitive. By default they are case sensitive.
	 * 
	 * @param caseInsensitive true for case insensitive; false for case
	 * sensitive.
	 */
	private void setCaseInsensitive(boolean caseInsensitive)
	{
		this.caseIns = caseInsensitive;
	}

	/*
	 * Set regexes to be processed as Case Insensitive. By default they are case sensitive.
	 * 
	 * @param verbose true for verbose mode; false for silent mode.
	 */
	private void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}

	/*
	 * Set regexes to be processed as Case Insensitive. By default they are case sensitive.
	 * 
	 * @param verbose true for verbose mode; false for silent mode.
	 */
	private void setMarking(boolean marking)
	{
		this.marking = marking;
	}

	/*
	 * Returns the text 'coded' with the applied regexes.
	 */
	private String getCodedText()
	{
		return codedText;
	}

	/*
	 * Returns the text 'marked' with the applied regexes.
	 */
	private String getMarkedText()
	{
		return markedText;
	}

	/*
	 * Assigns a new text to be used both as codedText and markedText.
	 * 
	 * @param text the new text to be used for regex processing.
	 */
	private void setText(String text)
	{
		codedText = new String(text);
		markedText = new String(text);
	}

	/*
	 * Assigns a mask to be used on decoding.
	 * 
	 * @param mask the mask string to be used for decoding.
	 */
	private void setMask(String mask)
	{
		codeMask = new String(mask);
	}


	/*
	 * Returns the mask to be used on decoding.
	 */
	private String getMask()
	{
		return codeMask;
	}

	/*
	 * Searches for an informed regex on the base codedText.
	 * 
	 * @param regex the regex to be searched on the codedText.
	 * 
	 * @return the number of occurrences of regex found in codedText.
	 */
	private int search(String regex)
	{
		Pattern pattern; // pattern of the regex.
		Matcher matcher; // matcher of the regex.
		int ret = 0; // number of occurrences found.

		if (caseIns)
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		else
			pattern = Pattern.compile(regex);

		matcher = pattern.matcher(codedText);

		// check all occurrences
		while (matcher.find())
		{
			ret++;
			if (verbose)
			{
				System.out.print("Found Start index: " + matcher.start());
				System.out.print(" End index: " + matcher.end() + " '");
				System.out.print(matcher.group());
				int ini = matcher.start() - 20;
				if (ini < 0)
					ini = 0;
				int fim = matcher.end() + 20;
				if (fim > codedText.length())
					fim = codedText.length();
				System.out.println("' context: '" + codedText.substring(ini, fim) + " '");
			}
		}

		if (verbose)
			System.out.println("Found " + ret + " occurrences of '" + regex + "'");

		return ret;
	}

	/*
	 * Replaces occurrences of formatting characters.
	 * 
	 * @param str the string to be cleaned.
	 * 
	 * @param use intended for the string(PRINTING or REGEX).
	 * 
	 * @return the string with formatting characters replaced.
	 */
	private String cleanFormatChars(String str)
	{
		String tmp = new String(str);
		
		tmp = tmp.replace("\n", " ");
		tmp = tmp.replace("\r", " ");
		tmp = tmp.replace("\t", " ");

		return tmp;
	}

	/*
	 * Replaces occurrences of regex on the base codedText by replacement.
	 * 
	 * @param regex the regex to be searched on the codedText.
	 * 
	 * @param replacement the destination codedText to replace occurrences of
	 * regex found in the base codedText.
	 * 
	 * @return the number of occurrences of regex found in codedText.
	 */
	private String getContext(String text, int start, int end)
	{
		String context;
		int ini = start - 30;
		if (ini < 0)
			ini = 0;
		int fim = end + 30;
		if (fim > text.length())
			fim = text.length();

		context = text.substring(ini, fim);
		
		return context;
	}
	
	/*
	 * Replaces occurrences of regex on the base codedText by replacement.
	 * 
	 * @param regex the regex to be searched on the codedText.
	 * 
	 * @param replacement the destination codedText to replace occurrences of
	 * regex found in the base codedText.
	 * 
	 * @return the number of occurrences of regex found in codedText.
	 */
	private String replace(RegexRule rule)
	{
		Pattern pattern;			// pattern of the regex.
		Matcher matcherCoded;	// matcher of the regex on the 'coded' text.
		Matcher matcherMarked;	// matcher of the regex on the 'marked' text.
		String regex;				// regex searched.
		String dest;				// replacement text.
		String context;			// context of the regex found.
		String group;				// regex group found.
		String repl;				// replacement for the regex group found.
		String mark;				// 'marked' replacement for the regex group found.

		regex = rule.getRegex();
		
		try
		{
			if (caseIns)
				pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			else
				pattern = Pattern.compile(regex);

			matcherCoded = pattern.matcher(codedText);
			while (matcherCoded.find())
			{
				group = cleanFormatChars(matcherCoded.group());

				if (rule.getType() == RegexTypes.TRANSFORMATION)
					dest = rule.getReplacement();
				else
					dest = rule.getCoding().getCode(group);
				
				if (verbose)
				{
					context = getContext(codedText, matcherCoded.start(), matcherCoded.end());
					context = cleanFormatChars(context);
					
					repl = cleanFormatChars(dest);

					System.out.println(String.format("Replacing '%s' by '%s' in '%s' at (%6d, %6d), ", group, repl, context,
							matcherCoded.start(), matcherCoded.end()));
				}
				codedText = matcherCoded.replaceFirst(dest);
				matcherCoded = pattern.matcher(codedText);
			}

			if (marking)
			{
				matcherMarked = pattern.matcher(markedText);
				while (matcherMarked.find())
				{
					group = matcherMarked.group();
					mark = "{" + group.toUpperCase() + "}";
					
					if (verbose)
					{
						context = getContext(markedText, matcherMarked.start(), matcherMarked.end());
						context = cleanFormatChars(context);
						group = cleanFormatChars(group);
						repl = cleanFormatChars(mark);

						System.out.println(String.format("Marking '%s' as '%s' in '%s' at (%6d, %6d), ", group, repl, context,
								matcherMarked.start(), matcherMarked.end()));
					}
					markedText = matcherMarked.replaceFirst(mark);
					matcherMarked = pattern.matcher(markedText);
				}
			}
		}
		catch (PatternSyntaxException e)
		{
			System.err.println(e.toString());
		}

		return codedText;
	}

	/*
	 * Returns the Excel type of the informed cell.
	 * 
	 * @param cell cell which type is to be obtained.
	 */
	private Object getCellValue(Cell cell)
	{
		switch (cell.getCellType())
		{
		case STRING:
			return cell.getStringCellValue();

		case BOOLEAN:
			return cell.getBooleanCellValue();

		case NUMERIC:
			return cell.getNumericCellValue();

		default:
			break;
		}

		return null;
	}

	/*
	 * Scans cells from the spreadsheet creating an ArrayList of REGEXes.
	 * 
	 * @param shet The spreadsheet to be scanned.
	 * 
	 * @return An ArrayList of RegexRule.
	 */
	private String changeBarChars(String str)
	{
		if (str != null)
		{
			str = str.replace("\\n", "\n");
			str = str.replace("\\t", "\t");
			str = str.replace("\\f", "\f");
			str = str.replace("\\r", "\r");
		}

		return str;
	}

	/*
	 * Scans cells from the spreadsheet creating an ArrayList of REGEXes.
	 * 
	 * @param shet The spreadsheet to be scanned.
	 * 
	 * @return An ArrayList of RegexRule.
	 */
	private ArrayList<RegexRule> scanSheet(Sheet sheet)
	{
		ArrayList<RegexRule> rulesList = new ArrayList<RegexRule>();
		Iterator<Row> rowIter = sheet.iterator();

		while (rowIter.hasNext())
		{
			Row curRow = rowIter.next();
			Iterator<Cell> cellIter = curRow.cellIterator();
			RegexRule rule = null;
			String type = null, regex = null, replacement = null, comment = null, category = null, subCategory = null, group = null;

			if (curRow.getRowNum() == 0)
				continue;
			
			while (cellIter.hasNext())
			{
				Cell currCell = cellIter.next();
				int columnIndex = currCell.getColumnIndex();
	
				switch (columnIndex)
				{
					case 0:
						type = ((String) getCellValue(currCell));
						break;
					case 1:
						regex = ((String) getCellValue(currCell));
						break;
					case 2:
						replacement = ((String) getCellValue(currCell));
						break;
					case 3:
						comment = ((String) getCellValue(currCell));
						break;
					case 4:
						category = ((String) getCellValue(currCell));
						break;
					case 5:
						subCategory = ((String) getCellValue(currCell));
						break;
					case 6:
						group = ((String) getCellValue(currCell));
						break;
				}
			}

			if (regex != null)
			{
				regex = changeBarChars(regex);
				if (replacement == null)
					replacement = "";
				else
					replacement = changeBarChars(replacement);
				if (comment == null)
					comment = "";
				rule = new RegexRule(type, regex, replacement, comment, this.getMask(), category, subCategory, group);
				if (verbose)
					System.out.println(rule);
				rulesList.add(rule);
			}
		}

		return rulesList;
	}

	/*
	 * Replaces regex rules on the informed XLSX file.
	 * 
	 * @param file the Excel spreadsheet to be read.
	 */
	private void loadRules(File rules)
	{
		FileInputStream inputStream = null;
		Workbook workbook = null;

		try
		{
			inputStream = new FileInputStream(rules);
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Exception while trying to read Excel file " + e);
			e.printStackTrace();
			return;
		}
		try
		{
			workbook = new XSSFWorkbook(inputStream);
		}
		catch (IOException e)
		{
			System.err.println("Exception while trying to read Excel file " + e);
			e.printStackTrace();
			return;
		}

		int numberOfSheets = workbook.getNumberOfSheets();

		for (int i = 0; i < numberOfSheets; i++)
		{
			Sheet aSheet = workbook.getSheetAt(i);
			rulesInfo.add(new RegexRulesInfo(aSheet.getSheetName(), scanSheet(aSheet)));
		}

		try
		{
			workbook.close();
			inputStream.close();
		}
		catch (IOException e)
		{
			System.err.println("Exception while trying to read Excel file " + e);
			e.printStackTrace();
		}
	}

	/*
	 * Processes the regex set of rules informed.
	 * 
	 * @param rulesSetName	the name of the RulesSet to be processed.
	 */
	private void processRules(String rulesSetName)
	{
		boolean found = false;
		// iterating ArrayList RulesInfo looking for rulesSetName
		for (RegexRulesInfo info : rulesInfo)
		{
			if (info.getName().toUpperCase().equals(rulesSetName.toUpperCase()))
			{
				found = true;
				// iterating ArrayList
				for (RegexRule rule : info.getRules())
				{
					if (verbose)
						System.out.println(rule);
					replace(rule);
				}
			}
		}
		if (!found)
			System.err.println("Rules Set '" + rulesSetName + "' not found!");
	}

	/*
	 * Checks is an informed char is valid.
	 * 
	 * @param c the char to be validated.
	 * 
	 * @return true if c is valid; false if c is not valid.
	 */
	private boolean isValid(char c)
	{
		String accents = ACCENTS;
		return ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
			|| (c == '-' || c == '<' || c == '>' || c == '#' || c == '&' || c == '∑') 
			|| accents.indexOf(c) != -1);
	}

	/*
	 * Checks is an informed char is valid.
	 * 
	 * @param c the char to be validated.
	 * 
	 * @return true if c is valid; false if c is not valid.
	 */
	private boolean isValid(String token)
	{
		int i, len;
		
		len = token.length();
		
		for (i = 0; i < len && isValid(token.charAt(i)); i++);
		
		return (i == len && len > 0);
	}

	/*
	 * Checks is an informed char is valid.
	 * 
	 * @param c the char to be validated.
	 * 
	 * @return true if c is valid; false if c is not valid.
	 */
	private String trimInvalid(String token)
	{
		int i, f, len;
		
		// Trims invalid characters
		len = token.length();
		for (i = 0; i < len && !isValid(token.charAt(i)); i++);
		for (f = len - 1; f >= i && !isValid(token.charAt(f)); f--);
		if (i > f)
			token = "";
		else
		if ((i != 0 || f != len - 1) && i <= f)
			token = token.substring(i, f + 1);
		
		// Trims the prefixes and suffixes connected to a key by an '-'
		String mask = getMask();
		len = mask.length();
		String key1 = "-" + mask.substring(0, 1);
		String key2 = mask.substring(len-1, len) + "-";
		i = token.indexOf(key1); 
		f = token.indexOf(key2); 
		if (i != -1 || f != -1)
		{
			i++;
			if (f == -1 || f <= i)
				f = token.length()-1;
			token = token.substring(i, f + 1);
		}
		
		// Trims leading and trailing '-'
		len = token.length();
		for (i = 0; i < len && token.charAt(i) == '-'; i++);
		for (f = len - 1; f >= i && token.charAt(f) == '-'; f--);
		if (i > f)
			token = "";
		else
		if ((i != 0 || f != len - 1) && i <= f)
			token = token.substring(i, f + 1);

		return token;
	}

	/*
	 * Creates an ArrayList of TokenCount with terms resultant of applied
	 * regexes.
	 * 
	 * @return An ArrayList of TokenCount with terms resultant of applied
	 * regexes.
	 */
	private ArrayList<TokenCount> tokenCount()
	{
		Hashtable<String, TokenCount> storage = new Hashtable<String, TokenCount>(1000);
		ArrayList<TokenCount> lista = new ArrayList<TokenCount>();
		TokenCount tc;
		String tokens[];

		tokens = this.codedText.split("[ \\n/]");

		int len = tokens.length;

		for (int scan = 0; scan < len; scan++)
		{
			String token = tokens[scan];

			token = trimInvalid(token);
			
			if (!token.isEmpty())
			{
				if (isValid(token))
				{
					tc = storage.get(token);
					if (tc == null)
					{
						tc = new TokenCount(token, 1);
						storage.put(token, tc);
					}
					else
						tc.incCount();
				}
				else
					System.out.println("Discarded token: '" + token + "'");
			}
		}

		List<String> lst = Collections.list(storage.keys());
		Collections.sort(lst);
		Iterator<String> it = lst.iterator();
		while (it.hasNext())
		{
			String element = it.next();
			lista.add(storage.get(element));
		}

		return lista;
	}
	
	/*
	 * Populates ArrayList's of TokenCount with categories, sub-categories or groups of
	 * terms.
	 * 
	 * @param ht (input) A HashTable of TokenCount with terms.
	 * 
	 * @param al (output) An ArrayList of TokenCount.
	 */
	private void listTokens(Hashtable<String, TokenCount> ht, ArrayList<TokenCount> al)
	{
		List<String> lst = Collections.list(ht.keys());
		Iterator<String> it = lst.iterator();
		lst = Collections.list(ht.keys());
		Collections.sort(lst);
		it = lst.iterator();
		while (it.hasNext())
		{
			String element = it.next();
			al.add(ht.get(element));
		}
	}
	
	
	private void addTokens(Hashtable<String, TokenCount> ht, String term, int count)
	{
		TokenCount tc;
		tc = ht.get(term);
		if (tc == null)
		{
			tc = new TokenCount(term, count);
			ht.put(term, tc);
		}
		else
			tc.incCount(count);
	}

	

	/*
	 * Populates ArrayList's of TokenCount with categories, sub-categories and groups of
	 * terms.
	 * 
	 * @param terms (input) An ArrayList of TokenCount with terms.
	 * 
	 * @param cats (output) An ArrayList of TokenCount with categories.
	 * 
	 * @param subs (output) An ArrayList of TokenCount with subcategories.
	 * 
	 * @param grps (output) An ArrayList of TokenCount with groups.
	 */
	private int codingCount(ArrayList<TokenCount> terms, ArrayList<TokenCount> cats, ArrayList<TokenCount> subs, ArrayList<TokenCount> grps)
	{
		Hashtable<String, TokenCount> htCat = new Hashtable<String, TokenCount>(20);
		Hashtable<String, TokenCount> htSub = new Hashtable<String, TokenCount>(200);
		Hashtable<String, TokenCount> htGrp = new Hashtable<String, TokenCount>(2000);
		Coding code; 
		String token, cat = null, sub = null, grp = null, trm = null;
		int count;
		int totalTerms = 0;

		for (TokenCount it : terms)
		{
			token = it.getToken();
			count = it.getCount();
			totalTerms += count;

			code = new Coding(this.codeMask, token);
			cat = code.getCategory();
			sub = code.getSubcategory();
			grp = code.getGroup();
			trm = code.getTerm();
			if (!cat.equals(Coding.UNCAT))
			{
				sub = cat+"-"+sub;
				grp = sub+"-"+grp;
				trm = trm.replace(Coding.SPACE, ' ');
			}
			// Counts categories
			addTokens(htCat, cat, count);
			// Counts sub-categories
			addTokens(htSub, sub, count);
			// Counts groups
			addTokens(htGrp, grp, count);
		}

		// Lists categories
		listTokens(htCat, cats);
		// Lists sub-categories
		listTokens(htSub, subs);
		// Lists sub-categories
		listTokens(htGrp, grps);
		
		return totalTerms;
	}

	/*
	 * Creates an Excel file with the tokens and their occurrences in the
	 * processed file.
	 * 
	 * @param alTerms the ArrayList containing tokenCounts.
	 * 
	 * @param sheetName the name of the sheet to be created.
	 * 
	 * @param columns the codedText of the headers of the sheets
	 * 
	 * @param workbook the workbook (spreadsheet) where to create the sheet.
	 * 
	 * @param headerCellStyle the style of the cells of the header of the
	 * columns in the sheet.
	 * 
	 * @param totalTerms the number of terms counted in the file.
	 */
	private void createXlsxSheet(ArrayList<TokenCount> alTerms, String sheetName, String[] columns, Workbook workbook,
			CellStyle headerCellStyle, int totalTerms)
	{
		// Creates a Sheet for Terms
		Sheet sheet = workbook.createSheet(sheetName);

		// Creates a CellStyle for the Percentage (Column C)
		CellStyle pctCellStyle = workbook.createCellStyle();
		pctCellStyle.setDataFormat(workbook.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(0xa)));

		// Creates a header row
		Row headerRow = sheet.createRow(0);

		// Create header row cells
		for (int i = 0; i < columns.length && columns[i] != null; i++)
		{
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i]);
			cell.setCellStyle(headerCellStyle);
		}
		
		// Create Other rows and cells with terms data
		int occCol = 1;
		int rowNum = 1;
		for (TokenCount tc : alTerms)
		{
			String cat = "", sub = "", grp = "", trm = "";
			String token = tc.getToken();
			int count = tc.getCount();
			double pct = (double) count / (double) totalTerms;
			Row row = sheet.createRow(rowNum++);
			
			if (sheetName.equals("Categories"))
			{
				occCol = 1;
				cat = token;
			}
			if (sheetName.equals("Subcategories"))
			{
				occCol = 2;
				int pos = token.indexOf('-');
				if (pos != -1)
				{
					cat = token.substring(0, pos);
					sub = token.substring(pos+1, token.length());
				}
				else
					cat = token;
			}
			if (sheetName.equals("Groups"))
			{
				occCol = 3;
				int p1 = token.indexOf('-');
				if (p1 != -1)
				{
					int p2 = token.indexOf('-', p1+1);
					cat = token.substring(0, p1);
					sub = token.substring(p1+1, p2);
					grp = token.substring(p2+1, token.length());
				}
				else
					cat = token;
			}
			if (sheetName.equals("Terms"))
			{
				occCol = 4;
				Coding code = new Coding(this.getMask(), token);
				cat = code.getCategory();
				sub = code.getSubcategory();
				grp = code.getGroup();
				trm = code.getTerm();
				
				if (cat.equals("Uncategorised"))
					trm = token;
			}

			Cell dataCell;
			// Prepares cell for Column 'Category'
			dataCell = row.createCell(0);
			dataCell.setCellValue(cat);
			
			// Prepares cells for next Columns
			int col = 1;
			if (occCol >= 2)
			{
				// Prepares cell for Column 'Subcategory"
				dataCell = row.createCell(col++);
				dataCell.setCellValue(sub);
			}
			if (occCol >= 3)
			{
				// Prepares cell for Column 'Group'
				dataCell = row.createCell(col++);
				dataCell.setCellValue(grp);
			}
			if (occCol >= 4)
			{
				// Prepares cell for Column 'Group'
				dataCell = row.createCell(col++);
				dataCell.setCellValue(trm);
			}
			
			// Prepares cell for Column 'Occurrences'
			dataCell = row.createCell(occCol);
			dataCell.setCellValue(count);
			// Prepares cell for Column 'Pct'
			dataCell = row.createCell(occCol+1);
			dataCell.setCellStyle(pctCellStyle);
			dataCell.setCellValue(pct);
		}
		
		// Creates final line Total
		Row row = sheet.createRow(rowNum++);
		// Prepares cell for Column 'Category"
		row.createCell(0).setCellValue("Total");
		// Prepares cell for Column 'Occurrences'
		row.createCell(occCol).setCellValue(totalTerms);
		// Prepares cell for Column 'Pct'
		Cell dataCell = row.createCell(occCol+1);
		dataCell.setCellStyle(pctCellStyle);
		dataCell.setCellValue(1);

		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++)
			sheet.autoSizeColumn(i);
	}

	/*
	 * Creates an Excel file with the tokens and their occurrences in the
	 * processed file.
	 * 
	 * @param wctFileName the name of the output word count codedText file.
	 */
	private void exportWordCountXlsx(String wctFileName)
	{
		int totalTerms = 0;
		String[][] columns = {	
				{ "Category", "Occurrences", "Pct", null, null, null },
				{ "Category", "Subcategory", "Occurrences", "Pct", null, null }, 
				{ "Category", "Subcategory", "Group", "Occurrences", "Pct", null }, 
				{ "Category", "Subcategory", "Group", "Term", "Occurrences", "Pct" } 
			};
		ArrayList<TokenCount> alTerms = tokenCount();
		ArrayList<TokenCount> alCats = new ArrayList<TokenCount>();
		ArrayList<TokenCount> alSubs = new ArrayList<TokenCount>();
		ArrayList<TokenCount> alGrps = new ArrayList<TokenCount>();

		// Counts Categories and Sub-categories
		totalTerms = codingCount(alTerms, alCats, alSubs, alGrps);

		// Creates a Workbook (new HSSFWorkbook()) for generating `.xlsx` file
		Workbook workbook = new XSSFWorkbook();

		/*
		 * CreationHelper helps us create instances of various things like
		 * DataFormat, Hyperlink, RichTextString etc, in a format (HSSF, XSSF)
		 * independent way
		 */
		// CreationHelper createHelper = workbook.getCreationHelper();

		// Creates a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);

		// Creates a CellStyle with the font
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

		// Creates Sheets
		createXlsxSheet(alCats, "Categories", columns[0], workbook, headerCellStyle, totalTerms);
		createXlsxSheet(alSubs, "Subcategories", columns[1], workbook, headerCellStyle, totalTerms);
		createXlsxSheet(alGrps, "Groups", columns[2], workbook, headerCellStyle, totalTerms);
		createXlsxSheet(alTerms, "Terms", columns[3], workbook, headerCellStyle, totalTerms);

		// Write the output to a file
		try
		{
			FileOutputStream fileOut = new FileOutputStream(wctFileName);
			workbook.write(fileOut);
			fileOut.close();

			// Closing the workbook
			workbook.close();
		}
		catch (IOException e)
		{
			System.err.println("Exception while trying to write 'word count' file - " + e);
			e.printStackTrace();
		}
	}

	/*
	 * Processes the commands script informed
	 * processed file.
	 * 
	 * @param cmdTxt the commands text to be processed.
	 */
	public static void processCommandsScript(RegexProcessor rgxPro, String cmdTxt)
	{
		String commands[] = cmdTxt.split("\n");
		
		for (String line : commands)
		{
			String cmd = line.trim();
			String command = cmd.toUpperCase();
			
			command = command.replaceAll("\t", " ");
			command = command.replaceAll("( ){2,}", " ");
			
			//Processes Comments (lines started with '#')
			if (command.startsWith("#"))
			{
				continue;
			}

			//Processes command Process <name>
			if (command.startsWith("PROCESS "))
			{
				String rulesSet = command.substring(8).trim();
				if (rulesSet.endsWith(";"))
				{
					rulesSet = rulesSet.substring(0, rulesSet.length()-1);
					System.out.println("=> Processing '" + rulesSet + "'");
					rgxPro.processRules(rulesSet);
				}
				else
					System.err.println("Error: Command 'Process <rulesSetName>;' expected in '" + cmd + "'");
					
				continue;
			}

			//Processes command Set Text CODED|MARKED
			if (command.startsWith("SETTEXT "))
			{
				if (command.endsWith("CODED;"))
				{
					rgxPro.setText(rgxPro.getCodedText());
					System.out.println("=> Text set to CODED");
					continue;
				}
				if (command.endsWith("MARKED;"))
				{
					rgxPro.setText(rgxPro.getMarkedText());
					System.out.println("=> Text set to MARKED");
					continue;
				}
				System.err.println("Error: Command 'SetText MARKED|CODED;' expected in '" + cmd + "'");
				continue;
			}

			//Processes command SetUpperCase
			if (command.startsWith("SETUPPERCASE;"))
			{
				rgxPro.setText(rgxPro.getCodedText().toUpperCase());
				System.out.println("=> Text set to UPPERCASE");
				continue;
			}

			//Processes command SetLowerCase
			if (command.startsWith("SETLOWERCASE;"))
			{
				rgxPro.setText(rgxPro.getCodedText().toLowerCase());
				System.out.println("=> Text set to LOWERCASE");
				continue;
			}

			//Processes command SetVerbose
			if (command.startsWith("SETVERBOSE;"))
			{
				rgxPro.setVerbose(true);
				System.out.println("=> Verbose set to ON");
				continue;
			}

			//Processes command ResetVerbose
			if (command.startsWith("RESETVERBOSE;"))
			{
				rgxPro.setVerbose(false);
				System.out.println("=> Verbose set to OFF");
				continue;
			}

			//Processes command SetMerking
			if (command.startsWith("SETMARKING;"))
			{
				rgxPro.setMarking(true);
				System.out.println("=> Marking set to ON");
				continue;
			}

			//Processes command ResetMerking
			if (command.startsWith("RESETMARKING;"))
			{
				rgxPro.setMarking(false);
				System.out.println("=> Marking set to OFF");
				continue;
			}

			//Processes command SetCaseInsensitive
			if (command.startsWith("SETCASEINSENSITIVE;"))
			{
				rgxPro.setCaseInsensitive(true);
				System.out.println("=> Case set to INSENSITIVE");
				continue;
			}

			//Processes command SetCaseSensitive
			if (command.startsWith("SETCASESENSITIVE;"))
			{
				rgxPro.setCaseInsensitive(false);
				System.out.println("=> Case set to SENSITIVE");
				continue;
			}

			//Unrecognized Command
			System.err.println("Error: Command '" + cmd + "' not recognised!");
		}
	}
	
	/**
	 * @param args
	 *           command line arguments.
	 */
	public static void main(String[] args)
	{
		File inpFile, rgxFile, cmdFile;
		String inpFileName, cmdFileName, codFileName, mrkFileName, rgxFileName, wctFileName, logFileName, impTxt, cmdTxt, fmtMsk;
		RegexProcessor rgxPro;

		// set of rules informed for command line arguments processing. The ':'
		// indicates that a value will be expected when that argument is
		// informed.
		String[][] rules = { 
			{ "-i:", "Informs the name of the text (.txt) input file (mandatory)." },
			{ "-r:", "Informs the name of the rules (.xlsx) input file (mandatory)." },
			{ "-s:", "Informs the name of the script (.txt) input file (mandatory)." },
			{ "-f:", "Informs the format string (mask) for 'conding' (mandatory)." },
			{ "-w:", "Informs the name of the 'word count' (.xlsx) output file (optional). If not informed, the input file name is used with the extension '.wct.xlsx'." },
			{ "-c:", "Informs the name of the 'coded' (.txt) output file (optional). If not informed, the input file name is used with the extension '.coded.txt'." },
			{ "-m:", "Informs the name of the 'marked' (.txt) output file (optional). If not informed, the input file name is used with the extension '.marked.txt'." },
		};

		// Redirects Sytem.out and System.err to "RegexProcessor.log"
		logFileName = "RegexProcessor.log";
		try
		{
			PrintStream out = new PrintStream(new File(logFileName));
			System.setOut(out);
			System.setErr(out);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		System.out.println("Process initiated!");
		// Processes arguments
		ArgsProcessor arguments = new ArgsProcessor(rules);
		if (!arguments.parseArgs(args))
		{
			System.out.println(arguments.usage());
			System.exit(-1);
		}

		// Verify required arguments
		if (!arguments.isInformed("-i") || !arguments.isInformed("-r")  || !arguments.isInformed("-s")  || !arguments.isInformed("-f"))
		{
			System.out.println(arguments.usage());
			System.exit(-1);
		}

		// Gets informed input file name
		inpFileName = arguments.getValues("-i").get(0);
		System.out.println("Input file set to '" + inpFileName + "'");

		// Prepares output coded file name
		if (!arguments.isInformed("-c"))
			codFileName = inpFileName.replace(".txt", ".coded.txt");
		else
			codFileName = arguments.getValues("-c").get(0);
		System.out.println("Code file set to '" + codFileName + "'");

		// Prepares output coded file name
		if (!arguments.isInformed("-m"))
			mrkFileName = inpFileName.replace(".txt", ".marked.txt");
		else
			mrkFileName = arguments.getValues("-m").get(0);
		System.out.println("Mark file set to '" + mrkFileName + "'");

		// Prepares 'TXT word count' file name
		if (!arguments.isInformed("-w"))
			wctFileName = inpFileName.replace(".txt", ".wct.xlsx");
		else
			wctFileName = arguments.getValues("-w").get(0);
		System.out.println("WordCount file set to '" + wctFileName + "'");

		// Gets fmt string (mask) for 'coding'
		fmtMsk = arguments.getValues("-f").get(0);
		System.out.println("Mask set to '" + fmtMsk + "'");

		// Checks if input file exists
		inpFile = new File(inpFileName);
		if (!inpFile.exists())
		{
			System.err.println("Input Text File '" + inpFileName + "' not found!");
			System.exit(-1);
		}

		// Checks for rules spreadsheet file
		rgxFileName = arguments.getValues("-r").get(0);
		rgxFile = new File(rgxFileName);
		if (!rgxFile.exists())
		{
			System.err.println("Regex Rules File '" + rgxFileName + "' not found!");
			System.exit(-1);
		}

		// Checks for command script file
		cmdFileName = arguments.getValues("-s").get(0);
		cmdFile = new File(cmdFileName);
		if (!cmdFile.exists())
		{
			System.err.println("Commands Script File '" + cmdFileName + "' not found!");
			System.exit(-1);
		}

		// Input codedText file reading
		impTxt = TextFileSupport.stringFromFile(inpFile);

		// Input scripts from command file
		cmdTxt = TextFileSupport.stringFromFile(cmdFile);

		// RegEx Processing
		rgxPro = new RegexProcessor(impTxt);
		rgxPro.setMask(fmtMsk);

		rgxPro.loadRules(rgxFile);

		processCommandsScript(rgxPro, cmdTxt);

		// Coded Text output file writing
		TextFileSupport.stringToFilename(rgxPro.getCodedText(), codFileName);
		// Marked Text output file writing
		TextFileSupport.stringToFilename(rgxPro.getMarkedText(), mrkFileName);
		// Word Count files writing
		rgxPro.exportWordCountXlsx(wctFileName);

		// Normal End signaling
		System.out.println("Normal termination!");
	}
}
