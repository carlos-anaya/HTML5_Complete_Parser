package com.html5parser.tokenizerStates;

import com.html5parser.classes.ASCIICharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;

public class Script_data_escape_start_dash_state implements ITokenizerState{

	@Override
	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		
		ASCIICharacter asciiCharacter = tokenizerContext.getCurrentASCIICharacter();
		
		switch (asciiCharacter) {
		case HYPHEN_MINUS:
			/* 
			 * Switch to the script data escaped dash dash state. 
			 * Emit a U+002D HYPHEN-MINUS character token.
			 */
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_escaped_dash_dash_state));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String
					.valueOf(0x002D)));
		default:
			/*
			 * Switch to the script data state. Reconsume the current input character.
			 */
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
		}
		
		context.setTokenizerContext(tokenizerContext);
		return context;
	}
	

}
