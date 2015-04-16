package TreeConstructor;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.html5parser.algorithms.ParsingHTMLFragments;
import com.html5parser.classes.ParserContext;
import com.html5parser.parser.Parser;

/* HTML5LIB FORMAT example
 * 
 * 

 #test
 <p>One<p>Two
 #errors
 3: Missing document type declaration
 #document
 | <html>
 |   <head>
 |   <body>
 |     <p>
 |       "One"
 |     <p>
 |       "Two"

 */

@RunWith(value = Parameterized.class)
public class TreeConstructorTesthtml5libsuite {

	private String testName;
	private String test;

	// parameters pass via this constructor
	public TreeConstructorTesthtml5libsuite(String testName, String test) {
		this.testName = testName;
		this.test = test;
	}

	// Declares parameters here
	@Parameters(name = "Test name: {0}")
	public static Iterable<Object[]> test1() {
		List<Object[]> testList = new ArrayList<Object[]>();

		String[] resources = {
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tree-construction/adoption01.dat",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tree-construction/adoption02.dat",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tree-construction/tables01.dat",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tree-construction/tricky01.dat",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tree-construction/tests1.dat"};

		for (String resource : resources) {
			testList = addTestFile(testList, resource);
		}

		return testList;
	}

	private static List<Object[]> addTestFile(List<Object[]> testList,
			String resource) {
		BufferedReader in = null;
		Scanner scanner = null;
		URL url;
		try {
			url = new URL(resource);
			in = new BufferedReader(new InputStreamReader(url.openStream()));

			// String resource = "C:\\Users\\JoséArmando\\Desktop\\test.txt";
			// in = new BufferedReader(new FileReader(new File(resource)));

			scanner = new Scanner(in);
			String testFile = scanner.useDelimiter("\\A").next();
			String[] tests = testFile.split("(^|\n\n)#data\n");

			for (int i = 1; i < tests.length; i++) {
				String test = tests[i];
				String testName = test.split("\\n")[0]; // i + "";
				testList.add(new Object[] { testName, test });
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				if (scanner != null)
					scanner.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return testList;
	}

	@Test
	public final void tests() {
		String contextElement = null;

		System.out.println("*************** " + testName);

		int errorsStart = test.indexOf("\n#errors\n");
		if (errorsStart != -1) {
			String input = test.substring(0, errorsStart);
			int fragmentStart = test.indexOf("\n#document-fragment\n");
			int domStart = test.indexOf("\n#document\n");
			if (fragmentStart != -1) {
				contextElement = test.substring(fragmentStart + 20, domStart);
			}
			if (domStart != -1) {
				String dom = test.substring(domStart + 11);
				if (dom.substring(dom.length() - 1) == "\n") {
					dom = dom.substring(0, dom.length() - 1);
				}
				run_test(input, contextElement, dom);
				return;
			}
		}
		System.out.println("Invalid test: " + test);

	}

	private void run_test(String input, String contextElement, String expected) {

		// remove new line character if is the last character
		if (expected.lastIndexOf('\n') == expected.length() - 1) {
			expected = expected.substring(0, expected.length() - 1);
		}

		// System.out.println("*************** " + input);
		// System.out.println("******Expected " + expected);
		if (contextElement != null) {
			Document document = null;
			ParserContext parserContext = new ParserContext();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = dbf.newDocumentBuilder();
				document = builder.newDocument();
				parserContext.setDocument(document);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Element element = document.createElement(contextElement);
			try {
				NodeList result = ParsingHTMLFragments.run(parserContext,
						element, input);

				int size = result.getLength();
				for (int i = 0; i < size; i++) {
					Node node = result.item(i);
					System.out.println(node);
					Node adopted = document.importNode(node, true);
					element.appendChild(adopted);
					process_result(input, element, expected);
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Parser parser = new Parser();
			Document result = parser.parse(input);
			process_result(input, result, expected);
		}

	}

	private void process_result(String input, Node element, String expected) {
		String result = dom2string(element);
		System.out.println();
		System.out.println("****************** Input: " + input
				+ "  ******************");
		System.out.println(input);
		System.out.println("*******************");
		System.out.println();
		System.out.println();
		System.out.println("****************** Expected: " + testName
				+ "  ******************");
		System.out.println(expected);
		System.out.println("*******************");
		System.out.println();
		System.out.println();
		System.out.println("****************** Result: " + testName
				+ "  ******************");
		System.out.println(result);
		System.out.println("*******************");
		System.out.println();
		assertEquals("TEST FAILED", expected, result);

	}

	private String indent(int ancestors) {
		String str = "";
		if (ancestors > 0) {
			while (0 <= --ancestors)
				str += "  ";
		}
		return str;
	}

	private String dom2string(Node node) {
		String str = "";
		int ancestors = 0;
		if (node.getFirstChild() == null)
			return "| ";
		Node parent = node;
		Node current = node.getFirstChild();
		Node next = null;
		for (;;) {
			str += "\n| " + indent(ancestors);
			switch (current.getNodeType()) {
			case Node.DOCUMENT_NODE:
				str += "<!DOCTYPE " + current.getNodeName() + '>';
				break;
			case Node.COMMENT_NODE:
				try {
					str += "<!-- " + current.getNodeValue() + " -->";
				} catch (NullPointerException e) {
					str += "<!--  -->";
				}
				if (parent != current.getParentNode()) {
					return str += " (misnested... aborting)";
				}
				break;
			case 7:
				str += "<?" + current.getNodeName() + current.getNodeValue()
						+ '>';
				break;
			case Node.CDATA_SECTION_NODE:
				str += "<![CDATA[ " + current.getNodeValue() + " ]]>";
				break;
			case Node.TEXT_NODE:
				str += '"' + current.getNodeValue() + '"';
				if (parent != current.getParentNode()) {
					return str += " (misnested... aborting)";
				}
				break;
			case Node.ELEMENT_NODE:
				str += "<";
				if (current.getNamespaceURI() != null)
					switch (current.getNamespaceURI()) {
					case "http://www.w3.org/2000/svg":
						str += "svg ";
						break;
					case "http://www.w3.org/1998/Math/MathML":
						str += "math ";
						break;
					}
				if (current.getNamespaceURI() != null
						&& current.getLocalName() != null) {
					str += current.getLocalName();
				} else {
					str += current.getNodeName().toLowerCase();
				}
				str += '>';
				if (parent != current.getParentNode()) {
					return str += " (misnested... aborting)";
				} else {
					if (current.hasAttributes()) {
						List<String> attrNames = new ArrayList<String>();
						Map<String, Integer> attrPos = new HashMap<String, Integer>();
						for (int j = 0; j < current.getAttributes().getLength(); j += 1) {
							if (current.getAttributes().item(j) != null) {
								String name = "";
								if (current.getAttributes().item(j)
										.getNamespaceURI() != null)
									switch (current.getAttributes().item(j)
											.getNamespaceURI()) {
									case "http://www.w3.org/XML/1998/namespace":
										name += "xml ";
										break;
									case "http://www.w3.org/2000/xmlns/":
										name += "xmlns ";
										break;
									case "http://www.w3.org/1999/xlink":
										name += "xlink ";
										break;
									}
								if (current.getAttributes().item(j)
										.getLocalName() != null) {
									name += current.getAttributes().item(j)
											.getLocalName();
								} else {
									name += current.getAttributes().item(j)
											.getNodeName();
								}
								attrNames.add(name);
								attrPos.put(name, j);
							}
						}
						if (attrNames.size() > 0) {
							attrNames.sort(null);
							for (int j = 0; j < attrNames.size(); j += 1) {
								str += "\n| " + indent(1 + ancestors)
										+ attrNames.get(j);
								str += "=\""
										+ current
												.getAttributes()
												.item(attrPos.get(attrNames
														.get(j)))
												.getNodeValue() + "\"";
							}
						}
					}
					next = current.getFirstChild();
					if (null != next) {
						parent = current;
						current = next;
						ancestors++;
						continue;
					}
				}
				break;
			}
			for (;;) {
				next = current.getNextSibling();
				if (next != null) {
					current = next;
					break;
				}
				current = current.getParentNode();
				parent = parent.getParentNode();
				ancestors--;
				if (current == node) {
					return str.substring(1);
				}
			}
		}
	}

}
