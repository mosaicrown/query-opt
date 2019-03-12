package Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy;

import Operation_allocator.Actors.Provider;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Data.AttributeState;
import Operation_allocator.DebugManager.Debugger;
import Operation_allocator.DebugManager.LogType;
import Operation_allocator.DebugManager.Report;

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
 * 2) The result of static functions is the left parameter
 */
public final class RelationProfile implements Serializable {

    private List<Attribute> rvp;
    private List<Attribute> rip;
    private List<Attribute> rve;
    private List<Attribute> rie;
    private List<List<Attribute>> ces;

    private static boolean debug_mode = false;
    private static boolean removeUniformVis = false;
    private static Debugger debugger = null;

    public static void setRemoveUniformVis(boolean removeUniformVis) {
        RelationProfile.removeUniformVis = removeUniformVis;
    }

    public static void setDebugger(Debugger debugger) {
        RelationProfile.debugger = debugger;
    }

    public static void setDebug_mode(boolean debug_mode) {
        RelationProfile.debug_mode = debug_mode;
    }

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
                if (!RelationProfile.hasAttribute(la, leset.get(j))) {
                    f = false;
                    break;
                }
            if (f && leset.size() == la.size())
                return res;
        }
        res.add(RelationProfile.copyLoA(la));
        res = mergeSets(res);
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
        res = mergeSets(res);
        return res;
    }

    private static List<List<Attribute>> mergeSets(List<List<Attribute>> eset) {
        boolean signal = false;
        boolean present = false;
        List<List<Attribute>> res = new LinkedList<>();
        List<Attribute> l1;
        List<List<Attribute>> temp = new LinkedList<>();
        if (eset.size() > 0)
            res.add(RelationProfile.copyLoA(eset.get(0)));
        else
            return temp;
        List<Attribute> lres;
        for (int i = 1; i < eset.size(); i++) {
            if (signal)
                res = mergeSets(res);
            signal = false;
            present = false;
            l1 = eset.get(i);
            for (Attribute a : l1
            ) {
                for (int j = 0; j < res.size(); j++) {
                    lres = res.get(j);
                    if (RelationProfile.hasAttribute(lres, a)) {
                        present = true;
                        if (RelationProfile.subtraction(l1, lres).size() != 0) {
                            res.remove(j);
                            res.add(j, RelationProfile.union(lres, l1));
                            signal = true;
                            break;
                        }
                    }
                }
                if (signal) {
                    i = 0;
                    break;
                }
            }
            if (!present)
                res.add(RelationProfile.copyLoA(l1));
        }

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

    public void setCes(List<List<Attribute>> c) {
        this.ces = RelationProfile.mergeSets(c);
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

    public RelationProfile copyRelationProfile() {
        RelationProfile res = new RelationProfile();

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

    public static List<Attribute> copyLoA(List<Attribute> la) {
        List<Attribute> l = new LinkedList<>();
        for (Attribute a : la
        ) {
            l.add(a.copyAttribute());
        }
        return l;
    }

    /**
     * DEFINITION 4.1 AUTHORIZED RELATION
     */
    public boolean isAuthorizedFor(Provider p) {
        boolean f = true;

        List<Attribute> plaintextset = p.getAplains();
        List<Attribute> encryptedset = p.getAencs();
        //RULE 1
        //check for relation visible plaintext values
        for (Attribute a : this.getRvp()
        ) {
            if (!RelationProfile.hasAttribute(plaintextset, a)) {
                if (debug_mode)
                    debugger.leaveTrace(new Report("RelationProfile",
                                    LogType.RELATION_PROFILE_FAILURE,
                                    "\tProvider: " + p.selfDescription() + "\tFAILED: RVP"
                            )
                    );
                return false;
            }
        }
        //check for relation implicit plaintext values
        for (Attribute a : this.getRip()
        ) {
            if (!RelationProfile.hasAttribute(plaintextset, a)) {
                if (debug_mode)
                    debugger.leaveTrace(new Report("RelationProfile",
                                    LogType.RELATION_PROFILE_FAILURE,
                                    "\tProvider: " + p.selfDescription() + "\tFAILED: RIP"
                            )
                    );
                return false;
            }
        }
        //RULE 2
        //check for relation visible encrypted values
        List<Attribute> wholeproviderset = RelationProfile.union(plaintextset, encryptedset);
        for (Attribute a : this.getRve()
        ) {
            if (!RelationProfile.hasAttribute(wholeproviderset, a)) {
                if (debug_mode)
                    debugger.leaveTrace(new Report("RelationProfile",
                                    LogType.RELATION_PROFILE_FAILURE,
                                    "\tProvider: " + p.selfDescription() + "\tFAILED: RVE"
                            )
                    );
                return false;
            }
        }
        //check for relation implicit encrypted values
        for (Attribute a : this.getRie()
        ) {
            if (!RelationProfile.hasAttribute(wholeproviderset, a)) {
                if (debug_mode)
                    debugger.leaveTrace(new Report("RelationProfile",
                                    LogType.RELATION_PROFILE_FAILURE,
                                    "\tProvider: " + p.selfDescription() + "\tFAILED: RIE"
                            )
                    );
                return false;
            }
        }
        //RULE 3
        //uniform visibility
        if (!removeUniformVis)
            for (List<Attribute> la : this.getCes()
            ) {
                for (int i = 0; i < la.size() - 1; i++) {
                    if (debug_mode)
                        debugger.leaveTrace(new Report("RelationProfile",
                                        LogType.GENERAL_INFO,
                                        "\t" + la.get(i) + " vis: " + getVisibility(la.get(i))
                                                + "  " + la.get(i + 1) + " vis: " + getVisibility(la.get(i + 1))
                                )
                        );
                    if (!compatibleVisibility(la.get(i), la.get(i + 1))) {
                        if (debug_mode)
                            debugger.leaveTrace(new Report("RelationProfile",
                                            LogType.RELATION_PROFILE_FAILURE,
                                            "\tProvider: " + p.selfDescription() + "\tFAILED: C.E.S."
                                    )
                            );
                        return false;
                    }
                }
            }
        //case all rules satisfied
        return f;
    }

    private boolean compatibleVisibility(Attribute a, Attribute b) {
        if ((RelationProfile.hasAttribute(this.rvp, a) || RelationProfile.hasAttribute(this.rip, a)) &&
                (RelationProfile.hasAttribute(this.rve, b) || RelationProfile.hasAttribute(this.rie, b)))
            return false;
        return true;
    }

    private Visibility getVisibility(Attribute a) {
        if (RelationProfile.hasAttribute(this.rvp, a) || RelationProfile.hasAttribute(this.rip, a))
            return Visibility.PLAINTEXT;
        else if (RelationProfile.hasAttribute(this.rve, a) || RelationProfile.hasAttribute(this.rie, a))
            return Visibility.ENCRYPTED;
        //warning this is a pessimistic response in case the provider do not have the attribute,
        //this enforces information leaks
        return Visibility.PLAINTEXT;
    }

    public static List<Attribute> getVisPlaint(List<Attribute> la) {
        List<Attribute> lm = new LinkedList<>();
        for (Attribute a : la
        ) {
            if (a.getState() == AttributeState.PLAINTEXT)
                RelationProfile.insertAttribute(lm, a);
        }
        return lm;
    }

    public static List<Attribute> getVisEnc(List<Attribute> la) {
        List<Attribute> lm = new LinkedList<>();
        for (Attribute a : la
        ) {
            if (a.getState() != AttributeState.PLAINTEXT)
                RelationProfile.insertAttribute(lm, a);
        }
        return lm;
    }

    public static RelationProfile copyRP(RelationProfile inrp) {
        RelationProfile res = new RelationProfile();

        res.setRvp(RelationProfile.copyLoA(inrp.getRvp()));
        res.setRve(RelationProfile.copyLoA(inrp.getRve()));
        res.setRip(RelationProfile.copyLoA(inrp.getRip()));
        res.setRie(RelationProfile.copyLoA(inrp.getRie()));
        res.setCes(RelationProfile.copyCE(inrp.getCes()));

        return res;
    }

    public static boolean isAllowedToSeePlaintext(Provider p, Attribute a) {
        if (RelationProfile.hasAttribute(p.getAplains(), a))
            return true;
        return false;
    }

    public static boolean isAllowedToSeeEncrypted(Provider p, Attribute a) {
        List<Attribute> wholeset = RelationProfile.union(p.getAencs(), p.getAplains());
        if (RelationProfile.hasAttribute(wholeset, a))
            return true;
        return false;
    }
}

