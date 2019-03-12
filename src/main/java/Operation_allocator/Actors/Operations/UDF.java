package Operation_allocator.Actors.Operations;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a point of variation from article' policy
 * The UDF acts like a special filter that preserves some attributes(in future even create new) and requires a uniform set of them
 * <p>
 * NB this class will be subject of changes
 */
public final class UDF extends Operation implements Serializable {

    //surviving attributes
    private List<Attribute> projectedSet;
    //computing set
    private List<Attribute> homogeneousSet;

    public UDF() {
        super.familyname = "UDF";
        projectedSet = new LinkedList<>();
        homogeneousSet = new LinkedList<>();
    }

    public UDF(List<Attribute> pset, List<Attribute> fset) {
        super.familyname = "UDF";
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
        //get visible plaintext data
        List<Attribute> vp = RelationProfile.getVisPlaint(super.inputAttributes);
        //get visible encrypted data
        List<Attribute> ve = RelationProfile.getVisEnc(super.inputAttributes);
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

        //set the new relation profile
        super.output_rp.setRvp(RelationProfile.intersection(vp, projectedSet));
        super.output_rp.setRve(RelationProfile.intersection(ve, projectedSet));
        super.output_rp.setRip(RelationProfile.union(ip, RelationProfile.subtraction(homogeneousSet, RelationProfile.intersection(homogeneousSet, ip))));
        super.output_rp.setRie(RelationProfile.union(ie, RelationProfile.subtraction(homogeneousSet, RelationProfile.intersection(homogeneousSet, ie))));
        super.output_rp.setCes(RelationProfile.copyCE(RelationProfile.unionCE(eset, homogeneousSet)));
    }

    @Override
    public RelationProfile simulateOutRelProf(List<RelationProfile> lrp, List<Attribute> newInputSet) {
        RelationProfile res = new RelationProfile();

        //get visible plaintext data
        List<Attribute> vp = RelationProfile.getVisPlaint(newInputSet);
        //get visible encrypted data
        List<Attribute> ve = RelationProfile.getVisEnc(newInputSet);
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

        //set the new relation profile
        res.setRvp(RelationProfile.intersection(vp, projectedSet));
        res.setRve(RelationProfile.intersection(ve, projectedSet));
        res.setRip(RelationProfile.union(ip, RelationProfile.subtraction(homogeneousSet, RelationProfile.intersection(homogeneousSet, ip))));
        res.setRie(RelationProfile.union(ie, RelationProfile.subtraction(homogeneousSet, RelationProfile.intersection(homogeneousSet, ie))));
        res.setCes(RelationProfile.copyCE(RelationProfile.unionCE(eset, homogeneousSet)));

        return res;
    }

    @Override
    public List<Attribute> getHomogeneousSet() {
        return RelationProfile.copyLoA(homogeneousSet);
    }
}
