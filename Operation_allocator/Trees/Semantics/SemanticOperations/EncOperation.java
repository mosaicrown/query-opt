package Trees.Semantics.SemanticOperations;

import Actors.Provider;
import Data.Attribute;
import Data.AttributeConstraint;
import Misc.Triple;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class EncOperation implements Serializable {

    /**
     * At the end of the computation of encryption/decryption this will collect all attributes after alteration
     */
    private List<Attribute> A;
    /**
     * This field contains the additional cost(in dollars) due to the application of encryption
     * It means that it synthesize the additional cost of motion and encryption
     */
    protected CostMetric deltaMetric;

    /**
     * This field collects (ONLY) new input stats to describe transformed attributes
     */
    BasicMetric inputBasicMetric;

    public EncOperation() {
        A = new LinkedList<>();
        deltaMetric = new CostMetric();
        deltaMetric.setAllZero();
        inputBasicMetric = new BasicMetric();
    }

    public String toString() {
        String s = " | ";
        for (Attribute a : A
                ) {
            s += "n: " + a.toString() + " s:" + a.getState() + "| ";
        }
        return s;
    }

    public CostMetric getDeltaMetric() {
        return deltaMetric;
    }

    public void setDeltaMetric(CostMetric deltaMetric) {
        this.deltaMetric = deltaMetric;
    }

    public List<Attribute> getA() {
        return A;
    }

    public void setA(List<Attribute> a) {
        A = a;
    }

    public BasicMetric getInputBasicMetric() {
        return inputBasicMetric;
    }

    public void setInputBasicMetric(BasicMetric inputBasicMetric) {
        this.inputBasicMetric = inputBasicMetric;
    }

    public void performOperation(
            List<Triple<RelationProfile, BasicMetric, Provider>> sonTable, List<AttributeConstraint> attrConstr, Provider end
    ) {
        Triple<CostMetric, BasicMetric, List<Attribute>> t = Encryption.applyEncryption(sonTable, attrConstr, end);
        A = t.getThird();
        inputBasicMetric = t.getSecond();
        deltaMetric = t.getFirst();

    }


}
