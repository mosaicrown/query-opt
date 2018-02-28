package Actors.Operations;

import Actors.Operation;
import Data.Attribute;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class Selection extends Operation implements Serializable {

    private List<Attribute> selectionSet;

    public Selection() {
        super.familyname = "Selection";
        selectionSet = new LinkedList<>();
    }

    public Selection(List<Attribute> la) {
        super.familyname = "Selection";
        selectionSet = la;
    }

    public List<Attribute> getSelectionSet() {
        return selectionSet;
    }

    public void setSelectionSet(List<Attribute> projSet) {
        this.selectionSet = projSet;
    }

    @Override
    public void computeOutRelProf(List<RelationProfile> lrp) {
        //retrieve data (single arity)
        RelationProfile rp = lrp.get(0);
        //get visible plaintext data
        List<Attribute> vp = RelationProfile.getVisPlaint(super.inputAttributes);
        //get visible ecnrypted data
        List<Attribute> ve = RelationProfile.getVisEnc(super.inputAttributes);
        //set the new relation profile
        super.output_rp.setRvp(vp);
        super.output_rp.setRve(ve);
        if (selectionSet.size() == 1) {
            super.output_rp.setRip(RelationProfile.union(rp.getRip(), (RelationProfile.intersection(vp, selectionSet))));
            super.output_rp.setRie(RelationProfile.union(rp.getRie(), (RelationProfile.intersection(ve, selectionSet))));
            super.output_rp.setCes(RelationProfile.copyCE(rp.getCes()));
        } else {
            super.output_rp.setRip(RelationProfile.copyLoA(rp.getRip()));
            super.output_rp.setRie(RelationProfile.copyLoA(rp.getRie()));
            super.output_rp.setCes(RelationProfile.unionCE(rp.getCes(), selectionSet));
        }
    }
}
