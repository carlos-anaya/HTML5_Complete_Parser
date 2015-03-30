package com.html5parser.parser;

import com.html5parser.classes.ParserContext;
import com.html5parser.interfaces.ITreeConstructor;

public class TreeConstructor implements ITreeConstructor {
	public ParserContext consumeToken(ParserContext parserContext) {
		return parserContext.getInsertionMode().process(parserContext);
	}
}
