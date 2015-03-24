package com.html5parser.tokenizerStates;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.parseError.ParseErrorType;

public class After_DOCTYPE_name_state implements ITokenizerState {

	/**
	 * The value of this variable is publicHex or system.
	 * This is compared to the six characters after the DOCTYPE keyword
	 */
	private int[] afterDOCTYPE_characters;
	private final int[] publicHex = { 0x0050, 0x0055, 0x0042, 0x004C, 0x0049,
			0x0043 };
	private final int[] system = { 0x0053, 0x0059, 0x0053, 0x0054, 0x0045,
			0x004D };

	private int afterDOCTYPE_characters_Index = 0;

	public ParserContext process(ParserContext context) {
		TokenizerStateFactory factory = TokenizerStateFactory.getInstance();
		TokenizerContext tokenizerContext = context.getTokenizerContext();
		int currentChar = tokenizerContext.getCurrentInputCharacter();
		DocTypeToken docToken = null;

		switch (tokenizerContext.getCurrentASCIICharacter()) {

		// U+0009 CHARACTER TABULATION (tab)
		case TAB:

			// U+000A LINE FEED (LF)
		case LF:

			// U+000C FORM FEED (FF)
		case FF:

			// U+0020 SPACE
			// Ignore the character.
		case SPACE:
			// Ignore the character.
			break;

		// U+003E GREATER-THAN SIGN (>)
		// Switch to the data state. Emit the current DOCTYPE token.
		case GREATER_THAN_SIGN:
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			tokenizerContext.setFlagEmitToken(true);
			break;

		// EOF
		// Parse error. Switch to the data state. Set the DOCTYPE token's
		// force-quirks flag to on. Emit that DOCTYPE token. Reconsume the EOF
		// character.
		case EOF:
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Data_state));
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.setForceQuircksFlag(true);
			tokenizerContext.setFlagEmitToken(true);
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(true);
			break;

		// Anything else
		default:
			if (afterDOCTYPE_characters == null) {
				if (currentChar == 0x0050 || currentChar == 0x0070) {// character
																		// P or
																		// p
					afterDOCTYPE_characters = publicHex;
					afterDOCTYPE_characters_Index = afterDOCTYPE_characters_Index + 1;
					break;
				} else if (currentChar == 0x0053 || currentChar == 0x0073) {// character
																			// S
																			// or
																			// s
					afterDOCTYPE_characters = system;
					afterDOCTYPE_characters_Index = afterDOCTYPE_characters_Index + 1;
					break;
				}

			} else {
				if (afterDOCTYPE_characters_Index <= 5 // for the next 6
														// characters
						&& (afterDOCTYPE_characters[afterDOCTYPE_characters_Index] == currentChar || afterDOCTYPE_characters[afterDOCTYPE_characters_Index]+ 0x0020 == currentChar )) {
					afterDOCTYPE_characters_Index++;
					if (afterDOCTYPE_characters_Index == 6) {
						// If the six characters starting from the current input
						// character
						// are
						// an ASCII case-insensitive match for the word
						// "PUBLIC", then
						// consume
						// those characters and switch to the after DOCTYPE
						// public
						// keyword
						// state.
						if (afterDOCTYPE_characters[0] == 0x0050) {
							tokenizerContext
									.setNextState(factory
											.getState(TokenizerState.After_DOCTYPE_public_keyword_state));
						}
						// Otherwise, if the six characters starting from the
						// current
						// input
						// character are an ASCII case-insensitive match for the
						// word
						// "SYSTEM", then consume those characters and switch to
						// the
						// after
						// DOCTYPE system keyword state.
						else {
							tokenizerContext
									.setNextState(factory
											.getState(TokenizerState.After_DOCTYPE_system_keyword_state));
						}
					}
					break;
				}
			}

			// Otherwise, this is a parse error. Set the DOCTYPE token's
			// force-quirks flag to on. Switch to the bogus DOCTYPE state.
			context.addParseErrors(ParseErrorType.UnexpectedInputCharacter);
			docToken = (DocTypeToken) tokenizerContext.getCurrentToken();
			docToken.setForceQuircksFlag(true);
			tokenizerContext.setNextState(factory
					.getState(TokenizerState.Bogus_DOCTYPE_state));
			break;
		}

		context.setTokenizerContext(tokenizerContext);
		return context;
	}
}