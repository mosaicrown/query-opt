### Brief exaplanation of encryption moves

<code>PolicyMovesGenerator</code> is a component that searches for encryption moves to make a set of attributes compliant
to the execution over a candidate provider. In order to do that, not only the providers authorization sets have to be considered, but even the constraints imposed by the particular operation. If a list of encryption moves is found, then the provider stands for a possible execution candidate for the current operation.

<code>PolicyMovesGenerator</code> has to consider mutiple constraints, but what constraint means? A constraint is a wrapping technique that need to be applied in order to perform a particular operation. There are multiple kind of constraints:
* confidentiality contraints, or rather the ones imposed by providers' confidentiality profiles;
* homogeneous set constraints, which specify the same type of wrapping to a group of attributes in order to evaluate an operation;
* constrained set constraints, which, for each attribute, impose a particular kind of wrapping (if wrapping has to be applied by security reasons) in order to make an attribute procceseable by an operation. 

The figure shows a fragment of 
<img src="/Images/allowedMovesFragment.png" width="680">
