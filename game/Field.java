package de.jroeger.game;

public class Field {
	public static final int VALUE_X 	= 1;
	public static final int VALUE_O 	= 2;
	public static final int VALUE_VOID 	= 0;
	
	/**
	 * saves the current state of the field
	 */
	private int value;
	
	private int index;
	
	/**
	 * count fields
	 */
	static int fields = 0;
	
	public Field(int index) {
		this.value = VALUE_VOID;
		this.index = index;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isEmpty() {
		return value == VALUE_VOID;
	}
	
	public int getIndex() {
		return index;
	}
}
