package main;

public class StringHolder {
	
    String value;
    
    public StringHolder(String value) {
        this.value = value;
    }
    
    public StringHolder() {
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }

	@Override
	public String toString() {
		return  value;
	}
    
    
}