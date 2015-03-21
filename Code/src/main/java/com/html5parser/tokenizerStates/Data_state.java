package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Data_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory;
		Token token = null;
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();

		switch (currentChar) {
		// U+0026 AMPERSAND (&)
		// Switch to the character reference in data state.
		case 0x0026:
			factory = TokenizerStateFactory.getInstance();
			tokenizerContext
					.setNextState(factory
							.getState(TokenizerState.Character_reference_in_data_state));
			break;

		// U+003C LESS-THAN SIGN (<)
		// Switch to the character reference in data state.
		case 0x003C:
			factory = TokenizerStateFactory.getInstance();
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Tag_open_state));
			break;

		// U+0000 NULL
		// Parse error. Emit the current input character as a character token.
		case 0x0000:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			token = new Token(TokenType.character, currentChar);
			tokenizerContext.emitCurrentToken(token);
			break;

		// EOF
		// Emit an end-of-file token.
		case -1:
			token = new Token(TokenType.end_of_file, null);
			tokenizerContext.emitCurrentToken(token);
			break;

		// Anything else
		// Emit the current input character as a character token.
		default:
			token = new Token(TokenType.character, currentChar);
			tokenizerContext.emitCurrentToken(token);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}