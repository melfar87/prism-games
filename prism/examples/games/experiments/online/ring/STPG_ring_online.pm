stpg

// parameters
const int n_resources = 3;
const int n_tasks = 2;
const int n_sensors = 5;


// sensor resources
const int resource1=1;
const int resource2=2;
const int resource3=1;
const int resource4=2;
const int resource5=3;

// network configuration
const int e12=1;
const int e13=0;
const int e14=0;
const int e15=1;

const int e21=e12;
const int e23=1;
const int e24=0;
const int e25=0;

const int e31=e13;
const int e32=e23;
const int e34=1;
const int e35=0;

const int e41=e14;
const int e42=e24;
const int e43=e34;
const int e45=1;

const int e51=e15;
const int e52=e25;
const int e53=e35;
const int e54=e45;

module controller // schedules the algorithm

	// algorithm status
	status : [0..8];

	// task resource indicator variables
	t1_r1 : [0..1];
	t1_r2 : [0..1];
	t1_r3 : [0..1];
	
	t2_r1 : [0..1];
	t2_r2 : [0..1];
	t2_r3 : [0..1];

	// schedule placeholders
	turn1 : [0..n_sensors];
	turn2 : [0..n_sensors];
	turn3 : [0..n_sensors];
	turn4 : [0..n_sensors];
	turn5 : [0..n_sensors];

	// selecting schedule uniformly at random
	[] status=0 -> 1/120 : (turn1'=1) & (turn2'=2) & (turn3'=3) & (turn4'=4) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=2) & (turn3'=3) & (turn4'=5) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=2) & (turn3'=4) & (turn4'=3) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=2) & (turn3'=4) & (turn4'=5) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=2) & (turn3'=5) & (turn4'=3) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=2) & (turn3'=5) & (turn4'=4) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=3) & (turn3'=2) & (turn4'=4) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=3) & (turn3'=2) & (turn4'=5) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=3) & (turn3'=4) & (turn4'=2) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=3) & (turn3'=4) & (turn4'=5) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=3) & (turn3'=5) & (turn4'=2) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=3) & (turn3'=5) & (turn4'=4) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=4) & (turn3'=2) & (turn4'=3) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=4) & (turn3'=2) & (turn4'=5) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=4) & (turn3'=3) & (turn4'=2) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=4) & (turn3'=3) & (turn4'=5) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=4) & (turn3'=5) & (turn4'=2) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=4) & (turn3'=5) & (turn4'=3) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=5) & (turn3'=2) & (turn4'=3) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=5) & (turn3'=2) & (turn4'=4) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=5) & (turn3'=3) & (turn4'=2) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=5) & (turn3'=3) & (turn4'=4) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=5) & (turn3'=4) & (turn4'=2) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=1) & (turn2'=5) & (turn3'=4) & (turn4'=3) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=1) & (turn3'=3) & (turn4'=4) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=1) & (turn3'=3) & (turn4'=5) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=1) & (turn3'=4) & (turn4'=3) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=1) & (turn3'=4) & (turn4'=5) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=1) & (turn3'=5) & (turn4'=3) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=1) & (turn3'=5) & (turn4'=4) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=3) & (turn3'=1) & (turn4'=4) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=3) & (turn3'=1) & (turn4'=5) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=3) & (turn3'=4) & (turn4'=1) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=3) & (turn3'=4) & (turn4'=5) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=3) & (turn3'=5) & (turn4'=1) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=3) & (turn3'=5) & (turn4'=4) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=4) & (turn3'=1) & (turn4'=3) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=4) & (turn3'=1) & (turn4'=5) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=4) & (turn3'=3) & (turn4'=1) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=4) & (turn3'=3) & (turn4'=5) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=4) & (turn3'=5) & (turn4'=1) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=4) & (turn3'=5) & (turn4'=3) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=5) & (turn3'=1) & (turn4'=3) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=5) & (turn3'=1) & (turn4'=4) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=5) & (turn3'=3) & (turn4'=1) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=5) & (turn3'=3) & (turn4'=4) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=5) & (turn3'=4) & (turn4'=1) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=2) & (turn2'=5) & (turn3'=4) & (turn4'=3) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=1) & (turn3'=2) & (turn4'=4) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=1) & (turn3'=2) & (turn4'=5) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=1) & (turn3'=4) & (turn4'=2) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=1) & (turn3'=4) & (turn4'=5) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=1) & (turn3'=5) & (turn4'=2) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=1) & (turn3'=5) & (turn4'=4) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=2) & (turn3'=1) & (turn4'=4) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=2) & (turn3'=1) & (turn4'=5) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=2) & (turn3'=4) & (turn4'=1) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=2) & (turn3'=4) & (turn4'=5) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=2) & (turn3'=5) & (turn4'=1) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=2) & (turn3'=5) & (turn4'=4) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=4) & (turn3'=1) & (turn4'=2) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=4) & (turn3'=1) & (turn4'=5) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=4) & (turn3'=2) & (turn4'=1) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=4) & (turn3'=2) & (turn4'=5) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=4) & (turn3'=5) & (turn4'=1) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=4) & (turn3'=5) & (turn4'=2) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=5) & (turn3'=1) & (turn4'=2) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=5) & (turn3'=1) & (turn4'=4) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=5) & (turn3'=2) & (turn4'=1) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=5) & (turn3'=2) & (turn4'=4) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=5) & (turn3'=4) & (turn4'=1) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=3) & (turn2'=5) & (turn3'=4) & (turn4'=2) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=1) & (turn3'=2) & (turn4'=3) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=1) & (turn3'=2) & (turn4'=5) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=1) & (turn3'=3) & (turn4'=2) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=1) & (turn3'=3) & (turn4'=5) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=1) & (turn3'=5) & (turn4'=2) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=1) & (turn3'=5) & (turn4'=3) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=2) & (turn3'=1) & (turn4'=3) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=2) & (turn3'=1) & (turn4'=5) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=2) & (turn3'=3) & (turn4'=1) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=2) & (turn3'=3) & (turn4'=5) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=2) & (turn3'=5) & (turn4'=1) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=2) & (turn3'=5) & (turn4'=3) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=3) & (turn3'=1) & (turn4'=2) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=3) & (turn3'=1) & (turn4'=5) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=3) & (turn3'=2) & (turn4'=1) & (turn5'=5) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=3) & (turn3'=2) & (turn4'=5) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=3) & (turn3'=5) & (turn4'=1) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=3) & (turn3'=5) & (turn4'=2) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=5) & (turn3'=1) & (turn4'=2) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=5) & (turn3'=1) & (turn4'=3) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=5) & (turn3'=2) & (turn4'=1) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=5) & (turn3'=2) & (turn4'=3) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=5) & (turn3'=3) & (turn4'=1) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=4) & (turn2'=5) & (turn3'=3) & (turn4'=2) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=1) & (turn3'=2) & (turn4'=3) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=1) & (turn3'=2) & (turn4'=4) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=1) & (turn3'=3) & (turn4'=2) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=1) & (turn3'=3) & (turn4'=4) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=1) & (turn3'=4) & (turn4'=2) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=1) & (turn3'=4) & (turn4'=3) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=2) & (turn3'=1) & (turn4'=3) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=2) & (turn3'=1) & (turn4'=4) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=2) & (turn3'=3) & (turn4'=1) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=2) & (turn3'=3) & (turn4'=4) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=2) & (turn3'=4) & (turn4'=1) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=2) & (turn3'=4) & (turn4'=3) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=3) & (turn3'=1) & (turn4'=2) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=3) & (turn3'=1) & (turn4'=4) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=3) & (turn3'=2) & (turn4'=1) & (turn5'=4) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=3) & (turn3'=2) & (turn4'=4) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=3) & (turn3'=4) & (turn4'=1) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=3) & (turn3'=4) & (turn4'=2) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=4) & (turn3'=1) & (turn4'=2) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=4) & (turn3'=1) & (turn4'=3) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=4) & (turn3'=2) & (turn4'=1) & (turn5'=3) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=4) & (turn3'=2) & (turn4'=3) & (turn5'=1) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=4) & (turn3'=3) & (turn4'=1) & (turn5'=2) & (status'=1)
		 + 1/120 : (turn1'=5) & (turn2'=4) & (turn3'=3) & (turn4'=2) & (turn5'=1) & (status'=1);


	
	// executing the schedule

	// 1st round
	[str1] status=1 & turn1=1 -> (status'=1);
	[fin1] status=1 & turn1=1 -> (status'=2);
	[str2] status=1 & turn1=2 -> (status'=1);
	[fin2] status=1 & turn1=2 -> (status'=2);
	[str3] status=1 & turn1=3 -> (status'=1);
	[fin3] status=1 & turn1=3 -> (status'=2);
	[str4] status=1 & turn1=4 -> (status'=1);
	[fin4] status=1 & turn1=4 -> (status'=2);
	[str5] status=1 & turn1=5 -> (status'=1);
	[fin5] status=1 & turn1=5 -> (status'=2);

	// 2nd round
	[str1] status=2 & turn2=1 -> (status'=2);
	[fin1] status=2 & turn2=1 -> (status'=3);
	[str2] status=2 & turn2=2 -> (status'=2);
	[fin2] status=2 & turn2=2 -> (status'=3);
	[str3] status=2 & turn2=3 -> (status'=2);
	[fin3] status=2 & turn2=3 -> (status'=3);
	[str4] status=2 & turn2=4 -> (status'=2);
	[fin4] status=2 & turn2=4 -> (status'=3);
	[str5] status=2 & turn2=5 -> (status'=2);
	[fin5] status=2 & turn2=5 -> (status'=3);

	// 3rd round
	[str1] status=3 & turn3=1 -> (status'=3);
	[fin1] status=3 & turn3=1 -> (status'=4);
	[str2] status=3 & turn3=2 -> (status'=3);
	[fin2] status=3 & turn3=2 -> (status'=4);
	[str3] status=3 & turn3=3 -> (status'=3);
	[fin3] status=3 & turn3=3 -> (status'=4);
	[str4] status=3 & turn3=4 -> (status'=3);
	[fin4] status=3 & turn3=4 -> (status'=4);
	[str5] status=3 & turn3=5 -> (status'=3);
	[fin5] status=3 & turn3=5 -> (status'=4);

	// 4th round
	[str1] status=4 & turn4=1 -> (status'=4);
	[fin1] status=4 & turn4=1 -> (status'=5);
	[str2] status=4 & turn4=2 -> (status'=4);
	[fin2] status=4 & turn4=2 -> (status'=5);
	[str3] status=4 & turn4=3 -> (status'=4);
	[fin3] status=4 & turn4=3 -> (status'=5);
	[str4] status=4 & turn4=4 -> (status'=4);
	[fin4] status=4 & turn4=4 -> (status'=5);
	[str5] status=4 & turn4=5 -> (status'=4);
	[fin5] status=4 & turn4=5 -> (status'=5);

	// 5th round
	[str1] status=5 & turn5=1 -> (status'=5);
	[fin1] status=5 & turn5=1 -> (status'=6);
	[str2] status=5 & turn5=2 -> (status'=5);
	[fin2] status=5 & turn5=2 -> (status'=6);
	[str3] status=5 & turn5=3 -> (status'=5);
	[fin3] status=5 & turn5=3 -> (status'=6);
	[str4] status=5 & turn5=4 -> (status'=5);
	[fin4] status=5 & turn5=4 -> (status'=6);
	[str5] status=5 & turn5=5 -> (status'=5);
	[fin5] status=5 & turn5=5 -> (status'=6);

// initialising non-empty tasks uniformly at random
	[] status=6 -> 1/49 : (t1_r1'=0) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=0) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=0) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=0) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=0) & (t2_r2'=1) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=0) & (t2_r3'=1) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=0) & (status'=7)
		 + 1/49 : (t1_r1'=1) & (t1_r2'=1) & (t1_r3'=1) & (t2_r1'=1) & (t2_r2'=1) & (t2_r3'=1) & (status'=7);


	[] status=7 -> (status'=8);

	[] status=8 -> true;

endmodule

module sensor1

	state1 : [0..1];

	// team membership indicators
	m1_t1 : [0..1];
	m1_t2 : [0..1];

	// starting turn, selecting order of tasks
	[str1] state1=0 -> (state1'=1);

	// if there is no team and has required skill - initiating the team
	[] state1=1 & !committed & team_size_t1=0 -> (m1_t1'=1);
	[] state1=1 & !committed & team_size_t2=0 -> (m1_t2'=1);

	// if team already exists and one of the neighbours is in it - joining the team 
	[] state1=1 & !committed & team_size_t1>0 & can_join_t1  & !resource_filled_t1 -> (m1_t1'=1);
	[] state1=1 & !committed & team_size_t2>0 & can_join_t2  & !resource_filled_t2 -> (m1_t2'=1);
	
	[fin1] state1>0 -> (state1'=0); 

endmodule

module sensor2 = sensor1 
[ 
	state1=state2, 

	str1=str2,
	fin1=fin2,

	m1_t1=m2_t1,
	m1_t2=m2_t2,

	m2_t1=m1_t1,
	m2_t2=m1_t2,

	resource1=resource2,	
	resource2=resource1,

	e12=e21,
	e13=e23,
	e14=e24,
	e15=e25,

	e21=e12,
	e23=e13,
	e24=e14,
	e25=e15
] 
endmodule

module sensor3 = sensor1 
[ 
	state1=state3, 

	str1=str3,
	fin1=fin3,

	m1_t1=m3_t1,
	m1_t2=m3_t2,
	m3_t1=m1_t1,
	m3_t2=m1_t2,

	resource1=resource3,	
	resource3=resource1,

	e12=e32,
	e13=e31,
	e14=e34,
	e15=e35,

	e31=e13,
	e32=e12,
	e34=e14,
	e35=e15
] 
endmodule

module sensor4 = sensor1 
[ 
	state1=state4, 

	str1=str4,
	fin1=fin4,

	m1_t1=m4_t1,
	m1_t2=m4_t2,

	m4_t1=m1_t1,
	m4_t2=m1_t2,

	resource1=resource4,	
	resource4=resource1,

	e12=e42,
	e13=e43,
	e14=e41,
	e15=e45,

	e41=e14,
	e42=e12,
	e43=e13,
	e45=e15
] 
endmodule

module sensor5 = sensor1 
[ 
	state1=state5, 

	str1=str5,
	fin1=fin5,

	m1_t1=m5_t1,
	m1_t2=m5_t2,

	m5_t1=m1_t1,
	m5_t2=m1_t2,

	resource1=resource5,	
	resource5=resource1,

	e12=e52,
	e13=e53,
	e14=e54,
	e15=e51,

	e51=e15,
	e52=e12,
	e53=e13,
	e54=e14
] 
endmodule

// agent is committed to some team
formula committed = (m1_t1+m1_t2) > 0;

// formulae to compute team sizes
formula team_size_t1 = m1_t1+m2_t1+m3_t1+m4_t1+m5_t1;
formula team_size_t2 = m1_t2+m2_t2+m3_t2+m4_t2+m5_t2;

// formulae to check whether the agent can join the team
formula can_join_t1 = e12*m2_t1 + e13*m3_t1 + e14*m4_t1 + e15*m5_t1 > 0;
formula can_join_t2 = e12*m2_t2 + e13*m3_t2 + e14*m4_t2 + e15*m5_t2 > 0;

// formulae to check whether agent has the resource required by the task
formula has_resource_t1 = ( (t1_r1=1&resource1=1) | (t1_r2=1&resource1=2) | (t1_r3=1&resource1=3) );
formula has_resource_t2 = ( (t2_r1=1&resource1=1) | (t2_r2=1&resource1=2) | (t2_r3=1&resource1=3) );

// formulae to check whether the resource of an agent has been already filled in the team
formula resource_filled_t1 = (m2_t1=1 & resource1=resource2) | (m3_t1=1 & resource1=resource3) | (m4_t1=1 & resource1=resource4) | (m5_t1=1 & resource1=resource5);
formula resource_filled_t2 = (m2_t2=1 & resource1=resource2) | (m3_t2=1 & resource1=resource3) | (m4_t2=1 & resource1=resource4) | (m5_t2=1 & resource1=resource5);




// labels and formulae for property specification 
formula finished = (status=7);

formula sensor1_joins_successful_team = (task1_completed & m1_t1=1) | (task2_completed & m1_t2=1);
formula sensor1_joins_successful_team_of_1 = (task1_completed & m1_t1=1 & team_size_t1=1) | (task2_completed & m1_t2=1 & team_size_t2=1);
formula sensor1_joins_successful_team_of_2 = (task1_completed & m1_t1=1 & team_size_t1=2) | (task2_completed & m1_t2=1 & team_size_t2=2);
formula sensor1_joins_successful_team_of_3 = (task1_completed & m1_t1=1 & team_size_t1=3) | (task2_completed & m1_t2=1 & team_size_t2=3);

formula sensor2_joins_successful_team = (task1_completed & m2_t1=1) | (task2_completed & m2_t2=1);
formula sensor2_joins_successful_team_of_1 = (task1_completed & m2_t1=1 & team_size_t1=1) | (task2_completed & m2_t2=1 & team_size_t2=1);
formula sensor2_joins_successful_team_of_2 = (task1_completed & m2_t1=1 & team_size_t1=2) | (task2_completed & m2_t2=1 & team_size_t2=2);
formula sensor2_joins_successful_team_of_3 = (task1_completed & m2_t1=1 & team_size_t1=3) | (task2_completed & m2_t2=1 & team_size_t2=3);

formula sensor3_joins_successful_team = (task1_completed & m3_t1=1) | (task2_completed & m3_t2=1);
formula sensor3_joins_successful_team_of_1 = (task1_completed & m3_t1=1 & team_size_t1=1) | (task2_completed & m3_t2=1 & team_size_t2=1);
formula sensor3_joins_successful_team_of_2 = (task1_completed & m3_t1=1 & team_size_t1=2) | (task2_completed & m3_t2=1 & team_size_t2=2);
formula sensor3_joins_successful_team_of_3 = (task1_completed & m3_t1=1 & team_size_t1=3) | (task2_completed & m3_t2=1 & team_size_t2=3);

formula sensor4_joins_successful_team = (task1_completed & m4_t1=1) | (task2_completed & m4_t2=1);
formula sensor4_joins_successful_team_of_1 = (task1_completed & m4_t1=1 & team_size_t1=1) | (task2_completed & m4_t2=1 & team_size_t2=1);
formula sensor4_joins_successful_team_of_2 = (task1_completed & m4_t1=1 & team_size_t1=2) | (task2_completed & m4_t2=1 & team_size_t2=2);
formula sensor4_joins_successful_team_of_3 = (task1_completed & m4_t1=1 & team_size_t1=3) | (task2_completed & m4_t2=1 & team_size_t2=3);

formula sensor5_joins_successful_team = (task1_completed & m5_t1=1) | (task2_completed & m5_t2=1);
formula sensor5_joins_successful_team_of_1 = (task1_completed & m5_t1=1 & team_size_t1=1) | (task2_completed & m5_t2=1 & team_size_t2=1);
formula sensor5_joins_successful_team_of_2 = (task1_completed & m5_t1=1 & team_size_t1=2) | (task2_completed & m5_t2=1 & team_size_t2=2);
formula sensor5_joins_successful_team_of_3 = (task1_completed & m5_t1=1 & team_size_t1=3) | (task2_completed & m5_t2=1 & team_size_t2=3);

formula task1_completed = finished 
		 	 & ((t1_r1=1)=>((m1_t1=1&resource1=1)|(m2_t1=1&resource2=1)|(m3_t1=1&resource3=1)|(m4_t1=1&resource4=1)|(m5_t1=1&resource5=1)))
			 & ((t1_r2=1)=>((m1_t1=1&resource1=2)|(m2_t1=1&resource2=2)|(m3_t1=1&resource3=2)|(m4_t1=1&resource4=2)|(m5_t1=1&resource5=2)))
			 & ((t1_r3=1)=>((m1_t1=1&resource1=3)|(m2_t1=1&resource2=3)|(m3_t1=1&resource3=3)|(m4_t1=1&resource4=3)|(m5_t1=1&resource5=3)));

formula task2_completed = finished
			 & ((t2_r1=1)=>((m1_t2=1&resource1=1)|(m2_t2=1&resource2=1)|(m3_t2=1&resource3=1)|(m4_t2=1&resource4=1)|(m5_t2=1&resource5=1)))
			 & ((t2_r2=1)=>((m1_t2=1&resource1=2)|(m2_t2=1&resource2=2)|(m3_t2=1&resource3=2)|(m4_t2=1&resource4=2)|(m5_t2=1&resource5=2)))
			 & ((t2_r3=1)=>((m1_t2=1&resource1=3)|(m2_t2=1&resource2=3)|(m3_t2=1&resource3=3)|(m4_t2=1&resource4=3)|(m5_t2=1&resource5=3)));

// rewards


rewards "w_1_total"
    sensor1_joins_successful_team : 1;
    sensor2_joins_successful_team : 1;
    sensor3_joins_successful_team : 1;
    sensor4_joins_successful_team : 1;
    sensor5_joins_successful_team : 1;
endrewards

rewards "w_2_total"
    task1_completed : 1;
    task2_completed : 1;
endrewards





