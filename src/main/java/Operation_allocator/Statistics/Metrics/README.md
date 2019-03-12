## Metrics

Metrics allow the cost optimizer to compute:
* execution cost;
* motion cost;
* encryption cost.

Metrics are divided into:
* ```BasicMetric``` which describes all the data ingoing and outgoing from an operator, plus the estimates of IO and CPU execution times;
* ```BasicMetricUpdater``` which allows to update time metrics after data expansion/contraction occured;
* ```ProviderMetric``` which describes all specific costs for execution, motion and encryption;
* ```UDFMetric``` which allows to calculate time estimates for UDFs based on a traditional computing architecture model (a machine is described by clock time, average cycles per instruction and IO operations per second).
