### Binary exhaustive search

Why do we talk about exhaustive research if the assignment algorithm proceeds by the application of a greedy strategy? The greedy strategy allows to build a good solution (a multi-provider query assignment) in most of cases, but sometimes it is preferable to find the best configuration compliant to the assignment strategy schema chosen. The greedy algorithm does not capture the maximization of profitability, so sometimes it assigns operation reducing the profit. This happens when, inside a CPU dominant cost sub-tree, an operation assignment insourcing or outsourcing can potentially reduce the cost profit margin. 

The allocator can be run in "depth mode", it means that when it recognises a doubtful situation it asks to the <code>BinaryDepthExplorer</code> for an assignment move. The <code>BinaryDepthExplorer</code> keeps track of multi-provider plans and the assignment moves necessary to build them (i.e. using a binary decision tree), so it makes possible an exhaustive evaluation of doubtful cases.  

Detail of strategy exploration.

<img src="/Images/strategyExploration.png" width="380"></img>
