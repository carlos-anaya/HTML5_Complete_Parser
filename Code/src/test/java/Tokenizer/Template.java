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
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;
import com.html5parser.parseError.ParseError;
import com.html5parser.parser.Tokenizer;

public class Template {

	@Test
	public final void testEmptyString() {

		try {

			ParserContext parserContext = new ParserContext();

			String string = "<html><? comment1 ,>\n"
					+ "<!><!$%><!12345678></$comment2><foo abcd=xyz  abcd='r' bcd=\"u\" e f  =  i />";

			tokenize(parserContext, string);
			printTokens(parserContext);

			Token tok = parserContext.getTokenizerContext().getTokens().poll();

			assertTrue("No EOF token",
					tok.getType().equals(TokenType.end_of_file));
			assertTrue("No more tokens expected", parserContext
					.getTokenizerContext().getTokens().isEmpty());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void printTokens(ParserContext parserContext) {
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

			// for (Token tok : parserContext.getTokenizerContext().getTokens())
			// {
			// lastToken = tok;// get the last token emitted from the queue
			// }
			//
			// } while (lastToken != null
			// && lastToken.getType() != TokenType.end_of_file);
		} while (currentChar != -1);
	}

}