package com.html5parser.algorithmsTests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.html5parser.algorithms.ElementInScope;
import com.html5parser.algorithms.ListOfActiveFormattingElements;
import com.html5parser.classes.ParserContext;

public class TestListOfActiveFormattingElements {

	@Test
	public void test_addEmptyFormattingElement() {
		String[] elementList = {};
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		Element newElement = doc.createElement("a");
		ListOfActiveFormattingElements.push(parserContext, newElement);
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		assertTrue(newElement.getNodeName() + " added",
				list.contains(newElement));
	}

	@Test
	public void test_addDirectlyFormattingElement() {
		String[] elementList = { "i", "b" };
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		Element newElement = doc.createElement("a");
		ListOfActiveFormattingElements.push(parserContext, newElement);
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		assertTrue(newElement.getNodeName() + " added",
				list.contains(newElement));
	}

	@Test
	public void test_removeFirstFormattingElement() {
		String[] elementList = { "i", "b", "strong" };
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		Element firstElement = list.get(0);
		Element newElement = doc.createElement("a");
		ListOfActiveFormattingElements.push(parserContext, newElement);

		assertTrue(newElement.getNodeName() + " added",
				list.contains(newElement) && !list.contains(firstElement));
	}

	@Test
	public void test_addDirectlyWithMarkerFormattingElement() {
		String[] elementList = { "i", "b", "a", null, "strong" };
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		Element newElement = doc.createElement("a");
		ListOfActiveFormattingElements.push(parserContext, newElement);

		assertTrue(newElement.getNodeName() + " added",
				list.contains(newElement) && list.size() == 6);
	}

	@Test
	public void test_replaceSameFormattingElement() {
		String[] elementList = { "i", "b", "strong" };
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		Element firstElement = list.get(0);
		Element newElement = doc.createElement("i");
		ListOfActiveFormattingElements.push(parserContext, newElement);

		assertTrue(newElement.getNodeName() + " added",
				list.contains(newElement) && !list.contains(firstElement));
	}

	@Test
	public void test_addFormattingWithAttributesElement() {
		String[] elementList = { "i", "b" };
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		Element firstElement = list.get(0);
		firstElement.setAttribute("a", "x");
		firstElement.setAttribute("b", "y");
		Element newElement = doc.createElement("i");
		firstElement.setAttribute("b", "y");
		ListOfActiveFormattingElements.push(parserContext, newElement);

		assertTrue(newElement.getNodeName() + " added",
				list.contains(newElement) && list.contains(firstElement));
	}

	@Test
	public void test_replaceSameFormattingWithAttributesElement() {
		String[] elementList = { "i", "b", "strong" };
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		Element firstElement = list.get(0);
		firstElement.setAttribute("a", "x");
		firstElement.setAttribute("b", "y");
		Element newElement = doc.createElement("i");
		firstElement.setAttribute("b", "y");
		firstElement.setAttribute("a", "x");
		ListOfActiveFormattingElements.push(parserContext, newElement);

		assertTrue(newElement.getNodeName() + " added",
				list.contains(newElement) && !list.contains(firstElement));
	}

	@Test
	public void test_addNonFormattingElement() {
		String[] elementList = { "i", "b" };
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		Element newElement = doc.createElement("aop");
		ListOfActiveFormattingElements.push(parserContext, newElement);
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		assertFalse(newElement.getNodeName() + " added",
				list.contains(newElement));
	}

	@Test
	public void test_clearEmptyFormattingElement() {
		String[] elementList = {};
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ListOfActiveFormattingElements.clear(parserContext);
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		assertTrue("Empty list", list.isEmpty());
	}
	
	@Test
	public void test_clearNoMarkerFormattingElement() {
		String[] elementList = {"a", "b"};
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ListOfActiveFormattingElements.clear(parserContext);
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		assertTrue("Empty list", list.isEmpty());
	}

	@Test
	public void test_clearUpToMarkerFormattingElement() {
		String[] elementList = {"a", "b", null, "i", "a"};
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ListOfActiveFormattingElements.clear(parserContext);
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		assertTrue("Empty list", list.size() == 3);
	}
	
	@Test
	public void test_clearNothingFormattingElement() {
		String[] elementList = {"a", "b", null};
		Document doc = createDocument();
		ParserContext parserContext = createListOfFormattingElements(doc,
				elementList);

		ListOfActiveFormattingElements.clear(parserContext);
		ArrayList<Element> list = parserContext.getActiveFormattingElements();
		assertTrue("Empty list", list.size() == 3);
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

	private ParserContext createListOfFormattingElements(Document doc,
			String[] elements) {
		ParserContext parserContext = new ParserContext();
		ArrayList<Element> list = parserContext.getActiveFormattingElements();

		for (String elementName : elements) {
			if (elementName != null) {
				Element el = doc.createElement(elementName);
				parserContext.getActiveFormattingElements().add(el);
			} else
				parserContext.getActiveFormattingElements().add(null);
		}

		return parserContext;
	}
}
