### Operations allocation

Operation assignment is the task that enables the creation of multi-provider query plans. The benefits achievable by the 
application of operation assignment are shown in the next figure.

<img src="/Images/assignments.png" width="600">

The allocation algorithm restricts the analysis to three candidates: the authority that owns the data, a 
trusted and confidential provider, a cheap and not confidential provider. 

The assignment process follows the steps below.
1. the assignment process starts from the leaves, for each operation, all the valid execution candidates are retrieved (the wrapping moves generator is asked to look for a set of encryption moves to be applied to attributes in order to move the data);
2. for each valid candidate the allocator simulates operation execution building relation profiles and simulating cost with a simple volatile cost engine;
3. a check for closure equivalence set is applied, so the possible executors are identified (a provider can be authorized to retrieve the data but not authorized to process them);
4. a greedy cost strategy assignment is performed considering the total sub-tree and the operations incremental costs.

### Proprieties
* no multi-provider plan with total cost bigger than single-provider one's is produced (a recursive validation and cost correction procedure is triggered when an assignment strategy fails);
* the allocator is able to find the assignment with greater profitability (according to the strategy schema) by the use of depth mode.


Detail of operations assignment and recursive validation procedure.
<img src="/Images/allocator_assignment.png" width="850">

<img src="/Images/allocator_validation.png" width="560">
