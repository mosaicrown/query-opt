### Introduction to semantics


A consistent evaluation is possible thanks to semantics classes, in particular by the <b>static class</b> <code>TreeNodeSemantics</code>, which creates and manages information flowing through the query tree. The objective is to allow a simple cost based analysis. The key principle is to build and manage the smallest and easiest set of metrics enabling approximated cost simulation and secure assignement of operations.

<code>TreeNodeSemantics</code> helps retrieving input attributes, applying transformations to output distribution, simulating execution cost and ensuring confidentiality profiles.

Particular attention has been dedicated to the time required for cost based analysis, so single sweep startegies have been 
implemented. 

The next figure shows partial descriptive content for some operations. 

<img src="/Images/decorated_tree.png" width="850">

