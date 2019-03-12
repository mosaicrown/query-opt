package Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.test;

import Operation_allocator.Data.Attribute;
import Operation_allocator.Data.AttributeConstraint;
import Operation_allocator.Data.AttributeState;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.MinimumRequiredView;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.util.LinkedList;
import java.util.List;

public class MinReqViewTest {

    public static void main(String[] args) {

        /**
         * In this class minimum required view is tested
         */
        RelationProfile rp = new RelationProfile();

        Attribute a1 = new Attribute("T1.A", 10);
        Attribute a2 = new Attribute("T1.B", 10);

        Attribute a3 = new Attribute("T2.C", 10);
        Attribute a4 = new Attribute("T2.D", 10);
        Attribute a5 = new Attribute("T2.E", 10);

        List<Attribute> l1 = new LinkedList<>();
        l1.add(a1);
        l1.add(a2);
        System.out.println(l1);

        List<Attribute> l2 = new LinkedList<>();
        l2.add(a3);

        rp.setRvp(l1);
        rp.setRve(l2);

        List<Attribute> z2 = new LinkedList<Attribute>();
        z2.add(a3);
        z2.add(a4);
        z2.add(a5);
        rp.setRie(z2);

        List<Attribute> z3 = new LinkedList<Attribute>();
        z3.add(a1);
        z3.add(a4);
        rp.setRip(z3);

        System.out.println("RelationProfile:\t"+rp.toString());

        List<AttributeConstraint> constraints=new LinkedList<>();
        constraints.add(new AttributeConstraint(a2, AttributeState.PLAINTEXT));

        RelationProfile mrv = MinimumRequiredView.computeMinimumReqView(rp, constraints);
        System.out.println("MinimumRequiredView:\t"+mrv.toString());
    }
}
