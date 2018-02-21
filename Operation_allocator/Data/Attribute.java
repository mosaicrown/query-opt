package Data;

import java.io.Serializable;

/**
 * This class represents database table column
 * Attribute name has to be unique in database
 */
public class Attribute implements Serializable, Comparable<Attribute> {


    private String name;                //unique attribute(column) name
    private double dimension;           //dimension of attribute in KB
    private AttributeState state;

    public Attribute(String n, double dim) {
        name = n;
        dimension = dim;
        state = AttributeState.PLAINTEXT;
    }

    public Attribute(String n, double dim, AttributeState s) {
        name = n;
        dimension = dim;
        state = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDimension() {
        return dimension;
    }

    public void setDimension(double dimension) {
        this.dimension = dimension;
    }

    public AttributeState getState() {
        return state;
    }

    public void setState(AttributeState state) {
        this.state = state;
    }

    //standard java methods

    public boolean equals(Attribute attr) {
        return name.equals(attr.getName());
    }

    public int compareTo(Attribute a) {
        return name.compareTo(a.getName());
    }

    @Override
    public String toString() {
        return name;
    }

    public Attribute copyAttribute(){
        return new Attribute(this.name, this.dimension, this.state);
    }
}
