package Operation_allocator.Actors.Operations.SetOperations;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Subtraction class does not automatically projects output attributes, it has to be specified what is projected
 * The same for homogeneous set
 * Remember that even constrained set is required to be fixed, it would be useless to apply subtraction to tuples
 * wrapped with randomized symmetric encryption
 * Homogeneous set must be the whole attribute set
 */
public final class Subtraction extends Operation implements Serializable {

    //surviving attributes
    private List<Attribute> projectedSet;
    //computing set
    private List<Attribute> homogeneousSet;

    public Subtraction() {
        super.familyname = "Subtraction";
        projectedSet = new LinkedList<>();
        homogeneousSet = new LinkedList<>();
    }

    public Subtraction(List<Attribute> pset, List<Attribute> fset) {
        super.familyname = "Subtraction";
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
        //it would be useless to apply intersection to implicit sets because all attributes in a union operation are implicit
        super.output_rp.setRip(ip);
        super.output_rp.setRie(ie);
        super.output_rp.setCes(eset);
    }

    @Override
    public RelationProfile simulateOutRelProf(List<RelationProfile> lrp, List<Attribute> newInputSet) {
        RelationProfile res = new RelationProfile();

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
        eset = RelationProfile.unionCE(eset, homogeneousSet);
        //add visible plaintext data
        List<Attribute> vp = RelationProfile.getVisPlaint(newInputSet);
        //add visible ecnrypted data
        List<Attribute> ve = RelationProfile.getVisEnc(newInputSet);
        //set the new relation profile
        res.setRvp(RelationProfile.intersection(vp, projectedSet));
        res.setRve(RelationProfile.intersection(ve, projectedSet));
        res.setRip(ip);
        res.setRie(ie);
        res.setCes(eset);

        return res;
    }

    @Override
    public List<Attribute> getHomogeneousSet() {
        return RelationProfile.copyLoA(homogeneousSet);
    }
}
