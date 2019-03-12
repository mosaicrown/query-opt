package Operation_allocator.Trees.Semantics.SemanticOperations;

import Operation_allocator.Actors.Provider;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Data.AttributeConstraint;
import Operation_allocator.Data.AttributeState;
import Operation_allocator.Misc.Triple;
import Operation_allocator.Statistics.Metrics.BasicMetric;
import Operation_allocator.Statistics.Metrics.CostMetric;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.util.LinkedList;
import java.util.List;

/**
 * Applies encryption required to a relation profile
 * It computes differential encryption cost, data expansion and state management
 * NOTE: the cost profiles are computed following some approximations, AES 128(block size) chosen as default deterministic symmetric encryption model
 */
public final class Encryption {

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

            for (Attribute za : rpattributes
            ) {
                Attribute a = za.copyAttribute();
                //incorporation of motion before encryption applied, this helps cost computation
                //NB the motion of data does not happens if it has to be encrypted
                cm.Cm += noftuples * a.getDimension() * (start.getMetrics().Km + end.getMetrics().Km);

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
                                    //1) compute encryption cost (a linear model is used)
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_rse;

                                    //2) set new attribute dimension
                                    a.setDimension(Encryption.calculateNewDimension(a, AttributeState.RANDSYMENC, noftuples));
                                    newAttributeDim = a.getDimension();

                                    //3) compute motion cost
                                    cm.Cm += noftuples * (newAttributeDim - oldAttributeDim) * (start.getMetrics().Km + end.getMetrics().Km);

                                    //4) set new attribute state
                                    a.setState(AttributeState.RANDSYMENC);

                                    break;
                                case DETSYMENC:
                                    //1) compute encryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_dse;

                                    //2) set new attribute dimension
                                    a.setDimension(Encryption.calculateNewDimension(a, AttributeState.DETSYMENC, noftuples));
                                    newAttributeDim = a.getDimension();

                                    //3) compute motion cost
                                    cm.Cm += noftuples * (newAttributeDim - oldAttributeDim) * (start.getMetrics().Km + end.getMetrics().Km);

                                    //4) set new attribute state
                                    a.setState(AttributeState.DETSYMENC);
                                    break;
                                case PALCRYPTENC:
                                    //1) compute encryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_pal;

                                    //2) set new attribute dimension
                                    a.setDimension(Encryption.calculateNewDimension(a, AttributeState.PALCRYPTENC, noftuples));
                                    newAttributeDim = a.getDimension();

                                    //3) compute motion cost
                                    cm.Cm += noftuples * (newAttributeDim - oldAttributeDim) * (start.getMetrics().Km + end.getMetrics().Km);

                                    //4) set new attribute state
                                    a.setState(AttributeState.PALCRYPTENC);
                                    break;
                                case OPESCHENC:
                                    //1) compute encryption cost (we assume an overhead to manage OPE B+TREE (logarithmic) )
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_ope * (1 + Math.log(noftuples) / noftuples);

                                    //2) set new attribute dimension
                                    a.setDimension(Encryption.calculateNewDimension(a, AttributeState.OPESCHENC, noftuples));
                                    newAttributeDim = a.getDimension();

                                    //3) compute motion cost
                                    cm.Cm += noftuples * (newAttributeDim - oldAttributeDim) * (start.getMetrics().Km + end.getMetrics().Km);

                                    //4) set new attribute state
                                    a.setState(AttributeState.OPESCHENC);
                                    break;

                                default:
                                    break;
                            }//end switch

                        } else if (a.getState() != AttributeState.PLAINTEXT && acstate == AttributeState.PLAINTEXT) {
                            //apply decryption, no additional motion cost(data is already on the right provider)
                            double oldAttributeDim = a.getDimension();

                            switch (acstate) {
                                case RANDSYMENC:
                                    //1) compute decryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_rse;
                                    break;
                                case DETSYMENC:
                                    //1) compute decryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_dse;
                                    break;
                                case PALCRYPTENC:
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_pal;
                                    break;
                                case OPESCHENC:
                                    //1) compute decryption cost
                                    cm.Cc += (noftuples * oldAttributeDim) * start.getMetrics().Kc_ope;
                                    break;

                                default:
                                    break;
                            }//end switch
                            //2) set new attribute dimension
                            a.setDimension(a.getOriginal_size());
                            //3) set new attribute state
                            a.setState(AttributeState.PLAINTEXT);

                        }

                        //useless to continue searching
                        break;
                    }//end first if

                }//end foreach 3

                //new tuple dimension update
                newtupledim += a.getDimension();
                /*System.out.print("Inserted :"+a.longToString()+" in la");*/
                RelationProfile.insertAttribute(la, a);

            }//end foreach 2
            /*System.out.println();*/
            //update single son's table metrics (bigger input data)
            bm.inputTupleSize += newtupledim;
            bm.inputSize += rpbasicmetric.getOutputSize() * (newtupledim / rpbasicmetric.getOutputTupleSize());

        }//end foreach 1

        //REMEMBER to update time and output stats
        /*System.out.print("Before to update: ");
        for (Attribute tempa : la
                ) {
            System.out.print(tempa.longToString() + "  ");
        }
        System.out.println();*/

        return new Triple<>(cm, bm, la);
    }

    /**
     * Returns new attribute dimension based on encryption target state
     *
     * @param a       attribute to be modified
     * @param eTarget wrapping target
     * @param nof     number of table tuples
     * @return new attribute dimension in KB
     */
    private static double calculateNewDimension(Attribute a, AttributeState eTarget, double nof) {
        //original attribute dimension in bits
        double odim = (a.getOriginal_size() * 1024 * 8);
        int quo = (int) (odim / 128);
        switch (eTarget) {
            case PLAINTEXT:
                return a.getOriginal_size();
            case RANDSYMENC:
                //we take AES with blocks of 128 bit with CTR mode as example
                //only one random value has to be sent, so approximately no data expansion (only block padding)
                return a.getOriginal_size() * ((double) (quo) + 1) / quo;
            case DETSYMENC:
                //we take AES with blocks of 128 bit as example (only block padding)
                return a.getOriginal_size() * ((quo) + 1) / quo;
            case PALCRYPTENC:
                //n-squared residuous classes, so a single bit expansion (we suppose 127 bit maximum range for n)
                int k = (int) (odim / 127) * 128;
                return a.getOriginal_size() * ((double) k);
            case OPESCHENC:
                //wes suppose to take AES 128 as DETSYMENC and then add an indexing prefix-tree
                return a.getOriginal_size() * (((double) (quo) + 1) / quo) * (1 + Math.log(nof) / nof);
            default:
                throw new RuntimeException("Target encryption wrapping state does not exists!");

        }
    }

}
