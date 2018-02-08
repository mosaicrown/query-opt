package Trees.Semantics.Policy;

import Actors.Operation;
import Trees.TreeNode;

public abstract class PolicyGenerator {

    public abstract <T extends Operation>  void generateCandidates(TreeNode<T> tn);

}
