package viewmodel.tools;

import java.text.DecimalFormat;
import java.text.ParsePosition;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
/**
 * Utility class providing formatters in order to 
 * restrict text input to specific types, like integers.
 * @author TLM
 */
public class TextFormatters {
	private TextFormatters() {}
	private final static IntegerStringConverter INT_STRING_CONVERTER = 
			new IntegerStringConverter();
	
	/**
	 * Initialises a {@link TextFormatter} object ready to format integers: it 
	 * only accepts numerical characters.
	 * @param previousValue	default value to set as the text of the formatter, 
	 * may be null.
	 * @return	a new formatter ready to validate strings.
	 */
	public static TextFormatter<Integer> getIntegerFormatter(Integer previousValue) {
		//Regex accepting only positive integers 
		//TODO also accept minus sign but only at the very beginning of the string
		DecimalFormat format = new DecimalFormat("0");
		return new TextFormatter<Integer>(INT_STRING_CONVERTER, previousValue, 
				change -> {
					//Removal of text cannot make an integer invalid, accept.
					if(change.getText().isEmpty()) {
						return change;
					}
					//Check addition of text for invalid characters.
					ParsePosition p = new ParsePosition(0);
					
					if(format.parse(change.getText(), p) == null) {
						/*
						 * TODO change this so that invalid characters inside a
						 * pasted sequence are filtered out
						 */
						return null;
					}
					return change;
				});
	}
}
