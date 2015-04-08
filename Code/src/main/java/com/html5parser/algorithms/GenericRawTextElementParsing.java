package com.html5parser.algorithms;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;

public class GenericRawTextElementParsing {

	public static ParserContext run(ParserContext context, TagToken token) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		InsertAnHTMLElement.run(context, token);
		context.getTokenizerContext().setNextState(factory
				.getState(TokenizerState.RCDATA_state));
		context.setOriginalInsertionMode(context.getInsertionMode());
		// TODO context.setInsertionMode(new TextInsertionMode);
		new UnsupportedOperationException();
		return context;
	}
}
