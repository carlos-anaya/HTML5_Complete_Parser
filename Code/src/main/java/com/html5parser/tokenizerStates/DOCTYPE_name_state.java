package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class DOCTYPE_name_state implements ITokenizerState {

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
			// Switch to the after DOCTYPE name state.
		case SPACE:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.After_DOCTYPE_name_state));
			break;

		// U+003E GREATER-THAN SIGN (>)
		// Switch to the data state. Emit the current DOCTYPE token.
		case GREATER_THAN_SIGN:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// U+0041 LATIN CAPITAL LETTER A through to U+005A LATIN CAPITAL LETTER
		// Z
		// Append the lowercase version of the current input character (add
		// 0x0020 to the character's code point) to the current DOCTYPE token's
		// name.
		case LATIN_CAPITAL_LETTER:
			currentChar += 0x0020;
			tokenizerContext.getCurrentToken().appendValue(currentChar);
			break;

		// U+0000 NULL
		// Parse error. Append a U+FFFD REPLACEMENT CHARACTER character to the
		// current DOCTYPE token's name.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.appendValue(0xFFFD);
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
		// name.
		default:
			tokenizerContext.getCurrentToken().appendValue(currentChar);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}