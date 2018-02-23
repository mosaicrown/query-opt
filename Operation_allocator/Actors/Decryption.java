package Actors;

import Data.Attribute;
import Data.AttributeConstraint;
import Data.AttributeState;
import Misc.Pair;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public final class Decryption extends EncOperation implements Serializable {

    private List<Pair<AttributeConstraint, Double>> A;

    public Decryption() {
        super();
        A = new LinkedList<>();
    }

    public Decryption(List<Pair<AttributeConstraint, Double>> set, Provider exec) {
        super();
        super.setExecutor(exec);
        A = set;
    }

    public List<Pair<AttributeConstraint, Double>> getA() {
        return A;
    }

    public void setA(List<Pair<AttributeConstraint, Double>> a) {
        A = a;
    }

    @Override
    public void applyOp(){
        for (Pair<AttributeConstraint, Double> pair: A
                ) {
            Attribute a = pair.getFirst().getAttr();
            AttributeState state = pair.getFirst().getState();
            double noftuples = pair.getSecond();
            /**
             * TODO foreach encryption operation it has to be calculated:
             * 1) the correct cost of encryption
             * 2) the correct data expansion
             * A model of encryption/decryption is needed
             */
            if (a.getState() != AttributeState.PLAINTEXT) {
                switch (state) {
                    case RANDSYMENC:

                        break;
                    case DETSYMENC:

                        break;
                    case PALCRYPTENC:

                        break;
                    case OPESCHENC:

                        break;

                    default:
                        break;
                }//end switch
            }//end if

        }
    }
}
