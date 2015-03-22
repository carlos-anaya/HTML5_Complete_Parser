package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Before_attribute_name_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// "tab" (U+0009)
		// "LF" (U+000A)
		// "FF" (U+000C)
		// U+0020 SPACE
		// Ignore the character..
		case TAB:
		case LF:
		case FF:
		case SPACE:
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
		// Parse error. Start a new attribute in the current tag token. Set that
		// attribute's name to a U+FFFD REPLACEMENT CHARACTER character, and its
		// value to the empty string. Switch to the attribute name state.
		case NULL:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			((TagToken) tokenizerContext.getCurrentToken())
					.createAttribute(0xFFFD);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Attribute_name_state));
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
		// Start a new attribute in the current tag token. Set that attribute's
		// name to the lowercase version of the current input character (add
		// 0x0020 to the character's code point), and its value to the empty
		// string. Switch to the attribute name state.
		case LATIN_CAPITAL_LETTER:
			currentChar += 0x0020;
			((TagToken) tokenizerContext.getCurrentToken())
					.createAttribute(currentChar);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Attribute_name_state));
			break;
		// U+0022 QUOTATION MARK (")
		// "'" (U+0027)
		// U+003C LESS-THAN SIGN (<)
		// "=" (U+003D)
		// Parse error. Treat it as per the "anything else" entry below.
		case QUOTATION_MARK:
		case APOSTROPHE:
		case LESS_THAN_SIGN:
		case EQUALS_SIGN:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			// Anything else
			// Start a new attribute in the current tag token. Set that
			// attribute's
			// name to the current input character, and its value to the empty
			// string. Switch to the attribute name state.
		default:
			((TagToken) tokenizerContext.getCurrentToken())
					.createAttribute(currentChar);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Attribute_name_state));
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}