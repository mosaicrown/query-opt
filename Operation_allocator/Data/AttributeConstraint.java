package Data;

import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class AttributeConstraint implements Serializable {

    private Attribute attr;
    private AttributeState state;

    public AttributeConstraint() {
    }

    public AttributeConstraint(Attribute a, AttributeState s) {
        attr = a;
        state = s;
    }

    public Attribute getAttr() {
        return attr;
    }

    public void setAttr(Attribute attr) {
        this.attr = attr;
    }

    public AttributeState getState() {
        return state;
    }

    public void setState(AttributeState state) {
        this.state = state;
    }

    public String longToString() {
        return new String("n: " + attr.toString() + " s: " + state);
    }
}
