package com.html5parser.CompleteParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.Document;

import com.html5parser.Classes.ParserContext;
import com.html5parser.Classes.TokenizerContext;
import com.html5parser.Interfaces.IParser;

public class Parser implements IParser {

	public Document parse(String htmlString) {
		return parse(new ByteArrayInputStream(htmlString.getBytes()));
	}

	public Document parse(InputStream stream) {
		Document doc = null;

		ParserContext parserContext = new ParserContext();
		Tokenizer tokenizer = new Tokenizer();
		TreeConstructor treeConstructor = new TreeConstructor();

		BufferedReader in;
		try {
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
					tokenizerContext
							.setFlagReconsumeCurrentInputCharacter(false);
					currentChar = in.read();
				}

				System.out.println(parserContext.getTokenizerContext().getTokens());
				
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
		}

		return doc;
	}
}