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
 * USEFUL NOTE: 1) Every object passed to this class is copied as it were immutable
 *              2) The result of static functions is the left parameter
 */
public final class RelationProfile implements Serializable {

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

    public static void insertAttribute(List<Attribute> l, Attribute a) {
        if (!RelationProfile.hasAttribute(l, a))
            l.add(a.copyAttribute());

    }

    public static List<Attribute> union(List<Attribute> op1, List<Attribute> op2) {
        List<Attribute> res = new LinkedList<>();
        for (int i = 0; i < op2.size(); i++)
            RelationProfile.insertAttribute(res, op2.get(i).copyAttribute());
        for (int i = 0; i < op1.size(); i++)
            RelationProfile.insertAttribute(res, op1.get(i).copyAttribute());
        return res;
    }

    public static List<Attribute> subtraction(List<Attribute> base, List<Attribute> complement) {
        List<Attribute> res = RelationProfile.copyLoA(base);
        for (int i = 0; i < complement.size(); i++)
            RelationProfile.removeAttribute(res, complement.get(i));
        return res;
    }

    public static List<Attribute> intersection(List<Attribute> base, List<Attribute> complement) {
        List<Attribute> res = new LinkedList<>();
        if (complement.size() == 0) {
            return res;
        }
        res = RelationProfile.copyLoA(base);
        for (int i = 0; i < res.size(); i++)
            if (!RelationProfile.hasAttribute(complement, res.get(i))) {
                RelationProfile.removeAttribute(res, res.get(i));
                i--;
            }
        return res;
    }

    public static List<List<Attribute>> unionCE(List<List<Attribute>> eset, List<Attribute> la) {
        boolean f;
        List<List<Attribute>> res = RelationProfile.copyCE(eset);
        Collections.sort(la);
        for (int i = 0; i < res.size(); i++) {
            f = true;
            List<Attribute> leset = res.get(i);
            Collections.sort(leset);
            for (int j = 0; j < leset.size(); j++)
                if (!leset.get(j).equals(la.get(j))) {
                    f = false;
                    break;
                }
            if (f && leset.size() == la.size())
                return res;
        }
        res.add(RelationProfile.copyLoA(la));
        return res;
    }

    private static void unionCElink(List<List<Attribute>> eset, List<Attribute> la) {
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
        eset.add(RelationProfile.copyLoA(la));
    }

    public static List<List<Attribute>> unionCEsets(List<List<Attribute>> eset1, List<List<Attribute>> eset2) {
        List<List<Attribute>> res = RelationProfile.copyCE(eset1);
        for (int i = 0; i < eset2.size(); i++)
            RelationProfile.unionCElink(res, eset2.get(i));
        return res;
    }

    public static List<List<Attribute>> copyCE(List<List<Attribute>> eset) {
        List<List<Attribute>> s = new LinkedList<>();
        List<Attribute> la;
        for (List<Attribute> li : eset
                ) {
            la = new LinkedList<>();
            for (Attribute a : li
                    ) {
                la.add(a.copyAttribute());
            }
            s.add(la);
        }
        return s;
    }

    public void insertVP(List<Attribute> l, Attribute a) {
        //remove from other lists if previously inserted
        removeAttribute(rve, a);
        //element insertion
        if (!hasAttribute(rvp, a))
            rvp.add(a.copyAttribute());
    }

    public void insertIP(List<Attribute> l, Attribute a) {
        //remove from other lists if previously inserted
        removeAttribute(rie, a);
        //element insertion
        if (!hasAttribute(rip, a))
            rip.add(a.copyAttribute());
    }

    public void insertVE(List<Attribute> l, Attribute a) {
        //remove from other lists if previously inserted
        removeAttribute(rvp, a);
        //element insertion
        if (!hasAttribute(rve, a))
            rve.add(a.copyAttribute());
    }

    public void insertIE(List<Attribute> l, Attribute a) {
        //remove from other lists if previously inserted
        removeAttribute(rip, a);
        //element insertion
        if (!hasAttribute(rie, a))
            rie.add(a.copyAttribute());
    }

    public List<Attribute> getRvp() {
        return rvp;
    }

    public void setRvp(List<Attribute> rvp) {
        this.rvp = RelationProfile.copyLoA(rvp);
    }

    public List<Attribute> getRip() {
        return rip;
    }

    public void setRip(List<Attribute> rip) {
        this.rip = RelationProfile.copyLoA(rip);
    }

    public List<Attribute> getRve() {
        return rve;
    }

    public void setRve(List<Attribute> rve) {
        this.rve = RelationProfile.copyLoA(rve);
    }

    public List<Attribute> getRie() {
        return rie;
    }

    public void setRie(List<Attribute> rie) {
        this.rie = RelationProfile.copyLoA(rie);
    }

    public List<List<Attribute>> getCes() {
        return ces;
    }

    public void setCes(List<List<Attribute>> ces) {
        this.ces = RelationProfile.copyCE(ces);
    }

    public double getTotalDimension() {
        double dim = 0;
        for (Attribute a : rvp
                ) {
            dim += a.getDimension();
        }
        for (Attribute a : rve
                ) {
            dim += a.getDimension();
        }
        return dim;
    }

    public RelationProfile copyRelationProfile(){
        RelationProfile res=new RelationProfile();

        res.setRvp(RelationProfile.copyLoA(rvp));
        res.setRve(RelationProfile.copyLoA(rve));
        res.setRip(RelationProfile.copyLoA(rip));
        res.setRie(RelationProfile.copyLoA(rie));
        res.setCes(RelationProfile.copyCE(ces));

        return res;
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

    public static List<Attribute> copyLoA(List<Attribute> la){
        List<Attribute> l = new LinkedList<>();
        for (Attribute a:la
             ) {
            l.add(a.copyAttribute());
        }
        return l;
    }
}
