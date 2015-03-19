package com.html5parser.CompleteParser;

import com.html5parser.Classes.ParserContext;
import com.html5parser.Interfaces.ITokenizer;

public class Tokenizer implements ITokenizer {

	public ParserContext tokenize(ParserContext context) {
		return context.getTokenizerContext().getNextState().process(context);
	}

}
