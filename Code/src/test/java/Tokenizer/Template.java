package Tokenizer;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.parser.Tokenizer;

public class Template {

	@Test
	public final void testEmptyString() {

		try {

			ParserContext parserContext = new ParserContext();

			String string = "";

			tokenize(parserContext, string);

			Token tok = parserContext.getTokenizerContext().getTokens().poll();

			assertTrue("No EOF token",
					tok.getType().equals(TokenType.end_of_file));
			assertTrue("No more tokens expected", parserContext
					.getTokenizerContext().getTokens().isEmpty());

			printTokens(parserContext);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void printTokens(ParserContext parserContext) {
		for (Token tok : parserContext.getTokenizerContext().getTokens()) {
			System.out.println(tok.getType() + " : " + tok.getValue());
		}
	}

	private void tokenize(ParserContext parserContext, String string)
			throws IOException {
		BufferedReader in = null;

		Tokenizer tokenizer = new Tokenizer();

		in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
				string.getBytes()), "UTF8"));

		// tokenize
		Token lastToken = null;
		int currentChar = in.read();
		do {
			TokenizerContext tokenizerContext = parserContext
					.getTokenizerContext();
			tokenizerContext.setCurrentInputCharacter(currentChar);
			parserContext = tokenizer.tokenize(parserContext);

			/*
			 * If not reconsume, then read next character of the stream
			 */
			if (!tokenizerContext.isFlagReconsumeCurrentInputCharacter()) {
				currentChar = in.read();
			} else {
				tokenizerContext.setFlagReconsumeCurrentInputCharacter(false);
			}

			for (Token tok : parserContext.getTokenizerContext().getTokens()) {
				lastToken = tok;// get the last token emitted from the queue
			}

		} while (lastToken != null
				&& lastToken.getType() != TokenType.end_of_file);
	}

}