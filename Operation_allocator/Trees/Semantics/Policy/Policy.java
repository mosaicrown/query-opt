package Trees.Semantics.Policy;

public class Policy {

    /**
     * Provider Visibility levels
     */
    private VisibilityLevel P1;
    private VisibilityLevel P2;
    private VisibilityLevel P3;

    public Policy() {
        P1 = VisibilityLevel.VISIBLE;
        P2 = VisibilityLevel.VISIBLE;
        P3 = VisibilityLevel.VISIBLE;
    }

    public VisibilityLevel getP1() {
        return P1;
    }

    public void setP1(VisibilityLevel p1) {
        P1 = p1;
    }

    public VisibilityLevel getP2() {
        return P2;
    }

    public void setP2(VisibilityLevel p2) {
        P2 = p2;
    }

    public VisibilityLevel getP3() {
        return P3;
    }

    public void setP3(VisibilityLevel p3) {
        P3 = p3;
    }

    public String printPolicy() {
        String s = "";

        //print P1 level
        s += "P1:";
        if (P1 == VisibilityLevel.VISIBLE)
            s += "V";
        else if (P1 == VisibilityLevel.ENCRYPTED)
            s += "E";
        else
            s += "NV";

        //print P2 level
        s += "-P2:";
        if (P2 == VisibilityLevel.VISIBLE)
            s += "V";
        else if (P2 == VisibilityLevel.ENCRYPTED)
            s += "E";
        else
            s += "NV";

        //print P3 level
        s += "-P3:";
        if (P3 == VisibilityLevel.VISIBLE)
            s += "V";
        else if (P3 == VisibilityLevel.ENCRYPTED)
            s += "E";
        else
            s += "NV";

        return s;
    }
}
