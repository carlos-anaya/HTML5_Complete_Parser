package Tokenizer;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.html5parser.classes.ParserContext;
import com.html5parser.classes.Token;
import com.html5parser.classes.TokenizerContext;
import com.html5parser.classes.Token.TokenType;
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
 
	//parameters pass via this constructor
	public TokenizerTesthtml5libsuite(String testName,JSONObject test) {
		this.testName = testName;
		this.test = test;
	}
 
	//Declares parameters here
	@Parameters(name = "Test name: {0}")
	public static Iterable<Object[]> data1() {
		
		List<Object[]> testList = new ArrayList<Object[]>();
		BufferedReader in = null;

		URL url;
		try {
			
			String resource = "https://raw.githubusercontent.com/html5lib/html5lib-tests/master/tokenizer/test1.test";
			url = new URL(resource);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			//String resource = "C:\\Users\\JoséArmando\\Desktop\\data.txt";
			//in = new BufferedReader(new FileReader(new File(resource)));

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(in);

			// get the test suite object
			JSONArray tests = (JSONArray) jsonObject.get("tests");
			
			for(int i=0; i<tests.size(); i++){
				JSONObject test = (JSONObject) tests.get(i);
				String testName = (String) test.get("description");
				//if(testName.equals("Ampersand EOF"))
				testList.add(new Object[]{testName,test});
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
		try{
		ParserContext parserContext = new ParserContext();
		tokenize(parserContext, (String) test.get("input"));
		printTokens(parserContext);
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	}

	private void tokenize(ParserContext parserContext, String string) throws IOException{
		BufferedReader in = null;
		
		Tokenizer tokenizer = new Tokenizer();
		
		in = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(string.getBytes()), "UTF-8"));
		
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
				tokenizerContext
						.setFlagReconsumeCurrentInputCharacter(false);
			}

			for (Token tok : parserContext.getTokenizerContext()
					.getTokens()) {
				lastToken = tok;// get the last token emitted from the queue
			}

		} while (lastToken != null
				&& lastToken.getType() != TokenType.end_of_file);
	}

	private void printTokens(ParserContext parserContext){
		for (Token tok : parserContext.getTokenizerContext().getTokens()) {
			System.out.println(tok.getType() + " : " + tok.getValue());
		}
	}
	
	private String serializeTokens(Queue<Token> tokens){
		StringBuilder output = new  StringBuilder("[");
		for (Token token : tokens) {
			output.append("[");
			
			switch (token.getType()){
			
			case DOCTYPE:
				output.append("\"DOCTYPE\"").append(", ");
				output.append(token.getValue()).append(", ");
				break;
			case character:
				break;
			case comment:
				break;
			case end_of_file:
				break;
			case end_tag:
				break;
			case start_tag:
				break;
			default:
				break;
			
			}
			
			output.append("]");
		}
		output.append("]");
		return output.toString();
	}
}
