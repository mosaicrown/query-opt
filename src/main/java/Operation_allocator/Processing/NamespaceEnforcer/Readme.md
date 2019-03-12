### Brief exaplanation

This component resolves name confilcts in user's data configuration. More precisely, the policy model requires unique attribute
names, so it is necessary to correct the names inside query, schema and authorization. With conflict is meant a repetition of a name in a single or multiple operation's tree leaves. 

Not only the names of the attributes have to be corrected, but even the constraints, the homogeneous and projected set references. To do so a post order visit is performed on the query tree, and for each child of a node the temporary bindings are retrieved and applied. The propagation of names is made possible by the projected set of each operation.

A set of bindings keeps track of the new and original names.

The following figure shows a fragment on namespace resolver name's correction code.

<img src="/Images/names_enf.png"></img>
