package com.html5parser.interfaces;

import com.html5parser.classes.ParserContext;

public interface ITreeConstructor {
	public ParserContext consumeToken(
			ParserContext parserContext);
}
