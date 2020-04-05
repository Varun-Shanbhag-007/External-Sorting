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

using namespace std;
using namespace std::chrono;


FILE* openfile(char* filename, char* mode){
    FILE* fp = fopen(filename, mode);
    if(fp == NULL){
        perror("Cannot open file");
        exit(0);
    }
    return fp;
}


vector<string> quicksort(vector<string> &data, int left, int right){
    int i= left;
    int j = right;
    string mid = data[(left+right)/2];

    while(i<=j){
        while(strcmp(data[i].c_str(), mid.c_str()) < 0)
            i++;
        while(strcmp(mid.c_str(), data[j].c_str()) < 0)
            j--;
        if(i<=j){
            swap(data[i], data[j]);
            i++;
            j--;
        }
    }

    if(left<j)
        quicksort(data, left, j);
    if(i<right)
        quicksort(data, i, right);
    
    return data;
}

int main(int arg, char *argc[]){

    auto start = high_resolution_clock::now();

    // get data from original file
    ifstream input("data.txt");

    // number of partitions to make
    // user command line input
    int num_partitions = atoi(argc[1]);

    // size in bytes of the original data file
    // user command line input
    int size = atoi(argc[2]);

    // number of lines in original data file
    // every line in data file is of 100 bytes
    int num_lines = size/100;
    
    // number of lines in every partition -- temp file
    int lines_in_temp_file = num_lines/num_partitions;

    // create file pointers for all temp files
    FILE* temp[num_partitions];

    char filename[4];

    for(int i=0; i<num_partitions; i++){
        // covert i to string
        snprintf(filename, sizeof(filename), "%d", i);
        // open temp file in write mode
        temp[i] = openfile(filename, "w");
    }


    /*
    Get data from original file and write to temp files
    */

    string line;
    for(int i=0; i<num_partitions; i++){

        // cout<<"Writing to file #"<<i<<endl;
        ofstream output(to_string(i));
        // num of lines to be copied into each temp file
        for(int j=0; j<lines_in_temp_file; j++){
            getline(input, line);
            output<<line<<endl;
        }
        output.close();
    }

    /*
    Sort individual temp files using quick sort
    */
    vector<string> data;
    for(int i=0; i<num_partitions; i++){
        ifstream input(to_string(i));
        while(getline(input, line))
            data.push_back(line);
        data = quicksort(data, 0, data.size()-1);
        input.close();
        ofstream output(to_string(i));
        for(string sorted_line : data)
            output<<sorted_line<<endl;
        output.close();
        data.clear();
    }

    auto stop = high_resolution_clock::now();

    auto duration = duration_cast<microseconds>(stop - start);

    cout<<endl<<"###############################################"<<endl;
    cout<<"Number of partitions: "<<num_partitions<<endl;
    cout<<"Total lines: "<<num_lines<<endl;
    cout<<"Lines in each file: "<<lines_in_temp_file<<endl;
    cout<<"Total file size: "<<size<<" bytes"<<endl;

    cout<<"Total Execution time: "<<duration.count()<<" microseconds"<<endl;
    cout<<"###############################################"<<endl<<endl;
    return 0;
}
