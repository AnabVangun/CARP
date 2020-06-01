package model.values;

/**
 * Container for the base numerical unit of measurement. A Value contains a 
 * base value.
 */
public class Value {
    private int value;
    
    /**
     * Initialises a simple Value object.
     * @param value	to store in the object.
     */
    public Value(int value) {
    	this.value = value;
    }
    
    /**
     * @return the total value of the object.
     */
    public int getValue() {
    	return this.value;
    }
    
    /**
     * Sets the value of the object.
     * @param value to set.
     */
    public void setValue(int value) {
    	this.value = value;
    }
}
