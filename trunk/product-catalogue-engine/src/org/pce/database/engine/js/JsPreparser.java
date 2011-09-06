package org.pce.database.engine.js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsPreparser {

	private Map<String, String> conditionMapping = new HashMap<String, String>();
	{
		conditionMapping.put("(.*) is (.*)", "$1 == $2");
		conditionMapping.put("(.*) equals (.*)", "$1 == $2");
		conditionMapping.put("(.*) and (.*)", "$1 && $2");
		conditionMapping.put("(.*) or (.*)", "$1 || $2");
		conditionMapping.put("(.*) startswith\\s*\\((.*?)\\)(.*)", "$1.indexOf($2) == 0 $3");
		conditionMapping.put("(.*) than (.*)", "$1 $2");
		conditionMapping.put("(.*) before (.*)", "$1 < $2");
		conditionMapping.put("(.*) after (.*)", "$1 > $2");
		conditionMapping.put("(.*) less (.*)", "$1 < $2");
		conditionMapping.put("(.*) bigger (.*)", "$1 > $2");
		conditionMapping.put("(.*) Less (.*)", "$1 <= $2");
		conditionMapping.put("(.*) Bigger (.*)", "$1 >=	 $2");
		conditionMapping.put("(.*) Before (.*)", "$1 <= $2");
		conditionMapping.put("(.*) After (.*)", "$1 >= $2");
		conditionMapping.put("(.*) smaller (.*)", "$1 < $2");
		conditionMapping.put("(.*) bigger (.*)", "$1 > $2");

	}

	private boolean isLiteral(String s) {
		if (s.length() == 0)
			return false;
		char c = s.charAt(0);
		return c >= '0' && c <= '9' || c == '\'';
	}

	private String quote(String literal) {
		char c = literal.charAt(0);
		if (c == '\'')
			return literal;
		return '\'' + literal + '\'';
	}

	private String removeFirst(String text, char c) {
		int i = text.indexOf(c);
		return text.substring(0, i) + text.substring(i + 1);
	}

	private String removeLast(String text, char c) {
		int i = text.lastIndexOf(c);
		return text.substring(0, i) + text.substring(i + 1);
	}

	private String stripBrackets(String text) {
		return removeFirst(removeLast(text, ']'), '[');
	}

	private String append(List<String> parts) {
		if (parts.isEmpty())
			return "context";
		String part = parts.get(0);
		parts = parts.subList(1, parts.size());
		if (part.startsWith("["))
			return "context.DB(context." + stripBrackets(part) + "("
					+ append(parts) + "))";
		if (!isLiteral(part))
			return "context." + part + "(" + append(parts) + ")";
		return "context.literal(" + append(parts) + "," + quote(part) + ")";
	}

	private void addPart(String part, List<String> parts) {
		if (part.trim().length() == 0)
			return;
		parts.add(part);
	}

	public String parse(String s) {
		List<String> parts = new ArrayList<String>();
		return parse(s, parts);
	}

	public String parse(String s, List<String> parts) {
		String part = "";
		String string = "";
		boolean inString = false;
		boolean inParenthesis = false;
		s += " ";
		for (char c : s.toCharArray()) {
			switch (c) {
			case '\'':
				inString = !inString;
				if (!inString) {
					addPart('\'' + string + '\'', parts);
					string = "";
				}
				break;
			case ' ':
				if (!inString) {
					addPart(part, parts);
					part = "";
				} else
					string += c;
				break;
			default:
				if (inString)
					string += c;
				else
					part += c;
			}
		}
		return append(parts);
	}

	public String parseCondition(String line){
		String code = line;
		for (String ce : conditionMapping.keySet())
			while (code.matches(ce)) {
				code = code.replaceAll(ce,
						conditionMapping.get(ce));
			}
		return code;
	}
}
