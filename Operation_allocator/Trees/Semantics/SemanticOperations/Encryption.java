package Trees.Semantics.SemanticOperations;

import Actors.Provider;
import Data.Attribute;
import Data.AttributeConstraint;
import Data.AttributeState;
import Misc.Pair;
import Misc.Triple;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class Encryption {
                                            //TODO COMPLETE 2 ENCRYPTION PROFILES

    public static Triple<CostMetric, BasicMetric, List<Attribute>> applyEncryption
            (List<Triple<RelationProfile, BasicMetric, Provider>> sonTable, List<AttributeConstraint> attrConstr, Provider end) {

        //result params
        CostMetric cm = new CostMetric();
        cm.setAllZero();
        BasicMetric bm = new BasicMetric();
        List<Attribute> la = new LinkedList<>();

        for (Triple<RelationProfile, BasicMetric, Provider> triple : sonTable
                ) {
            //retrieve table information
            RelationProfile rp = triple.getFirst();
            BasicMetric rpbasicmetric = triple.getSecond();
            Provider start = triple.getThird();
            //retrieve table stats
            double noftuples = rpbasicmetric.getOutNofTuples();
            double newtupledim = 0;

            //retrieve table attributes
            List<Attribute> rpattributes = RelationProfile.union(RelationProfile.copyLoA(rp.getRvp()), RelationProfile.copyLoA(rp.getRve()));

            for (Attribute a : rpattributes
                    ) {
                for (AttributeConstraint ac : attrConstr
                        ) {
                    Attribute acattr = ac.getAttr();
                    AttributeState acstate = ac.getState();
                    if (a.equals(acattr)) {
                        if (a.getState() == AttributeState.PLAINTEXT && acstate != AttributeState.PLAINTEXT) {
                            double oldAttributeDim = a.getDimension();
                            double newAttributeDim;
                            //apply encryption
                            switch (acstate) {
                                case RANDSYMENC:
                                    /**
                                     * Considered data expansion of 1 block over 16 bytes blocks (AES)
                                     * NB attribute's dimensions are in KB
                                     */
                                    //1) compute encryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_rse;

                                    //2) set new attribute dimension
                                    a.setDimension(((long) (oldAttributeDim * 1024) / 16 + 1) * (double) (16 / 1024));
                                    newAttributeDim = a.getDimension();

                                    //3) compute motion cost
                                    cm.Cm += noftuples * (newAttributeDim - oldAttributeDim) * (start.getMetrics().Km + end.getMetrics().Km);

                                    //4) set new attribute state
                                    a.setState(AttributeState.RANDSYMENC);

                                    break;
                                case DETSYMENC:
                                    /**
                                     * Considering RSA, the padding length is no significant because we assume the message
                                     * to be always shorter than the key minus PKCS padding scheme( key minus 11 bytes)
                                     * If attribute length was shorter than minimum required length the data will be expanded
                                     */
                                    //1) compute encryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_dse;

                                    //2) set new attribute dimension
                                    if (((long) (oldAttributeDim * 1024)) < 128) {
                                        //0.125 KB equal to 128 Byte
                                        a.setDimension(0.125);
                                    }
                                    newAttributeDim = a.getDimension();

                                    //3) compute motion cost
                                    cm.Cm += noftuples * (newAttributeDim - oldAttributeDim) * (start.getMetrics().Km + end.getMetrics().Km);

                                    //4) set new attribute state
                                    a.setState(AttributeState.DETSYMENC);
                                    break;
                                case PALCRYPTENC:

                                    break;
                                case OPESCHENC:

                                    break;

                                default:
                                    break;
                            }//end switch

                        } else if (a.getState() != AttributeState.PLAINTEXT && acstate == AttributeState.PLAINTEXT) {
                            //apply decryption, no additional motion cost(data is already on the right provider)
                            double oldAttributeDim;

                            switch (acstate) {
                                case RANDSYMENC:
                                    oldAttributeDim = a.getDimension();
                                    //1) compute decryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_rse;
                                    //2) set new attribute dimension
                                    a.setDimension(a.getOriginal_size());
                                    //3) set new attribute state
                                    a.setState(AttributeState.PLAINTEXT);
                                    break;

                                case DETSYMENC:
                                    oldAttributeDim = a.getDimension();
                                    //1) compute decryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_dse;
                                    //2) set new attribute dimension
                                    a.setDimension(a.getOriginal_size());
                                    //3) set new attribute state
                                    a.setState(AttributeState.PLAINTEXT);
                                    break;
                                case PALCRYPTENC:
                                    oldAttributeDim = a.getDimension();
                                    //1) compute decryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_pal;
                                    //2) set new attribute dimension
                                    a.setDimension(a.getOriginal_size());
                                    //3) set new attribute state
                                    a.setState(AttributeState.PLAINTEXT);
                                    break;
                                case OPESCHENC:
                                    oldAttributeDim = a.getDimension();
                                    //1) compute decryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_ope;
                                    //2) set new attribute dimension
                                    a.setDimension(a.getOriginal_size());
                                    //3) set new attribute state
                                    a.setState(AttributeState.PLAINTEXT);
                                    break;

                                default:
                                    break;
                            }//end switch

                        }

                        //useless to continue searching
                        break;
                    }//end first if

                }//end foreach 3

                //new tuple dimension update
                newtupledim += a.getDimension();
                //insert attribute inside list of attributes
                RelationProfile.insertAttribute(la, a);

            }//end foreach 2

            //update single son's table metrics (bigger input data)
            bm.inputTupleSize += newtupledim;
            bm.inputSize += rpbasicmetric.getOutputSize() * (newtupledim / rpbasicmetric.getOutputTupleSize());

        }//end foreach 1

        //REMEMBER to update time and output stats

        return new Triple<>(cm, bm, la);
    }

}
