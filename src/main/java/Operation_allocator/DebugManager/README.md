## Debugger

The behavior of this kind of tools is sometimes difficult to evaluate because of the proccessing of multiple steps in sequence. In
order to make it easier to monitor the state, a simple trace reporter has been implemented.

Trace reporting is only enabled in ```debug_mode```, in this configuration, for each query, a ```report_TIME.txt```  file is produced. Each trace (or report) is composed by the name of the component writing the report, the type of report and a string representing the data or a description. 

A report could easily be thousand of lines long, especially when depth mode is enabled. The idea was to make it easier to look for a particular string in the text, for example, to look for the the space of alternatives it is possible to type
```
cat report__.txt | grep -i -B 3 -A 10 "strategy space"
```
which prints the previous and next lines to the one containing the string ```strategy space```, a possible result could then be
```
-------------------------------------------------------------------------------------------------------
***Processor***
	->GENERAL_INFO
	STRATEGY SPACE
	Lev-0(0)
		|__Lev-1(0)
			|__Lev-2(0)
				|__Lev-3(0)
					|__Lev-4(0)--> Ct: 0.04669083402426081
					|__Lev-4(1)
						|__Lev-5(0)--> Ct: 0.019548900847310417
						|__Lev-5(1)--> Ct: 0.019548900847310417
				|__Lev-3(1)
					|__Lev-4(0)
```
An example fragment of semantic step is instead
```
-------------------------------------------------------------------------------------------------------
***Processor***
	->STEP_COMPLETION
	QUERY AFTER SEMANTIC ENRICHMENT

	project10	 Fam.name:  Projection
		 Executor:  Proprietary	 Ct:0.04669083402426081	 Ce:2.8792166666666666E-6	 Cm:0.0	 Cc:0.0
		 Op. metrics:  InputSize: 7462.0	 InputTupleSize: 0.06	 OutputSize: 2488.0	 OutputTupleSize: 0.02	 CPU_time: 0.028	 IO_time: 1.0E-5
		 Features:  *EXPDATADOMCOST
		 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
		 Output Relation Profile:  Rvp:	t2.c1	Rve:		Rip:	t3.e5,t3.g7,t1.a3,t1.b4,t2.c1	Rie:	t1.a3	Rces:	(t1.a3,t2.d2,t3.f6)
		 Input attributes:  | n: t1.a3 d: 0.02 s: PLAINTEXT od: 0.02 | n: t2.c1 d: 0.02 s: PLAINTEXT od: 0.02 | n: t2.d2 d: 0.02 s: PLAINTEXT od: 0.02 | 
		 Projected set:  | n: t2.c1 od: 0.02 | 
		 Attribute constraints:  | 
		 Applied encryption:   | 
	|____nestedloopjoin9	 Fam.name:  JoinNAry
			 Executor:  Proprietary	 Ct:0.04668795480759414	 Ce:1.0647777777777779E-5	 Cm:0.0	 Cc:0.0
			 Op. metrics:  InputSize: 574.0	 InputTupleSize: 0.06	 OutputSize: 7462.0	 OutputTupleSize: 0.06	 CPU_time: 0.098	 IO_time: 0.004
			 Features:  *EXPDATADOMCOST
			 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
			 Output Relation Profile:  Rvp:	t1.a3,t2.c1,t2.d2	Rve:		Rip:	t3.e5,t3.g7,t1.a3,t1.b4,t2.c1	Rie:	t1.a3	Rces:	(t1.a3,t2.d2,t3.f6)
			 Input attributes:  | n: t1.a3 d: 0.02 s: PLAINTEXT od: 0.02 | n: t2.c1 d: 0.02 s: PLAINTEXT od: 0.02 | n: t2.d2 d: 0.02 s: PLAINTEXT od: 0.02 | 
			 Homogeneous set:  | n: t1.a3 od: 0.02 | n: t2.d2 od: 0.02 | 
			 Attribute constraints:  | 
			 Applied encryption:   | 
		|____selection8	 Fam.name:  Selection
				 Executor:  Proprietary	 Ct:1.3826488888888888E-5	 Ce:8.353777777777777E-7	 Cm:0.0	 Cc:0.0
				 Op. metrics:  InputSize: 600.0	 InputTupleSize: 0.04	 OutputSize: 550.0	 OutputTupleSize: 0.04	 CPU_time: 0.0081	 IO_time: 2.0E-5
				 Features:  *EXPDATADOMCOST
				 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
				 Output Relation Profile:  Rvp:	t2.c1,t2.d2	Rve:		Rip:	t2.c1	Rie:		Rces:	
				 Input attributes:  | n: t2.c1 d: 0.02 s: PLAINTEXT od: 0.02 | n: t2.d2 d: 0.02 s: PLAINTEXT od: 0.02 | 
				 Homogeneous set:  | n: t2.c1 od: 0.02 | 
				 Attribute constraints:  | n: t2.d2 s: DETSYMENC | 
				 Applied encryption:   | 
			|____indexscan7	 Fam.name:  Projection
					 Executor:  Proprietary	 Ct:1.299111111111111E-5	 Ce:1.299111111111111E-5	 Cm:0.0	 Cc:0.0
					 Op. metrics:  InputSize: 600.0	 InputTupleSize: 0.04	 OutputSize: 600.0	 OutputTupleSize: 0.04	 CPU_time: 4.0E-4	 IO_time: 0.09
					 Features:  *EXPDATADOMCOST
					 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
					 Output Relation Profile:  Rvp:	t2.c1,t2.d2	Rve:		Rip:		Rie:		Rces:	
					 Input attributes:  | n: t2.c1 d: 0.02 s: PLAINTEXT od: 0.02 | n: t2.d2 d: 0.02 s: PLAINTEXT od: 0.02 | 
					 Projected set:  | n: t2.c1 od: 0.02 | n: t2.d2 od: 0.02 | 
					 Attribute constraints:  | 
					 Applied encryption:   | 
		|____conflictsf6	 Fam.name:  UDF
				 Executor:  Proprietary	 Ct:0.04666348054092748	 Ce:0.04465065998537192	 Cm:0.0	 Cc:0.0
				 Op. metrics:  InputSize: 278400.0	 InputTupleSize: 1.74	 OutputSize: 24.0	 OutputTupleSize: 0.02	 CPU_time: 433.6705183944499	 IO_time: 0.5488110837692355	 CPI:	2000.0	 CT:	5.0E-10	 IOPS:	100000.0	 Not:	160000.0
				 Features:  *EXPCPUDOMCOST
				 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
				 Output Relation Profile:  Rvp:	t1.a3	Rve:		Rip:	t3.e5,t3.g7,t1.a3,t1.b4	Rie:	t1.a3	Rces:	(t1.a3,t3.f6)
				 Input attributes:  | n: t3.e5 d: 0.02 s: PLAINTEXT od: 0.02 | n: t3.g7 d: 1.0 s: PLAINTEXT od: 1.0 | n: t1.a3 d: 0.02 s: PLAINTEXT od: 0.02 | n: t1.b4 d: 0.7 s: PLAINTEXT od: 0.7 | 
				 Homogeneous set:  | n: t1.a3 od: 0.02 | 
				 Projected set:  | n: t1.a3 od: 0.02 | 
				 Attribute constraints:  | n: t1.b4 s: DETSYMENC | n: t3.g7 s: DETSYMENC | 
				 Applied encryption:   | 
			|____groupby5	 Fam.name:  GroupBy
					 Executor:  Proprietary	 Ct:0.002012820555555556	 Ce:5.597277777777778E-4	 Cm:0.0	 Cc:0.0
					 Op. metrics:  InputSize: 281600.0	 InputTupleSize: 1.76	 OutputSize: 278400.0	 OutputTupleSize: 1.74	 CPU_time: 4.2	 IO_time: 0.89
					 Features:  *EXPCPUDOMCOST
					 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
					 Output Relation Profile:  Rvp:	t3.e5,t3.g7,t1.a3,t1.b4	Rve:		Rip:	t3.e5,t3.g7,t1.a3,t1.b4	Rie:		Rces:	(t1.a3,t3.f6)
					 Input attributes:  | n: t3.e5 d: 0.02 s: PLAINTEXT od: 0.02 | n: t3.f6 d: 0.02 s: PLAINTEXT od: 0.02 | n: t3.g7 d: 1.0 s: PLAINTEXT od: 1.0 | n: t1.a3 d: 0.02 s: PLAINTEXT od: 0.02 | n: t1.b4 d: 0.7 s: PLAINTEXT od: 0.7 | 
					 Homogeneous set:  | n: t1.a3 od: 0.02 | 
					 Projected set:  | n: t1.a3 od: 0.02 | n: t1.b4 od: 0.7 | n: t3.e5 od: 0.02 | n: t3.g7 od: 1.0 | 
					 Attribute constraints:  | 
					 Applied encryption:   | 
				|____join4	 Fam.name:  JoinNAry
						 Executor:  Proprietary	 Ct:0.001453092777777778	 Ce:4.941555555555556E-4	 Cm:0.0	 Cc:0.0
						 Op. metrics:  InputSize: 214480.0	 InputTupleSize: 1.76	 OutputSize: 281600.0	 OutputTupleSize: 1.76	 CPU_time: 3.8	 IO_time: 0.72
						 Features:  *EXPCPUDOMCOST
						 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
						 Output Relation Profile:  Rvp:	t3.e5,t3.f6,t3.g7,t1.a3,t1.b4	Rve:		Rip:	t1.b4	Rie:		Rces:	(t1.a3,t3.f6)
						 Input attributes:  | n: t3.e5 d: 0.02 s: PLAINTEXT od: 0.02 | n: t3.f6 d: 0.02 s: PLAINTEXT od: 0.02 | n: t3.g7 d: 1.0 s: PLAINTEXT od: 1.0 | n: t1.a3 d: 0.02 s: PLAINTEXT od: 0.02 | n: t1.b4 d: 0.7 s: PLAINTEXT od: 0.7 | 
						 Homogeneous set:  | n: t1.a3 od: 0.02 | n: t3.f6 od: 0.02 | 
						 Attribute constraints:  | 
						 Applied encryption:   | 
					|____selection3	 Fam.name:  Selection
							 Executor:  Proprietary	 Ct:1.8097111111111113E-4	 Ce:7.326E-5	 Cm:0.0	 Cc:0.0
							 Op. metrics:  InputSize: 7200.0	 InputTupleSize: 0.72	 OutputSize: 6480.0	 OutputTupleSize: 0.72	 CPU_time: 0.71	 IO_time: 0.002
							 Features:  *EXPCPUDOMCOST
							 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
							 Output Relation Profile:  Rvp:	t1.a3,t1.b4	Rve:		Rip:	t1.b4	Rie:		Rces:	
							 Input attributes:  | n: t1.a3 d: 0.02 s: PLAINTEXT od: 0.02 | n: t1.b4 d: 0.7 s: PLAINTEXT od: 0.7 | 
							 Homogeneous set:  | n: t1.b4 od: 0.7 | 
							 Attribute constraints:  | n: t1.b4 s: DETSYMENC | 
							 Applied encryption:   | 
						|____indexseek2	 Fam.name:  Projection
								 Executor:  Proprietary	 Ct:1.0771111111111112E-4	 Ce:1.0771111111111112E-4	 Cm:0.0	 Cc:0.0
								 Op. metrics:  InputSize: 7200.0	 InputTupleSize: 0.72	 OutputSize: 7200.0	 OutputTupleSize: 0.72	 CPU_time: 0.11	 IO_time: 0.67
								 Features:  *EXPCPUDOMCOST
								 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
								 Output Relation Profile:  Rvp:	t1.a3,t1.b4	Rve:		Rip:		Rie:		Rces:	
								 Input attributes:  | n: t1.a3 d: 0.02 s: PLAINTEXT od: 0.02 | n: t1.b4 d: 0.7 s: PLAINTEXT od: 0.7 | 
								 Projected set:  | n: t1.a3 od: 0.02 | n: t1.b4 od: 0.7 | 
								 Attribute constraints:  | 
								 Applied encryption:   | 
					|____indexseek1	 Fam.name:  Projection
							 Executor:  Proprietary	 Ct:7.779661111111111E-4	 Ce:7.779661111111111E-4	 Cm:0.0	 Cc:0.0
							 Op. metrics:  InputSize: 208000.0	 InputTupleSize: 1.04	 OutputSize: 208000.0	 OutputTupleSize: 1.04	 CPU_time: 0.68	 IO_time: 4.921
							 Features:  *EXPCPUDOMCOST
							 Minimum required view:  Rvp:		Rve:		Rip:		Rie:		Rces:	
							 Output Relation Profile:  Rvp:	t3.e5,t3.f6,t3.g7	Rve:		Rip:		Rie:		Rces:	
							 Input attributes:  | n: t3.e5 d: 0.02 s: PLAINTEXT od: 0.02 | n: t3.f6 d: 0.02 s: PLAINTEXT od: 0.02 | n: t3.g7 d: 1.0 s: PLAINTEXT od: 1.0 | 
							 Projected set:  | n: t3.e5 od: 0.02 | n: t3.f6 od: 0.02 | n: t3.g7 od: 1.0 | 
							 Attribute constraints:  | 
							 Applied encryption:   | 

-------------------------------------------------------------------------------------------------------
```
(in this example the report is 12527 lines long)

To insert a new report it is enough doing
```(java code)
    if (debug_mode)
            debugger.leaveTrace(
                    new Report(
                            "OperationAllocator",
                            LogType.GENERAL_INFO,
                            "\tAsking moves to p3 for op: " + tn.getElement().toString()
                                    + ...
                    )
     );
```
