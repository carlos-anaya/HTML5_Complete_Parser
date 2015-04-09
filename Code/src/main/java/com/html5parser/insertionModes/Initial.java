package com.html5parser.insertionModes;

import com.html5parser.algorithms.InsertComment;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;

public class Initial implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {
		// A character token that is one of U+0009 CHARACTER TABULATION, "LF"
		// (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
		// Ignore the token.
		case character:
			if (!token.isSpaceCharacter())
				anythingElse(parserContext);
			break;
		// A comment token
		// Insert a comment as the last child of the Document object.
		case comment:
			InsertComment.run(parserContext, token);
			break;

		// TODO A DOCTYPE token
		case DOCTYPE:
			throw new UnsupportedOperationException();

		default:
			anythingElse(parserContext);
			break;
		}
		return parserContext;
	}

	public void anythingElse(ParserContext parserContext) {
		// Anything else
		// TODO If the document is not an iframe srcdoc document, then this is a
		// parse error; set the Document to quirks mode.
		// In any case, switch the insertion mode to "before html", then
		// reprocess the token.
		parserContext.setInsertionMode(InsertionModeFactory.getInstance()
				.getInsertionMode(InsertionMode.before_html));
		parserContext.setFlagReconsumeToken(true);
	}
}
