package com.html5parser.algorithms;

import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.factories.TokenizerStateFactory;

public class GenericRawTextElementParsing {

	public static ParserContext run(ParserContext context, TagToken token) {
		TokenizerStateFactory tokenizerFactory = TokenizerStateFactory
				.getInstance();
		InsertionModeFactory insertionModeFactory = InsertionModeFactory
				.getInstance();
		InsertAnHTMLElement.run(context, token);
		context.getTokenizerContext().setNextState(
				tokenizerFactory.getState(TokenizerState.RCDATA_state));
		context.setOriginalInsertionMode(context.getInsertionMode());
		context.setInsertionMode(insertionModeFactory
				.getInsertionMode(InsertionMode.text));
		return context;
	}
}
