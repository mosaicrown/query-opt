package Actors.Operations;

import Actors.Operation;
import Data.Attribute;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class CartesianProduct extends Operation implements Serializable {

    public CartesianProduct() {
        super.familyname = "Cartesian product";
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
}
