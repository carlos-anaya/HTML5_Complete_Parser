package Tokenizer;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
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
import com.html5parser.classes.TokenizerState;
import com.html5parser.classes.token.DocTypeToken;
import com.html5parser.classes.token.TagToken;
import com.html5parser.classes.token.TagToken.Attribute;
import com.html5parser.factories.TokenizerStateFactory;
import com.html5parser.parser.Parser;

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

		String[] resources = {
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/test1.test",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/test2.test",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/test3.test",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/test4.test",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/entities.test",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/unicodeChars.test",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/unicodeCharsProblematic.test",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/contentModelFlags.test",
				"https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/escapeFlag.test" };

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
		ParserContext parserContext = new ParserContext();
		Parser parser = new Parser();

		String output = "";

		int j = test.containsKey("initialStates") ? ((JSONArray) test
				.get("initialStates")).size() : 0;
		do {

			if (test.containsKey("initialStates")) {
				parserContext = new ParserContext();
				JSONArray initialStates = (JSONArray) test.get("initialStates");
				// int size = initialStates.size();
				TokenizerStateFactory factory = TokenizerStateFactory
						.getInstance();
				if (initialStates.get(j - 1).toString()
						.equals("PLAINTEXT state"))
					parserContext.getTokenizerContext().setNextState(
							factory.getState(TokenizerState.PLAINTEXT_state));
				else if (initialStates.get(j - 1).toString()
						.equals("RAWTEXT state"))
					parserContext.getTokenizerContext().setNextState(
							factory.getState(TokenizerState.RAWTEXT_state));
				else
					parserContext.getTokenizerContext().setNextState(
							factory.getState(TokenizerState.RCDATA_state));
				String lastStartTag = (String) test.get("lastStartTag");
				parserContext.getTokenizerContext().getEmittedStartTags()
						.push(lastStartTag);

			}
			
			String input = (String) test.get("input");
			if( test.get("doubleEscaped")!=null && (boolean) test.get("doubleEscaped")){
				String codePoint=input.substring(input.indexOf("\\u")+2, input.lastIndexOf("\\u")+6);
				String co = String.valueOf((char) Integer.parseInt(codePoint,16));
				input = input.replaceFirst("\\\\u....", "\\\\"+String.valueOf((char) Integer.parseInt(codePoint,16)));
			}
			
			parserContext = parser.tokenize(parserContext,
					input);
			output = serializeTokens(simplifyCharacterTokens(parserContext
					.getTokenizerContext().getTokens()));
			
			JSONArray expectedOutput = (JSONArray) test.get("output");
			// String expected = expectedOutput.toString().replaceAll(
			// "\"ParseError\"(,)|(,)?\"ParseError\"", "");
			String expected = formatHtml5libOutput(expectedOutput);
			if( test.get("doubleEscaped")!=null && (boolean) test.get("doubleEscaped")){
				String sub = expected.substring(expected.indexOf("\\u")+2, expected.lastIndexOf("\\u")+6);
				int codePoint=Integer.parseUnsignedInt(sub,16);
				String co=String.valueOf(Character.toChars(codePoint));
				 co=String.valueOf(Character.toChars(co.getBytes()[0]));
				expected = expected.replaceFirst("\\\\u....", "\\\\"+co);
			}
			assertEquals("Wrong tokens", expected, output);

			int expectedParseErrors = 0;
			for (int i = 0; i < expectedOutput.size(); i++) {
				Object obj = expectedOutput.get(i);
				if (obj.toString().equals("ParseError"))
					expectedParseErrors++;
			}

			int parseErrors = parserContext.getParseErrors().size();
			assertEquals("Different number of parse errors",
					expectedParseErrors, parseErrors);
			j--;

		} while (j > 0);

	}

	private String serializeTokens(Queue<Token> tokens) {
		JSONArray tokenArray = new JSONArray();
		JSONArray tokensArray = new JSONArray();
		JSONObject attributesArray = new JSONObject();
		tokens:

		for (Token token : tokens) {
			tokenArray = new JSONArray();
			attributesArray = new JSONObject();

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

	private String formatHtml5libOutput(JSONArray output) {
		Boolean charBefore = false;
		String temp = "";
		JSONArray formatedOutput = new JSONArray();
		for (Object val : output) {
			if (!val.toString().equals("ParseError")) {
				JSONArray item = (JSONArray) val;
				if (item.get(0).toString().equals("Character")) {
					temp = temp.concat(item.get(1).toString());
					charBefore = true;
				} else {
					if (charBefore) {
						JSONArray charItem = new JSONArray();
						charItem.add("Character");
						charItem.add(temp);
						formatedOutput.add(charItem);
						temp = "";
						charBefore = false;
					}
					formatedOutput.add(item);
				}
			}
		}
		if (!temp.isEmpty()) {
			JSONArray charItem = new JSONArray();
			charItem.add("Character");
			charItem.add(temp);
			formatedOutput.add(charItem);
		}

		return formatedOutput.toString();
	}
}
