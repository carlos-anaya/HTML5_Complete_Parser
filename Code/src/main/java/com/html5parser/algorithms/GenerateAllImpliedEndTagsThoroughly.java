package com.html5parser.algorithms;

import com.html5parser.classes.ParserContext;

public class GenerateAllImpliedEndTagsThoroughly {
	
	public static ParserContext run(ParserContext context, String tagException) {
		String nodeName = context.getCurrentNode().getNodeName();
		while(nodeName.equals("dd") || nodeName.equals("dt") 
				|| nodeName.equals("li")
				|| nodeName.equals("option") 
				|| nodeName.equals("optgroup") 
				|| nodeName.equals("p") 
				|| nodeName.equals("rp") 
				|| nodeName.equals("rt") 
				){
			if(tagException!=null && tagException.equals(nodeName)) break;
			context.getOpenElements().pop();
			nodeName = context.getCurrentNode().getNodeName();
		}
		return context;
	}

	public static ParserContext run(ParserContext context) {
		return run( context, null);
	}
}
