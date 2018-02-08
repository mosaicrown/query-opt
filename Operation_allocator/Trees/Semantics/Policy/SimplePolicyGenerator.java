package Trees.Semantics.Policy;

import Actors.Operation;
import Trees.Semantics.Features;
import Trees.Semantics.MetaChoke;
import Trees.TreeNode;

public class SimplePolicyGenerator extends PolicyGenerator {

    public <T extends Operation> void generateCandidates(TreeNode<T> tn) {
        for (TreeNode<T> tns : tn.getSons()
                ) {
            generateCandidates(tns);
        }
        if (tn.isLeaf()) {
            generateVisibilityLevels(tn);
        } else {
            //select most restrictive level
            //let's start from the less restrictive
            Features visLev = Features.PUBLIC;
            MetaChoke mc;

            for (TreeNode<T> tns : tn.getSons()
                    ) {
                //retrieve the choke
                mc = tns.getInfo();
                //feature escalation
                if (mc.hasFeature(Features.PUBLIC)) {
                    //do nothing
                } else if (mc.hasFeature(Features.RESERVED)) {
                    if (visLev == Features.PUBLIC)
                        visLev = Features.RESERVED;
                } else if (mc.hasFeature(Features.CONFIDENTIAL)) {
                    if (visLev == Features.PUBLIC || visLev == Features.RESERVED)
                        visLev = Features.CONFIDENTIAL;
                } else if (mc.hasFeature(Features.SECRET)) {
                    visLev = Features.SECRET;
                }
            }//end foreach
            //establish new policy
            Policy p = null;
            switch (visLev) {
                case PUBLIC:
                    p = applyPublicVF();
                    break;
                case RESERVED:
                    p = applyReservedVF();
                    break;
                case CONFIDENTIAL:
                    p = applyConfidentialVF();
                    break;
                case SECRET:
                    p = applySecretVF();
                    break;
            }
            /**
             * Visibility level application
             */
            //remove default visibility feature
            tn.getInfo().removeFeature(Features.PUBLIC);
            //add new visibility feature
            tn.getInfo().addFeature(visLev);
            //set new policy
            tn.getElement().setPolicy(p);
        }
    }

    /**
     * Generation of basic visibility levels
     *
     * @param op
     * @param f
     */
    private <T extends Operation>void generateVisibilityLevels(TreeNode <T>tn) {
        Policy opp = new Policy();
        MetaChoke f = tn.getInfo();
        if (f.hasFeature(Features.PUBLIC)) {
            opp = applyPublicVF();
        } else if (f.hasFeature(Features.RESERVED)) {
            opp = applyReservedVF();
        } else if (f.hasFeature(Features.CONFIDENTIAL)) {
            opp = applyConfidentialVF();
        } else if (f.hasFeature(Features.SECRET)) {
            opp = applySecretVF();
        }
        else{
            //IN CASE NO FEATURE WAS PROVIDED TO LEAF SET IT TO PUBLIC
            f.addFeature(Features.PUBLIC);
        }
        tn.getElement().setPolicy(opp);
    }

    private Policy applyPublicVF() {
        Policy p = new Policy();
        p.setP1(VisibilityLevel.VISIBLE);
        p.setP2(VisibilityLevel.VISIBLE);
        p.setP3(VisibilityLevel.VISIBLE);
        return p;
    }

    private Policy applyReservedVF() {
        Policy p = new Policy();
        p.setP1(VisibilityLevel.VISIBLE);
        p.setP2(VisibilityLevel.VISIBLE);
        p.setP3(VisibilityLevel.ENCRYPTED);
        return p;
    }

    private Policy applyConfidentialVF() {
        Policy p = new Policy();
        p.setP1(VisibilityLevel.VISIBLE);
        p.setP2(VisibilityLevel.ENCRYPTED);
        p.setP3(VisibilityLevel.NOTVISIBLE);
        return p;
    }

    private Policy applySecretVF() {
        Policy p = new Policy();
        p.setP1(VisibilityLevel.VISIBLE);
        p.setP2(VisibilityLevel.NOTVISIBLE);
        p.setP3(VisibilityLevel.NOTVISIBLE);
        return p;
    }

}
