# Tool interaction
## Description
<p>
This folder groups:
<ul>
  <li><code>ConfigFiles</code> contains description of the database schema and the authorizations of providers;</li>
  <li><code>InputData</code> contains the query to be processed;</li>
  <li><code>OutputData</code> contains the output results. Results are in two forms: short which only gives information about
  operation-cost-assignment, extended with information about output relation profile and encryption moves;</li>
  <li>bash scripts respectively to run the project, to compile(deprecated, now mvn project) and to launch a single query evaluation.</li>
</ul>
N.B. In order to run properly, all configuration files have to be put in the correct folder.
</p>
<p>
To interact with the tool it is only required to specify the query to be processed as XML format. XML was 
chosen to highlight the nested structure of the query in a more human readable way.
In order to run the processor it has to be prompted:
</p>
<code>
./soqd.sh inputquery.xml dbschema.xml providerset.xml [no_uvr] [debug_mode] [depth=X]
</code>
<p> 
The fourth, fifth and sixth parameters are optional; without ithem the uniform visibility rule will be applied, report will not be created and maximum strategy depth will be set to 0. 
</p>
<p>
A simple demonstration (3 hybrid query batch) can be run typing:
</p>
<p>
<code>
./demo.sh [no_uvr] [debug_mode] [depth=X]
</code>
</p>

## Input files example

### Configuration
<p>
Configuration refers to the database schema and the description of providers and their authorizations.
The following images show an example. </p>

<img src="/Images/db_schema.png" width="280"></img>

<img src="/Images/provider_auth.png" width="360"></img>

### Input
<p>
A query input example.
</p>

<img src="/Images/query_in.png" width="450"></img>

<p>
The statistics to be specified for relational operators are the following:
<ul>
  <li><code>inputsize</code> the total input quantity of data;</li>
  <li><code>inputtuplesize</code> the length of the tuple resulting from concatenation of all tables' input tuples;</li>
  <li><code>outputsize</code> the total output quantity of data;</li>
  <li><code>outputtuplesize</code> the length of the output table's tuple;</li>
  <li><code>cputime</code> an estimate of the cpu execution time;</li>
  <li><code>iotime</code> an estimate of the io execution time;</li>
</ul>
All the metrics refer to the execution on a fixed plate power cluster (this has to be true even for provider's costs).
Used Defined Functions (UDFs) require a different set of stats:
<ul>
  <li><code>inputsize</code>, <code>inputtuplesize</code>, <code>outputsize</code> and <code>outputtuplesize</code> as
  relational operators;</li>
  <li><code>cpi</code> clocks per instruction required to perfor computation;</li>
  <li><code>ct</code> clock time of the executor where they are executed;</li>
  <li><code>iops</code> I/O per second processed by the executor;</li>
  <li><code>profiler</code> a complexity profile formula to describe computation;</li>
  <li><code>k,..,k</code> a number of coefficients to be applied to the complexity profile.</li>
</ul>
These parameters allow to reconstruct <code>cputime</code> and <code>iotime</code> and to compare them to relational operators 
(for a detailed description see metrics). 
</p>
<p>
An example of operation metrics:

<img src="/Images/udf.det.png" width="360"></img>

</p>

## Output files example

<p>
Example of short results:

<img src="/Images/res_short.png" width="400"></img>

</p>

<p>
Example of extended results:

<img src="/Images/res_extended.png" width="390"></img>

</p>

<p>
Example of performance results (parsing and configuration excluded):

<img src="/Images/performance.png" width="670"></img>

</p>
