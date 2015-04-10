package com.html5parser.insertionModes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import com.html5parser.algorithms.AdoptionAgencyAlgorithm;
import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.GenerateAllImpliedEndTagsThoroughly;
import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.algorithms.InsertCharacter;
import com.html5parser.algorithms.InsertComment;
import com.html5parser.algorithms.ListOfActiveFormattingElements;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.constants.HTML5Elements;
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
		else if ((tokenType == TokenType.start_tag && isOneOf(token.getValue(),
				new String[] { "base", "basefont", "bgsound", "link", "meta",
						"noframes", "script", "style", "template", "title" }))
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
					if (!isOneOf(name, new String[] { "dd", "dt", "li", "p",
							"tbody", "td", "tfoot", "th", "thead", "tr",
							"body", "html" })) {
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
		 * A start tag whose tag name is one of: "address", "article", "aside",
		 * "blockquote", "center", "details", "dialog", "dir", "div", "dl",
		 * "fieldset", "figcaption", "figure", "footer", "header", "hgroup",
		 * "main", "nav", "ol", "p", "section", "summary", "ul" If the stack of
		 * open elements has a p element in button scope, then close a p
		 * element. Insert an HTML element for the token.
		 */
		else if (tokenType == TokenType.start_tag
				&& isOneOf(token.getValue(), new String[] { "address",
						"article", "aside", "blockquote", "center", "details",
						"dialog", "dir", "div", "dl", "fieldset", "figcaption",
						"figure", "footer", "header", "hgroup", "main", "nav",
						"ol", "p", "section", "summary", "ul" })) {
			if (ElementInScope.isInButtonScope(parserContext, "p"))
				closeApElement(parserContext);
			InsertAnHTMLElement.run(parserContext, token);
		}

		/*
		 * An end tag whose tag name is "p" If the stack of open elements does
		 * not have a p element in button scope, then this is a parse error;
		 * insert an HTML element for a "p" start tag token with no attributes.
		 * Close a p element.
		 */
		else if (tokenType == TokenType.end_tag && token.getValue().equals("p")) {
			if (ElementInScope.isInButtonScope(parserContext, "p")) {
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				InsertAnHTMLElement.run(parserContext, new TagToken(
						TokenType.start_tag, "p"));
			}
			closeApElement(parserContext);
		}

		/*
		 * A start tag whose tag name is "a" If the list of active formatting
		 * elements contains an a element between the end of the list and the
		 * last marker on the list (or the start of the list if there is no
		 * marker on the list), then this is a parse error; run the adoption
		 * agency algorithm for the tag name "a", then remove that element from
		 * the list of active formatting elements and the stack of open elements
		 * if the adoption agency algorithm didn't already remove it (it might
		 * not have if the element is not in table scope).
		 * 
		 * In the non-conforming stream <a href="a">a<table><a
		 * href="b">b</table>x, the first a element would be closed upon seeing
		 * the second one, and the "x" character would be inside a link to "b",
		 * not to "a". This is despite the fact that the outer a element is not
		 * in table scope (meaning that a regular </a> end tag at the start of
		 * the table wouldn't close the outer a element). The result is that the
		 * two a elements are indirectly nested inside each other —
		 * non-conforming markup will often result in non-conforming DOMs when
		 * parsed.
		 * 
		 * Reconstruct the active formatting elements, if any.
		 * 
		 * Insert an HTML element for the token. Push onto the list of active
		 * formatting elements that element.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("a")) {
			ArrayList<Element> list = parserContext
					.getActiveFormattingElements();
			List<Element> sublist = list.subList(list.lastIndexOf(null) + 1,
					list.size());
			for (Element e : sublist)
				if (e.getNodeName().equals("a")) {
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);

					parserContext = AdoptionAgencyAlgorithm.Run(parserContext,
							token.getValue());
					list = parserContext.getActiveFormattingElements();
					Element last = list.get(list.size() - 1);
					if (last.getNodeName().equals("a"))
						parserContext.getActiveFormattingElements()
								.remove(last);
					last = parserContext.getOpenElements().peek();
					if (last.getNodeName().equals("a"))
						parserContext.getOpenElements().pop();
				}
			ListOfActiveFormattingElements.reconstruct(parserContext);
			Element e = InsertAnHTMLElement.run(parserContext, token);
			ListOfActiveFormattingElements.push(parserContext, e);
		}
		/*
		 * A start tag whose tag name is one of: "b", "big", "code", "em",
		 * "font", "i", "s", "small", "strike", "strong", "tt", "u" Reconstruct
		 * the active formatting elements, if any.
		 * 
		 * Insert an HTML element for the token. Push onto the list of active
		 * formatting elements that element.
		 */

		else if (tokenType == TokenType.start_tag
				&& isOneOf(token.getValue(), new String[] { "b", "big", "code",
						"em", "font", "i", "s", "small", "strike", "strong",
						"tt", "u" })) {
			ListOfActiveFormattingElements.reconstruct(parserContext);
			Element e = InsertAnHTMLElement.run(parserContext, token);
			ListOfActiveFormattingElements.push(parserContext, e);
		}

		/*
		 * A start tag whose tag name is "nobr" Reconstruct the active
		 * formatting elements, if any.
		 * 
		 * If the stack of open elements has a nobr element in scope, then this
		 * is a parse error; run the adoption agency algorithm for the tag name
		 * "nobr", then once again reconstruct the active formatting elements,
		 * if any.
		 * 
		 * Insert an HTML element for the token. Push onto the list of active
		 * formatting elements that element.
		 */

		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("nobr")) {
			if (ElementInScope.isInScope(parserContext, token.getValue()))
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			AdoptionAgencyAlgorithm.Run(parserContext, token.getValue());
			ListOfActiveFormattingElements.reconstruct(parserContext);
			Element e = InsertAnHTMLElement.run(parserContext, token);
			ListOfActiveFormattingElements.push(parserContext, e);
		}

		/*
		 * An end tag whose tag name is one of: "a", "b", "big", "code", "em",
		 * "font", "i", "nobr", "s", "small", "strike", "strong", "tt", "u" Run
		 * the adoption agency algorithm for the token's tag name.
		 */
		else if (tokenType == TokenType.end_tag
				&& isOneOf(token.getValue(), new String[] { "a", "b", "big",
						"code", "em", "font", "i", "nobr", "s", "small",
						"strike", "strong", "tt", "u" })) {
			AdoptionAgencyAlgorithm.Run(parserContext, token.getValue());
		}

		else if (tokenType == TokenType.end_tag) {

		}

		// /*
		// * A start tag whose tag name is one of: "base", "basefont",
		// "bgsound",
		// * "link" Insert an HTML element for the token. Immediately pop the
		// * current node off the stack of open elements. Acknowledge the
		// token's
		// * self-closing flag, if it is set.
		// */
		// else if (tokenType == TokenType.start_tag
		// && (token.getValue().equals("base")
		// || token.getValue().equals("basefont")
		// || token.getValue().equals("bgsound") || token
		// .getValue().equals("link"))) {
		// InsertAnHTMLElement.run(parserContext, token);
		// parserContext.getOpenElements().pop();
		// ((TagToken) token).setFlagAcknowledgeSelfClosingTag(true);
		// return parserContext;
		// }
		// /*
		// * A start tag whose tag name is "meta" Insert an HTML element for the
		// * token. Immediately pop the current node off the stack of open
		// * elements. Acknowledge the token's self-closing flag, if it is set.
		// If
		// * the element has a charset attribute, and getting an encoding from
		// its
		// * value results in a supported ASCII-compatible character encoding or
		// a
		// * UTF-16 encoding, and the confidence is currently tentative, then
		// * change the encoding to the resulting encoding. Otherwise, if the
		// * element has an http-equiv attribute whose value is an ASCII
		// * case-insensitive match for the string "Content-Type", and the
		// element
		// * has a content attribute, and applying the algorithm for extracting
		// a
		// * character encoding from a meta element to that attribute's value
		// * returns a supported ASCII-compatible character encoding or a UTF-16
		// * encoding, and the confidence is currently tentative, then change
		// the
		// * encoding to the extracted encoding.
		// */
		// else if (tokenType == TokenType.start_tag
		// && token.getValue().equals("meta")) {
		// InsertAnHTMLElement.run(parserContext, token);
		// parserContext.getOpenElements().pop();
		// ((TagToken) token).setFlagAcknowledgeSelfClosingTag(true);
		// // TODO
		// return parserContext;
		// }
		// /*
		// * A start tag whose tag name is "title" Follow the generic RCDATA
		// * element parsing algorithm.
		// */
		// else if (tokenType == TokenType.start_tag
		// && token.getValue().equals("title")) {
		// GenericRCDATAElementParsing.run(parserContext, (TagToken) token);
		// throw new UnsupportedOperationException();
		// }
		// /*
		// * A start tag whose tag name is "noscript", if the scripting flag is
		// * enabled A start tag whose tag name is one of: "noframes", "style"
		// * Follow the generic raw text element parsing algorithm.
		// */
		// else if ((tokenType == TokenType.start_tag
		// && token.getValue().equals("noscript") && parserContext
		// .isFlagScripting())
		// || (tokenType == TokenType.start_tag && (token.getValue()
		// .equals("noframes") || token.getValue().equals("style")))) {
		// GenericRawTextElementParsing.run(parserContext, (TagToken) token);
		// throw new UnsupportedOperationException();
		// }
		// /*
		// * A start tag whose tag name is "noscript", if the scripting flag is
		// * disabled Insert an HTML element for the token. Switch the insertion
		// * mode to "in head noscript".
		// */
		// else if (tokenType == TokenType.start_tag
		// && token.getValue().equals("noscript")
		// && !parserContext.isFlagScripting()) {
		// InsertAnHTMLElement.run(parserContext, token);
		// parserContext.setInsertionMode(factory
		// .getInsertionMode(InsertionMode.in_head_noscript));
		// }
		// /*
		// * A start tag whose tag name is "script" Run these steps: Let the
		// * adjusted insertion location be the appropriate place for inserting
		// a
		// * node. Create an element for the token in the HTML namespace, with
		// the
		// * intended parent being the element in which the adjusted insertion
		// * location finds itself. Mark the element as being "parser-inserted"
		// * and unset the element's "force-async" flag. This ensures that, if
		// the
		// * script is external, any document.write() calls in the script will
		// * execute in-line, instead of blowing the document away, as would
		// * happen in most other cases. It also prevents the script from
		// * executing until the end tag is seen. If the parser was originally
		// * created for the HTML fragment parsing algorithm, then mark the
		// script
		// * element as "already started". (fragment case) Insert the newly
		// * created element at the adjusted insertion location. Push the
		// element
		// * onto the stack of open elements so that it is the new current node.
		// * Switch the tokenizer to the script data state. Let the original
		// * insertion mode be the current insertion mode. Switch the insertion
		// * mode to "text".
		// */
		// else if (tokenType == TokenType.start_tag
		// && token.getValue().equals("script")) {
		// // TODO
		// // AdjustedInsertionLocation location = new
		// // Element element =
		// // CreateAnElementForAToken.run(intendedParentElement, namespace,
		// // currentToken, context)
		//
		// }
		// /*
		// * An end tag whose tag name is "head" Pop the current node (which
		// will
		// * be the head element) off the stack of open elements. Switch the
		// * insertion mode to "after head".
		// */
		// else if (tokenType == TokenType.end_tag
		// && token.getValue().equals("head")) {
		// parserContext.getOpenElements().pop();
		// parserContext.setInsertionMode(factory
		// .getInsertionMode(InsertionMode.after_head));
		// }
		// /*
		// * A start tag whose tag name is "template" Insert an HTML element for
		// * the token. Insert a marker at the end of the list of active
		// * formatting elements. Set the frameset-ok flag to "not ok". Switch
		// the
		// * insertion mode to "in template". Push "in template" onto the stack
		// of
		// * template insertion modes so that it is the new current template
		// * insertion mode.
		// */
		// else if (tokenType == TokenType.start_tag
		// && token.getValue().equals("template")) {
		// InsertAnHTMLElement.run(parserContext, token);
		// ListOfActiveFormattingElements.insertMarker(parserContext);
		// parserContext.setFlagFramesetOk(false);
		// IInsertionMode in_template = factory
		// .getInsertionMode(InsertionMode.in_template);
		// parserContext.setInsertionMode(in_template);
		// parserContext.getTemplateInsertionModes().push(in_template);
		// }
		// /*
		// * A start tag whose tag name is "head" Any other end tag Parse error.
		// * Ignore the token.
		// */
		// else if ((tokenType == TokenType.start_tag &&
		// token.getValue().equals(
		// "head"))
		// || tokenType == TokenType.end_tag) {
		// parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		// }
		/*
		 * Anything else Pop the current node (which will be the head element)
		 * off the stack of open elements. Switch the insertion mode to
		 * "after head". Reprocess the token.
		 */
		// else {
		// parserContext.getOpenElements().pop();
		// parserContext.setInsertionMode(factory
		// .getInsertionMode(InsertionMode.after_head));
		// parserContext.setFlagReconsumeToken(true);
		// }

		return parserContext;
	}

	public void anyOtherEndTag(ParserContext parserContext) {
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		// Any other end tag
		// Run these steps:
		// 1 Initialize node to be the current node (the bottommost node of the
		// stack).
		ArrayList<Element> stack = new ArrayList<>();
		stack.addAll(parserContext.getOpenElements());
		// 2 Loop: If node is an HTML element with the same tag name as the
		// token, then:
		for (int i = stack.size() - 1; i >= 0; i--) {
			Element node = stack.get(i);
			if (node.getNodeName().equals(token.getValue())) {
				// 2.1 Generate implied end tags, except for HTML elements with
				// the same tag name as the token.
				GenerateAllImpliedEndTagsThoroughly.run(parserContext,
						token.getValue());
				// 2.2 If node is not the current node, then this is a parse
				// error.
				if (!node.equals(parserContext.getCurrentNode()))
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
				// 2.3 Pop all the nodes from the current node up to node,
				// including node, then stop these steps.
				while (true) {
					Element e = parserContext.getOpenElements().pop();
					if (e.equals(node))
						return;
				}
			}
			// 3 Otherwise, if node is in the special category, then this is a
			// parse error; ignore the token, and abort these steps.
			else if (Arrays.asList(HTML5Elements.SPECIAL).contains(
					node.getNodeName())) {
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				return;
			}
			// 4 Set node to the previous entry in the stack of open elements.
			// 5 Return to the step labeled loop.
		}
	}

	private void closeApElement(ParserContext parserContext) {
		// it means that the user agent must run the following steps:
		// Generate implied end tags, except for p elements.
		// If the current node is not a p element, then this is a parse error.
		// Pop elements from the stack of open elements until a p element has
		// been popped from the stack.
		GenerateAllImpliedEndTagsThoroughly.run(parserContext, "p");
		if (!parserContext.getCurrentNode().getNodeName().equals("p"))
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		while (!parserContext.getOpenElements().isEmpty()) {
			Element e = parserContext.getOpenElements().pop();
			if (e.getNodeName().equals("p"))
				return;
		}
	}

	private Boolean isOneOf(String value, String[] values) {
		for (String s : values)
			if (s.equals(value))
				return true;

		return false;
	}
}
