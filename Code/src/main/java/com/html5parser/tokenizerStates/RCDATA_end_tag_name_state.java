package com.html5parser.tokenizerStates;

import com.html5parser.classes.ASCIICharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;

public class RCDATA_end_tag_name_state implements ITokenizerState {

	@Override
	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();		
		Token currentToken = tokenizerContext.getCurrentToken();
		int currentChar = tokenizerContext.getCurrentInputCharacter();
		
		ASCIICharacter asciiCharacter = tokenizerContext.getCurrentASCIICharacter();
		
		switch(asciiCharacter){
		
		case TAB:
		case LF:
		case FF:
		case SPACE:
			throw new UnsupportedOperationException();
		case DASH:
			throw new UnsupportedOperationException();
//			tokenizerContext.setNextState(new Self_closing_start_tag_state());
		case GREATER_THAN_SIGN:
			tokenizerContext.setFlagEmitToken(true);;			
		case LATIN_CAPITAL_LETTER:
			currentChar += 0x0020;
			currentToken.setValue(currentToken.getValue().concat(
					String.valueOf(Character.toChars(currentChar))));
			break;
		case LATIN_SMALL_LETTER:
			currentToken.setValue(currentToken.getValue().concat(
				String.valueOf(Character.toChars(currentChar))));
			break;
		default:
			tokenizerContext.setNextState(factory.getState(TokenizerState.RCDATA_state));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String.valueOf(0x003C)));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String.valueOf(0x002F)));
			//a character token for each of the characters in the temporary buffer (in the order they were added to the buffer).
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);			
			break;
		}
		
		context.setTokenizerContext(tokenizerContext);
		
		return context;
	}


}
