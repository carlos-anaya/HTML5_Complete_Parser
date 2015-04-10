package com.html5parser.factories;

import com.html5parser.classes.InsertionMode;
import com.html5parser.insertionModes.AfterHead;
import com.html5parser.insertionModes.BeforeHTML;
import com.html5parser.insertionModes.BeforeHead;
import com.html5parser.insertionModes.InBody;
import com.html5parser.insertionModes.InCaption;
import com.html5parser.insertionModes.InCell;
import com.html5parser.insertionModes.InColumnGroup;
import com.html5parser.insertionModes.InHead;
import com.html5parser.insertionModes.InHeadNoScript;
import com.html5parser.insertionModes.InRow;
import com.html5parser.insertionModes.InSelect;
import com.html5parser.insertionModes.InSelectInTable;
import com.html5parser.insertionModes.InTable;
import com.html5parser.insertionModes.InTableBody;
import com.html5parser.insertionModes.InTableText;
import com.html5parser.insertionModes.Initial;
import com.html5parser.insertionModes.Text;
import com.html5parser.interfaces.IInsertionMode;

public class InsertionModeFactory {

	private static InsertionModeFactory factory;

	private InsertionModeFactory() {
	}

	public static InsertionModeFactory getInstance() {
		if (factory == null) {
			factory = new InsertionModeFactory();
		}
		return factory;
	}

	public IInsertionMode getInsertionMode(InsertionMode insertionMode) {
		switch (insertionMode) {
		case after_head:
			return new AfterHead();
		case before_head:
			return new BeforeHead();
		case before_html:
			return new BeforeHTML();
		case in_body:
			return new InBody();
		case in_caption:
			return new InCaption();
		case in_cell:
			return new InCell();
		case in_column_group:
			return new InColumnGroup();
		case in_head:
			return new InHead();
		case in_head_noscript:
			return new InHeadNoScript();
		case in_row:
			return new InRow();
		case in_select:
			return new InSelect();
		case in_select_in_table:
			return new InSelectInTable();
		case in_table:
			return new InTable();
		case in_table_body:
			return new InTableBody();
		case in_table_text:
			return new InTableText();
		case initial:
			return new Initial();
		case text:
			return new Text();
			
		case after_after_body:
		case after_after_frameset:
		case after_body:
		case after_frameset:
		case in_frameset:
		case in_template:
		
		default:
			throw new UnsupportedOperationException();
		}
	}
}