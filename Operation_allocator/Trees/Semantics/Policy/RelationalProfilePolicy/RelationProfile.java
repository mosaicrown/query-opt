package Trees.Semantics.Policy.RelationalProfilePolicy;

import Data.Attribute;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents    RELATION PROFILE
 * from                     DEFINITION 3.1
 * in article               An authorization model for multi-provider queries
 */

/**
 * USEFUL NOTE: The result of static functions is the left parameter
 */
public class RelationProfile implements Serializable {

    private List<Attribute> rvp;
    private List<Attribute> rip;
    private List<Attribute> rve;
    private List<Attribute> rie;
    private List<List<Attribute>> ces;

    public RelationProfile() {
        rvp = new LinkedList<>();
        rip = new LinkedList<>();
        rve = new LinkedList<>();
        rie = new LinkedList<>();
        ces = new LinkedList<>(new LinkedList<>());
    }

    public static boolean hasAttribute(List<Attribute> l, Attribute a) {
        boolean f = false;
        for (int i = 0; i < l.size(); i++)
            if (l.get(i).equals(a)) {
                f = true;
                break;
            }
        return f;
    }

    public static void removeAttribute(List<Attribute> l, Attribute a) {
        for (int i = 0; i < l.size(); i++)
            if (l.get(i).equals(a)) {
                l.remove(i);
                i--;
            }
    }

    public static void union(List<Attribute> base, List<Attribute> complement) {
        for (int i = 0; i < complement.size(); i++)
            RelationProfile.insertAttribute(base, complement.get(i));
    }

    public static void subtraction(List<Attribute> base, List<Attribute> complement) {
        for (int i = 0; i < complement.size(); i++)
            RelationProfile.removeAttribute(base, complement.get(i));
    }

    public static void intersection(List<Attribute> base, List<Attribute> complement) {
        if (complement.size() == 0) {
            base.clear();
            return;
        }
        for (int i = 0; i < base.size(); i++)
            if (!RelationProfile.hasAttribute(complement, base.get(i))) {
                RelationProfile.removeAttribute(base, base.get(i));
                i--;
            }

    }

    public static void unionCE(List<List<Attribute>> eset, List<Attribute> la) {
        boolean f;
        Collections.sort(la);
        for (int i = 0; i < eset.size(); i++) {
            f = true;
            List<Attribute> leset = eset.get(i);
            Collections.sort(leset);
            for (int j = 0; j < leset.size(); j++)
                if (!leset.get(j).equals(la.get(j))) {
                    f = false;
                    break;
                }
            if (f && leset.size() == la.size())
                return;
        }
        eset.add(la);
    }

    public static void unionCEsets(List<List<Attribute>> eset1, List<List<Attribute>> eset2) {
        for (int i = 0; i < eset2.size(); i++)
            RelationProfile.unionCE(eset1, eset2.get(i));
    }

    public static void insertAttribute(List<Attribute> l, Attribute a) {
        if (!RelationProfile.hasAttribute(l, a))
            l.add(a);

    }

    public void insertVP(List<Attribute> l, Attribute a) {
        //remove from other lists if previously inserted
        removeAttribute(rve, a);
        //element insertion
        if (!hasAttribute(rvp, a))
            rvp.add(a);
    }

    public void insertIP(List<Attribute> l, Attribute a) {
        //remove from other lists if previously inserted
        removeAttribute(rie, a);
        //element insertion
        if (!hasAttribute(rip, a))
            rip.add(a);
    }

    public void insertVE(List<Attribute> l, Attribute a) {
        //remove from other lists if previously inserted
        removeAttribute(rvp, a);
        //element insertion
        if (!hasAttribute(rve, a))
            rve.add(a);
    }

    public void insertIE(List<Attribute> l, Attribute a) {
        //remove from other lists if previously inserted
        removeAttribute(rip, a);
        //element insertion
        if (!hasAttribute(rie, a))
            rie.add(a);
    }

    public List<Attribute> getRvp() {
        return rvp;
    }

    public void setRvp(List<Attribute> rvp) {
        this.rvp = rvp;
    }

    public List<Attribute> getRip() {
        return rip;
    }

    public void setRip(List<Attribute> rip) {
        this.rip = rip;
    }

    public List<Attribute> getRve() {
        return rve;
    }

    public void setRve(List<Attribute> rve) {
        this.rve = rve;
    }

    public List<Attribute> getRie() {
        return rie;
    }

    public void setRie(List<Attribute> rie) {
        this.rie = rie;
    }

    public List<List<Attribute>> getCes() {
        return ces;
    }

    public void setCes(List<List<Attribute>> ces) {
        this.ces = ces;
    }

    public double getTotalDimension(){
        double dim=0;
        for (Attribute a:rvp
             ) {
            dim+=a.getDimension();
        }
        for (Attribute a:rve
                ) {
            dim+=a.getDimension();
        }
        return dim;
    }

    public String toString() {
        String s = "";
        s += "Rvp:\t";
        int dim;
        int i;
        dim = rvp.size();
        i = 0;
        for (Attribute a : rvp
                ) {
            s += a.toString();
            i++;
            if (i < dim)
                s += ",";
        }
        s += "\tRve:\t";
        dim = rve.size();
        i = 0;
        for (Attribute a : rve
                ) {
            s += a.toString();
            i++;
            if (i < dim)
                s += ",";
        }
        s += "\tRip:\t";
        dim = rip.size();
        i = 0;
        for (Attribute a : rip
                ) {
            s += a.toString();
            i++;
            if (i < dim)
                s += ",";
        }
        s += "\tRie:\t";
        dim = rie.size();
        i = 0;
        for (Attribute a : rie
                ) {
            s += a.toString();
            i++;
            if (i < dim)
                s += ",";
        }
        s += "\tRces:\t";
        for (List<Attribute> l : ces
                ) {
            s += "(";
            dim = l.size();
            i = 0;
            for (Attribute a : l
                    ) {
                s += a.toString();
                i++;
                if (i < dim)
                    s += ",";
            }
            s += ")";
        }
        return s;
    }
}
