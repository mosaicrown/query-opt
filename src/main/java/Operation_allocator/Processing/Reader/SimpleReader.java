package Operation_allocator.Processing.Reader;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Actors.Operations.*;
import Operation_allocator.Actors.Operations.SetOperations.Intersection;
import Operation_allocator.Actors.Operations.SetOperations.Subtraction;
import Operation_allocator.Actors.Operations.SetOperations.Union;
import Operation_allocator.Actors.Provider;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Data.AttributeConstraint;
import Operation_allocator.Data.AttributeState;
import Operation_allocator.Misc.Pair;
import Operation_allocator.Statistics.Metrics.BasicMetric;
import Operation_allocator.Statistics.Metrics.CostMetric;
import Operation_allocator.Statistics.Metrics.ProviderMetric;
import Operation_allocator.Statistics.Metrics.UDFMetric;
import Operation_allocator.Statistics.UDFprofilers.*;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;
import Operation_allocator.Trees.TreeNode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public final class SimpleReader {

    //input paths
    private String path_schema = "DB.xml";
    private String path_authorization = "ProviderSet.xml";
    private String path_config_dir = "../ConfigFiles/";
    private String path_input_dir = "../InputData/";
    private String path_query = null;
    //actual query tree
    private TreeNode<Operation> q = null;
    //XML helper
    private SAXBuilder saxBuilder = null;

    public SimpleReader(String p1, String p2, String p3) {
        path_query = p1;
        path_schema = p2;
        path_authorization = p3;

        saxBuilder = new SAXBuilder();
        fileChecker();
    }

    public SimpleReader(String p) {
        path_query = p;
        saxBuilder = new SAXBuilder();
        fileChecker();
    }

    private void fileChecker() {
        boolean f = true;
        f = f && new File(path_config_dir + path_schema).isFile();
        f = f && new File(path_config_dir + path_authorization).isFile();
        f = f && new File(path_input_dir + path_query).isFile();

        if (!f)
            throw new RuntimeException("Some input files are missing");
        /*
        System.out.println("Query:\t" + path_input_dir + path_query);
        System.out.println("Schema:\t" + path_config_dir + path_schema);
        System.out.println("Auth:\t" + path_config_dir + path_authorization);*/
    }

    public Pair<List<Attribute>, List<Provider>> loadConfiguration() {
        Pair<List<Attribute>, List<Provider>> res = new Pair<>();
        res.setFirst(new LinkedList<>());
        res.setSecond(new LinkedList<>());
        /**
         * XML data fetch
         */
        //read database schema
        readDB(saxBuilder, res.getFirst());
        //read provider set
        readProviderSet(saxBuilder, res.getSecond(), res.getFirst());

        return res;
    }

    public TreeNode loadQuery(List<Attribute> attributes, List<Provider> providers) {
        TreeNode<Operation> q = null;
        //read query
        q = readQuery(saxBuilder, attributes, providers);
        return q;
    }

    private void readProviderSet(SAXBuilder saxBuilder, List<Provider> providers, List<Attribute> attributes) throws RuntimeException {
        File inputFile = new File(path_config_dir + path_authorization);
        Document document;
        int idx = -1;
        try {
            document = saxBuilder.build(inputFile);
            Element dbSchema = document.getDocument().getRootElement();
            if (dbSchema != null) {
                //list of providers
                List<org.jdom2.Element> el = dbSchema.getChildren();

                //foreach provider
                for (Element e : el
                        ) {
                    Element ename = null;
                    ename = e.getChild("name");
                    if (ename == null)
                        throw new RuntimeException("Missing provider name");
                    //provider name
                    String pname = ename.getValue();

                    if (e.getChild("providermetrics") == null)
                        throw new RuntimeException("Incomplete provider metrics");
                    if (e.getChild("providermetrics").getChildren() == null)
                        throw new RuntimeException("Incomplete provider metrics");
                    if (e.getChild("providermetrics").getChildren().size() < 7)
                        throw new RuntimeException("Incomplete provider metrics");

                    //retrieve metrics
                    e = e.getChild("providermetrics");
                    if (e.getChild("km") == null ||
                            e.getChild("kcpu") == null ||
                            e.getChild("kio") == null ||
                            e.getChild("krse") == null ||
                            e.getChild("kdse") == null ||
                            e.getChild("kpal") == null ||
                            e.getChild("kope") == null)
                        throw new RuntimeException("Incomplete provider metrics");

                    double km, kcpu, kio, krse, kdse, kpal, kope;
                    km = Double.parseDouble(e.getChild("km").getValue());
                    kcpu = Double.parseDouble(e.getChild("kcpu").getValue());
                    kio = Double.parseDouble(e.getChild("kio").getValue());
                    krse = Double.parseDouble(e.getChild("krse").getValue());
                    kdse = Double.parseDouble(e.getChild("kdse").getValue());
                    kpal = Double.parseDouble(e.getChild("kpal").getValue());
                    kope = Double.parseDouble(e.getChild("kope").getValue());

                    if (km < 0 || kcpu < 0 || kio < 0 || krse < 0 || kdse < 0 || kpal < 0 || kope < 0)
                        throw new RuntimeException("Inconsistent provider metrics");

                    //insert new provider
                    providers.add(new Provider(pname, new ProviderMetric(km, kcpu, kio, krse, kdse, kpal, kope)));

                    //read the attribute set
                    providers.get(providers.size() - 1).setAplains(new LinkedList<Attribute>());
                    if (e.getParentElement().getChild("plaintextset") == null)
                        throw new RuntimeException("Incomplete provider policy");
                    List<Attribute> plset = new LinkedList<>();
                    if (e.getParentElement().getChild("plaintextset").getChildren() != null)
                        if (e.getParentElement().getChild("plaintextset").getChildren().size() > 0) {
                            for (Element tel : e.getParentElement().getChild("plaintextset").getChildren()
                                    ) {
                                String noun = tel.getValue();
                                boolean found = false;
                                for (Attribute aa : attributes
                                        ) {
                                    if (aa.getName().equals(noun)) {
                                        RelationProfile.insertAttribute(plset, aa);
                                        found = true;
                                    }
                                }
                                if (!found)
                                    throw new RuntimeException("Attribute " + noun + " does not exists");
                            }
                            providers.get(providers.size() - 1).setAplains(plset);
                        }

                    providers.get(providers.size() - 1).setAencs(new LinkedList<Attribute>());
                    if (e.getParentElement().getChild("encryptedset") == null)
                        throw new RuntimeException("Incomplete provider policy");
                    List<Attribute> encset = new LinkedList<>();
                    if (e.getParentElement().getChild("encryptedset").getChildren() != null)
                        if (e.getParentElement().getChild("encryptedset").getChildren().size() > 0) {
                            for (Element tel : e.getParentElement().getChild("encryptedset").getChildren()
                                    ) {
                                String noun = tel.getValue();
                                boolean found = false;
                                for (Attribute aa : attributes
                                        ) {
                                    if (aa.getName().equals(noun)) {
                                        RelationProfile.insertAttribute(encset, aa);
                                        found = true;
                                    }
                                }
                                if (!found)
                                    throw new RuntimeException("Attribute " + noun + " does not exists");
                            }
                            providers.get(providers.size() - 1).setAencs(encset);
                        }

                    if (e.getParentElement().getChild("authority") != null)
                        if (e.getParentElement().getChild("authority").getValue().equals("yes")) {
                            if (idx != -1)
                                throw new RuntimeException("More than one data authority");
                            idx = providers.size() - 1;
                        }

                }
            } else
                throw new RuntimeException("Incomplete provider metrics");
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //some other checks
        doProviderChecks(providers, idx, attributes);
    }

    private void doProviderChecks(List<Provider> providers, int idx, List<Attribute> attributes) throws RuntimeException {
        if (idx == -1)
            throw new RuntimeException("Missing data authority");
        //lack of providers
        if (providers.size() != 3)
            throw new RuntimeException("Provider alternatives must be 3!");
        //provider ordering, element at index 0 must be the data authority(it's only a fool assumption)
        if (idx != 0) {
            Provider temp = providers.get(0);
            providers.set(0, providers.get(idx));
            providers.set(idx, temp);
        }
        //ordering
        if (providers.get(1).getMetrics().Kcpu < providers.get(2).getMetrics().Kcpu) {
            Provider temp = providers.get(1);
            providers.set(1, providers.get(2));
            providers.set(2, temp);
        }
        //authority authorizations correction
        providers.get(0).setAplains(attributes);
        providers.get(0).setAencs(new LinkedList<>());
        //now check for completeness and conflicts
        List<Attribute> intersection = RelationProfile.intersection(providers.get(1).getAencs(), providers.get(1).getAplains());
        if (intersection.size() > 0)
            throw new RuntimeException("Multiple authorization to provider 2");
        intersection = RelationProfile.intersection(providers.get(2).getAencs(), providers.get(2).getAplains());
        if (intersection.size() > 0)
            throw new RuntimeException("Multiple authorization to provider 3");

    }

    private void readDB(SAXBuilder saxBuilder, List<Attribute> attributes) throws RuntimeException {
        File inputFile = new File(path_config_dir + path_schema);
        Document document;
        try {
            document = saxBuilder.build(inputFile);
            Element dbSchema = document.getDocument().getRootElement();
            if (dbSchema != null) {
                List<org.jdom2.Element> el = dbSchema.getChildren();
                if (el == null)
                    throw new RuntimeException("Missing attribute properties");
                List<org.jdom2.Element> ec;
                String aname = null;
                double asize = 0;
                for (Element e : el
                        ) {
                    ec = e.getChildren();
                    if (ec == null)
                        throw new RuntimeException("Missing attribute properties");
                    if (ec.size() < 2)
                        throw new RuntimeException("Missing attribute properties");
                    if (ec.get(0) == null || ec.get(1) == null ||
                            !ec.get(0).getName().equals("name") || !ec.get(1).getName().equals("size"))
                        throw new RuntimeException("Missing attribute properties");
                    //read data
                    aname = ec.get(0).getValue();
                    asize = Double.parseDouble(ec.get(1).getValue());
                    if (asize < 0)
                        throw new RuntimeException("Inconsistent attribute properties");
                    //insert db column inside attribute list
                    RelationProfile.insertAttribute(attributes, new Attribute(aname, asize));
                }
            } else
                throw new RuntimeException("Incomplete attribute metrics");
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TreeNode<Operation> readQuery(SAXBuilder saxBuilder, List<Attribute> attributes, List<Provider> providers)
            throws RuntimeException {

        TreeNode<Operation> q = null;
        File inputFile = new File(path_input_dir + path_query);
        Document document;
        try {
            document = saxBuilder.build(inputFile);
            Element qroot = document.getDocument().getRootElement();
            if (qroot != null) {
                q = readOperation(attributes, providers, qroot);
            } else
                throw new RuntimeException("Empty query");
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return q;
    }

    private TreeNode<Operation> readOperation(List<Attribute> attributes, List<Provider> providers, Element o) {
        TreeNode<Operation> q = null;
        Operation op = null;
        List<TreeNode<Operation>> s = new LinkedList<>();

        String optype, opname;
        optype = "";
        opname = "";

        //--->>>recursion, instantiate sons before father
        if (o.getChild("sonoperations") != null)
            s = readSons(attributes, providers, o.getChild("sonoperations"));
        q = new TreeNode<>();
        q.setSons(s);

        //--->>>get back, read operation

        //read the type
        if (o.getChild("type") != null) {
            optype = o.getChild("type").getValue();
            optype = optype.toLowerCase();
        } else
            throw new RuntimeException("No type for operation, wrong xml description");
        //read the name
        if (o.getChild("name") != null) {
            opname = o.getChild("name").getValue();
            opname = opname.toLowerCase();
        }

        //instantiate operation
        op = instantiateOperation(optype, opname);
        q.setElement(op);

        //set input attributes
        List<Attribute> attr = new LinkedList<>();
        op.setInputAttributes(attr);
        boolean error = true;
        if (o.getChild("inputattributes") != null) {
            if (o.getChild("inputattributes").getChildren("name") != null) {
                if (o.getChild("inputattributes").getChildren("name").size() > 0) {
                    error = false;
                    op.setInputAttributes(getInputAttribute(attributes, o.getChild("inputattributes").getChildren("name")));
                }
            }
        }
        if (error && q.isLeaf())
            throw new RuntimeException("Leaf operation with no input attributes");

        //setting up constraints
        List<AttributeConstraint> ac = new LinkedList<>();
        if (o.getChild("attributeconstrains") != null)
            if (o.getChild("attributeconstrains").getChildren("constraint") != null)
                if (o.getChild("attributeconstrains").getChildren("constraint").size() > 0) {
                    ac = getAttributeConstraints(attributes, o.getChild("attributeconstrains").getChildren("constraint"));
                }
        op.setConstraints(ac);

        //set executor
        op.setExecutor(providers.get(0));

        //set cost metric
        CostMetric cm = new CostMetric();
        cm.setAllZero();
        op.setCost(cm);

        //setting up projected and homogeneous set
        //homog
        getHomogeneousSet(attributes, o, op);
        //proj
        getProjectedSet(attributes, o, op);

        //read metrics
        getMetrics(o, op);

        return q;
    }

    private List<TreeNode<Operation>> readSons(List<Attribute> attributes, List<Provider> providers, Element sl) {
        List<TreeNode<Operation>> s = new LinkedList<>();
        TreeNode<Operation> op = null;
        if (sl.getChildren("operation") != null)
            for (Element e : sl.getChildren("operation")
                    ) {
                //recursion, read operation
                op = readOperation(attributes, providers, e);
                //add operation to list
                s.add(op);
            }
        return s;
    }

    private Operation instantiateOperation(String optype, String opname) {
        Operation op = null;
        String classname = "Operation_allocator.Actors.Operations.";
        Class<?> clazz = null;
        Constructor<?> constr = null;

        switch (optype) {
            case "cartesianproduct":
                classname += "CartesianProduct";
                break;
            case "groupby":
                classname += "GroupBy";
                break;
            case "joinnary":
                classname += "JoinNAry";
                break;
            case "projection":
                classname += "Projection";
                break;
            case "selection":
                classname += "Selection";
                break;
            case "udf":
                classname += "UDF";
                break;
            case "intersection":
                classname += "SetOperations.Intersection";
                break;
            case "Subtraction":
                classname += "SetOperations.Subtraction";
                break;
            case "Union":
                classname += "SetOperations.Union";
                break;
            default:
                throw new RuntimeException("Invalid type of operation: " + optype);
        }
        try {
            clazz = Class.forName(classname);
            constr = clazz.getConstructor();
            op = (Operation) constr.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("Java system can not instantiate");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Java system can not invoke");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Java system can not find method");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Java system illegal access");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Java system can not find class");
        }
        op.setName(opname);
        return op;
    }

    private <T extends Operation> void getMetrics(Element o, T op) {
        String name = null;
        boolean error = true;

        if (o.getChild("basicmetrics") != null)
            if (o.getChild("basicmetrics").getChildren() != null)
                if (o.getChild("basicmetrics").getChildren().size() == 6) {
                    error = false;
                    double m1, m2, m3, m4, m5, m6;
                    m1 = m2 = m3 = m4 = m5 = m6 = 0;
                    if (o.getChild("basicmetrics").getChild("inputsize") != null)
                        m1 = Double.parseDouble(o.getChild("basicmetrics").getChild("inputsize").getValue());

                    if (o.getChild("basicmetrics").getChild("inputtuplesize") != null)
                        m2 = Double.parseDouble(o.getChild("basicmetrics").getChild("inputtuplesize").getValue());

                    if (o.getChild("basicmetrics").getChild("outputsize") != null)
                        m3 = Double.parseDouble(o.getChild("basicmetrics").getChild("outputsize").getValue());

                    if (o.getChild("basicmetrics").getChild("outputtuplesize") != null)
                        m4 = Double.parseDouble(o.getChild("basicmetrics").getChild("outputtuplesize").getValue());

                    if (o.getChild("basicmetrics").getChild("cputime") != null)
                        m5 = Double.parseDouble(o.getChild("basicmetrics").getChild("cputime").getValue());

                    if (o.getChild("basicmetrics").getChild("iotime") != null)
                        m6 = Double.parseDouble(o.getChild("basicmetrics").getChild("iotime").getValue());

                    BasicMetric bm = new BasicMetric(m1, m2, m3, m4, m5, m6);
                    if (!(op instanceof UDF)) {
                        op.setOp_metric(bm);
                    } else {
                        //retrieve udf metric
                        getUDFMetrics(o, op, bm);
                    }
                }
        if (error)
            throw new RuntimeException("Incomplete metrics");


    }

    @SuppressWarnings("unchecked")
    private <T extends Operation> void getUDFMetrics(Element o, T op, BasicMetric bm) {
        String profType = null;
        int numParams = 0;
        List<Double> params = new LinkedList<>();

        double cpi, ct, iops;
        cpi = ct = iops = 0;

        boolean error = true;

        if (o.getChild("udfmetrics") != null)
            if (o.getChild("udfmetrics").getChildren() != null)
                if (o.getChild("udfmetrics").getChildren().size() == 4) {

                    boolean block = true;

                    if (o.getChild("udfmetrics").getChild("profiler") != null)
                        if (o.getChild("udfmetrics").getChild("profiler").getChild("form") != null) {

                            profType = o.getChild("udfmetrics").getChild("profiler").getChild("form").getValue();
                            if (o.getChild("udfmetrics").getChild("profiler").getChildren("k") != null) {
                                block = false;
                                for (Element tem : o.getChild("udfmetrics").getChild("profiler").getChildren("k")
                                        ) {
                                    params.add(numParams, Double.parseDouble(tem.getValue()));
                                    numParams++;
                                }
                            }
                        }
                    if (block)
                        throw new RuntimeException("Wrong UDF profiler");

                    if (o.getChild("udfmetrics").getChild("cpi") != null) {
                        if (o.getChild("udfmetrics").getChild("ct") != null) {
                            if (o.getChild("udfmetrics").getChild("iops") != null) {
                                cpi = Double.parseDouble(o.getChild("udfmetrics").getChild("cpi").getValue());
                                ct = Double.parseDouble(o.getChild("udfmetrics").getChild("ct").getValue());
                                iops = Double.parseDouble(o.getChild("udfmetrics").getChild("iops").getValue());
                                if (cpi > 0 && ct > 0 && iops > 0)
                                    error = false;
                            }
                        }
                    }

                }
        if (error)
            throw new RuntimeException("Wrong UDF metrics");

        profType = profType.toLowerCase();
        Profiler prof = null;
        UDFMetric udm = null;

        switch (profType) {
            case "linear":
                prof = new LinearProfile(params);
                udm = new UDFMetric<LinearProfile>(cpi, ct, iops, bm.inputSize, bm.inputTupleSize, bm.outputSize, bm.outputTupleSize);
                break;
            case "pseudolinear":
                prof = new PseudolinearProfile(params);
                udm = new UDFMetric<PseudolinearProfile>(cpi, ct, iops, bm.inputSize, bm.inputTupleSize, bm.outputSize, bm.outputTupleSize);
                break;
            case "quadratic":
                prof = new QuadraticProfile(params);
                udm = new UDFMetric<QuadraticProfile>(cpi, ct, iops, bm.inputSize, bm.inputTupleSize, bm.outputSize, bm.outputTupleSize);
                break;
            case "cubic":
                prof = new CubicProfile(params);
                udm = new UDFMetric<CubicProfile>(cpi, ct, iops, bm.inputSize, bm.inputTupleSize, bm.outputSize, bm.outputTupleSize);
                break;
            case "polynomial":
                prof = new PolynomialProfile(params);
                udm = new UDFMetric<PolynomialProfile>(cpi, ct, iops, bm.inputSize, bm.inputTupleSize, bm.outputSize, bm.outputTupleSize);
                break;
            case "logpolynomial":
                prof = new LogPolynomialProfile(params);
                udm = new UDFMetric<LogPolynomialProfile>(cpi, ct, iops, bm.inputSize, bm.inputTupleSize, bm.outputSize, bm.outputTupleSize);
                break;
            default:
                throw new RuntimeException("Missing UDF profile");
        }
        //set profiler
        udm.setProfiler(prof);
        //now available the pointer, let's allocate metrics
        op.setOp_metric(udm);

    }

    private <T extends Operation> void getHomogeneousSet(List<Attribute> attributes, Element o, T op) {
        if (op instanceof CartesianProduct || op instanceof GroupBy || op instanceof JoinNAry
                || op instanceof Selection || op instanceof UDF|| op instanceof Union
                || op instanceof Intersection || op instanceof Subtraction) {

            String name = null;
            boolean error = true;
            List<Attribute> hset = new LinkedList<>();

            if (o.getChild("homogeneousset") != null)
                if (o.getChild("homogeneousset").getChildren("name") != null)
                    if (o.getChild("homogeneousset").getChildren("name").size() > 0) {
                        error = false;
                        for (Element e : o.getChild("homogeneousset").getChildren("name")
                                ) {
                            name = e.getValue();
                            for (Attribute a : attributes
                                    ) {
                                if (a.getName().equals(name)) {
                                    RelationProfile.insertAttribute(hset, a);
                                    break;
                                }
                            }
                        }
                    }
            if (error)
                throw new RuntimeException("Homogeneous attribute required for: " + op.getFamilyname()+" "+op.toString());
            op.setHomogeneousSet(hset);
        }
    }

    private <T extends Operation> void getProjectedSet(List<Attribute> attributes, Element o, T op) {
        if (op instanceof GroupBy || op instanceof Projection || op instanceof UDF || op instanceof Union
                || op instanceof Intersection || op instanceof Subtraction) {

            String name = null;
            boolean error = true;
            List<Attribute> pset = new LinkedList<>();

            if (o.getChild("projectedset") != null)
                if (o.getChild("projectedset").getChildren("name") != null)
                    if (o.getChild("projectedset").getChildren("name").size() > 0) {
                        error = false;
                        for (Element e : o.getChild("projectedset").getChildren("name")
                                ) {
                            name = e.getValue();
                            for (Attribute a : attributes
                                    ) {
                                if (a.getName().equals(name)) {
                                    RelationProfile.insertAttribute(pset, a);
                                    break;
                                }
                            }
                        }
                    }
            if (error)
                throw new RuntimeException("Projected attribute required for: " + op.getFamilyname());
            op.setProjectedSet(pset);
        }
    }

    private List<Attribute> getInputAttribute(List<Attribute> attributes, List<Element> o) {
        List<Attribute> ac = new LinkedList<>();
        String name;
        for (Element e : o
                ) {
            name = e.getValue();
            for (Attribute a : attributes
                    ) {
                if (a.getName().equals(name)) {
                    ac.add(a);
                    break;
                }
            }
        }
        return ac;
    }


    private List<AttributeConstraint> getAttributeConstraints(List<Attribute> attributes, List<Element> o) {
        List<AttributeConstraint> ac = new LinkedList<>();
        String name;
        AttributeState state;
        String temp = null;
        for (Element e : o
                ) {
            if (e.getChild("attribute") != null)
                name = e.getChild("attribute").getValue();
            else
                throw new RuntimeException("Attribute with no name");
            if (e.getChild("state") != null)
                temp = e.getChild("state").getValue();
            temp = temp.toLowerCase();
            switch (temp) {
                case "plaintext":
                    state = AttributeState.PLAINTEXT;
                    break;
                case "randsymenc":
                    state = AttributeState.RANDSYMENC;
                    break;
                case "detsymenc":
                    state = AttributeState.DETSYMENC;
                    break;
                case "palcryptenc":
                    state = AttributeState.PALCRYPTENC;
                    break;
                case "opeschenc":
                    state = AttributeState.OPESCHENC;
                    break;
                default:
                    throw new RuntimeException("Wrong state");
            }
            for (Attribute at : attributes
                    ) {
                if (at.getName().equals(name)) {
                    ac.add(new AttributeConstraint(at, state));
                    break;
                }
            }
        }

        return ac;
    }


}
