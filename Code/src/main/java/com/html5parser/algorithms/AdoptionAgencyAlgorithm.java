package com.html5parser.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.token.TagToken;
import com.html5parser.constants.HTML5Elements;
import com.html5parser.constants.Namespace;
import com.html5parser.insertionModes.InBody;
import com.html5parser.parseError.ParseErrorType;

public class AdoptionAgencyAlgorithm {

	// The adoption agency algorithm, which takes as its only argument a tag
	// name subject for which the algorithm is being run, consists of the
	// following steps:
	public static ParserContext Run(ParserContext parserContext, String subject) {
		Element currentNode = parserContext.getCurrentNode();
		Element formattingElement = null;
		Element furthestBlock = null;
		Element commonAncestor = null;
		Element node = null;
		Element lastNode = null;

		Document doc = parserContext.getDocument();

		ArrayList<Element> stackOpenElements = new ArrayList<Element>();
		ArrayList<Element> listFormattingElements = new ArrayList<Element>();

		int outerLoop = 0;
		int innerLoop = 0;
		int formattingElementIndex = 0;
		int nodeIndex = 0;

		// 1 If the current node is an HTML element whose tag name is subject,
		// then run these substeps:
		if (currentNode.getNodeName().equals(subject)) {
			// 1.1 Let element be the current node.
			// 1.2 Pop element off the stack of open elements.
			parserContext.getOpenElements().pop();
			// 1.3 If element is also in the list of active formatting elements,
			// remove the element from the list.
			parserContext.getActiveFormattingElements().remove(currentNode);
			// 1.4 Abort the adoption agency algorithm.
			return parserContext;
		}
		// 2 Let outer loop counter be zero.
		// 3 Outer loop: If outer loop counter is greater than or equal to
		// eight, then abort these steps.
		// 4 Increment outer loop counter by one.
		for (outerLoop = 1; outerLoop < 8; outerLoop++) {

			// 5 Let formatting element be the last element in the list of
			// active formatting elements that:
			// - is between the end of the list and the last scope marker in the
			// list, if any, or the start of the list otherwise, and
			// - has the tag name subject.
			listFormattingElements = parserContext
					.getActiveFormattingElements();
			for (int i = listFormattingElements.size() - 1; i >= (listFormattingElements
					.lastIndexOf(null) + 1); i--) {
				if (listFormattingElements.get(i).getNodeName().equals(subject)) {
					formattingElement = listFormattingElements.get(i);
					break;
				}
			}
			// If there is no such element, then abort these steps and instead
			// act as described in the "any other end tag" entry above.
			if (formattingElement == null) {
				new InBody().anyOtherEndTag(parserContext);
			}

			// 6 If formatting element is not in the stack of open elements,
			// then this is a parse error; remove the element from the list, and
			// abort these steps.
			if (!parserContext.getOpenElements().contains(formattingElement)) {
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);
				parserContext.getActiveFormattingElements().remove(
						formattingElement);
				return parserContext;
			}

			// 7 If formatting element is in the stack of open elements, but the
			// element is not in scope, then this is a parse error; abort these
			// steps.
			else {
				if (!ElementInScope.isInScope(parserContext,
						formattingElement.getNodeName())) {
					parserContext
							.addParseErrors(ParseErrorType.UnexpectedToken);
					return parserContext;
				}
			}
			// 8 If formatting element is not the current node, this is a parse
			// error. (But do not abort these steps.)
			if (!formattingElement.equals(currentNode))
				parserContext.addParseErrors(ParseErrorType.UnexpectedToken);

			// 9 Let furthest block be the topmost node in the stack of open
			// elements that is lower in the stack than formatting element, and
			// is an element in the special category. There might not be one.
			stackOpenElements.addAll(parserContext.getOpenElements());
			formattingElementIndex = stackOpenElements
					.indexOf(formattingElement);
			for (int i = formattingElementIndex + 1; i < stackOpenElements
					.size(); i++)
				if (Arrays.asList(HTML5Elements.SPECIAL).contains(
						stackOpenElements.get(i).getNodeName())) {
					furthestBlock = stackOpenElements.get(i);
					break;
				}

			// 10 If there is no furthest block, then the UA must first pop all
			// the nodes from the bottom of the stack of open elements, from the
			// current node up to and including formatting element, then remove
			// formatting element from the list of active formatting elements,
			// and finally
			// abort these steps.
			if (furthestBlock == null) {
				do {
					Element e = parserContext.getOpenElements().pop();
					if (e.equals(formattingElement))
						break;
				} while (!parserContext.getOpenElements().isEmpty());
				parserContext.getActiveFormattingElements().remove(
						formattingElement);
				return parserContext;
			}

			// 11 Let common ancestor be the element immediately above
			// formatting
			// element in the stack of open elements.
			commonAncestor = stackOpenElements.get(formattingElementIndex - 1);

			// 12 Let a bookmark note the position of formatting element in the
			// list of active formatting elements relative to the elements on
			// either
			// side of it in the list.

			// 13 Let node and last node be furthest block. Follow these steps:
			node = furthestBlock;
			nodeIndex = stackOpenElements.indexOf(node);
			lastNode = furthestBlock;

			// 13.1 Let inner loop counter be zero.
			innerLoop = 0;
			while (true) {
				// 13.2 Inner loop: Increment inner loop counter by one.
				innerLoop++;

				// 13.3 Let node be the element immediately above node in the
				// stack of open elements, or if node is no longer in the stack
				// of
				// open elements (e.g. because it got removed by this
				// algorithm), the
				// element that was immediately above node in the stack of open
				// elements before node was removed.
				if (stackOpenElements.contains(node))
					node = stackOpenElements.get(nodeIndex - 1);
				else
					node = stackOpenElements.get(nodeIndex);
				nodeIndex = stackOpenElements.indexOf(node);

				// 13.4 If node is formatting element, then go to the next step
				// in the overall algorithm.
				if (node.equals(formattingElement))
					break;

				// 13.5 If inner loop counter is greater than three and node is
				// in the list of active formatting elements, then remove node
				// from
				// the list of active formatting elements.
				if (innerLoop > 3) {
					if (listFormattingElements.contains(node))
						listFormattingElements.remove(node);
				}

				// 13.6 If node is not in the list of active formatting
				// elements, then remove node from the stack of open elements
				// and then go back to the step labeled inner loop.
				if (!parserContext.getActiveFormattingElements().contains(node)) {
					stackOpenElements.remove(node);
					continue;
				}

				// 13.7 Create an element for the token for which the element
				// node was created, in the HTML namespace, with common ancestor
				// as the intended parent; replace the entry for node in the
				// list of active formatting elements with an entry for the new
				// element, replace the entry for node in the stack of open
				// elements with an entry for the new element, and let node be
				// the new element.

				// TODO add new element
				TagToken t = (TagToken) node.getUserData("0");
				Element newElement = CreateAnElementForAToken.run(
						commonAncestor, Namespace.HTML, t, parserContext);
				int nodeIndexFormatting = listFormattingElements.indexOf(node);
				listFormattingElements.remove(node);
				listFormattingElements.add(nodeIndexFormatting, newElement);
				stackOpenElements.remove(nodeIndex);
				stackOpenElements.add(nodeIndex, newElement);
				node = newElement;

				// 13.8 If last node is furthest block, then move the
				// aforementioned bookmark to be immediately after the new node
				// in the list of active formatting elements.
				if (lastNode.equals(furthestBlock))
					formattingElementIndex = nodeIndexFormatting + 1;

				// 13.9 Insert last node into node, first removing it from its
				// previous parent node if any.
				try {
					doc.removeChild(lastNode);
				} finally {
					node.appendChild(lastNode);
				}

				// 13.10 Let last node be node.
				lastNode = node;

				// 13.11 Return to the step labeled inner loop.
			}
			// 14 Insert whatever last node ended up being in the previous step
			// at the appropriate place for inserting a node, but using common
			// ancestor as the override target.

			// TODO add last node at the appropriate place
			AdjustedInsertionLocation loc = AppropiatePlaceForInsertingANode
					.run(parserContext, commonAncestor);
			loc.getParent().appendChild(lastNode);

			// 15 Create an element for the token for which formatting element
			// was created, in the HTML namespace, with furthest block as the
			// intended parent.

			// TODO create element for formatting element token
			TagToken t = (TagToken) formattingElement.getUserData("0");
			Element newElement = CreateAnElementForAToken.run(furthestBlock,
					Namespace.HTML, t, parserContext);

			// 16 Take all of the child nodes of furthest block and append them
			// to the element created in the last step.
			NodeList nl = furthestBlock.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				newElement.appendChild(nl.item(i).cloneNode(true));
			}
			while (furthestBlock.getChildNodes().getLength() > 0)
				furthestBlock.removeChild(furthestBlock.getFirstChild());

			// 17 Append that new element to furthest block.
			furthestBlock.appendChild(newElement);

			// 18 Remove formatting element from the list of active formatting
			// elements, and insert the new element into the list of active
			// formatting elements at the position of the aforementioned
			// bookmark.
			formattingElementIndex = listFormattingElements
					.indexOf(formattingElement);
			listFormattingElements.remove(formattingElement);
			listFormattingElements.add(formattingElementIndex, newElement);

			// 19 Remove formatting element from the stack of open elements, and
			// insert the new element into the stack of open elements
			// immediately below the position of furthest block in that stack.
			stackOpenElements.remove(formattingElement);
			stackOpenElements.add(stackOpenElements.indexOf(furthestBlock) - 1,
					newElement);

			// 20 Jump back to the step labeled outer loop.
		}
		Stack<Element> s = new Stack<>();
		s.addAll(stackOpenElements);
		parserContext.setOpenElements(s);
		return parserContext;
	}
}
