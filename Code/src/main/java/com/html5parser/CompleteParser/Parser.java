package com.html5parser.CompleteParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.html5parser.Classes.ParserContext;
import com.html5parser.Classes.Token;
import com.html5parser.Classes.TokenizerContext;
import com.html5parser.Interfaces.IParser;

public class Parser implements IParser {

	public Document parse(String htmlString) {
		return parse(new ByteArrayInputStream(htmlString.getBytes()));
	}

	public Document parse(InputStream stream) {

		ParserContext parserContext = new ParserContext();
		Tokenizer tokenizer = new Tokenizer();
		TreeConstructor treeConstructor = new TreeConstructor();
		
		BufferedReader in;
		try {
			
			Document doc;
			DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			doc = builder.newDocument();
			parserContext.setDocument(doc);
			
			in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

			int currentChar = in.read();
			while (!parserContext.isFlagStopParsing()) {
				TokenizerContext tokenizerContext = parserContext
						.getTokenizerContext();
				tokenizerContext.setCurrentInputCharacter(currentChar);
				parserContext = tokenizer.tokenize(parserContext);

				/*
				 * If not reconsume, then read next character of the stream
				 */
				if (!tokenizerContext.isFlagReconsumeCurrentInputCharacter()) {
					currentChar = in.read();
				}else{
					tokenizerContext.setFlagReconsumeCurrentInputCharacter(false);					
				}

				for(Token tok : parserContext.getTokenizerContext().getTokens()){
					System.out.println(tok.getType()+" : "+tok.getValue());
				}
				
				
				/*
				 * Consume all the tokens emited
				 */
				if (tokenizerContext.isFlagEmitToken()) {
					while (!parserContext.getTokenizerContext().getTokens()
							.isEmpty()) {
						/*
						 * Get the next token of the queue for the tree
						 * constructor
						 */
						parserContext.getTokenizerContext().pollCurrentToken();
						do {
							parserContext.setFlagReconsumeToken(false);
							parserContext = treeConstructor
									.consumeToken(parserContext);
						} while (parserContext.isFlagReconsumeToken());
					}
					parserContext.getTokenizerContext().setFlagEmitToken(false);
				}
				
				
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parserContext.getDocument();
	}
}