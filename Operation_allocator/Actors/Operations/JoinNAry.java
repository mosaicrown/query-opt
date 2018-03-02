package Actors.Operations;

import Actors.Operation;
import Data.Attribute;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class JoinNAry extends Operation implements Serializable {

    private List<Attribute> equivalentSet;

    public JoinNAry() {
        super.familyname = "Join N Ary";
        equivalentSet = new LinkedList<>();
    }

    public JoinNAry(List<Attribute> la) {
        super.familyname = "Join N Ary";
        equivalentSet = la;
    }

    public List<Attribute> getEquivalentSet() {
        return equivalentSet;
    }

    public void setEquivalentSet(List<Attribute> equivalentSet) {
        this.equivalentSet = equivalentSet;
    }

    @Override
    public void computeOutRelProf(List<RelationProfile> lrp) {
        //implicit attributes
        List<Attribute> ip = new LinkedList<>();
        List<Attribute> ie = new LinkedList<>();
        //closure of eq. set
        List<List<Attribute>> eset = new LinkedList<>();
        //retrieve data for implicit
        for (RelationProfile rp : lrp
                ) {
            ip = RelationProfile.union(ip, rp.getRip());
            ie = RelationProfile.union(ie, rp.getRie());
            eset = RelationProfile.unionCEsets(eset, rp.getCes());
        }
        //completion of new equivalence set
        eset = RelationProfile.unionCE(eset, equivalentSet);
        //add visible plaintext data
        List<Attribute> vp = RelationProfile.getVisPlaint(super.inputAttributes);
        //add visible ecnrypted data
        List<Attribute> ve = RelationProfile.getVisEnc(super.inputAttributes);
        //set the new relation profile
        super.output_rp.setRvp(vp);
        super.output_rp.setRve(ve);
        super.output_rp.setRip(ip);
        super.output_rp.setRie(ie);
        super.output_rp.setCes(eset);
    }

    @Override
    public List<Attribute> getHomogeneousSet(){
        return RelationProfile.copyLoA(equivalentSet);
    }
}
