package com.html5parser.CompleteParser;

import com.html5parser.Classes.ParserContext;
import com.html5parser.Interfaces.ITreeConstructor;

public class TreeConstructor implements ITreeConstructor {
	public ParserContext consumeToken(ParserContext parserContext) {
		return parserContext.getInsertionMode().process(parserContext);
	}
}
