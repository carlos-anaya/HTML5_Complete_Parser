package com.html5parser.algorithms;

import java.util.ArrayList;
import java.util.Stack;

import org.w3c.dom.Element;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.token.TagToken;
import com.html5parser.constants.Namespace;
import com.html5parser.parseError.ParseErrorType;
import com.html5parser.parser.TreeConstructor;

public class ForeignContent {

	public static ParserContext run(ParserContext parserContext) {
		Token token = parserContext.getTokenizerContext().getCurrentToken();

		switch (token.getType()) {

		// A character token that is U+0000 NULL
		// Parse error. Insert a U+FFFD REPLACEMENT CHARACTER character.

		// A character token that is one of U+0009 CHARACTER TABULATION, "LF"
		// (U+000A), "FF" (U+000C), "CR" (U+000D), or U+0020 SPACE
		// Insert the token's character.

		// Any other character token
		// Insert the token's character.
		// Set the frameset-ok flag to "not ok".
		case character:
			if (token.getIntValue() == 0x000)
				InsertCharacter.run(parserContext, 0xFFFD);
			else if (token.isSpaceCharacter())
				InsertCharacter.run(parserContext, token);
			else {
				InsertCharacter.run(parserContext, token);
				parserContext.setFlagFramesetOk(false);
			}
			break;

		// A comment token
		// Insert a comment.
		case comment:
			InsertComment.run(parserContext, token);
			break;

		// A DOCTYPE token
		// Parse error. Ignore the token.
		case DOCTYPE:
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			break;

		// A start tag whose tag name is one of: "b", "big", "blockquote",
		// "body", "br", "center", "code", "dd", "div", "dl", "dt", "em",
		// "embed", "h1", "h2", "h3", "h4", "h5", "h6", "head", "hr", "i",
		// "img", "li", "listing", "main", "meta", "nobr", "ol", "p", "pre",
		// "ruby", "s", "small", "span", "strong", "strike", "sub", "sup",
		// "table", "tt", "u", "ul", "var"

		// A start tag whose tag name is "font", if the token has any attributes
		// named "color", "face", or "size"

		// Parse error.
		//
		// If the parser was originally created for the HTML fragment parsing
		// algorithm, then act as described in the "any other start tag" entry
		// below. (fragment case)
		//
		// Otherwise:
		//
		// Pop an element from the stack of open elements, and then keep popping
		// more elements from the stack of open elements until the current node
		// is a MathML text integration point, an HTML integration point, or an
		// element in the HTML namespace.
		//
		// Then, reprocess the token.
		case start_tag:
			if (isOneOf(token.getValue(), new String[] { "b", "big",
					"blockquote", "body", "br", "center", "code", "dd", "div",
					"dl", "dt", "em", "embed", "h1", "h2", "h3", "h4", "h5",
					"h6", "head", "hr", "i", "img", "li", "listing", "main",
					"meta", "nobr", "ol", "p", "pre", "ruby", "s", "small",
					"span", "strong", "strike", "sub", "sup", "table", "tt",
					"u", "ul", "var" })
					|| (token.getValue().equals("font") && ((TagToken) token)
							.hasAttribute(new String[] { "color", "face",
									"size" }))) {
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				if (parserContext.isFlagHTMLFragmentParser())
					anyOtherEndTag(parserContext, token);
				else {
					Element e = parserContext.getOpenElements().pop();
					while (!parserContext.getOpenElements().isEmpty()) {
						e = parserContext.getCurrentNode();
						if (IntegrationPoint.isHtmlIntegrationPoint(e)
								|| IntegrationPoint
										.isMathMLTextIntegrationPoint(e)
								|| (e.getNamespaceURI() != null && e
										.getNamespaceURI().equals(
												Namespace.HTML))) {
							break;
						} else
							parserContext.getOpenElements().pop();
					}
					parserContext.setFlagReconsumeToken(true);
				}
			}
			// Any other start tag
			// If the adjusted current node is an element in the MathML
			// namespace, adjust MathML attributes for the token. (This fixes
			// the case of MathML attributes that are not all lowercase.)

			// If the adjusted current node is an element in the SVG namespace,
			// and the token's tag name is one of the ones in the first column
			// of the following table, change the tag name to the name given in
			// the corresponding cell in the second column. (This fixes the case
			// of SVG elements that are not all lowercase.)

			// If the adjusted current node is an element in the SVG namespace,
			// adjust SVG attributes for the token. (This fixes the case of SVG
			// attributes that are not all lowercase.)

			// Adjust foreign attributes for the token. (This fixes the use of
			// namespaced attributes, in particular XLink in SVG.)

			// Insert a foreign element for the token, in the same namespace as
			// the adjusted current node.

			// If the token has its self-closing flag set, then run the
			// appropriate steps from the following list:

			// If the token's tag name is "script"
			// Acknowledge the token's self-closing flag, and then act as
			// described in the steps for a "script" end tag below.

			// Otherwise
			// Pop the current node off the stack of open elements and
			// acknowledge the token's self-closing flag.
			else {
				Element adjustedCurrentNode = parserContext
						.getAdjustedCurrentNode();
				if (adjustedCurrentNode.getNamespaceURI() != null
						&& adjustedCurrentNode.getNamespaceURI().equals(
								Namespace.MathML))
					AdjustMathMLAttributes.run((TagToken) token);
				if (adjustedCurrentNode.getNamespaceURI() != null
						&& adjustedCurrentNode.getNamespaceURI().equals(
								Namespace.SVG)) {
					token = changeTokenName(token);
					AdjustSVGAttributes.run((TagToken) token);
				}
				AdjustForeignAttributes.run((TagToken) token);
				InsertForeignElement.run(parserContext, token,
						adjustedCurrentNode.getNamespaceURI());

				if (((TagToken) token).isFlagSelfClosingTag()) {
					if (!token.getValue().equals("script")) {
						parserContext.getOpenElements().pop();
						((TagToken) token)
								.setFlagAcknowledgeSelfClosingTag(true);
					} else {
						((TagToken) token)
								.setFlagAcknowledgeSelfClosingTag(true);
						anScriptEndTag(parserContext);
					}
				}
			}
			break;
		case end_tag:
			if (!token.getValue().equals("script"))
				anyOtherEndTag(parserContext, token);
			else
				anScriptEndTag(parserContext);
			break;
		default:
			break;
		}
		return parserContext;

	}

	private static void anyOtherEndTag(ParserContext parserContext, Token token) {
		ArrayList<Element> openElements = new ArrayList<Element>();
		openElements.addAll(parserContext.getOpenElements());
		Stack<Element> stack = parserContext.getOpenElements();
		// Any other end tag
		// Run these steps:
		// 1 Initialize node to be the current node (the bottommost node of the
		// stack).
		int nodeIndex = openElements.size() - 1;
		Element node = openElements.get(nodeIndex);
		// 2 If node's tag name, converted to ASCII lowercase, is not the same
		// as the tag name of the token, then this is a parse error.
		if (!node.getNodeName().toLowerCase().equals(token.getValue()))
			parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
		// 3 Loop: If node is the topmost element in the stack of open elements,
		// abort these steps. (fragment case)

		while (!stack.isEmpty()) {
			if (stack.size() == 1)
				return;
			// 4 If node's tag name, converted to ASCII lowercase, is the same
			// as
			// the tag name of the token, pop elements from the stack of open
			// elements until node has been popped from the stack, and then
			// abort
			// these steps.
			if (node.getNodeName().toLowerCase().equals(token.getValue()))
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
			{
				while (!stack.isEmpty()) {
					Element e = stack.pop();
					if (e.equals(node))
						return;
				}
			}
			// 5 Set node to the previous entry in the stack of open elements.
			nodeIndex = openElements.size() - 1;
			node = openElements.get(nodeIndex);
			// 6 If node is not an element in the HTML namespace, return to the
			// step
			// labeled loop.
			// 7 Otherwise, process the token according to the rules given in
			// the
			// section corresponding to the current insertion mode in HTML
			// content.
			if (node.getNamespaceURI() != null
					&& node.getNamespaceURI().equals(Namespace.HTML)) {
				parserContext = new TreeConstructor()
						.consumeToken(parserContext);
				return;
			}
		}

	}

	private static void anScriptEndTag(ParserContext parserContext) {
		//TODO this steps
		// An end tag whose tag name is "script", if the current node is a
		// script element in the SVG namespace
		// Pop the current node off the stack of open elements.
		//
		// Let the old insertion point have the same value as the current
		// insertion point. Let the insertion point be just before the next
		// input character.
		//
		// Increment the parser's script nesting level by one. Set the parser
		// pause flag to true.
		//
		// Process the script element according to the SVG rules, if the user
		// agent supports SVG. [SVG]
		//
		// Even if this causes new characters to be inserted into the tokenizer,
		// the parser will not be executed reentrantly, since the parser pause
		// flag is true.
		//
		// Decrement the parser's script nesting level by one. If the parser's
		// script nesting level is zero, then set the parser pause flag to
		// false.
		//
		// Let the insertion point have the value of the old insertion point.
		// (In other words, restore the insertion point to its previous value.
		// This value might be the "undefined" value.)
	}

	private static Token changeTokenName(Token token) {
		switch (token.getValue()) {
		case "altglyph":
			token.setValue("altGlyph");
			break;
		case "altglyphdef":
			token.setValue("altGlyphDef");
			break;
		case "altglyphitem":
			token.setValue("altGlyphItem");
			break;
		case "animatecolor":
			token.setValue("animateColor");
			break;
		case "animatemotion":
			token.setValue("animateMotion");
			break;
		case "animatetransform":
			token.setValue("animateTransform");
			break;
		case "clippath":
			token.setValue("clipPath");
			break;
		case "feblend":
			token.setValue("feBlend");
			break;
		case "fecolormatrix":
			token.setValue("feColorMatrix");
			break;
		case "fecomponenttransfer":
			token.setValue("feComponentTransfer");
			break;
		case "fecomposite":
			token.setValue("feComposite");
			break;
		case "feconvolvematrix":
			token.setValue("feConvolveMatrix");
			break;
		case "fediffuselighting":
			token.setValue("feDiffuseLighting");
			break;
		case "fedisplacementmap":
			token.setValue("feDisplacementMap");
			break;
		case "fedistantlight":
			token.setValue("feDistantLight");
			break;
		case "fedropshadow":
			token.setValue("feDropShadow");
			break;
		case "feflood":
			token.setValue("feFlood");
			break;
		case "fefunca":
			token.setValue("feFuncA");
			break;
		case "fefuncb":
			token.setValue("feFuncB");
			break;
		case "fefuncg":
			token.setValue("feFuncG");
			break;
		case "fefuncr":
			token.setValue("feFuncR");
			break;
		case "fegaussianblur":
			token.setValue("feGaussianBlur");
			break;
		case "feimage":
			token.setValue("feImage");
			break;
		case "femerge":
			token.setValue("feMerge");
			break;
		case "femergenode":
			token.setValue("feMergeNode");
			break;
		case "femorphology":
			token.setValue("feMorphology");
			break;
		case "feoffset":
			token.setValue("feOffset");
			break;
		case "fepointlight":
			token.setValue("fePointLight");
			break;
		case "fespecularlighting":
			token.setValue("feSpecularLighting");
			break;
		case "fespotlight":
			token.setValue("feSpotLight");
			break;
		case "fetile":
			token.setValue("feTile");
			break;
		case "feturbulence":
			token.setValue("feTurbulence");
			break;
		case "foreignobject":
			token.setValue("foreignObject");
			break;
		case "glyphref":
			token.setValue("glyphRef");
			break;
		case "lineargradient":
			token.setValue("linearGradient");
			break;
		case "radialgradient":
			token.setValue("radialGradient");
			break;
		case "textpath":
			token.setValue("textPath");
			break;
		default:
			break;
		}
		return token;
	}

	private static Boolean isOneOf(String value, String[] values) {
		for (String s : values)
			if (s.equals(value))
				return true;

		return false;
	}
}
