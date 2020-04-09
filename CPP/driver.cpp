/* 
Date: April 5 2020
Course: Cloud Computing CS553
External sort -- subpart
Creates temporary sorted files
Compile: g++ driver.cpp -o driver
Execute: ./driver <# of partitions> <# total file size in bytes>
*/
#include<iostream>
#include<fstream>
#include<vector>
#include<stdlib.h>
#include<string.h>
#include<algorithm>
#include<chrono>
#include<stdio.h>
#include<pthread.h>
#include<queue>
using namespace std;
using namespace std::chrono;

struct thread_data {
    int id;
    vector<string> data;
};

struct MinHeapNode 
{ 
	string element; 
	int i;
};

struct Comp{
    bool operator()(const MinHeapNode& a, const MinHeapNode& b){
        if(strcmp(b.element.c_str(), a.element.c_str()) < 0)
            return true;
        else
            return false;
    }
};

FILE* openfile(char* filename, char* mode){
    FILE* fp = fopen(filename, mode);
    if(fp == NULL){
        perror("Cannot open file");
        exit(0);
    }
    return fp;
}

int partition (vector<string> &vec, int low, int high){
    string pivot = vec[high]; // pivot
    int i = (low - 1); // Index of smaller element
    string temp;
    for (int j = low; j <= high - 1; j++){
        if ((vec[j].compare(pivot)) < 0){
            i++; // increment index of smaller element
            temp = vec[j];
            vec[j] = vec[i];
            vec[i] = temp;
        }
    }
    temp = vec[high];
    vec[high] = vec[i + 1];
    vec[i + 1] = temp;
    return (i + 1);
}

void quicksort(vector<string> &vec, int low, int high){
    
		
	if (low < high){
        int pi = partition(vec, low, high);
        quicksort(vec, low, pi - 1);
        quicksort(vec, pi + 1, high);
    }
}

void *quicksort_helper(void *threadarg){

    struct thread_data *mydata;
    mydata = (struct thread_data*) threadarg;
    quicksort(mydata->data, 0, (mydata->data).size()-1);
	
	
	//writing the sorted vector to corressponding file
	ofstream write(to_string(mydata->id));
	string line;
	
	for(string sorted_line : mydata->data){
		write<<sorted_line<<endl;
	}

	write.close();

}

FILE* openFile(char* fileName, char* mode) 
{ 
	FILE* fp = fopen(fileName, mode); 
	if (fp == NULL) 
	{ 
		perror("Error while opening the file.\n"); 
		exit(EXIT_FAILURE); 
	} 
	return fp; 
}

void mergeFiles(int k){
    ofstream output("result.txt");
    
    ifstream in[k]; 
    
	for (int i = 0; i < k; i++) 
	{ 
		
        in[i] = ifstream (to_string(i));
	}
    priority_queue <MinHeapNode, vector<MinHeapNode>, Comp> minHeap;
    int i; 
    string line;
    
	for (i = 0; i < k; i++) 
	{ 
		getline(in[i], line);
        MinHeapNode temp;
        temp.element = line;
        temp.i = i;
        minHeap.push(temp);
	} 
   
    int count = 0;  
   
	while (count != i) 
	{
	
		MinHeapNode root = minHeap.top();
        
        minHeap.pop();
        
        output<<root.element<<endl;
		
		int fileNumber = root.i;
        string ele; 
        
		if(!in[fileNumber].eof()){
		    
			getline(in[fileNumber], ele);    
            MinHeapNode newNode;
            newNode.i = fileNumber;
            newNode.element = ele;
            minHeap.push(newNode);
		}
        else{
            root.element = '~';
            count++;
        }
    } 
    
	for (int x = 0; i < k; x++) 
		in[x].close(); 
	output.close();
}

void without_Threading(long long int size, ifstream &input){
	
	cout<<"Without Threading"<<endl;
	vector<string> buffer;//stores data from the input file to buffer
	ofstream output(to_string(0));
	
	string line;
	while(getline(input, line))
		buffer.push_back(line);
	
	//sorting the buffer
	quicksort(buffer, 0, buffer.size()-1);
	
	//copying the buffer outputfile
	for(string sorted_line : buffer)
        output<<sorted_line<<endl;
	
	output.close();
    buffer.clear();

}

//make files according to number of threads give and size is fixed i:e 8GB
void multithreading_on_eightGB(int threads, ifstream &input, int fileName){

    long long int lines_in_each_file = 40000000/threads;

    cout<<endl<<"lines in each file = "<<lines_in_each_file<<endl<<"Threads = "<<threads<<endl;
    cout<<"fileName = "<<fileName;
	
	pthread_t pthreads[threads];
	struct thread_data td[threads];

  	auto start_Sorting = high_resolution_clock::now();
	
	string orgline;
	for(int i = 0; i < threads; i++){
		td[i].id = fileName + i;
		
		for(int j = 1; j <= lines_in_each_file; j++){
			
			getline(input, orgline);
			td[i].data.push_back(orgline);
		}
	}

	cout<<endl<<"data inserted into threads";
	
	// spwan each thread
    int rc;
    for(int i=0; i < threads; i++){

        rc = pthread_create(&pthreads[i], NULL, quicksort_helper, (void *)&td[i]);
        
		if(rc){
            cout<<"Error"<<endl;
            exit(-1);
        }
    }

	//join all the threads after execution
    
	for(int i = 0; i < threads; i++){
       
		pthread_join(pthreads[i], NULL);
    }

	cout<<endl<<"all threads joined";
	cout<<endl<<"Sorted files created";

	auto stop_Sorting = high_resolution_clock::now();
	auto duration_sorting = duration_cast<microseconds>(stop_Sorting - start_Sorting);
	
	cout<<endl<<"time for sorting ============== "<<duration_sorting.count();	


}


void multiThreading_helper(int num_threads, long long int size, ifstream &input){
		
	//number of times the mutithgreading should be involked
	int number_runs = size/4000000000;
		
	int fileName = 0;
	
	for(int i = 0; i < number_runs; i++){
				
			multithreading_on_eightGB(num_threads, input, fileName);
			fileName += num_threads;
		
	}		
	auto start_Merging = high_resolution_clock::now();
	mergeFiles(number_runs * num_threads);
	auto stop_Merging = high_resolution_clock::now();
	auto duration_Merging = duration_cast<microseconds>(stop_Merging - start_Merging);
    cout<<endl<<"time for Merging ============== "<<duration_Merging.count()<<endl;		
}

	
int main(int arg, char *argc[]){
    //auto start = high_resolution_clock::now();
    
    ifstream input("8GB");
    
    int num_partitions = atoi(argc[1]);
    long long int size = atol(argc[2]);
    
    
	long long int fourGB = 4000000000;
	
	if(size < fourGB){
		cout<<"inside if";
		without_Threading(size, input);
	}
	else{
		cout<<"inside else";
		multiThreading_helper(num_partitions, size, input);
		
	}

    //auto stop = high_resolution_clock::now();
    //auto duration = duration_cast<microseconds>(stop - start);
    cout<<endl<<"###############################################"<<endl;
    cout<<"Number of partitions: "<<num_partitions<<endl;
    cout<<"Total file size: "<<size<<" bytes"<<endl;
    //cout<<"Total Execution time: "<<duration.count()<<" microseconds"<<endl;
    cout<<"###############################################"<<endl<<endl;
    return 0;
}

