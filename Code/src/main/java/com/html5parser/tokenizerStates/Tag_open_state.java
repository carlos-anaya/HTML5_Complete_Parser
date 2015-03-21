package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Tag_open_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		Token token = null;
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		// "!" (U+0021)
		// Switch to the markup declaration open state.
		case EXCLAMATION_MARK:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Markup_declaration_open_state));
			break;

		// "/" (U+002F)
		// Switch to the end tag open state.
		case DASH:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.End_tag_open_state));
			break;

		// "?" (U+003F)
		// Parse error. Switch to the bogus comment state.
		case QUESTION_MARK:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Bogus_comment_state));
			break;

		// U+0041 LATIN CAPITAL LETTER A through to U+005A LATIN CAPITAL LETTER
		// Z Create a new start tag token, set its tag name to the lowercase
		// version of the current input character (add 0x0020 to the character's
		// code point), then switch to the tag name state. (Don't emit the token
		// yet; further details will be filled in before it is emitted.)
		case LATIN_CAPITAL_LETTER:
			currentChar += 0x0020;

			// U+0061 LATIN SMALL LETTER A through to U+007A LATIN SMALL LETTER
			// Z
			// Create a new start tag token, set its tag name to the current
			// input
			// character, then switch to the tag name state. (Don't emit the
			// token
			// yet; further details will be filled in before it is emitted.)
		case LATIN_SMALL_LETTER:
			Token currentToken = new TagToken(TokenType.start_tag, currentChar);
			tokenizerContext.setCurrentToken(currentToken);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Tag_name_state));
			break;
		// Anything else
		// Parse error. Switch to the data state. Emit a U+003C LESS-THAN
		// SIGN character token. Reconsume the current input character.
		default:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			token = new Token(TokenType.character, 0x003C);
			tokenizerContext.emitCurrentToken(token);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}