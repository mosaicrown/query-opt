package Trees.Semantics.Policy.RelationalProfilePolicy.test;

import Data.Attribute;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.util.LinkedList;
import java.util.List;

public class RelProfTests {
    public static void main(String[] args) {
        /**
         * Attribute creation
         */
        Attribute a1 = new Attribute("T1.A", 10);
        Attribute a2 = new Attribute("T1.B", 10);

        Attribute a3 = new Attribute("T2.C", 10);
        Attribute a4 = new Attribute("T2.D", 10);
        Attribute a5 = new Attribute("T2.E", 10);

        Attribute a6 = new Attribute("T3.F", 10);
        Attribute a7 = new Attribute("T3.G", 10);
        Attribute a8 = new Attribute("T3.H", 10);
        Attribute a9 = new Attribute("T3.I", 10);
        /**
         * Basic relation profile
         */
        RelationProfile rpp = new RelationProfile();
        System.out.println(rpp);
        /**
         * Some static methods
         */
        List<Attribute> l1 = new LinkedList<>();
        l1.add(a1);
        l1.add(a2);
        System.out.println(l1);
        List<Attribute> l2 = new LinkedList<>();
        l2.add(a3);
        l2 = RelationProfile.union(l1, l2);
        System.out.println(l1);
        l2 = RelationProfile.intersection(l2, l1);
        System.out.println(l2);
        l2 = RelationProfile.subtraction(l2, l1);
        System.out.println(l2);
        /**
         * Insertion, deletion
         */
        RelationProfile.insertAttribute(l1, a4);
        System.out.println(l1);
        RelationProfile.removeAttribute(l1, a5);
        System.out.println(l1);
        RelationProfile.removeAttribute(l1, a4);
        System.out.println(l1);
        /**
         * Some relation profile operations
         */
        RelationProfile rp1 = new RelationProfile();
        RelationProfile rp2 = new RelationProfile();
        RelationProfile rp3 = new RelationProfile();

        List<Attribute> z1 = new LinkedList<Attribute>();
        z1.add(a1);
        z1.add(a2);
        rp1.setRip(z1);

        List<Attribute> z2 = new LinkedList<Attribute>();
        z2.add(a3);
        z2.add(a4);
        z2.add(a5);
        rp2.setRie(z2);

        List<Attribute> z3 = new LinkedList<Attribute>();
        z3.add(a6);
        z3.add(a7);
        z3.add(a8);
        rp3.setRvp(z3);

        System.out.println(rp1);
        System.out.println(rp2);
        System.out.println(rp3);

        rp1.setRip(RelationProfile.union(rp1.getRip(), rp2.getRie()));
        System.out.println(rp1);
        rp1.setRip(RelationProfile.intersection(rp1.getRip(), rp2.getRie()));
        System.out.println(rp1);
        /**
         * Some operation with the closure of equivalence set
         */
        List<List<Attribute>> ll = new LinkedList<>();
        ll.add(z1);
        ll.add(z2);
        rp1.setCes(ll);
        System.out.println(rp1);

        rp1.setCes(RelationProfile.unionCE(rp1.getCes(), z3));
        System.out.println(rp1);

        rp1.setCes(RelationProfile.unionCE(rp1.getCes(), z2));
        System.out.println(rp1);

        List<List<Attribute>> lt = new LinkedList<>();
        lt.add(z3);
        rp2.setCes(lt);

        List<List<Attribute>> lz = new LinkedList<>();
        List x1 = new LinkedList<Attribute>();
        x1.add(a1);
        lz.add(x1);
        List x2 = new LinkedList<Attribute>();
        x2.add(a1);
        x2.add(a4);
        lz.add(x1);
        lz.add(x2);
        rp3.setCes(lz);

        System.out.println(rp2.getCes());
        System.out.println(rp3.getCes());

        rp2.setCes(RelationProfile.unionCEsets(rp2.getCes(), rp3.getCes()));
        rp2.setCes(RelationProfile.unionCEsets(rp2.getCes(), lt));
        System.out.println(rp2.getCes());

    }
}
