package dk.rump.andrew.firebasedemo;

/**
 * Created by andrew on 07/02/17.
 */

public class FirebaseClass {
    private String stringValue;
    private Boolean booleanValue;
    private Integer integerValue;

    public FirebaseClass()
    {
        this.stringValue = "";
        this.booleanValue = false;
        this.integerValue = 0;
    }

    public FirebaseClass(String stringValue, Boolean booleanValue, Integer integerValue)
    {
        this.stringValue = stringValue;
        this.booleanValue = booleanValue;
        this.integerValue = integerValue;
    }

    public FirebaseClass(String stringValue, Boolean booleanValue, String integerValue)
    {
        this(stringValue, booleanValue, integerValue.length() > 0 ? Integer.parseInt(integerValue) : 0);
        /* Not allowed as this() has to be the first line in the constructor
        if (integerValue.length() > 0) {
            this(stringValue, booleanValue, Integer.parseInt(integerValue));
        } else {
            this(stringValue, booleanValue, 0);
        }
        */
    }

    public String getStringValue()
    {
        return stringValue;
    }

    public Boolean getBooleanValue()
    {
        return booleanValue;
    }

    public Integer getIntegerValue()
    {
        return integerValue;
    }
}
