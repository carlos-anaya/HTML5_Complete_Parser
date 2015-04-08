package com.html5parser.insertionModes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.html5parser.algorithms.InsertAnHTMLElement;
import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.token.TagToken;
import com.html5parser.factories.InsertionModeFactory;
import com.html5parser.interfaces.IInsertionMode;
import com.html5parser.parseError.ParseErrorType;

public class InHead implements IInsertionMode {

	public ParserContext process(ParserContext parserContext) {

		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Token token = parserContext.getTokenizerContext().getCurrentToken();
		Document doc = parserContext.getDocument();
		TokenType tokenType = token.getType();

		/*
		 * A character token that is one of U+0009 CHARACTER TABULATION, 
		 * "LF" (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
		 * Insert the character.
		 */
		if (tokenType == TokenType.character
				&& (token.getValue().equals(
						String.valueOf(Character.toChars(0x0009)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000A)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000C)))
						|| token.getValue().equals(
								String.valueOf(Character.toChars(0x000D))) || token
						.getValue().equals(
								String.valueOf(Character.toChars(0x0020))))) {
			//TODO 
			return parserContext;
		}
		/*
		 * A comment token
		 * Insert a comment.
		 */
		else if (tokenType == TokenType.comment) {
			//TODO
			throw new UnsupportedOperationException();
		}
		/*
		 * A DOCTYPE token Parse error. Ignore the token.
		 */
		else if (tokenType == TokenType.DOCTYPE) {
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			return parserContext;
		}
		/*A start tag whose tag name is "html"
		 *Process the token using the rules for the "in body" insertion mode.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("html")){
			//TODO
			return parserContext;
		}
		/*
		 * A start tag whose tag name is one of: "base", "basefont", "bgsound", "link"
		 * Insert an HTML element for the token. 
		 * Immediately pop the current node off the stack of open elements.
		 * Acknowledge the token's self-closing flag, if it is set.
		 */
		else if(tokenType == TokenType.start_tag
				&& (token.getValue().equals("base")
				|| token.getValue().equals("basefont")
				|| token.getValue().equals("bgsound")
				|| token.getValue().equals("link"))){
			Element element = InsertAnHTMLElement.run(parserContext, token);
			parserContext.getOpenElements().pop();
			
			//TODO
			return parserContext;
		}
		/*A start tag whose tag name is "meta"
		 * Insert an HTML element for the token. 
		 * Immediately pop the current node off the stack of open elements.
		 * Acknowledge the token's self-closing flag, if it is set.
		 * If the element has a charset attribute, 
		 * and getting an encoding from its value results in 
		 * a supported ASCII-compatible character encoding or a UTF-16 encoding, 
		 * and the confidence is currently tentative, 
		 * then change the encoding to the resulting encoding.
		 * Otherwise, if the element has an http-equiv attribute 
		 * whose value is an ASCII case-insensitive match for the string "Content-Type", 
		 * and the element has a content attribute, 
		 * and applying the algorithm for extracting a character encoding 
		 * from a meta element to that attribute's value returns 
		 * a supported ASCII-compatible character encoding or a UTF-16 encoding, 
		 * and the confidence is currently tentative, 
		 * then change the encoding to the extracted encoding.		
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("meta")){
			Element element = InsertAnHTMLElement.run(parserContext, token);
			parserContext.getOpenElements().pop();
			//TODO
			return parserContext;
		}
		/* A start tag whose tag name is "title"
		 * Follow the generic RCDATA element parsing algorithm.
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("title")){
			//TODO
			throw new UnsupportedOperationException();
		}
		/* A start tag whose tag name is "noscript", if the scripting flag is enabled
		 * A start tag whose tag name is one of: "noframes", "style"
		 * Follow the generic raw text element parsing algorithm.
		 */
		else if ((tokenType == TokenType.start_tag
				&& token.getValue().equals("noscript") && !parserContext.isFlagScripting())
				||(tokenType == TokenType.start_tag && (token.getValue().equals("noframes")||
						token.getValue().equals("style")))){
			//TODO
			throw new UnsupportedOperationException();
		}
		/* A start tag whose tag name is "noscript", if the scripting flag is disabled
		 * Insert an HTML element for the token.
         * Switch the insertion mode to "in head noscript".
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("noscript") && !parserContext.isFlagScripting()){
			InsertAnHTMLElement.run(parserContext, token);
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_head_noscript));
		}
		/*A start tag whose tag name is "script"
		 * Run these steps:
		 * Let the adjusted insertion location be the appropriate place for inserting a node.
		 * Create an element for the token in the HTML namespace,
		 * with the intended parent being the element in which the adjusted insertion location finds itself.
		 * Mark the element as being "parser-inserted" and unset the element's "force-async" flag.
		 * This ensures that, if the script is external, 
		 * any document.write() calls in the script will execute in-line, instead of blowing the document away, 
		 * as would happen in most other cases. It also prevents the script from executing until the end tag is seen.
		 * If the parser was originally created for the HTML fragment parsing algorithm, 
		 * then mark the script element as "already started". (fragment case)
		 * Insert the newly created element at the adjusted insertion location.
		 * Push the element onto the stack of open elements so that it is the new current node.
		 * Switch the tokenizer to the script data state.
		 * Let the original insertion mode be the current insertion mode.
		 * Switch the insertion mode to "text".
		 */
		else if (tokenType == TokenType.start_tag
				&& token.getValue().equals("script")){
			parserContext.set
		}

		
		
		/* An end tag whose tag name is one of: "head", "body", "html", "br"
		 * Act as described in the "anything else" entry below.
		 * Any other end tag
		 * Parse error. Ignore the token.
		 */
		else if (tokenType == TokenType.end_tag && !(token.getValue().equals("head")
				||token.getValue().equals("body")
				||token.getValue().equals("html")
				||token.getValue().equals("br")
				)){
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			return parserContext;
		}
		/* Anything else
		 * Insert an HTML element for a "head" start tag token with no attributes.
		 * Set the head element pointer to the newly created head element.
		 * Switch the insertion mode to "in head".
		 * Reprocess the current token.
		 */
		else {
			Token inserttoken = new TagToken(TokenType.start_tag,"head");
			Element element = InsertAnHTMLElement.run(parserContext, inserttoken);
			parserContext.setHeadElementPointer(element);
			parserContext.setInsertionMode(factory
					.getInsertionMode(InsertionMode.in_head));
			parserContext.setFlagReconsumeToken(true);
			return parserContext;
		}
		
	}
}
