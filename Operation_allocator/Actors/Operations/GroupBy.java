package Actors.Operations;

import Actors.Operation;
import Data.Attribute;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class GroupBy extends Operation implements Serializable {

    private List<Attribute> projectedSet;
    private List<Attribute> functionSet;

    public GroupBy() {
        super.familyname = "Group By";
        projectedSet = new LinkedList<>();
        functionSet = new LinkedList<>();
    }

    public GroupBy(List<Attribute> pset, List<Attribute> fset) {
        super.familyname = "Group By";
        projectedSet = pset;
        functionSet = fset;
    }

    public List<Attribute> getProjectedSet() {
        return projectedSet;
    }

    public void setProjectedSet(List<Attribute> projectedSet) {
        this.projectedSet = projectedSet;
    }

    public List<Attribute> getFunctionSet() {
        return functionSet;
    }

    public void setFunctionSet(List<Attribute> functionSet) {
        this.functionSet = functionSet;
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
        super.output_rp.setRvp(RelationProfile.intersection(vp, RelationProfile.union(projectedSet, functionSet)));
        super.output_rp.setRve(RelationProfile.intersection(ve, RelationProfile.union(projectedSet, functionSet)));
        super.output_rp.setRip(RelationProfile.union(rp.getRip(), RelationProfile.intersection(vp, projectedSet)));
        super.output_rp.setRie(RelationProfile.union(rp.getRie(), RelationProfile.intersection(ve, projectedSet)));
        super.output_rp.setCes(RelationProfile.copyCE(rp.getCes()));
    }
}
