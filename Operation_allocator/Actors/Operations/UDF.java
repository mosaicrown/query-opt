package Actors.Operations;

import Actors.Operation;
import Data.Attribute;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a point of variation from article' policy
 * The UDF acts like a special filter that preserves some attributes(in future even create new) and requires a uniform set of them
 *
 * NB this class will be subject of changes
 */
public final class UDF extends Operation implements Serializable {

    //surviving attributes
    private List<Attribute> projectedSet;
    //computing set
    private List<Attribute> functionSet;

    public UDF() {
        super.familyname = "UDF";
        projectedSet = new LinkedList<>();
        functionSet = new LinkedList<>();
    }

    public UDF(List<Attribute> pset, List<Attribute> fset) {
        super.familyname = "UDF";
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
        //get visible plaintext data
        List<Attribute> vp = RelationProfile.getVisPlaint(super.inputAttributes);
        //get visible ecnrypted data
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
        super.output_rp.setRip(RelationProfile.union(ip, RelationProfile.subtraction(functionSet, RelationProfile.intersection(functionSet, ip))));
        super.output_rp.setRie(RelationProfile.union(ie, RelationProfile.subtraction(functionSet, RelationProfile.intersection(functionSet, ie))));
        super.output_rp.setCes(RelationProfile.copyCE(RelationProfile.unionCE(eset, functionSet)));
    }
}
