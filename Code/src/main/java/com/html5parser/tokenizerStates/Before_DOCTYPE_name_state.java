package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Before_DOCTYPE_name_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();
		DocTypeToken docToken = null;

		switch (tokenizerContext.getCurrentASCIICharacter()) {

		// U+0009 CHARACTER TABULATION (tab)
		case TAB:

			// U+000A LINE FEED (LF)
		case LF:

			// U+000C FORM FEED (FF)
		case FF:

			// U+0020 SPACE
			// Ignore the character.
		case SPACE:
			// Ignore the character.
			break;

		// U+0041 LATIN CAPITAL LETTER A through to U+005A LATIN CAPITAL LETTER
		// Z
		// Create a new DOCTYPE token. Set the token's name to the lowercase
		// version of the current input character (add 0x0020 to the character's
		// code point). Switch to the DOCTYPE name state.
		case LATIN_CAPITAL_LETTER:
			currentChar += 0x0020;
			docToken = new DocTypeToken(currentChar);
			tokenizerContext.setCurrentToken(docToken);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.DOCTYPE_name_state));
			break;

		// U+0000 NULL
		// Parse error. Create a new DOCTYPE token. Set the token's name to a
		// U+FFFD REPLACEMENT CHARACTER character. Switch to the DOCTYPE name
		// state.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			docToken = new DocTypeToken(0xFFFD);
			tokenizerContext.setCurrentToken(docToken);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.DOCTYPE_name_state));
			break;

		// U+003E GREATER-THAN SIGN (>)
		// Parse error. Create a new DOCTYPE token. Set its force-quirks flag to
		// on. Switch to the data state. Emit the token.
		case GREATER_THAN_SIGN:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			docToken = new DocTypeToken(null);
			docToken.setForceQuircksFlag(true);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.emitCurrentToken(docToken);
			break;

		// EOF
		// Parse error. Switch to the data state. Create a new DOCTYPE token.
		// Set its force-quirks flag to on. Emit the token. Reconsume the EOF
		// character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			docToken = new DocTypeToken(null);
			docToken.setForceQuircksFlag(true);
			tokenizerContext.emitCurrentToken(docToken);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Create a new DOCTYPE token. Set the token's name to the current input
		// character. Switch to the DOCTYPE name state.
		default:
			docToken = new DocTypeToken(currentChar);
			tokenizerContext.setCurrentToken(docToken);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.DOCTYPE_name_state));
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}