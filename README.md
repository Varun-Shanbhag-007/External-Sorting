      __  __        _____            _    
    |  \/  |      / ____|          | |   
    | \  / |_   _| (___   ___  _ __| |_  
    | |\/| | | | |\___ \ / _ \| '__| __| 
    | |  | | |_| |____) | (_) | |  | |_  
    |_|  |_|\__, |_____/ \___/|_|   \__| 
             __/ |                       
            |___/                        
            
Generating data using `gensort`
==
#### `./gensort -a 10000000 1GB`

##### `-a` stands for ASCII values 
##### 10000000 is number of lines in files (100 bytes / line) 
##### 1GB name of the file

Executing MySort
==
#### Prerequisites 
Make sure you have latest JDK installed. You check this using `javac`.
##### execute `$ make`. 
This will compile Java files and generate executable.

#### How to Run
##### `$ java Mysort <file name> <# of threads>`
e.g. `$ java MySort 1GB 8` This will sort 1GB file using 8 threads.

#### Generate Log Report and Plots
##### Execute `$ pidstat -dru -hl 1 >> log_file` before executing java file and stop this once java program finishes executing.
This will capture the CPU, MEMORY, I/O utilization stats in log file. But, there are other processes running in parallel along with this program so to capture the stats relevant to our program execute the following command:

`$ python3 plot.py <log file name> <PID of java program> <no-plot/show-plot>`

`<no-plot/plot>` plots the graph w.r.t sort on above stated stats.

##### NOTE:
1. Number of threads should be greater than 1 and power of 2 if file size > 8GB.
2. For files less than that of 8GB you can use >= 1 thread and in the power of 2.
3. Sorted file will be created and necessary verbose are printed on screen.
4. We are assuming we have at least 8GB available main memory for running this program.
5. The program decides itself when to go for in-memory/external sort.
6. In order to get accurate results make sure flush the after every run.
           


