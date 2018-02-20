package Data;

import java.io.Serializable;

/**
 * This class represents database table column
 * Attribute name has to be unique in database
 */
public class Attribute implements Serializable{


    private String name;
    private double dimension;

    public Attribute(String n){
        name=n;
    }

    @Override
    public boolean equals(Attribute attr) {
        return name.equals(attr.getName());
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
}
