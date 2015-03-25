package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class DOCTYPE_system_identifier_single_quoted_state implements
		ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();
		DocTypeToken docToken = null;

		switch (tokenizerContext.getCurrentASCIICharacter()) {

		// "'" (U+0027)
		// Switch to the after DOCTYPE system identifier state.
		case APOSTROPHE:
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.After_DOCTYPE_system_identifier_state));
			break;

		// U+0000 NULL
		// Parse error. Append a U+FFFD REPLACEMENT CHARACTER character to the
		// current DOCTYPE token's system identifier.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.appendSystemIdentifier(0xFFFD);
			break;

		// U+003E GREATER-THAN SIGN (>)
		// Parse error. Set the DOCTYPE token's force-quirks flag to on. Switch
		// to the data state. Emit that DOCTYPE token.
		case GREATER_THAN_SIGN:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.setForceQuircksFlag(true);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// EOF
		// Parse error. Switch to the data state. Set the DOCTYPE token's
		// force-quirks flag to on. Emit that DOCTYPE token. Reconsume the EOF
		// character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.setForceQuircksFlag(true);
			tokenizerContext.setFlagEmitToken(true);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Append the current input character to the current DOCTYPE token's
		// system identifier.
		default:
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.appendSystemIdentifier(currentChar);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}