package com.html5parser.tokenizerStates;

import com.html5parser.classes.ASCIICharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;

public class Script_data_escaped_less_than_sign_state implements ITokenizerState {

	@Override
	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();
		ASCIICharacter asciiCharacter = tokenizerContext.getCurrentASCIICharacter();
		
		switch (asciiCharacter) {
		case DASH:
			/* 
			 * Set the temporary buffer to the empty string. 
			 * Switch to the script data escaped end tag open state.
			 */
			tokenizerContext.setTemporaryBuffer("");
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_escaped_end_tag_open_state));
			break;
		case LATIN_CAPITAL_LETTER:
			/*
			 * change to lower case
			 */
			currentChar += 0x0020;
		case LATIN_SMALL_LETTER:
			/*
			 * Set the temporary buffer to the empty string. 
			 * Append the current input character to the temporary buffer.
			 * Switch to the script data double escape start state. 
			 * Emit a U+003C LESS-THAN SIGN character token and the current input character as a character token.
			 */
			tokenizerContext.setTemporaryBuffer("");
			tokenizerContext.setTemporaryBuffer(tokenizerContext.getTemporaryBuffer().concat(
					String.valueOf(Character.toChars(currentChar))));
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_double_escape_start_state));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String
					.valueOf(0x003C)));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String
							.valueOf(currentChar)));
			break;
			
		default:
			/*
			 * Switch to the script data escaped state. 
			 * Emit a U+003C LESS-THAN SIGN character token. 
			 * Reconsume the current input character.
			 */
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_escaped_state));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String
					.valueOf(Character.toChars(0x003C))));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;
		}
		
		context.setTokenizerContext(tokenizerContext);
		return context;
	}

}
