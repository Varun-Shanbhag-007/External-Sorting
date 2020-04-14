      __  __        _____            _    
    |  \/  |      / ____|          | |   
    | \  / |_   _| (___   ___  _ __| |_  
    | |\/| | | | |\___ \ / _ \| '__| __| 
    | |  | | |_| |____) | (_) | |  | |_  
    |_|  |_|\__, |_____/ \___/|_|   \__| 
             __/ |                       
            |___/                        
            
About
-- 
As part of the  external sort, we have implemented the following logic :

We assume that the RAM of the system is at least 8GB, so if we are sorting a file, which is less than 8GB, we can sort that file directly using the in-memory sort, which in our case is Quick Sort. However if a user intends to sort this small file using multiple threads we divide this file in smaller chunks(within the memory), apply quick sort individually and then merge them together into a single file.
If data size goes beyond 8GB, we opt for External sort. The way this works is we divide the data file into smaller temp files based on the number of threads and sort them individually using Quick Sort. 
Users can choose the number of threads to be used for faster processing.

We spawn n threads at a time so that all thread sorts total data of 8GB(Quick Sort) at a go(Restricting memory usage below 8gb and also improving overall speed leveraging the power of hardware threads which in our case are 48), and this is repeated based on total temp files size. For example, if we have 16 temp files for 16GB we have 8 threads then processing will happen as follows:
8 threads sorting 8GB(1GB each)  at a go X  2 cycles.

We wait for the spawned thread to complete before spawning a new batch of 8 threads to prevent additional RAM usage.

After this process we have sorted temp files of 1GB each.  Next step is to merge all this smaller temp files into a single file. We use the heap to do this. Initially, we put the 1st line from each file into Min Heap and pop the top element from the heap and write to the final file. We keep track of the file of which record/line is popped and put its next line into the heap.We repeat this process until our heap is empty, and thus we have a sorted file.


            
            
Generating data using `gensort`
--
#### `./gensort -a 10000000 1GB`

##### `-a` stands for ASCII values 
##### 10000000 is number of lines in files (100 bytes / line) 
##### 1GB name of the file

Executing MySort
--
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
           


