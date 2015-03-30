package com.html5parser.parser;

import com.html5parser.classes.ParserContext;
import com.html5parser.interfaces.ITokenizer;

public class Tokenizer implements ITokenizer {

	public ParserContext tokenize(ParserContext context) {
		return context.getTokenizerContext().getNextState().process(context);
	}

}
