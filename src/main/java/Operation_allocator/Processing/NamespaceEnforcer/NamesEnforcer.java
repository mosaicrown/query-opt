package Operation_allocator.Processing.NamespaceEnforcer;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Actors.Operations.GroupBy;
import Operation_allocator.Actors.Operations.Projection;
import Operation_allocator.Actors.Operations.SetOperations.Intersection;
import Operation_allocator.Actors.Operations.SetOperations.Subtraction;
import Operation_allocator.Actors.Operations.SetOperations.Union;
import Operation_allocator.Actors.Operations.UDF;
import Operation_allocator.Actors.Provider;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Data.AttributeConstraint;
import Operation_allocator.Misc.Pair;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;
import Operation_allocator.Trees.TreeNode;

import java.util.LinkedList;
import java.util.List;

public final class NamesEnforcer {

    //query to be verified
    private TreeNode<Operation> query = null;
    //database schema attributes
    private List<Attribute> schema = null;
    //bindings
    private List<Pair<Attribute, Attribute>> bindings = null;
    //incremental new schema
    private List<Attribute> newSet = null;
    //providers
    private Provider p1 = null;
    private Provider p2 = null;
    private Provider p3 = null;
    //unique names enforcers
    static int sticker = 0;

    public NamesEnforcer() {
        schema = new LinkedList<>();
        bindings = new LinkedList<>();
    }

    public void debugInfo() {
        System.out.println("\tSchema:");
        System.out.print("\t[  ");
        for (Attribute a : schema
                ) {
            System.out.print(a + "  ");
        }
        System.out.println("]\n");
    }

    /**
     * Resolve name conflicts applying aliases, keeps trace of all the bindings
     */
    public void resolveConflicts() {
        if (query == null || schema.size() == 0 || p1 == null || p2 == null || p3 == null)
            throw new RuntimeException("Wrong names enforcer configuration");
        newSet = new LinkedList<>();

        performResolution(query);
        correctProviderSets();
        correctSchema();

        /**
         * DEBUG INFO
         */
        System.out.println("\n\tBINDINGS");
        System.out.print("\t[  ");
        for (Pair<Attribute, Attribute> p : bindings
                ) {
            System.out.print(p.getFirst().getName() + "->" + p.getSecond().getName() + "  ");
        }
        System.out.println("]\n");

        //debugInfo();

        /*
        System.out.println("Newset:");
        System.out.print("\t[  ");
        for (Attribute a : newSet
                ) {
            System.out.print(a + "  ");
        }
        System.out.println("]\n");
        */
        /*
        System.out.print("\tP1 \n\t");
        System.out.println("Plaintext set: \t[ "+stampaL(p1.getAplains()) + " ]");
        System.out.println("\tEncrypted set: \t[ "+stampaL(p1.getAencs()) + " ]\n");
        System.out.print("\tP2 \n\t");
        System.out.println("Plaintext set: \t[ "+stampaL(p2.getAplains()) + " ]");
        System.out.println("\tEncrypted set: \t[ "+stampaL(p2.getAencs()) + " ]\n");
        System.out.print("\tP3 \n\t");
        System.out.println("Plaintext set: \t[ "+stampaL(p3.getAplains()) + " ]");
        System.out.println("\tEncrypted set: \t[ "+stampaL(p3.getAencs()) + " ]\n");
        */
    }

    private void correctSchema() {
        for (Pair<Attribute, Attribute> p : bindings
                ) {
            for (Attribute a : schema
                    ) {
                if (a.equals(p.getFirst())) {
                    a.setName(p.getSecond().getName());
                    break;
                }
            }
        }
    }

    private void correctProviderSets() {
        correctProviderSet(p1.getAplains());
        correctProviderSet(p1.getAencs());
        correctProviderSet(p2.getAplains());
        correctProviderSet(p2.getAencs());
        correctProviderSet(p3.getAplains());
        correctProviderSet(p3.getAencs());
    }

    private void correctProviderSet(List<Attribute> ll) {
        for (Pair<Attribute, Attribute> p : bindings
                ) {
            for (Attribute a : ll
                    ) {
                if (a.equals(p.getFirst())) {
                    a.setName(p.getSecond().getName());
                    break;
                }
            }
        }
    }

    public <T extends Operation> List<Pair<Attribute, Attribute>> performResolution(TreeNode<T> q) {
        List<Pair<Attribute, Attribute>> nextTempBind = new LinkedList<>();
        //generate bindings for leaves
        if (q.isLeaf()) {
            Operation qop = q.getElement();
            List<Attribute> input = qop.getInputAttributes();
            List<Pair<Attribute, Attribute>> tempBind = new LinkedList<>();
            int i = 0;
            for (Attribute z : input
                    ) {
                do {
                    sticker++;
                }
                while (RelationProfile.hasAttribute(newSet, new Attribute(z.getName() + sticker, z.getDimension())));
                Attribute n = new Attribute(z.getName() + sticker, z.getDimension());
                RelationProfile.insertAttribute(newSet, n);
                Pair<Attribute, Attribute> sbin = new Pair<>(z.copyAttribute(), n);
                z.setName(n.getName());
                tempBind.add(sbin);
                bindings.add(sbin);
                i++;
            }
            apply(q, tempBind);

        }
        List<Pair<Attribute, Attribute>> tempBind = null;
        for (TreeNode<T> s : q.getSons()
                ) {
            //jump to son
            tempBind = performResolution(s);
            //add elements in new temp list
            for (Pair<Attribute, Attribute> p : tempBind
                    ) {
                boolean f = false;
                for (Pair<Attribute, Attribute> h : nextTempBind
                        ) {
                    if (h.getSecond().getName().equals(p.getSecond().getName()))
                        f = true;
                }
                if (!f)
                    nextTempBind.add(p);
            }
            apply(q, tempBind);
        }
        //build the next temp list
        List<Attribute> collected = new LinkedList<>();
        T op = q.getElement();
        if (!q.isLeaf()) {
            for (Pair<Attribute, Attribute> pa : nextTempBind
                    ) {
                collected.add(pa.getSecond().copyAttribute());
            }
        } else {
            for (Attribute a : op.getInputAttributes()
                    ) {
                collected.add(a.copyAttribute());
            }
        }
        List<Attribute> survivingAttrs = null;
        if (op instanceof GroupBy || op instanceof Projection || op instanceof UDF || op instanceof Union
                || op instanceof Intersection || op instanceof Subtraction) {
            survivingAttrs = RelationProfile.intersection(collected, op.getProjectedSet());
        } else
            survivingAttrs = collected;
        nextTempBind = new LinkedList<>();
        for (Attribute a : survivingAttrs
                ) {
            for (Pair<Attribute, Attribute> p : bindings
                    ) {
                if (p.getSecond().getName().equals(a.getName())) {
                    nextTempBind.add(new Pair<>(p.getFirst().copyAttribute(), a));
                    break;
                }
            }
        }

        return nextTempBind;
    }

    public String stampaL(List l) {
        String s = "";
        for (Object i : l
                ) {
            s += " " + i.toString() + " ";
        }
        return s;
    }

    private <T extends Operation> void apply(TreeNode<T> q, List<Pair<Attribute, Attribute>> tempBind) {
        //now try to apply all temporary bindings

        for (Pair<Attribute, Attribute> tbind : tempBind
                ) {
            Attribute attr = tbind.getFirst();
            Attribute alias = tbind.getSecond();

            //repair homogeneous set
            List<Attribute> clonedHomog = RelationProfile.copyLoA(q.getElement().getHomogeneousSet());
            for (Attribute a : clonedHomog
                    ) {
                if (a.equals(attr)) {
                    a.setName(alias.getName());
                    break;
                }
            }
            q.getElement().setHomogeneousSet(clonedHomog);


            //repair projected set
            for (Attribute a : q.getElement().getProjectedSet()
                    ) {
                if (a.equals(attr)) {
                    a.setName(alias.getName());
                    break;
                }
            }


            //repair constraints
            for (AttributeConstraint ac : q.getElement().getConstraints()
                    ) {
                Attribute a = ac.getAttr();
                if (a.equals(attr)) {
                    a.setName(alias.getName());
                    break;
                }
            }


        }
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

    public TreeNode<Operation> getQuery() {
        return query;
    }

    public void setQuery(TreeNode<Operation> query) {
        this.query = query;
    }

    public List<Attribute> getSchema() {
        return schema;
    }

    public void setSchema(List<Attribute> schema) {
        this.schema = schema;
    }

    public List<Pair<Attribute, Attribute>> getBindings() {
        return bindings;
    }

    public void setBindings(List<Pair<Attribute, Attribute>> bindings) {
        this.bindings = bindings;
    }
}
