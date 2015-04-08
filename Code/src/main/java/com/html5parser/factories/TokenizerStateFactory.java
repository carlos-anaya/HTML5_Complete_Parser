package com.html5parser.factories;

import com.html5parser.classes.TokenizerState;
import com.html5parser.interfaces.ITokenizerState;
import com.html5parser.tokenizerStates.After_DOCTYPE_name_state;
import com.html5parser.tokenizerStates.After_DOCTYPE_public_identifier_state;
import com.html5parser.tokenizerStates.After_DOCTYPE_public_keyword_state;
import com.html5parser.tokenizerStates.After_DOCTYPE_system_identifier_state;
import com.html5parser.tokenizerStates.After_DOCTYPE_system_keyword_state;
import com.html5parser.tokenizerStates.After_attribute_name_state;
import com.html5parser.tokenizerStates.After_attribute_value_quoted_state;
import com.html5parser.tokenizerStates.Attribute_name_state;
import com.html5parser.tokenizerStates.Attribute_value_double_quoted_state;
import com.html5parser.tokenizerStates.Attribute_value_single_quoted_state;
import com.html5parser.tokenizerStates.Attribute_value_unquoted_state;
import com.html5parser.tokenizerStates.Before_DOCTYPE_name_state;
import com.html5parser.tokenizerStates.Before_DOCTYPE_public_identifier_state;
import com.html5parser.tokenizerStates.Before_DOCTYPE_system_identifier_state;
import com.html5parser.tokenizerStates.Before_attribute_name_state;
import com.html5parser.tokenizerStates.Before_attribute_value_state;
import com.html5parser.tokenizerStates.Between_DOCTYPE_public_and_system_identifiers_state;
import com.html5parser.tokenizerStates.Bogus_DOCTYPE_state;
import com.html5parser.tokenizerStates.Bogus_comment_state;
import com.html5parser.tokenizerStates.CData_section_state;
import com.html5parser.tokenizerStates.Character_reference_in_RCDATA_state;
import com.html5parser.tokenizerStates.Character_reference_in_data_state;
import com.html5parser.tokenizerStates.Comment_end_bang_state;
import com.html5parser.tokenizerStates.Comment_end_dash_state;
import com.html5parser.tokenizerStates.Comment_end_state;
import com.html5parser.tokenizerStates.Comment_start_dash_state;
import com.html5parser.tokenizerStates.Comment_start_state;
import com.html5parser.tokenizerStates.Comment_state;
import com.html5parser.tokenizerStates.DOCTYPE_name_state;
import com.html5parser.tokenizerStates.DOCTYPE_public_identifier_double_quoted_state;
import com.html5parser.tokenizerStates.DOCTYPE_public_identifier_single_quoted_state;
import com.html5parser.tokenizerStates.DOCTYPE_state;
import com.html5parser.tokenizerStates.DOCTYPE_system_identifier_double_quoted_state;
import com.html5parser.tokenizerStates.DOCTYPE_system_identifier_single_quoted_state;
import com.html5parser.tokenizerStates.Data_state;
import com.html5parser.tokenizerStates.End_tag_open_state;
import com.html5parser.tokenizerStates.Markup_declaration_open_state;
import com.html5parser.tokenizerStates.PLAINTEXT_state;
import com.html5parser.tokenizerStates.RAWTEXT_end_tag_name_state;
import com.html5parser.tokenizerStates.RAWTEXT_end_tag_open_state;
import com.html5parser.tokenizerStates.RAWTEXT_less_than_sign_state;
import com.html5parser.tokenizerStates.RAWTEXT_state;
import com.html5parser.tokenizerStates.RCDATA_end_tag_name_state;
import com.html5parser.tokenizerStates.RCDATA_end_tag_open_state;
import com.html5parser.tokenizerStates.RCDATA_less_than_sign_state;
import com.html5parser.tokenizerStates.RCDATA_state;
import com.html5parser.tokenizerStates.Script_data_double_escape_end_state;
import com.html5parser.tokenizerStates.Script_data_double_escape_start_state;
import com.html5parser.tokenizerStates.Script_data_double_escaped_dash_dash_state;
import com.html5parser.tokenizerStates.Script_data_double_escaped_dash_state;
import com.html5parser.tokenizerStates.Script_data_double_escaped_less_than_sign_state;
import com.html5parser.tokenizerStates.Script_data_double_escaped_state;
import com.html5parser.tokenizerStates.Script_data_end_tag_name_state;
import com.html5parser.tokenizerStates.Script_data_end_tag_open_state;
import com.html5parser.tokenizerStates.Script_data_escape_start_dash_state;
import com.html5parser.tokenizerStates.Script_data_escape_start_state;
import com.html5parser.tokenizerStates.Script_data_escaped_dash_dash_state;
import com.html5parser.tokenizerStates.Script_data_escaped_dash_state;
import com.html5parser.tokenizerStates.Script_data_escaped_end_tag_name_state;
import com.html5parser.tokenizerStates.Script_data_escaped_end_tag_open_state;
import com.html5parser.tokenizerStates.Script_data_escaped_less_than_sign_state;
import com.html5parser.tokenizerStates.Script_data_escaped_state;
import com.html5parser.tokenizerStates.Script_data_less_than_sign_state;
import com.html5parser.tokenizerStates.Script_data_state;
import com.html5parser.tokenizerStates.Self_closing_start_tag_state;
import com.html5parser.tokenizerStates.Tag_name_state;
import com.html5parser.tokenizerStates.Tag_open_state;

public class TokenizerStateFactory {

	private static TokenizerStateFactory factory;

	private TokenizerStateFactory() {
	}

	public static TokenizerStateFactory getInstance() {
		if (factory == null) {
			factory = new TokenizerStateFactory();
		}
		return factory;
	}

	public ITokenizerState getState(TokenizerState stateType) {
		switch (stateType) {
		case Data_state:
			return new Data_state();
		case End_tag_open_state:
			return new End_tag_open_state();
		case Self_closing_start_tag_state:
			return new Self_closing_start_tag_state();
		case Tag_name_state:
			return new Tag_name_state();
		case Tag_open_state:
			return new Tag_open_state();
		case After_attribute_name_state:
			return new After_attribute_name_state();
		case After_attribute_value_quoted_state:
			return new After_attribute_value_quoted_state();
		case Attribute_name_state:
			return new Attribute_name_state();
		case Attribute_value_double_quoted_state:
			return new Attribute_value_double_quoted_state();
		case Attribute_value_single_quoted_state:
			return new Attribute_value_single_quoted_state();
		case Attribute_value_unquoted_state:
			return new Attribute_value_unquoted_state();
		case Before_attribute_name_state:
			return new Before_attribute_name_state();
		case Before_attribute_value_state:
			return new Before_attribute_value_state();
		case Bogus_comment_state:
			return new Bogus_comment_state();
		case Comment_start_state:
			return new Comment_start_state();
		case Markup_declaration_open_state:
			return new Markup_declaration_open_state();
		case PLAINTEXT_state:
			return new PLAINTEXT_state();
		case RAWTEXT_state:
			return new RAWTEXT_state();
		case Script_data_state:
			return new Script_data_state();

		case After_DOCTYPE_name_state:
			return new After_DOCTYPE_name_state();
		case After_DOCTYPE_public_identifier_state:
			return new After_DOCTYPE_public_identifier_state();
		case After_DOCTYPE_public_keyword_state:
			return new After_DOCTYPE_public_keyword_state();
		case After_DOCTYPE_system_identifier_state:
			return new After_DOCTYPE_system_identifier_state();
		case After_DOCTYPE_system_keyword_state:
			return new After_DOCTYPE_system_keyword_state();
		case Before_DOCTYPE_name_state:
			return new Before_DOCTYPE_name_state();
		case Before_DOCTYPE_public_identifier_state:
			return new Before_DOCTYPE_public_identifier_state();
		case Before_DOCTYPE_system_identifier_state:
			return new Before_DOCTYPE_system_identifier_state();
		case Between_DOCTYPE_public_and_system_identifiers_state:
			return new Between_DOCTYPE_public_and_system_identifiers_state();
		case Bogus_DOCTYPE_state:
			return new Bogus_DOCTYPE_state();
		case DOCTYPE_name_state:
			return new DOCTYPE_name_state();
		case DOCTYPE_public_identifier_double_quoted_state:
			return new DOCTYPE_public_identifier_double_quoted_state();
		case DOCTYPE_public_identifier_single_quoted_state:
			return new DOCTYPE_public_identifier_single_quoted_state();
		case DOCTYPE_state:
			return new DOCTYPE_state();
		case DOCTYPE_system_identifier_double_quoted_state:
			return new DOCTYPE_system_identifier_double_quoted_state();
		case DOCTYPE_system_identifier_single_quoted_state:
			return new DOCTYPE_system_identifier_single_quoted_state();
		case CDATA_section_state:
			return new CData_section_state();
		case Comment_end_bang_state:
			return new Comment_end_bang_state();
		case Comment_end_dash_state:
			return new Comment_end_dash_state();
		case Comment_end_state:
			return new Comment_end_state();
		case Comment_start_dash_state:
			return new Comment_start_dash_state();
		case Comment_state:
			return new Comment_state();

		case RCDATA_end_tag_name_state:
			return new RCDATA_end_tag_name_state();
		case RCDATA_end_tag_open_state:
			return new RCDATA_end_tag_open_state();
		case RCDATA_less_than_sign_state:
			return new RCDATA_less_than_sign_state();
		case RCDATA_state:
			return new RCDATA_state();
		case RAWTEXT_end_tag_name_state:
			return new RAWTEXT_end_tag_name_state();
		case RAWTEXT_end_tag_open_state:
			return new RAWTEXT_end_tag_open_state();
		case RAWTEXT_less_than_sign_state:
			return new RAWTEXT_less_than_sign_state();
		case Script_data_double_escape_end_state:
			return new Script_data_double_escape_end_state();
		case Script_data_double_escape_start_state:
			return new Script_data_double_escape_start_state();
		case Script_data_double_escaped_dash_dash_state:
			return new Script_data_double_escaped_dash_dash_state();
		case Script_data_double_escaped_dash_state:
			return new Script_data_double_escaped_dash_state();
		case Script_data_double_escaped_less_than_sign_state:
			return new Script_data_double_escaped_less_than_sign_state();
		case Script_data_double_escaped_state:
			return new Script_data_double_escaped_state();
		case Script_data_end_tag_name_state:
			return new Script_data_end_tag_name_state();
		case Script_data_end_tag_open_state:
			return new Script_data_end_tag_open_state();
		case Script_data_escape_start_dash_state:
			return new Script_data_escape_start_dash_state();
		case Script_data_escape_start_state:
			return new Script_data_escape_start_state();
		case Script_data_escaped_dash_dash_state:
			return new Script_data_escaped_dash_dash_state();
		case Script_data_escaped_dash_state:
			return new Script_data_escaped_dash_state();
		case Script_data_escaped_end_tag_name_state:
			return new Script_data_escaped_end_tag_name_state();
		case Script_data_escaped_end_tag_open_state:
			return new Script_data_escaped_end_tag_open_state();
		case Script_data_escaped_less_than_sign_state:
			return new Script_data_escaped_less_than_sign_state();
		case Script_data_escaped_state:
			return new Script_data_escaped_state();
		case Script_data_less_than_sign_state:
			return new Script_data_less_than_sign_state();

		case Character_reference_in_RCDATA_state:
			return new Character_reference_in_RCDATA_state();

		case Character_reference_in_data_state:
			return new Character_reference_in_data_state();

		case Character_reference_in_attribute_value_state:

		case Tokenizing_character_references:

		default:
			throw new UnsupportedOperationException();
		}
	}
}
