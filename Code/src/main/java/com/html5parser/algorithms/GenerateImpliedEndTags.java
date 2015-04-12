package com.html5parser.algorithms;

import com.html5parser.classes.ParserContext;

public class GenerateImpliedEndTags {

	public static ParserContext run(ParserContext context) {
		String nodeName = context.getCurrentNode().getNodeName();
		while (nodeName.equals("caption") || nodeName.equals("colgroup")
				|| nodeName.equals("tbody") || nodeName.equals("td")
				|| nodeName.equals("tfoot") || nodeName.equals("th")
				|| nodeName.equals("thead") || nodeName.equals("tr")

				|| nodeName.equals("dd") || nodeName.equals("dt")
				|| nodeName.equals("li") || nodeName.equals("option")
				|| nodeName.equals("optgroup") || nodeName.equals("p")
				|| nodeName.equals("rp") || nodeName.equals("rt")) {
			context.getOpenElements().pop();
			nodeName = context.getCurrentNode().getNodeName();
		}
		return context;
	}
	public static ParserContext run(ParserContext context, String exception) {
		String nodeName = context.getCurrentNode().getNodeName();
		while (!nodeName.equals(exception) && nodeName.equals("caption") || nodeName.equals("colgroup")
				|| nodeName.equals("tbody") || nodeName.equals("td")
				|| nodeName.equals("tfoot") || nodeName.equals("th")
				|| nodeName.equals("thead") || nodeName.equals("tr")
				
				|| nodeName.equals("dd") || nodeName.equals("dt")
				|| nodeName.equals("li") || nodeName.equals("option")
				|| nodeName.equals("optgroup") || nodeName.equals("p")
				|| nodeName.equals("rp") || nodeName.equals("rt")) {
			context.getOpenElements().pop();
			nodeName = context.getCurrentNode().getNodeName();
		}
		return context;
	}
}
