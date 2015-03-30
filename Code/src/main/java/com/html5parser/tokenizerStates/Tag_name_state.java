package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Tag_name_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// "tab" (U+0009)
		// "LF" (U+000A)
		// "FF" (U+000C)
		// U+0020 SPACE
		// Switch to the before attribute name state.
		case TAB:
		case LF:
		case FF:
		case SPACE:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Before_attribute_name_state));
			break;
		// "/" (U+002F)
		// Switch to the self-closing start tag state.
		case DASH:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Self_closing_start_tag_state));
			break;
		// U+003E GREATER-THAN SIGN (>)
		// Switch to the data state. Emit the current tag token.
		case GREATER_THAN_SIGN:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// U+0000 NULL
		// Parse error. Append a U+FFFD REPLACEMENT CHARACTER character to the
		// current tag token's tag name.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.getCurrentToken().appendValue(0xFFFD);
			break;

		// EOF
		// Parse error. Switch to the data state. Reconsume the EOF character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// U+0041 LATIN CAPITAL LETTER A through to U+005A LATIN CAPITAL LETTER
		// Z
		// Append the lowercase version of the current input character (add
		// 0x0020 to the character's code point) to the current tag token's tag
		// name.
		case LATIN_CAPITAL_LETTER:
			currentChar += 0x0020;

			// Anything else
			// Append the current input character to the current tag token's tag
			// name.
		default:
			tokenizerContext.getCurrentToken().appendValue(currentChar);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}