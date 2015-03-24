package Tokenizer;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.Token.TokenType;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;
import com.html5parser.parser.Tokenizer;

/* HTML5LIB FORMAT
 * 
 * {"tests": [
 {"description": "Test description",
 "input": "input_string",
 "output": [expected_output_tokens],
 "initialStates": [initial_states],
 "lastStartTag": last_start_tag,
 "ignoreErrorOrder": ignore_error_order
 }
 ]}
 */

@RunWith(value = Parameterized.class)
public class TokenizerTesthtml5libsuite {

	private String testName;
	private JSONObject test;

	// parameters pass via this constructor
	public TokenizerTesthtml5libsuite(String testName, JSONObject test) {
		this.testName = testName;
		this.test = test;
	}

	// Declares parameters here
	@Parameters(name = "Test name: {0}")
	public static Iterable<Object[]> data1() {
		List<Object[]> testList = new ArrayList<Object[]>();

		String[] resources = { "https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/test1.test",
		// "https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/test2.test",
		};

		for (String resource : resources) {
			testList = addTestFile(testList, resource);
		}

		return testList;
	}

	private static List<Object[]> addTestFile(List<Object[]> testList,
			String resource) {
		BufferedReader in = null;

		URL url;
		try {
			url = new URL(resource);
			in = new BufferedReader(new InputStreamReader(url.openStream()));

			// String resource = "C:\\Users\\JoséArmando\\Desktop\\data.txt";
			// in = new BufferedReader(new FileReader(new File(resource)));

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(in);

			// get the test suite object
			JSONArray tests = (JSONArray) jsonObject.get("tests");

			for (int i = 0; i < tests.size(); i++) {
				JSONObject test = (JSONObject) tests.get(i);
				String testName = (String) test.get("description");
				testList.add(new Object[] { testName, test });
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return testList;
	}

	@Test
	public final void tests() {
		try {
			ParserContext parserContext = new ParserContext();
			tokenize(parserContext, (String) test.get("input"));
			String output = serializeTokens(simplifyCharacterTokens(parserContext
					.getTokenizerContext().getTokens()));

			JSONArray expectedOutput = (JSONArray) test.get("output");
			String expected = expectedOutput.toString().replaceAll(
					"\"ParseError\"(,)|(,)?\"ParseError\"", "");
			assertEquals("Wrong tokens", expected, output);

			int expectedParseErrors = 0;
			for (int i = 0; i < expectedOutput.size(); i++) {
				Object obj = expectedOutput.get(i);
				if (obj.toString().equals("ParseError"))
					expectedParseErrors++;
			}

			int parseErrors = parserContext.getParseErrors().size();
			;
			assertEquals("Different number of parse errors",
					expectedParseErrors, parseErrors);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void tokenize(ParserContext parserContext, String string)
			throws IOException {
		BufferedReader in = null;

		Tokenizer tokenizer = new Tokenizer();

		in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
				string.getBytes()), "UTF8"));

		// tokenize
		// Token lastToken = null;
		int currentChar = -1;
		do {

			TokenizerContext tokenizerContext = parserContext
					.getTokenizerContext();

			/*
			 * If not reconsume, then read next character of the stream
			 */
			if (!tokenizerContext.isFlagReconsumeCurrentInputCharacter())
				currentChar = in.read();

			// If not specified by the spec, not reconsume in the next state
			tokenizerContext.setFlagReconsumeCurrentInputCharacter(false);

			tokenizerContext.setCurrentInputCharacter(currentChar);
			parserContext = tokenizer.tokenize(parserContext);

			// for (Token tok : parserContext.getTokenizerContext().getTokens())
			// {
			// lastToken = tok;// get the last token emitted from the queue
			// }
			//
			// } while (lastToken != null
			// && lastToken.getType() != TokenType.end_of_file);
		} while (currentChar != -1);
	}

	private void printTokens(ParserContext parserContext) {
		for (Token tok : parserContext.getTokenizerContext().getTokens()) {
			System.out.println(tok.getType() + " : " + tok.getValue());
		}
	}

	private String serializeTokens(Queue<Token> tokens) {
		JSONArray tokenArray = new JSONArray();
		JSONArray tokensArray = new JSONArray();
		JSONObject attributesArray = new JSONObject();
		tokens:

		for (Token token : tokens) {
			tokenArray = new JSONArray();

			switch (token.getType()) {

			case DOCTYPE:
				tokenArray.add("DOCTYPE");
				tokenArray.add((token.getValue()));
				tokenArray.add(((DocTypeToken) token).getPublicIdentifier());
				tokenArray.add(((DocTypeToken) token).getSystemIdentifier());
				tokenArray.add(!((DocTypeToken) token).isForceQuircksFlag());
				// true corresponds to the force-quirks flag being false, and
				// vice-versa. check
				// https://github.com/html5lib/html5lib-tests/tree/master/tokenizer
				// for more info
				break;
			case character:
				tokenArray.add("Character");
				tokenArray.add((token.getValue()));
				break;
			case comment:
				tokenArray.add("Comment");
				tokenArray.add(token.getValue());
				break;
			case end_of_file:
				break tokens;
			case end_tag:
				tokenArray.add("EndTag");
				tokenArray.add((token.getValue()));
				break;
			case start_tag:
				tokenArray.add("StartTag");
				tokenArray.add((token.getValue()));
				for (Attribute att : ((TagToken) token).getAttributes()) {
					attributesArray.put(att.getName(), att.getValue());
				}
				tokenArray.add(attributesArray);
				// TODO add attributes
				if (((TagToken) token).isFlagSelfClosingTag()) {
					tokenArray.add(true);
				}
				break;
			default:
				break;

			}
			tokensArray.add(tokenArray);
		}
		return tokensArray.toString();
	}

	private Queue<Token> simplifyCharacterTokens(Queue<Token> tokens) {
		Queue<Token> tokens2 = new LinkedList<Token>();
		Boolean charTokenBefore = false;
		String temp = "";
		for (Token token : tokens)
			if (token.getType() != TokenType.character) {
				if (charTokenBefore) {
					tokens2.add(new Token(TokenType.character, temp));
					temp = "";
					charTokenBefore = false;
				}
				tokens2.add(token);
			} else {
				temp = temp.concat(token.getValue());
				charTokenBefore = true;
			}

		return tokens2;
	}
}
