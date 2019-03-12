package Operation_allocator.Trees.Semantics.Policy;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Trees.TreeNode;

public abstract class PolicyGenerator {

    public abstract <T extends Operation>  void generateCandidates(TreeNode<T> tn);

}
