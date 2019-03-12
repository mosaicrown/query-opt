package Operation_allocator.Actors.Operations;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class GroupBy extends Operation implements Serializable {

    private List<Attribute> projectedSet;
    private List<Attribute> homogeneousSet;

    public GroupBy() {
        super.familyname = "GroupBy";
        projectedSet = new LinkedList<>();
        homogeneousSet = new LinkedList<>();
    }

    public GroupBy(List<Attribute> pset, List<Attribute> fset) {
        super.familyname = "GroupBy";
        projectedSet = pset;
        homogeneousSet = fset;
    }

    public List<Attribute> getProjectedSet() {
        return projectedSet;
    }

    public void setProjectedSet(List<Attribute> projectedSet) {
        this.projectedSet = projectedSet;
    }

    public void setHomogeneousSet(List<Attribute> homogeneousSet) {
        this.homogeneousSet = homogeneousSet;
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
        super.output_rp.setRvp(RelationProfile.intersection(vp, RelationProfile.union(projectedSet, homogeneousSet)));
        super.output_rp.setRve(RelationProfile.intersection(ve, RelationProfile.union(projectedSet, homogeneousSet)));
        super.output_rp.setRip(RelationProfile.union(rp.getRip(), RelationProfile.intersection(vp, projectedSet)));
        super.output_rp.setRie(RelationProfile.union(rp.getRie(), RelationProfile.intersection(ve, projectedSet)));
        super.output_rp.setCes(RelationProfile.copyCE(rp.getCes()));
    }

    @Override
    public RelationProfile simulateOutRelProf(List<RelationProfile> lrp, List<Attribute> newInputSet) {
        RelationProfile res = new RelationProfile();

        //retrieve data (single arity)
        RelationProfile rp = lrp.get(0);
        //get visible plaintext data
        List<Attribute> vp = RelationProfile.getVisPlaint(newInputSet);
        //get visible ecnrypted data
        List<Attribute> ve = RelationProfile.getVisEnc(newInputSet);
        //set the new relation profile
        res.setRvp(RelationProfile.intersection(vp, RelationProfile.union(projectedSet, homogeneousSet)));
        res.setRve(RelationProfile.intersection(ve, RelationProfile.union(projectedSet, homogeneousSet)));
        res.setRip(RelationProfile.union(rp.getRip(), RelationProfile.intersection(vp, projectedSet)));
        res.setRie(RelationProfile.union(rp.getRie(), RelationProfile.intersection(ve, projectedSet)));
        res.setCes(RelationProfile.copyCE(rp.getCes()));

        return res;
    }

    @Override
    public List<Attribute> getHomogeneousSet() {
        return RelationProfile.copyLoA(homogeneousSet);
    }
}
