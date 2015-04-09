package com.html5parser.insertionModes;

import java.util.ArrayList;

import org.w3c.dom.Element;

import com.html5parser.algorithms.GenericRCDATAElementParsing;
import com.html5parser.algorithms.GenericRawTextElementParsing;
import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.algorithms.ListOfActiveFormattingElements;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InBody implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		TokenType tokenType = token.getType();

		/*
		 * A character token that is U+0000 NULL Parse error. Ignore the token.
		 */
		if (tokenType == TokenType.character && token.getIntValue() == 0x000) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		}
		/*
		 * A character token that is one of U+0009 CHARACTER TABULATION, "LF"
		 * (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE Reconstruct
		 * the active formatting elements, if any. Insert the token's character.
		 */
		else if (token.isSpaceCharacter()) {
			ListOfActiveFormattingElements.reconstruct(parserContext);
			InsertCharacter.run(parserContext, token);
		}
		/*
		 * Any other character token Reconstruct the active formatting elements,
		 * if any. Insert the token's character. Set the frameset-ok flag to
		 * "not ok".
		 */
		else if (tokenType == TokenType.character) {
			ListOfActiveFormattingElements.reconstruct(parserContext);
			InsertCharacter.run(parserContext, token);
			parserContext.setFlagReconsumeToken(false);
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
		 * A start tag whose tag name is "html" Parse error. If there is a
		 * template element on the stack of open elements, then ignore the
		 * token. Otherwise, for each attribute on the token, check to see if
		 * the attribute is already present on the top element of the stack of
		 * open elements. If it is not, add the attribute and its corresponding
		 * value to that element.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("html")) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			// TODO
			// parserContext.getOpenElements().contains(o)
			return parserContext;
		}
		/*
		 * A start tag whose tag name is one of: "base", "basefont", "bgsound",
		 * "link", "meta", "noframes", "script", "style", "template", "title" An
		 * end tag whose tag name is "template" Process the token using the
		 * rules for the "in head" insertion mode.
		 */
		else if ((tokenType == TokenType.start_tag && (token.getValue().equals(
				"base")
				|| token.getValue().equals("basefont")
				|| token.getValue().equals("bgsound")
				|| token.getValue().equals("link")
				|| token.getValue().equals("meta")
				|| token.getValue().equals("noframes")
				|| token.getValue().equals("script")
				|| token.getValue().equals("style")
				|| token.getValue().equals("template") || token.getValue()
				.equals("title")))
				|| (tokenType == TokenType.end_tag && token.getValue().equals(
						"template"))) {
			IInsertionMode inHead = factory
					.getInsertionMode(InsertionMode.in_head);
			parserContext = inHead.process(parserContext);
		}
		/*
		 * An end-of-file token If there is a node in the stack of open elements
		 * that is not either a dd element, a dt element, an li element, a p
		 * element, a tbody element, a td element, a tfoot element, a th
		 * element, a thead element, a tr element, the body element, or the html
		 * element, then this is a parse error.
		 * 
		 * If the stack of template insertion modes is not empty, then process
		 * the token using the rules for the "in template" insertion mode.
		 * Otherwise, stop parsing.
		 */
		else if (tokenType == TokenType.end_of_file) {
			if (!parserContext.getOpenElements().isEmpty()) {
				ArrayList<Element> stack = new ArrayList<Element>();
				stack.addAll(parserContext.getOpenElements());
				boolean flag = true;
				for (Element element : stack) {
					String name = element.getNodeName();
					if (!(name.equals("dd") || name.equals("dt")
							|| name.equals("li") || name.equals("p")
							|| name.equals("tbody") || name.equals("td")
							|| name.equals("tfoot") || name.equals("th")
							|| name.equals("thead") || name.equals("tr")
							|| name.equals("body") || name.equals("html"))) {
						flag = false;
						break;
					}
				}
				if (!flag) {
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				}
			}
			if (!parserContext.getTemplateInsertionModes().isEmpty()) {
				IInsertionMode inTemplate = factory
						.getInsertionMode(InsertionMode.in_template);
				parserContext = inTemplate.process(parserContext);
			} else {
				parserContext.setFlagStopParsing(true);
			}

		}

		/*
		 * A start tag whose tag name is one of: "base", "basefont", "bgsound",
		 * "link" Insert an HTML element for the token. Immediately pop the
		 * current node off the stack of open elements. Acknowledge the token's
		 * self-closing flag, if it is set.
		 */
		else if (tokenType == TokenType.start_tag
				&& (token.getValue().equals("base")
						|| token.getValue().equals("basefont")
						|| token.getValue().equals("bgsound") || token
						.getValue().equals("link"))) {
			InsertAnHTMLElement.run(parserContext, token);
			parserContext.getOpenElements().pop();
			((TagToken) token).setFlagAcknowledgeSelfClosingTag(true);
			return parserContext;
		}
		/*
		 * A start tag whose tag name is "meta" Insert an HTML element for the
		 * token. Immediately pop the current node off the stack of open
		 * elements. Acknowledge the token's self-closing flag, if it is set. If
		 * the element has a charset attribute, and getting an encoding from its
		 * value results in a supported ASCII-compatible character encoding or a
		 * UTF-16 encoding, and the confidence is currently tentative, then
		 * change the encoding to the resulting encoding. Otherwise, if the
		 * element has an http-equiv attribute whose value is an ASCII
		 * case-insensitive match for the string "Content-Type", and the element
		 * has a content attribute, and applying the algorithm for extracting a
		 * character encoding from a meta element to that attribute's value
		 * returns a supported ASCII-compatible character encoding or a UTF-16
		 * encoding, and the confidence is currently tentative, then change the
		 * encoding to the extracted encoding.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("meta")) {
			InsertAnHTMLElement.run(parserContext, token);
			parserContext.getOpenElements().pop();
			((TagToken) token).setFlagAcknowledgeSelfClosingTag(true);
			// TODO
			return parserContext;
		}
		/*
		 * A start tag whose tag name is "title" Follow the generic RCDATA
		 * element parsing algorithm.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("title")) {
			GenericRCDATAElementParsing.run(parserContext, (TagToken) token);
			throw new UnsupportedOperationException();
		}
		/*
		 * A start tag whose tag name is "noscript", if the scripting flag is
		 * enabled A start tag whose tag name is one of: "noframes", "style"
		 * Follow the generic raw text element parsing algorithm.
		 */
		else if ((tokenType == TokenType.start_tag
				&& token.getValue().equals("noscript") && parserContext
					.isFlagScripting())
				|| (tokenType == TokenType.start_tag && (token.getValue()
						.equals("noframes") || token.getValue().equals("style")))) {
			GenericRawTextElementParsing.run(parserContext, (TagToken) token);
			throw new UnsupportedOperationException();
		}
		/*
		 * A start tag whose tag name is "noscript", if the scripting flag is
		 * disabled Insert an HTML element for the token. Switch the insertion
		 * mode to "in head noscript".
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("noscript")
				&& !parserContext.isFlagScripting()) {
			InsertAnHTMLElement.run(parserContext, token);
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_head_noscript));
		}
		/*
		 * A start tag whose tag name is "script" Run these steps: Let the
		 * adjusted insertion location be the appropriate place for inserting a
		 * node. Create an element for the token in the HTML namespace, with the
		 * intended parent being the element in which the adjusted insertion
		 * location finds itself. Mark the element as being "parser-inserted"
		 * and unset the element's "force-async" flag. This ensures that, if the
		 * script is external, any document.write() calls in the script will
		 * execute in-line, instead of blowing the document away, as would
		 * happen in most other cases. It also prevents the script from
		 * executing until the end tag is seen. If the parser was originally
		 * created for the HTML fragment parsing algorithm, then mark the script
		 * element as "already started". (fragment case) Insert the newly
		 * created element at the adjusted insertion location. Push the element
		 * onto the stack of open elements so that it is the new current node.
		 * Switch the tokenizer to the script data state. Let the original
		 * insertion mode be the current insertion mode. Switch the insertion
		 * mode to "text".
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("script")) {
			// TODO
			// AdjustedInsertionLocation location = new
			// Element element =
			// CreateAnElementForAToken.run(intendedParentElement, namespace,
			// currentToken, context)

		}
		/*
		 * An end tag whose tag name is "head" Pop the current node (which will
		 * be the head element) off the stack of open elements. Switch the
		 * insertion mode to "after head".
		 */
		else if (tokenType == TokenType.end_tag
				&& token.getValue().equals("head")) {
			parserContext.getOpenElements().pop();
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.after_head));
		}
		/*
		 * A start tag whose tag name is "template" Insert an HTML element for
		 * the token. Insert a marker at the end of the list of active
		 * formatting elements. Set the frameset-ok flag to "not ok". Switch the
		 * insertion mode to "in template". Push "in template" onto the stack of
		 * template insertion modes so that it is the new current template
		 * insertion mode.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("template")) {
			InsertAnHTMLElement.run(parserContext, token);
			ListOfActiveFormattingElements.insertMarker(parserContext);
			parserContext.setFlagFramesetOk(false);
			IInsertionMode in_template = factory
					.getInsertionMode(InsertionMode.in_template);
			parserContext.setInsertionMode(in_template);
			parserContext.getTemplateInsertionModes().push(in_template);
		}
		/*
		 * A start tag whose tag name is "head" Any other end tag Parse error.
		 * Ignore the token.
		 */
		else if ((tokenType == TokenType.start_tag && token.getValue().equals(
				"head"))
				|| tokenType == TokenType.end_tag) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		}
		/*
		 * Anything else Pop the current node (which will be the head element)
		 * off the stack of open elements. Switch the insertion mode to
		 * "after head". Reprocess the token.
		 */
//		else {
//			parserContext.getOpenElements().pop();
//			parserContext.setInsertionMode(factory
//					.getInsertionMode(InsertionMode.after_head));
//			parserContext.setFlagReconsumeToken(true);
//		}
		return parserContext;
	}
}
