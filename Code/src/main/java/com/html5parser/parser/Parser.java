package com.html5parser.parser;

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

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;
import com.html5parser.interfaces.IParser;
import com.html5parser.parseError.ParseError;
import com.html5parser.parseError.ParseErrorType;

public class Parser implements IParser {

	public Document parse(String htmlString) {
		return parse(new ByteArrayInputStream(htmlString.getBytes()));
	}

	public Document parse(InputStream stream) {

		ParserContext parserContext = new ParserContext();
		Tokenizer tokenizer = new Tokenizer();
		StreamPreprocessor streamPreprocessor = new StreamPreprocessor();
		TreeConstructor treeConstructor = new TreeConstructor();

		BufferedReader in;
		try {

			Document doc;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			doc = builder.newDocument();
			parserContext.setDocument(doc);

			in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

			int currentChar = in.read();
			while (!parserContext.isFlagStopParsing()) {
				TokenizerContext tokenizerContext = parserContext
						.getTokenizerContext();
				/*
				 * Preprocess character
				 */
				Integer preProCurrentChar = streamPreprocessor
						.processLFAndCRCharacters(currentChar);
				// Ignore the LF characters that immediately follow a CR
				// character
				while (preProCurrentChar == 0x000D) {
					currentChar = in.read();
					preProCurrentChar = streamPreprocessor
							.processLFAndCRCharacters(currentChar);
				}
				// If invalid character add a parse error
				if (streamPreprocessor.isInvalidCharacter(preProCurrentChar)
						&& tokenizerContext.getCurrentInputCharacter() != preProCurrentChar) {
					tokenizerContext
							.setCurrentInputCharacter(preProCurrentChar);
					parserContext
							.addParseErrors(ParseErrorType.InvalidInputCharacter);
				}
				tokenizerContext.setCurrentInputCharacter(preProCurrentChar);
				parserContext = tokenizer.tokenize(parserContext);

				/*
				 * If not reconsume, then read next character of the stream
				 */
				if (!tokenizerContext.isFlagReconsumeCurrentInputCharacter()) {
					currentChar = in.read();
				} else {
					tokenizerContext
							.setFlagReconsumeCurrentInputCharacter(false);
				}

				for (Token tok : parserContext.getTokenizerContext()
						.getTokens()) {
					System.out.println(tok.getType() + " : " + tok.getValue());
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

	public ParserContext tokenize(ParserContext parserContext, String string) {
		BufferedReader in = null;

		Tokenizer tokenizer = new Tokenizer();
		StreamPreprocessor streamPreprocessor = new StreamPreprocessor();

		try {
			in = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(string.getBytes()), "UTF8"));

			int currentChar = in.read();
			Boolean stop = false;
			do {
				TokenizerContext tokenizerContext = parserContext
						.getTokenizerContext();

				/*
				 * Preprocess character
				 */
				Integer preProCurrentChar = streamPreprocessor
						.processLFAndCRCharacters(currentChar);
				// Ignore the LF characters that immediately follow a CR
				// character
				while (preProCurrentChar == 0x000D) {
					currentChar = in.read();
					preProCurrentChar = streamPreprocessor
							.processLFAndCRCharacters(currentChar);
				}
				// If invalid character add a parse error
				if (streamPreprocessor.isInvalidCharacter(preProCurrentChar)
						&& tokenizerContext.getCurrentInputCharacter() != preProCurrentChar) {
					tokenizerContext
							.setCurrentInputCharacter(preProCurrentChar);
					parserContext
							.addParseErrors(ParseErrorType.InvalidInputCharacter);
				}

				tokenizerContext.setCurrentInputCharacter(preProCurrentChar);

				parserContext = tokenizer.tokenize(parserContext);
				stop = currentChar == -1
						&& !tokenizerContext
								.isFlagReconsumeCurrentInputCharacter();
				/*
				 * If not reconsume, then read next character of the stream
				 */
				if (!tokenizerContext.isFlagReconsumeCurrentInputCharacter()) {
					currentChar = in.read();
				} else {
					tokenizerContext
							.setFlagReconsumeCurrentInputCharacter(false);
				}

			} while (!stop);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parserContext;
	}

	public void printTokens(ParserContext parserContext) {
		System.out.println("*** TOKENS ***\n");
		for (Token token : parserContext.getTokenizerContext().getTokens()) {
			switch (token.getType()) {
			case end_of_file:
				System.out.println("EOF");
				break;
			case character:
			case comment:
				System.out.println(token.getType() + " : " + token.getValue());
				break;
			case DOCTYPE:
				DocTypeToken docTypeToken = (DocTypeToken) token;
				System.out.println(docTypeToken.getType() + " : "
						+ docTypeToken.getValue() + " public id. "
						+ docTypeToken.getPublicIdentifier() + " system id. "
						+ docTypeToken.getSystemIdentifier()
						+ " force-quirks flag "
						+ docTypeToken.isForceQuircksFlag());
				break;
			case end_tag:
			case start_tag:
				TagToken tagToken = (TagToken) token;
				System.out.println(tagToken.getType() + " : "
						+ tagToken.getValue() + " self-closing flag "
						+ tagToken.isFlagSelfClosingTag() + " attributes: ");
				for (Attribute att : tagToken.getAttributes()) {
					System.out.println(att.getName() + " : " + att.getValue());
				}
				break;
			default:
				System.out.println("Error");
				break;
			}

		}

		System.out.println("\n\n*** ERRORS ***\n");

		for (ParseError error : parserContext.getParseErrors()) {
			System.out.println(error.getMessage());
		}
	}
}