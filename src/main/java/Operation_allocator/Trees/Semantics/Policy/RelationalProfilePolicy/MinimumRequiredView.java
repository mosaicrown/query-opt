package Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy;

import Operation_allocator.Data.Attribute;
import Operation_allocator.Data.AttributeConstraint;
import Operation_allocator.Data.AttributeState;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents    MINIMUM REQUIRED VIEW
 * from                     DEFINITION 5.2
 * in article               An authorization model for multi-provider queries
 */
public final class MinimumRequiredView {

    private MinimumRequiredView() {
    }

    public static RelationProfile computeMinimumReqView(RelationProfile oprp, List<AttributeConstraint> constraints) {
        //copy and encrypt original relation profile
        RelationProfile rp = encryptAllVisibleAttributes(oprp);
        //apply operation' necessary decryption
        Attribute attr;
        AttributeState state;
        List<Attribute> la = rp.getRve();
        for (AttributeConstraint constr : constraints
                ) {
            attr = constr.getAttr();
            state = constr.getState();
            if (state == AttributeState.PLAINTEXT)
                if (RelationProfile.hasAttribute(la, attr))
                    moveToPlaintext(rp, attr);
        }
        return rp;
    }

    private static RelationProfile encryptAllVisibleAttributes(RelationProfile rp) {
        //create a copy of relation profile
        RelationProfile rpe = rp.copyRelationProfile();
        //move every attribute visible plaintext to visible encrypted
        rpe.setRve(RelationProfile.union(rpe.getRvp(), rpe.getRve()));
        //empty visible plaintext attributes
        List<Attribute> l = new LinkedList<>();
        rpe.setRvp(l);

        return rpe;
    }

    private static void moveToPlaintext(RelationProfile r, Attribute a) {
        RelationProfile.insertAttribute(r.getRvp(), a.copyAttribute());
        //RelationProfile.insertAttribute(r.getRip(), a);
        RelationProfile.removeAttribute(r.getRve(), a.copyAttribute());
        //RelationProfile.removeAttribute(r.getRie(), a);
    }
}
