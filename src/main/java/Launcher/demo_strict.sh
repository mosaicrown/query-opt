#!/bin/bash

#run examples

rm -rf ./OutputData/Reports/*.txt

echo -ne "\nFIRST QUERY (Social Network)\nUDF complexity: quadratic\n"
./soqd.sh Query_galeshapley.xml DB_galeshapley.xml ProviderSet_galeshapley.xml $1 $2 $3

echo "-----------------------------------------------------------------------------------------------------------"

echo -ne "\nSECOND QUERY (Health Providers)\nUDF complexity: linear\n"
./soqd.sh Query_ssn.xml DB_ssn.xml ProviderSet_ssn.xml $1 $2 $3

echo "-----------------------------------------------------------------------------------------------------------"

echo -ne "\nTHIRD QUERY (Financial Markets)\nUDF complexity: pseudo-linear\n"
./soqd.sh Query_finance.xml DB_finance.xml ProviderSet_finance.xml $1 $2 $3

