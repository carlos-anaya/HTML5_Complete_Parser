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
import com.html5parser.parser.Parser;
import com.html5parser.parser.Tokenizer;

public class Template {

	@Test
	public final void testEmptyString() {

		Parser parser = new Parser();
		ParserContext parserContext = new ParserContext();

		String string = "\u0000";

		parserContext = parser.tokenize(parserContext, string);
		parser.printTokens(parserContext);

		Token tok = parserContext.getTokenizerContext().getTokens().poll();

		assertTrue("No EOF token", tok.getType().equals(TokenType.end_of_file));
		assertTrue("No more tokens expected", parserContext
				.getTokenizerContext().getTokens().isEmpty());

	}

}