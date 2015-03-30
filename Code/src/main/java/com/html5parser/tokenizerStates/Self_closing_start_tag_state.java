package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Self_closing_start_tag_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// U+003E GREATER-THAN SIGN (>)
		// Set the self-closing flag of the current tag token. Switch to the
		// data state. Emit the current tag token.
		case GREATER_THAN_SIGN:
			// If end tag token is emitted with its self-closing flag set, it is
			// a parse error.
			if (tokenizerContext.getCurrentToken().getType() == TokenType.end_tag) {
				TagToken token = (TagToken) tokenizerContext.getCurrentToken();
				if (!token.isFlagSelfClosingTag())
					context.addParseErrors(ParseErrorType.EndTagWithSelfClosingFlag);
			}
			
			((TagToken) tokenizerContext.getCurrentToken())
					.setFlagSelfClosingTag(true);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// EOF
		// Parse error. Switch to the data state. Reconsume the EOF character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Parse error. Switch to the before attribute name state. Reconsume the
		// character.
		default:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Before_attribute_name_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}