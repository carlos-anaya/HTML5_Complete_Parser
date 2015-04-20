package com.html5parser.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.html5parser.classes.ParserContext;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		// String html = "<!Doctype html>";
		// System.out.println(parser.parse(html));
		// System.out.println(serializeDocument(parseString(html)));
		String filePath = "C:\\Users\\hs012\\Desktop\\Amazon.co.uk.html";
		System.out.println(serializeDocument(parseHtmlFile(filePath), true));

	}

	private static Document parseString(String html) {
		Parser parser = new Parser();
		ParserContext parserContext = parser
				.tokenize(new ParserContext(), html);
		parser.printTokens(parserContext);
		return parser.parse(html);
	}

	private static Document parseHtmlFile(String filePath) {
		InputStream is;
		Document doc = null;

		try {
			is = new FileInputStream(filePath);
			Parser parser = new Parser();
			doc = parser.parse(is);
			is.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	private static String serializeDocument(Document doc)
	{
		return serializeDocument(doc, false);
	}
	
	private static String serializeDocument(Document doc, Boolean saveFile) {
		boolean indent = true;
		try {
			StringWriter writer = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes"
					: "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			if(saveFile)
			{
				transformer.transform(new DOMSource(doc), new StreamResult(new File("output.html")));
			}
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.toString();
		} catch (IllegalArgumentException
				| TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
			return null;

		}
	}
}
