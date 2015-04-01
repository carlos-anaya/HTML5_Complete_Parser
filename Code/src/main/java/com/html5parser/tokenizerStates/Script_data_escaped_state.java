package com.html5parser.tokenizerStates;

import com.html5parser.classes.ASCIICharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class Script_data_escaped_state implements ITokenizerState{

	@Override
	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		
		ASCIICharacter asciiCharacter = tokenizerContext.getCurrentASCIICharacter();
		
		switch (asciiCharacter) {
		case HYPHEN_MINUS:
			/* 
			 * Switch to the script data escaped dash state. 
			 * Emit a U+002D HYPHEN-MINUS character token.
			 */
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_escaped_dash_state));
			tokenizerContext.emitCurrentToken(new Token(TokenType.character, String
					.valueOf(0x002D)));
			break;
		case LESS_THAN_SIGN:
			/* 
			 * Switch to the script data escaped less-than sign state.
			 */
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Script_data_escaped_less_than_sign_state));
			break;
		case NULL:
			/*
			 * Parse error. Emit a U+FFFD REPLACEMENT CHARACTER character token.
			 */
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			break;
		case EOF:
			/*
			 * Switch to the data state. Parse error. Reconsume the EOF character.
			 */
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;
			
		default:
			/*
			 * Emit the current input character as a character token.
			 */
			tokenizerContext.setFlagEmitToken(true);
			break;
		}
		
		context.setTokenizerContext(tokenizerContext);
		return context;
	}

}
