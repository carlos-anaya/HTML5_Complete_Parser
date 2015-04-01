package com.html5parser.tokenizerStates;

import com.html5parser.classes.ASCIICharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;

public class Script_data_end_tag_open_state implements ITokenizerState{

	@Override
	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();
		ASCIICharacter asciiCharacter = tokenizerContext.getCurrentASCIICharacter();
		
		switch (asciiCharacter) {
		case LATIN_CAPITAL_LETTER:
			/* 
			 * Create a new end tag token, 
			 * and set its tag name to the lowercase version of the current input character 
			 * (add 0x0020 to the character's code point). 
			 * Append the current input character to the temporary buffer. 
			 * Finally, switch to the script data end tag name state. 
			 * (Don't emit the token yet; further details will be filled in before it is emitted.)
			 */
			currentChar += 0x0020;
		case LATIN_SMALL_LETTER:
			String addedChar = String.valueOf(Character.toChars(currentChar));
			Token token = new Token(TokenType.end_tag, addedChar);
			tokenizerContext.setTemporaryBuffer(tokenizerContext.getTemporaryBuffer().concat(
					String.valueOf(Character.toChars(currentChar))));
			tokenizerContext.setCurrentToken(token);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_end_tag_name_state));
			break;
		default:
			/*
			 * Switch to the script data state. 
			 * Emit a U+003C LESS-THAN SIGN character token and a U+002F SOLIDUS character token. 
			 * Reconsume the current input character.
			 */
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_state));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String
					.valueOf(0x003C)));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String
					.valueOf(0x002F)));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;
			
		}
		
		context.setTokenizerContext(tokenizerContext);
		return context;
	}

}
