package Operation_allocator.Actors.Operations;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class Selection extends Operation implements Serializable {

    private List<Attribute> homogeneousSet;

    public Selection() {
        super.familyname = "Selection";
        homogeneousSet = new LinkedList<>();
    }

    public Selection(List<Attribute> la) {
        super.familyname = "Selection";
        homogeneousSet = la;
    }

    public void setHomogeneousSet(List<Attribute> projSet) {
        this.homogeneousSet = projSet;
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
        if (homogeneousSet.size() == 1) {
            super.output_rp.setRip(RelationProfile.union(rp.getRip(), (RelationProfile.intersection(vp, homogeneousSet))));
            super.output_rp.setRie(RelationProfile.union(rp.getRie(), (RelationProfile.intersection(ve, homogeneousSet))));
            super.output_rp.setCes(RelationProfile.copyCE(rp.getCes()));
        } else {
            super.output_rp.setRip(RelationProfile.copyLoA(rp.getRip()));
            super.output_rp.setRie(RelationProfile.copyLoA(rp.getRie()));
            super.output_rp.setCes(RelationProfile.unionCE(rp.getCes(), homogeneousSet));
        }
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
        res.setRvp(vp);
        res.setRve(ve);
        if (homogeneousSet.size() == 1) {
            res.setRip(RelationProfile.union(rp.getRip(), (RelationProfile.intersection(vp, homogeneousSet))));
            res.setRie(RelationProfile.union(rp.getRie(), (RelationProfile.intersection(ve, homogeneousSet))));
            res.setCes(RelationProfile.copyCE(rp.getCes()));
        } else {
            res.setRip(RelationProfile.copyLoA(rp.getRip()));
            res.setRie(RelationProfile.copyLoA(rp.getRie()));
            res.setCes(RelationProfile.unionCE(rp.getCes(), homogeneousSet));
        }

        return res;
    }

    @Override
    public List<Attribute> getHomogeneousSet() {
        return RelationProfile.copyLoA(homogeneousSet);
    }
}
