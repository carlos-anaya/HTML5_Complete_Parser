package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class DOCTYPE_state implements ITokenizerState {

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		tokenizerContext.getCurrentInputCharacter();

		switch (tokenizerContext.getCurrentASCIICharacter()) {
		
		// U+0009 CHARACTER TABULATION (tab)
		// Switch to the character reference in data state.
		case TAB:
			
		// U+000A LINE FEED (LF)
		// Switch to the character reference in data state.
		case LF:
			
		// U+000C FORM FEED (FF)
		// Switch to the character reference in data state.
		case FF:
			
		// U+0020 SPACE
		// Switch to the character reference in data state.
		case SPACE:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Before_DOCTYPE_name_state));
			break;

		// EOF
		// Parse error. Switch to the data state. Create a new DOCTYPE token.
		// Set its force-quirks flag to on. Emit the token. Reconsume the EOF
		// character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			DocTypeToken docToken = new DocTypeToken(null);
			docToken.setForceQuircksFlag(true);
			tokenizerContext.emitCurrentToken(docToken);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		// Parse error. Switch to the before DOCTYPE name state. Reconsume the
		// character.
		default:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Before_DOCTYPE_name_state));
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}