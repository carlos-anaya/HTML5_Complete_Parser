package com.html5parser.insertionModes;

import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class AfterHead implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		TokenType tokenType = token.getType();

		/*
		 * A character token that is one of U+0009 CHARACTER TABULATION, "LF"
		 * (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE Insert the
		 * character.
		 */
		if (token.isSpaceCharacter()) {
			InsertCharacter.run(parserContext, token);
		}
		/*
		 * A comment token Insert a comment.
		 */
		else if (tokenType == TokenType.comment) {
			InsertComment.run(parserContext, token);
		}
		/*
		 * A DOCTYPE token Parse error. Ignore the token.
		 */
		else if (tokenType == TokenType.DOCTYPE) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			return parserContext;
		}
		/*
		 * A start tag whose tag name is "html"Process the token using the rules
		 * for the "in body" insertion mode.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("html")) {
			IInsertionMode inBody = factory
					.getInsertionMode(InsertionMode.in_body);
			parserContext = inBody.process(parserContext);
		}
		/*
		 * A start tag whose tag name is "body" Insert an HTML element for the
		 * token. Set the frameset-ok flag to "not ok". Switch the insertion
		 * mode to "in body".
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("body")) {
			InsertAnHTMLElement.run(parserContext, token);
			parserContext.setFlagFramesetOk(false);
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_body));
		}
		/*
		 * A start tag whose tag name is "frameset" Insert an HTML element for
		 * the token. Switch the insertion mode to "in frameset".
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("frameset")) {
			InsertAnHTMLElement.run(parserContext, token);
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_frameset));
		}
		/*
		 * A start tag whose tag name is one of: "base", "basefont", "bgsound",
		 * "link", "meta", "noframes", "script", "style", "template", "title"
		 * Parse error. Push the node pointed to by the head element pointer
		 * onto the stack of open elements. Process the token using the rules
		 * for the "in head" insertion mode. Remove the node pointed to by the
		 * head element pointer from the stack of open elements. (It might not
		 * be the current node at this point.)
		 */
		else if (tokenType == TokenType.start_tag
				&& (token.getValue().equals("base")
						|| token.getValue().equals("basefont")
						|| token.getValue().equals("bgsound")
						|| token.getValue().equals("link")
						|| token.getValue().equals("meta")
						|| token.getValue().equals("noframes")
						|| token.getValue().equals("script")
						|| token.getValue().equals("style")
						|| token.getValue().equals("template") || token
						.getValue().equals("title"))) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			parserContext.getOpenElements().push(
					parserContext.getHeadElementPointer());
			IInsertionMode insertionMode = factory
					.getInsertionMode(InsertionMode.in_head);
			parserContext = insertionMode.process(parserContext);
			parserContext.getOpenElements().remove(parserContext.getHeadElementPointer());
		}
		/*
		 * An end tag whose tag name is "template" Process the token using the
		 * rules for the "in head" insertion mode.
		 */
		else if (tokenType == TokenType.end_tag
				&& token.getValue().equals("template")) {
			IInsertionMode insertionMode = factory
					.getInsertionMode(InsertionMode.in_head);
			parserContext = insertionMode.process(parserContext);
		}
		/*
		 * An end tag whose tag name is one of: "body", "html", "br" Act as
		 * described in the "anything else" entry below. A start tag whose tag
		 * name is "head" Any other end tag Parse error. Ignore the token.
		 */
		else if ((tokenType == TokenType.start_tag && token.getValue().equals(
				"head"))
				|| (tokenType == TokenType.end_tag
						&& !token.getValue().equals("body")
						&& !token.getValue().equals("html") && !token
						.getValue().equals("br"))) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		}
		/*
		 * Anything else Insert an HTML element for a "body" start tag token
		 * with no attributes. Switch the insertion mode to "in body". Reprocess
		 * the current token.
		 */
		else {
			InsertAnHTMLElement.run(parserContext, new TagToken(
					TokenType.start_tag, "body"));
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_body));
			parserContext.setFlagReconsumeToken(true);

		}

		return parserContext;
	}
}
