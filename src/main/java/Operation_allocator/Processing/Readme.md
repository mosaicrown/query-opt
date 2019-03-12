# Overview

<p>
<code>Processor.java</code> allows the execution of standardized test. The computation follows the steps listed:
<ol>
  <li>loading test configuration by parsing .xml which describe providers and schema;</li>
  <li>loading query by parsing .xml specified by the user;</li>
  <li>semantic analyzer configuration;</li>
  <li>running names enforcer to resolve conflicts;</li>
  <li>performing operation's assignment;</li>
  <li>parsing output results to .xml.</li>
</ol>
All of the steps but the fifth do not require explanation, they simply translate data to allow the standalone execution mode.
Step 5 is what has been developed to be fully integrated inside a traditional optimization chain, it is composed by multiple phases which are detailed below.
</p>

### 5.1 Attributes synthesizing

<p>
The data flows from the leaves of the query tree to the root, attributes are synthesized, UDFs statistics completed and 
output relation profiles computed. 
</p>

### 5.2 Cost barriers retrieving, single-provider plan creation

<p>
A test provider is used to evaluate the query, the objective is to identify operations charactherized by high cost of CPU 
computation (CPU dominant cost) decorating the tree with semantic enrichers. After that, cost synthesization is performed. 

### 5.3 Deriving minimum required view, oracle creation and cloning

<p>
Minimun required view is derived starting from the root. The single-provider plan is available and will be used as 
an oracle. The oracle is then cloned using object serialization.
</p>

### 5.4 Multi-provider plan creation

<p>
Oracle's clone undergoes operation assignment by means of the allocation engine. The algorithm uses a greedy approach,
it compares the partial results built with simple semantic enrichers with the information stored inside oracle. The choiches 
are supported by an encryption moves generator, a cost engine (volatile or persistent) and a correction function.
</p>
