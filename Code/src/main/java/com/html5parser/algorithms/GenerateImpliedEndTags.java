package com.html5parser.algorithms;

import com.html5parser.classes.ParserContext;

public class GenerateImpliedEndTags {

	public static ParserContext run(ParserContext context) {
		String nodeName = context.getCurrentNode().getNodeName();
		// nodeName.equals("caption") || nodeName.equals("colgroup")
		// || nodeName.equals("tbody") || nodeName.equals("td")
		// || nodeName.equals("tfoot") || nodeName.equals("th")
		// || nodeName.equals("thead") || nodeName.equals("tr")

		// || nodeName.equals("dd") || nodeName.equals("dt")
		// || nodeName.equals("li") || nodeName.equals("option")
		// || nodeName.equals("optgroup") || nodeName.equals("p")
		// || nodeName.equals("rp") || nodeName.equals("rt")

		// When the steps below require the UA to generate implied end tags,
		// then, while the current node is a dd element, a dt element, an li
		// element, an option element, an optgroup element, a p element, an rb
		// element, an rp element, an rt element, or an rtc element, the UA must
		// pop the current node off the stack of open elements.
		while (isOneOf(nodeName, new String[] { "dd", "dt", "li", "option",
				"optgroup", "p", "rb", "rp", "rt", "rtc" })) {
			context.getOpenElements().pop();
			nodeName = context.getCurrentNode().getNodeName();
		}
		return context;
	}

	public static ParserContext run(ParserContext context, String exception) {
		String nodeName = context.getCurrentNode().getNodeName();
		// If a step requires the UA to generate implied end tags but lists an
		// element to exclude from the process, then the UA must perform the
		// above steps as if that element was not in the above list.
		while (!nodeName.equals(exception)
				&& isOneOf(nodeName, new String[] { "dd", "dt", "li", "option",
						"optgroup", "p", "rb", "rp", "rt", "rtc" })) {
			context.getOpenElements().pop();
			nodeName = context.getCurrentNode().getNodeName();
		}
		return context;
	}

	private static Boolean isOneOf(String value, String[] values) {
		for (String s : values)
			if (s.equals(value))
				return true;

		return false;
	}
}
