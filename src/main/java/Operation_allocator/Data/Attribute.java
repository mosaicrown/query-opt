package Operation_allocator.Data;

import java.io.Serializable;

/**
 * This class represents database table column
 * Attribute name has to be unique in database
 */
public class Attribute implements Serializable, Comparable<Attribute> {


    private String name;                //unique attribute(column) name
    private double dimension;           //dimension of attribute in KB
    private double original_size;
    private AttributeState state;

    public Attribute(String n, double dim) {
        name = n;
        dimension = dim;
        original_size = dim;
        state = AttributeState.PLAINTEXT;
    }

    public Attribute(String n, double dim, AttributeState s) {
        name = n;
        dimension = dim;
        original_size = dim;
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

    public double getOriginal_size() {
        return original_size;
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

    public String longToString() {
        return new String("n: " + name + " d: " + dimension + " s: " + state + " od: " + original_size);
    }

    public String shortToString() {
        return new String("n: " + name + " od: " + original_size);
    }

    public Attribute copyAttribute() {
        return new Attribute(this.name, this.dimension, this.state);
    }
}
