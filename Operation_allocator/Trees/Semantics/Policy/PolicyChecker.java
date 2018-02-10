package Trees.Semantics.Policy;

import Actors.Provider;
import Trees.Semantics.Features;
import Trees.Semantics.MetaChoke;

import java.util.LinkedList;
import java.util.List;

public class PolicyChecker {

    private Provider p1;
    private Provider p2;
    private Provider p3;

    public PolicyChecker() {

    }

    public Provider getP1() {
        return p1;
    }

    public void setP1(Provider p1) {
        this.p1 = p1;
    }

    public Provider getP2() {
        return p2;
    }

    public void setP2(Provider p2) {
        this.p2 = p2;
    }

    public Provider getP3() {
        return p3;
    }

    public void setP3(Provider p3) {
        this.p3 = p3;
    }

    public List<PolicyPair> allowedTwistP3(Policy p, MetaChoke meta) {
        List<PolicyPair> pp = new LinkedList<>();
        PolicyPair policyPair = null;

        if (!(p.getP3() == VisibilityLevel.NOTVISIBLE)) {
            if (p.getP3() == VisibilityLevel.VISIBLE) {
                policyPair = new PolicyPair();
                policyPair.provider = p3;
                policyPair.feature = Features.NOTENCRYPTED;
                pp.add(policyPair);
            }
            if (p.getP3() == VisibilityLevel.ENCRYPTED) {
                if (!meta.hasFeature(Features.ENCRYPTEDSYM)) { //or public?
                    policyPair = new PolicyPair();
                    policyPair.provider = p3;
                    policyPair.feature = Features.ENCRYPTEDHOM;
                    pp.add(policyPair);
                }
                if (meta.hasFeature(Features.ENCRYPTEDHOM)) {
                    policyPair = new PolicyPair();
                    policyPair.provider = p3;
                    policyPair.feature = Features.ENCRYPTEDSYM;
                    pp.add(policyPair);
                }
            }

        }
        return pp;
    }

    public List<PolicyPair> allowedTwistP2(Policy p, MetaChoke meta) {
        List<PolicyPair> pp = new LinkedList<>();
        PolicyPair policyPair = null;

        if (!(p.getP2() == VisibilityLevel.NOTVISIBLE)) {
            if (p.getP2() == VisibilityLevel.VISIBLE) {
                policyPair = new PolicyPair();
                policyPair.provider = p2;
                policyPair.feature = Features.NOTENCRYPTED;
                pp.add(policyPair);
            }
            if (p.getP2() == VisibilityLevel.ENCRYPTED) {
                if (meta.hasFeature(Features.ENCRYPTEDSYM)) {
                    policyPair = new PolicyPair();
                    policyPair.provider = p2;
                    policyPair.feature = Features.ENCRYPTEDHOM;
                    pp.add(policyPair);
                }
                if (meta.hasFeature(Features.ENCRYPTEDHOM)) {
                    policyPair = new PolicyPair();
                    policyPair.provider = p2;
                    policyPair.feature = Features.ENCRYPTEDSYM;
                    pp.add(policyPair);
                }
            }

        }
        return pp;
    }


}
