package com.html5parser.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import com.html5parser.classes.ParserContext;
import com.html5parser.constants.HTML5Elements;

public class ListOfActiveFormattingElements {
		// private String[] markerElements = { "applet", "marquee", "object", "th",
	// "td", "template" };

	public static void push(ParserContext parserContext, Element element) {

		// Not a formatting element
		if (!Arrays.asList(HTML5Elements.FORMATTING).contains(element.getNodeName()))
			return;

		ArrayList<Element> list = parserContext.getActiveFormattingElements();

		// 1 If there are already three elements in the list of active
		// formatting elements after the last list marker, if any, or anywhere
		// in the list if there are no list markers, that have the same tag
		// name, namespace, and attributes as element, then remove the earliest
		// such element from the list of active formatting elements. For these
		// purposes, the attributes must be compared as they were when the
		// elements were created by the parser; two elements have the same
		// attributes if all their parsed attributes can be paired such that the
		// two attributes in each pair have identical names, namespaces, and
		// values (the order of the attributes does not matter).
		List<Element> sublist = list.subList(list.lastIndexOf(null) + 1,
				list.size());
		if (sublist.size() > 2) {
			Boolean repeated = false;
			for (Element e : sublist) {
				if (e.isEqualNode(element)) {
					list.remove(e);
					repeated = true;
					break;
				}
			}
			if (!repeated) {
				list.remove(list.lastIndexOf(null) + 1);
			}
		}
		// 2 Add element to the list of active formatting elements.
		list.add(element);
		parserContext.setActiveFormattingElements(list);
	}

	public static void insertMarker(ParserContext parserContext) {
		parserContext.getActiveFormattingElements().add(null);
	}

	public static void clear(ParserContext parserContext) {
		// 1 Let entry be the last (most recently added) entry in the list of
		// active formatting elements.
		// 2 Remove entry from the list of active formatting elements.
		// 3 If entry was a marker, then stop the algorithm at this point. The
		// list has been cleared up to the last marker.
		// 4 Go to step 1.
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		int indexLastMarker = list.lastIndexOf(null) + 1;
		while (list.size() > indexLastMarker)
			list.remove(indexLastMarker);
	}

	public static void reconstruct(ParserContext parserContext) {
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		// 1 If there are no entries in the list of active formatting elements,
		// then there is nothing to reconstruct; stop this algorithm.
		if (list.isEmpty())
			return;

		// 2 If the last (most recently added) entry in the list of active
		// formatting elements is a marker, or if it is an element that is in
		// the stack of open elements, then there is nothing to reconstruct;
		// stop this algorithm.
		int lastEntry = list.size() - 1;
		int lastInList = 0;
		Element entry = list.get(lastEntry);
		if (entry == null)
			return;
		if (parserContext.getOpenElements().contains(entry))
			return;

		// 3 Let entry be the last (most recently added) element in the list of
		// active formatting elements.

		// 4 Rewind: If there are no entries before entry in the list of active
		// formatting elements, then jump to the step labeled create.

		// 5 Let entry be the entry one earlier than entry in the list of active
		// formatting elements.

		// 6 If entry is neither a marker nor an element that is also in the
		// stack
		// of open elements, go to the step labeled rewind.
		for (lastInList = lastEntry - 1; lastInList > 0; lastInList--) {
			entry = list.get(lastInList);
			if (entry == null)
				break;
			if (parserContext.getOpenElements().contains(entry))
				break;
		}

		// 7 Advance: Let entry be the element one later than entry in the list
		// of
		// active formatting elements.

		// 8 Create: Insert an HTML element for the token for which the element
		// entry was created, to obtain new element.

		// 9 Replace the entry for entry in the list with an entry for new
		// element.

		// 10 If the entry for new element in the list of active formatting
		// elements is not the last entry in the list, return to the step
		// labeled advance.
		for (lastInList++; lastInList <= lastEntry; lastInList++) {
			entry = list.get(lastInList);
			// TODO insert element
		}
	}
}
