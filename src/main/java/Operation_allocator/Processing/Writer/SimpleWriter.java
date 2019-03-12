package Operation_allocator.Processing.Writer;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Trees.TreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public final class SimpleWriter {

    private String path_res = "ResultShort.xml";
    private String path_res_extended = "ResultExtended.xml";
    private String path_dir = "../OutputData/";
    private String customd = "";

    private TreeNode<Operation> query = null;

    private Document doc = null;

    private Boolean extMode = false;

    public SimpleWriter() {

    }

    public void writeResultsExtended() {
        extMode = true;
        writeResults();
        extMode = false;
    }

    public void writeResults() {
        validateConfiguration();
        //new document builder
        try {
            //document creation helper
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException("Wrong parser configuration (writer)");
        }
        //root creation
        Element rootElement = doc.createElement("operation");
        doc.appendChild(rootElement);
        //root content
        Element sons_el = writeOperationContent(rootElement, query);
        if (!query.isLeaf())
            for (TreeNode<Operation> son : query.getSons()
            ) {
                writeOperation(sons_el, son);
            }
        //write the content to filesystem
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = null;
            if (!customd.equals("")) {
                path_res = "ResultShort_" + customd;
                path_res_extended = "ResultExtended_" + customd;
            }
            if (!extMode)
                result = new StreamResult(new File(path_dir + path_res));
            else
                result = new StreamResult(new File(path_dir + path_res_extended));
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new RuntimeException("Transformer wrong configuration");
        }

    }

    /**
     * Retrieve each operation and launch DOM parser
     *
     * @param node_op root of tree to be written
     * @param tn
     */
    private void writeOperation(Element node_op, TreeNode<Operation> tn) {
        Element op_el = doc.createElement("operation");
        node_op.appendChild(op_el);
        Element sons = writeOperationContent(op_el, tn);
        if (!tn.isLeaf())
            for (TreeNode<Operation> son : tn.getSons()
            ) {
                writeOperation(sons, son);
            }
    }

    /**
     * Write operation content
     *
     * @param node_op
     * @param tn
     * @return
     */
    private Element writeOperationContent(Element node_op, TreeNode<Operation> tn) {
        Operation operation = tn.getElement();
        Element sons = null;

        //setting up type
        Element type = doc.createElement("type");
        type.appendChild(doc.createTextNode(operation.getFamilyname()));
        //setting up name
        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode(operation.toString()));
        //setting up executor
        Element executor = doc.createElement("executor");
        executor.appendChild(doc.createTextNode(operation.getExecutor().selfDescription()));
        //setting up cost
        Element cost = doc.createElement("cost");
        cost.appendChild(doc.createTextNode(Double.toString(operation.getCost().Ct)));

        sons = doc.createElement("sonoperations");

        //hook information
        node_op.appendChild(type);
        node_op.appendChild(name);
        node_op.appendChild(executor);
        node_op.appendChild(cost);
        if (extMode)
            writeOperationContentExtended(node_op, tn);
        node_op.appendChild(sons);

        return sons;
    }

    private void writeOperationContentExtended(Element node_op, TreeNode<Operation> tn) {
        Operation operation = tn.getElement();
        //setting up output relation profile
        Element rel_prof = doc.createElement("outRelProf");
        //building output relation profile content
        Element attr = null;
        //visible plaintext
        Element rvp = doc.createElement("rvp");
        for (Attribute a : operation.getOutput_rp().getRvp()
        ) {
            attr = doc.createElement("attribute");
            attr.appendChild(doc.createTextNode(a.getName()));
            rvp.appendChild(attr);

        }
        //visible encrypted
        Element rve = doc.createElement("rve");
        for (Attribute a : operation.getOutput_rp().getRve()
        ) {
            attr = doc.createElement("attribute");
            attr.appendChild(doc.createTextNode(a.getName()));
            rve.appendChild(attr);

        }
        //implicit plaintext
        Element rip = doc.createElement("rip");
        for (Attribute a : operation.getOutput_rp().getRip()
        ) {
            attr = doc.createElement("attribute");
            attr.appendChild(doc.createTextNode(a.getName()));
            rip.appendChild(attr);

        }
        //implicit encrypted
        Element rie = doc.createElement("rie");
        for (Attribute a : operation.getOutput_rp().getRie()
        ) {
            attr = doc.createElement("attribute");
            attr.appendChild(doc.createTextNode(a.getName()));
            rie.appendChild(attr);

        }

        //ces
        Element ces = doc.createElement("ces");
        for (List<Attribute> la : operation.getOutput_rp().getCes()
        ) {
            Element set = doc.createElement("set");
            for (Attribute a : la
            ) {
                attr = doc.createElement("attribute");
                attr.appendChild(doc.createTextNode(a.getName()));
                set.appendChild(attr);

            }
            ces.appendChild(set);
        }

        //append relation profile sets
        rel_prof.appendChild(rvp);
        rel_prof.appendChild(rve);
        rel_prof.appendChild(rip);
        rel_prof.appendChild(rie);
        rel_prof.appendChild(ces);

        //append relation profile
        node_op.appendChild(rel_prof);

        //append encryption moves
        writeEncryptionMoves(node_op, tn);
    }

    private void writeEncryptionMoves(Element node_op, TreeNode<Operation> tn) {
        //setting up encryption moves
        Element enc_moves = doc.createElement("postEncryptionMoves");
        //building content
        Element move = null;
        Element attr = null;
        Element tState = null;
        Element aState = null;
        //retrieve operation's input attributes
        List<Attribute> inputAttrs = tn.getElement().getInputAttributes();
        //foreach attribute in encryption set
        boolean attrFound = false;
        for (Attribute a : tn.getEncryption().getA()
        ) {
            attrFound = false;
            move = doc.createElement("move");
            attr = doc.createElement("attribute");
            attr.appendChild(doc.createTextNode(a.getName()));
            //retrieve initial state
            for (Attribute ai : inputAttrs
            ) {
                if (a.equals(ai))
                    if (!a.getState().equals(ai.getState())) {
                        attrFound = true;
                        aState = doc.createElement("startState");
                        aState.appendChild(doc.createTextNode(ai.getState().toString()));
                        break;
                    }
            }
            if (attrFound) {
                tState = doc.createElement("targetState");
                tState.appendChild(doc.createTextNode(a.getState().toString()));
                move.appendChild(attr);
                move.appendChild(aState);
                move.appendChild(tState);
                enc_moves.appendChild(move);
            }
        }

        //append encryption moves
        node_op.appendChild(enc_moves);
    }

    private void validateConfiguration() {
        if (query == null)
            throw new RuntimeException("Wrong writer configuration");
    }

    public String getPath_res() {
        return path_res;
    }

    public void setPath_res(String path_res) {
        this.path_res = path_res;
    }

    public TreeNode<Operation> getQuery() {
        return query;
    }

    public void setQuery(TreeNode<Operation> query) {
        this.query = query;
    }

    public void setCustomd(String customd) {
        this.customd = customd;
    }
}
