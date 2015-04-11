package com.html5parser.algorithms;

import java.util.Stack;

import org.w3c.dom.Element;

import com.html5parser.classes.InsertionMode;
import com.html5parser.classes.ParserContext;
import com.html5parser.factories.InsertionModeFactory;

public class ResetTheInsertionModeAppropriately {
	
	public static void Run(ParserContext parserContext) {
		Run(parserContext, null) ;
	}
	
	public static void Run(ParserContext parserContext, Element context) {
		InsertionModeFactory factory = InsertionModeFactory.getInstance();
		Stack<Element> openElements = new Stack<Element>();
		openElements.addAll(parserContext.getOpenElements());

		// 1 Let last be false.
		Boolean last = false;

		// 2 Let node be the last node in the stack of open elements.
		Element node = openElements.pop();

		// 3 Loop: If node is the first node in the stack of open elements, then
		// set last to true, and, if the parser was originally created as part
		// of the HTML fragment parsing algorithm (fragment case) set node to
		// the context element.

		do {

			if (openElements.isEmpty()) {
				last = true;
				// if fragment case, node = context element
				if(context!=null){
					node=context;
				}
			}
			switch (node.getNodeName()) {

			// 4 If node is a select element, run these substeps:
			case "select":
				Boolean done = false;
				// 1 If last is true, jump to the step below labeled done.
				if (last)
					done = true;
				else {
					// 2 Let ancestor be node.
					Element ancestor = node;

					// 3 Loop: If ancestor is the first node in the stack of
					// open
					// elements, jump to the step below labeled done.
					do {
						if (openElements.isEmpty())
							done = true;

						// 4 Let ancestor be the node before ancestor in the
						// stack of open elements.
						ancestor = openElements.pop();

						// 5 If ancestor is a template node, jump to the step
						// below
						// labeled done.
						if (ancestor.getNodeName().equals("template"))
							done = true;

						// 6 If ancestor is a table node, switch the insertion
						// mode
						// to "in select in table" and abort these steps.
						if (ancestor.getNodeName().equals("table")) {
							parserContext
									.setInsertionMode(factory
											.getInsertionMode(InsertionMode.in_select_in_table));
							return;
						}

						// 7 Jump back to the step labeled loop.
					} while (!done);
				}
				// 8 Done: Switch the insertion mode to "in select" and
				// abort these steps.
				if (done) {
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_select));
					return;
				}
				break;

			// 5 If node is a td or th element and last is false, then
			// switch
			// the insertion mode to "in cell" and abort these steps.
			case "td":
			case "th":
				if (!last) {
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_cell));
					return;
				}
				break;

			// 6 If node is a tr element, then switch the insertion mode to
			// "in row" and abort these steps.
			case "tr":
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_row));
				return;

				// 7 If node is a tbody, thead, or tfoot element, then switch
				// the insertion mode to "in table body" and abort these steps.
			case "tbody":
			case "thead":
			case "tfoot":
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_table_body));
				return;

				// 8 If node is a caption element, then switch the insertion
				// mode to "in caption" and abort these steps.
			case "caption":
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_caption));
				return;

				// 9 If node is a colgroup element, then switch the insertion
				// mode to "in column group" and abort these steps.
			case "colgroup":
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_column_group));
				return;

				// 10 If node is a table element, then switch the insertion mode
				// to "in table" and abort these steps.
			case "table":
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_table));
				return;

				// 11 If node is a template element, then switch the insertion
				// mode to the
				// current template insertion mode and abort these steps.
			case "template":
				parserContext.setInsertionMode(parserContext
						.getCurrentTemplateInsertionMode());
				return;

				// 12 If node is a head element and last is true, then switch
				// the insertion mode to "in body" ("in body"! not "in head"!)
				// and abort these steps. (fragment case)
				// 13 If node is a head element and last is false, then switch
				// the insertion mode to "in head" and abort these steps.
			case "head":
				if (last)
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_body));
				else
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_head));
				return;
				// 14 If node is a body element, then switch the insertion mode
				// to "in body" and abort these steps.
			case "body":
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_body));
				return;

				// 15 If node is a frameset element, then switch the insertion
				// mode to "in frameset" and abort these steps. (fragment case)
			case "frameset":
				parserContext.setInsertionMode(factory
						.getInsertionMode(InsertionMode.in_frameset));
				return;

				// 16 If node is an html element, run these substeps:
			case "html":
				// 1 If the head element pointer is null, switch the insertion
				// mode to "before head" and abort these steps. (fragment case)
				if (parserContext.getHeadElementPointer() == null)
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.before_head));
				// 2 Otherwise, the head element pointer is not null, switch the
				// insertion mode to "after head" and abort these steps.
				else
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.after_head));
				return;

				// 17 If last is true, then switch the insertion mode to
				// "in body" and
				// abort these steps. (fragment case)
			default:
				if (last) {
					parserContext.setInsertionMode(factory
							.getInsertionMode(InsertionMode.in_body));
					return;
				}
				break;
			}

			// 18 Let node now be the node before node in the stack of open
			// elements.

			// 19 Return to the step labeled loop.

			node = openElements.pop();

		} while (true);
	}
}