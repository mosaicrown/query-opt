package Operation_allocator.Actors.Operations;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class Projection extends Operation implements Serializable {

    private List<Attribute> projectedSet;

    public Projection() {
        super.familyname = "Projection";
        projectedSet = new LinkedList<>();
    }

    public Projection(List<Attribute> la) {
        super.familyname = "Projection";
        projectedSet = la;
    }

    public List<Attribute> getProjectedSet() {
        return projectedSet;
    }

    public void setProjectedSet(List<Attribute> projSet) {
        this.projectedSet = projSet;
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
        super.output_rp.setRvp(RelationProfile.intersection(vp, projectedSet));
        super.output_rp.setRve(RelationProfile.intersection(ve, projectedSet));
        super.output_rp.setRip(RelationProfile.copyLoA(rp.getRip()));
        super.output_rp.setRie(RelationProfile.copyLoA(rp.getRie()));
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
        res.setRvp(RelationProfile.intersection(vp, projectedSet));
        res.setRve(RelationProfile.intersection(ve, projectedSet));
        res.setRip(RelationProfile.copyLoA(rp.getRip()));
        res.setRie(RelationProfile.copyLoA(rp.getRie()));
        res.setCes(RelationProfile.copyCE(rp.getCes()));

        return res;
    }
}
