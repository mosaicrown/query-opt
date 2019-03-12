package Operation_allocator.Actors.Operations;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class CartesianProduct extends Operation implements Serializable {

    private List<Attribute> homogeneousSet;

    public CartesianProduct() {
        homogeneousSet = new LinkedList<>();
        super.familyname = "Cartesianproduct";
    }

    public CartesianProduct(List<Attribute> es) {
        homogeneousSet = es;
        super.familyname = "Cartesianproduct";
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
        eset = RelationProfile.unionCE(eset, homogeneousSet);
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
        //add visible encrypted data
        List<Attribute> ve = RelationProfile.getVisEnc(newInputSet);
        //set the new relation profile
        res.setRvp(vp);
        res.setRve(ve);
        res.setRip(ip);
        res.setRie(ie);
        res.setCes(eset);

        return res;
    }

    @Override
    public List<Attribute> getHomogeneousSet() {
        return homogeneousSet;
    }

    public void setHomogeneousSet(List<Attribute> homogeneousSet) {
        this.homogeneousSet = homogeneousSet;
    }
}
