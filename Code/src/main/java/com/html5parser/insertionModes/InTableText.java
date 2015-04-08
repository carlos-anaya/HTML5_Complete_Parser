package com.html5parser.insertionModes;

import java.util.ArrayList;

import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InTableText implements IInsertionMode {

	private ArrayList<Token> pendingTableCharacterTokens = new ArrayList<Token>();

	public ParserContext process(ParserContext parserContext) {

		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {

		// A character token that is U+0000 NULL
		// Parse error. Ignore the token.

		// Any other character token
		// Append the character token to the pending table character tokens
		// list.
		case character:
			if (token.getIntValue() == 0x000)
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			else {
				pendingTableCharacterTokens.add(token);
			}
			break;

		// Anything else
		// If any of the tokens in the pending table character tokens list are
		// character tokens that are not space characters, then reprocess the
		// character tokens in the pending table character tokens list using the
		// rules given in the "anything else" entry in the "in table" insertion
		// mode.

		// Otherwise, insert the characters given by the pending table character
		// tokens list.

		// Switch the insertion mode to the original insertion mode and
		// reprocess the token.
		default:
			Boolean onlySpaceCharacters = true;
			for (Token t : pendingTableCharacterTokens)
				if (!isSpaceCharacter(t.getIntValue())) {
					onlySpaceCharacters = false;
					break;
				}
			if (!onlySpaceCharacters) {
				for (Token t : pendingTableCharacterTokens)
					new InTable().anythingElse(parserContext, t);
			} else {
				for (Token t : pendingTableCharacterTokens)
					InsertCharacter.run(parserContext, t);
			}
			parserContext.setInsertionMode(parserContext
					.getOriginalInsertionMode());
			parserContext.setFlagReconsumeToken(true);
			break;
		}
		return parserContext;
	}

	private boolean isSpaceCharacter(int value) {
		if (value == 0x0020 || value == 0x0009 || value == 0x000A
				|| value == 0x000C || value == 0x000D)
			return true;
		return false;

	}
}