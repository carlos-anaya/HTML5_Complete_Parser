package com.html5parser.algorithmsTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.classes.ParserContext;

public class TestElementInScope {
	
	@Test
	public void test_pIsInButtonScopeByChild() {
		String elementName = "p";
		String[] stack = { "html", "body", "button", "p", "b" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in button Scope",
				ElementInScope.isInButtonScope(parserContext, elementName));
	}
	
	@Test
	public void test_pIsInButtonScopeDirect() {
		String elementName = "p";
		String[] stack = { "html", "body", "button", "p"};
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in button Scope",
				ElementInScope.isInButtonScope(parserContext, elementName));
	}
	
	@Test
	public void test_pIsNotInButtonScope() {
		String elementName = "p";
		String[] stack = { "html", "body", "button"};
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertFalse(elementName + " is not in button Scope",
				ElementInScope.isInButtonScope(parserContext, elementName));
	}
	
	@Test
	public void test_liIsInButtonScopeByChild() {
		String elementName = "li";
		String[] stack = { "html", "body", "ul", "li", "i", "b" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in list item Scope",
				ElementInScope.isInListItemScope(parserContext, elementName));
	}
	
	@Test
	public void test_liIsInButtonScopeDirect() {
		String elementName = "li";
		String[] stack = { "html", "body", "ul", "li"};
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in list item Scope",
				ElementInScope.isInListItemScope(parserContext, elementName));
	}
	
	@Test
	public void test_liIsNotInButtonScope() {
		String elementName = "li";
		String[] stack = { "html", "body", "ul"};
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertFalse(elementName + " is not in list item Scope",
				ElementInScope.isInListItemScope(parserContext, elementName));
	}
	
	@Test
	public void test_selectIsInSelectScope() {
		String elementName = "select";
		String[] stack = { "html", "body", "select" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in Select Scope",
				ElementInScope.isInSelectScope(parserContext, elementName));
	}

	@Test
	public void test_selectIsInSelectScopeByChild() {
		String elementName = "select";
		String[] stack = { "html", "body", "select", "option" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in Select Scope",
				ElementInScope.isInSelectScope(parserContext, elementName));
	}
	
	@Test
	public void test_selectIsNotInSelectScope() {
		String elementName = "select";
		String[] stack = { "html", "body"};
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertFalse(elementName + " is not in Select Scope",
				ElementInScope.isInSelectScope(parserContext, elementName));
	}

	@Test
	public void test_tableIsInTableScopeNested() {
		String elementName = "table";
		String[] stack = { "html", "body", "table", "tbody", "td" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in Table Scope",
				ElementInScope.isInTableScope(parserContext, elementName));
	}
	
	@Test
	public void test_tableIsInTableScopeDirect() {
		String elementName = "table";
		String[] stack = { "html", "body", "table" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in Table Scope",
				ElementInScope.isInTableScope(parserContext, elementName));
	}

	@Test
	public void test_tableIsNotInTableScope() {
		String elementName = "table";
		String[] stack = { "html", "body" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertFalse(elementName + " is not in Table Scope",
				ElementInScope.isInTableScope(parserContext, elementName));
	}
	
	@Test
	public void test_tbodyIsInTableScopeNested() {
		String elementName = "tbody";
		String[] stack = { "html", "body", "table", "tbody", "td", "p" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in Table Scope",
				ElementInScope.isInTableScope(parserContext, elementName));
	}

	@Test
	public void test_tbodyIsInTableScopeDirect() {
		String elementName = "tbody";
		String[] stack = { "html", "body", "table", "tbody" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertTrue(elementName + " is in Table Scope",
				ElementInScope.isInTableScope(parserContext, elementName));
	}

	@Test
	public void test_tbodyIsNotInTableScope() {
		String elementName = "tbody";
		String[] stack = { "html", "body", "table" };
		Document doc = createDocument();
		ParserContext parserContext = createStackOfOpenElements(doc, stack);

		assertFalse(elementName + " is not in Table Scope",
				ElementInScope.isInTableScope(parserContext, elementName));
	}

	private Document createDocument() {

		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = dbf.newDocumentBuilder();
			doc = builder.newDocument();

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	private ParserContext createStackOfOpenElements(Document doc,
			String[] elements) {
		ParserContext parserContext = new ParserContext();
		for (String elementName : elements) {
			Element el = doc.createElement(elementName);
			parserContext.getOpenElements().push(el);

		}
		return parserContext;
	}
}
