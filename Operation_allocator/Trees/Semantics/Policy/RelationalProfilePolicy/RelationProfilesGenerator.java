package Trees.Semantics.Policy.RelationalProfilePolicy;

import Actors.Operation;
import Actors.Provider;
import Data.Attribute;
import Data.AttributeConstraint;
import Data.AttributeState;
import Misc.Pair;
import Trees.TreeNode;

import java.util.LinkedList;
import java.util.List;

public class RelationProfilesGenerator {


    private Provider p1;
    private Provider p2;
    private Provider p3;

    public RelationProfilesGenerator() {

    }

    public Provider getP1() {
        return p1;
    }

    public void setP1(Provider p1) {
        this.p1 = p1;
    }

    public Provider getP2() {
        return p2;
    }

    public void setP2(Provider p2) {
        this.p2 = p2;
    }

    public Provider getP3() {
        return p3;
    }

    public void setP3(Provider p3) {
        this.p3 = p3;
    }

    //NB this methods provides only 1 compliant computation move
    public <T extends Operation> List<Pair<List<AttributeConstraint>, Provider>> allowedMoves(Provider end, TreeNode<T> tn) {
        //result is at the moment an empty list
        List<Pair<List<AttributeConstraint>, Provider>> res = new LinkedList<>();

        //retrieve operation input list of attributes
        List<Attribute> inla = tn.getElement().getInputAttributes();
        //retrieve operation constraints
        List<AttributeConstraint> lopac = tn.getElement().getConstraints();
        //retrieve operation equivalent set
        List<Attribute> lopes = tn.getElement().getHomogeneousSet();

        //temporary list of attributes to be encrypted or decrypted
        List<Attribute> hasToBeEncrypted = new LinkedList<>();
        List<Attribute> hasToBePlaintext = new LinkedList<>();

        /**
         * CONSTRAINTS EXPLANATION
         * The meaning of operation constraints is this:
         * Everything could work plaintext, but if a provider needs the encryption to see, then someone is
         * constrained to apply that specific kind of encryption to make the operation possible
         */
        /**
         * ENCRYPTION-DECRYPTION MOVES
         * Sets could contain attributes with wrong encryption technique applied, this would require an infeasible
         * movement to an authorized provider to re-adjust encryption type
         * At first step we suppose an intelligent user(or parser) that assigns the correct constraints to attributes
         * that appear in homogeneous set (when query compiler optimizes the plan it has to enforce distribution of data)
         */

        /**
         * 1)   try to identify attributes that need to be plaintext to be computed, so they should be decrypted
         */

        for (AttributeConstraint ac : lopac
                ) {
            Attribute a1 = ac.getAttr();
            AttributeState tstate = ac.getState();
            if (tstate == AttributeState.PLAINTEXT) {
                //if provider end can't see it in plaintext the next is useless
                if (!RelationProfile.isAllowedToSeePlaintext(end, a1))
                    return res;
                RelationProfile.insertAttribute(hasToBePlaintext, a1);
            }
        }
        /**
         * 2)   try to identify attributes that end is not allowed to see plaintext but encrypted, so they should
         *      be encrypted
         */
        for (Attribute a : inla
                ) {
            if (!RelationProfile.isAllowedToSeeEncrypted(end, a))
                return res;
            if (!RelationProfile.isAllowedToSeePlaintext(end, a)) {
                RelationProfile.insertAttribute(hasToBeEncrypted, a);
            }
        }
        /**
         * 3)   check for incoherence of intents
         */
        List<Attribute> incompatibility = RelationProfile.intersection(hasToBeEncrypted, hasToBePlaintext);
        if (incompatibility.size() != 0)
            return res;

        /**
         * 4) check for inconsistency of intents
         */

        //try to build new attribute set
        List<Attribute> newAttrSet = RelationProfile.copyLoA(inla);

        // 4.1) attempts to apply decryption required by tn's operation
        for (Attribute htbp : hasToBePlaintext
                ) {
            for (Attribute ta : newAttrSet
                    ) {
                if (htbp.equals(ta)) {
                    ta.setState(AttributeState.PLAINTEXT);
                    break;
                }
            }
        }

        // 4.2) attempts to apply correct encryption required by restriction policy on end's

        //identify attributes that would need constraints to be applied
        List<Attribute> constrainedAttributes = new LinkedList<>();
        for (AttributeConstraint ac : lopac
                ) {
            RelationProfile.insertAttribute(constrainedAttributes, ac.getAttr());
        }
        //intercept all attributes that have to be encrypted and are subjected to constrains
        List<Attribute> encryptedConstrained = RelationProfile.intersection(hasToBeEncrypted, constrainedAttributes);
        AttributeState commonTarget = AttributeState.PLAINTEXT;

        if (encryptedConstrained.size() > 0) {
            //consistency of intents (among this particular set) needs to ve verified
            //try to apply constraints to a test set and see what happens
            for (Attribute eca : encryptedConstrained
                    ) {
                //identify required encryption
                AttributeState ecaState = AttributeState.PLAINTEXT;
                for (AttributeConstraint ac : lopac
                        ) {
                    if (eca.equals(ac.getAttr())) {
                        ecaState = ac.getState();
                        if (commonTarget == AttributeState.PLAINTEXT)
                            commonTarget = ecaState;
                        break;
                    }
                }
                //apply constraint
                for (Attribute newAs : newAttrSet
                        ) {
                    if (eca.equals(newAs)) {
                        //check for danger of double encryption
                        if (newAs.getState() != AttributeState.PLAINTEXT && newAs.getState() != ecaState)
                            return res;
                        newAs.setState(ecaState);
                        break;
                    }
                }
            }
        }//end if, case multiple encrypted attributes constrained

        //now newAttrSet has decryption and constrained encryption applied

        //if the homogeneous set contains attributes enforced at the same time encrypted and plaintext, the attempt fails
        //(constrained homogeneous)
        boolean encryptAllHomog = false;
        boolean decryptAllHomog = false;
        List<Attribute> i1 = RelationProfile.intersection(lopes, hasToBeEncrypted);
        List<Attribute> i2 = RelationProfile.intersection(lopes, hasToBePlaintext);
        if (i1.size() != 0) {
            if (i2.size() != 0)
                return res;
            else
                encryptAllHomog = true;
        } else if (i2.size() != 0) {
            decryptAllHomog = true;
        }

        //now is known what to do with attributes in homogeneous set
        if (decryptAllHomog) {
            for (Attribute dha : newAttrSet
                    ) {
                if (RelationProfile.hasAttribute(lopes, dha)) {
                    if (RelationProfile.isAllowedToSeePlaintext(end, dha)) {
                        dha.setState(AttributeState.PLAINTEXT);
                    } else
                        return res;
                }
            }
        } else if (encryptAllHomog) {
            //if there is a constraint try to apply it foreach attributes in the set, else try to find a new constraint,
            //else apply cheapest
            //now identify if there are constraints on homogeneous set
            List<AttributeState> constrainedHomogStates = new LinkedList<>();
            for (Attribute cha : lopes
                    ) {
                for (AttributeConstraint tempcons : lopac
                        ) {
                    Attribute tempconsattr = tempcons.getAttr();
                    AttributeState tempconsstate = tempcons.getState();
                    if (cha.equals(tempconsattr)) {
                        constrainedHomogStates.add(tempconsstate);
                    }
                }
            }
            //2 cases:
            // -an encryption constraint and compatible list of states
            // -no encryption constraint and homogeneous list of states
            if (commonTarget != AttributeState.PLAINTEXT) {
                if (areCompatibles(commonTarget, constrainedHomogStates)) {
                    //assign encryption taking care of no double encryption
                    for (Attribute na : newAttrSet
                            ) {
                        for (Attribute nb : lopes
                                ) {
                            if (na.equals(nb))
                                if (na.getState() == AttributeState.PLAINTEXT || na.getState() == commonTarget)
                                    na.setState(commonTarget);
                        }
                    }
                } else
                    return res;
            } else {
                //verify homogeneity of constrained homog. states and assign it
                if (isHomogeneous(constrainedHomogStates)) {
                    //retrieve or create a target
                    if (constrainedHomogStates.size() == 0)
                        commonTarget = AttributeState.DETSYMENC;
                    else commonTarget = constrainedHomogStates.get(0);
                    //assign encryption taking care of no double encryption
                    for (Attribute na : newAttrSet
                            ) {
                        for (Attribute nb : lopes
                                ) {
                            if (na.equals(nb))
                                if (na.getState() == AttributeState.PLAINTEXT || na.getState() == commonTarget)
                                    na.setState(commonTarget);
                        }
                    }
                } else
                    return res;
            }
        }

        /**
         * 5) At this point of computation newAttr is a valid set of attributes for the execution of tn's operation
         * on provider end, so the compliant list of moves to transform data has to be extracted
         */

        //generate encryption moves
        AttributeConstraint tac = new AttributeConstraint();
        List<AttributeConstraint> ltac = new LinkedList<>();

        for (Attribute ta : newAttrSet
                ) {
            for (Attribute tb : inla
                    ) {
                if (ta.equals(tb)) {
                    if (ta.getState() != tb.getState()) {
                        //retrieve old data and new target state (encryption operation will enforce that attribute)
                        tac = new AttributeConstraint(tb.copyAttribute(), ta.getState());
                        ltac.add(tac);
                    }
                }
            }
        }

        //building results
        //at the moment only one transformation inserted
        Pair<List<AttributeConstraint>, Provider> pair = new Pair<>(ltac, end);
        res.add(pair);

        return res;
    }

    private boolean areCompatibles(AttributeState constraint, List<AttributeState> lofconstr) {
        boolean f = true;
        for (AttributeState stateL : lofconstr
                ) {
            if (constraint != stateL)
                return false;
        }
        return f;
    }

    private boolean isHomogeneous(List<AttributeState> lofconstr) {
        boolean f = true;
        for (int i = 0; i < lofconstr.size() - 1; i++)
            if (!lofconstr.get(i).equals(lofconstr.get(i + 1)))
                return false;
        return f;
    }

}
