import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class MySort {

    static BufferedReader[] filePointers ;
    static ArrayList<ArrayList<String>> allList = new ArrayList<>();
    static long chunk_file_size;
    //8Gb or 4gb
    static long  maxRam = 8000000000L;

    public static void main(String[] args) {

        //Data File name
        String fName = args[0];
        String param2 = args[1];

        int noOfThreads = Integer.valueOf(param2);

        File file = new File(fName);

        z: if(file.length() > maxRam){

            if(noOfThreads <= 1 ){
                System.out.println("Please use more than one thread. Number of threads should be in power of 2.");
                break z;
            }

            long sizerPerChunk = maxRam/noOfThreads;

            System.out.println("\nSize of Temp file: " + sizerPerChunk + " bytes");

            chunk_file_size = sizerPerChunk/100;

            long start = System.currentTimeMillis();

            //each chunks of 500MB == 500000000L
            int chunks = (int)Math.ceil ((float)file.length()/sizerPerChunk);

            externalSort(file,chunks,noOfThreads);

            long end = System.currentTimeMillis();
            System.out.println("Sorted " + fName + " in :" + (end-start)/1000);
            System.out.println("*** Units are in Seconds");

        }
        else{
            long sizerPerChunk = file.length()/noOfThreads;

            System.out.println("\nSize of Temp file: " + sizerPerChunk+" bytes");

            chunk_file_size = sizerPerChunk/100;

            long start = System.currentTimeMillis();

            if(noOfThreads > 1){
                System.out.println("Using "+noOfThreads+" threads");
                inMemorySortMT(fName,noOfThreads);
            }
            else {
                System.out.println("Using Single Thread");
                inMemorySort(fName);
            }
            long end = System.currentTimeMillis();
            System.out.println("Sorted " + fName + " in :" + (end-start)/1000);
            System.out.println("*** Units are in Seconds");
        }

    }

    public static void externalSort(File file,int chunks,int noOfThreadsperLoop){
        //array for temp file pointers
        filePointers = new BufferedReader[chunks];

        //chunk_file_size = file.length()/(chunks*100);

        long start = System.currentTimeMillis();

        sortTempFilesMT(file,noOfThreadsperLoop);

        long finish = System.currentTimeMillis();

        long timeElapsed = finish - start;

        System.out.println("Temp Files are created & sorted in: "+timeElapsed);

        start = System.currentTimeMillis();

        //Merge the sorted files
        mergeKSortedArrays(chunks,file.length()/100 , file.getName());

        finish = System.currentTimeMillis();

        timeElapsed = finish - start;

        System.out.println("Temp Files merged in: "+timeElapsed);

        //Delete Temp Files
        deleteTempFiles(chunks);

    }

    public static void inMemorySort(String fName) {
        File file = new File(fName);
        BufferedReader br = null;

        ArrayList<String> lines = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(file));

            String contentLine = br.readLine();
            while (contentLine != null) {
                lines.add(contentLine);
                contentLine = br.readLine();
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sort lines of a given file
        quickSort(lines, 0, lines.size() - 1);

        try {
            File sortedFile = new File(fName+"Sorted");
            BufferedWriter writer = new BufferedWriter(new FileWriter(sortedFile, false));
            for (int i = 0; i < lines.size(); i++) {
                String x = lines.get(i);
                writer.write(x);
                writer.write("\r\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void inMemorySortMT(String fName,int noOfThreads) {

        File file = new File(fName);
        BufferedReader br = null;
        int counter = noOfThreads;

        ArrayList<Thread> threadPool = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(file));
            String contentLine = br.readLine();

                while(counter > 0 && contentLine != null){

                    long size =  chunk_file_size;

                    ArrayList<String> lines = new ArrayList<>();
                    allList.add(lines);
                        while (size != 0 && contentLine != null) {
                            lines.add(contentLine);
                            contentLine = br.readLine();
                            size--;

                        }

                    InMemorySortHelper r = new InMemorySortHelper(lines);
                    Thread t = new Thread(r);
                    t.start();
                    threadPool.add(t);
                    counter--;
                }
        br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Thread thread : threadPool) {
            try {
                thread.join();
                thread.interrupt();
                Runtime r = Runtime.getRuntime();
                r.gc();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

        mergeKSortedArrayList(noOfThreads,file.length()/100 , file.getName());



    }

    public static void sortTempFilesMT(File file , int noOfThreadsperLoop) {


        BufferedReader br = null;
        int loops = (int)(file.length()/maxRam);

        int counter = 0;
        try {
            br   = new BufferedReader(new FileReader(file));
            String contentLine = br.readLine();

            while (loops > 0 && contentLine != null) {

                ArrayList<Thread> threadPool = new ArrayList<>();


                for (int i = 0; i < noOfThreadsperLoop; i++) {

                    long size =  chunk_file_size;

                    ArrayList<String> lines = new ArrayList<>();

                    while (size != 0) {
                        lines.add(contentLine);
                        contentLine = br.readLine();
                        size--;

                    }
                    SortHelper r = new SortHelper(counter,lines);
                    Thread t = new Thread(r);
                    t.start();
                    threadPool.add(t);
                    counter++;

                }

                for (Thread thread : threadPool) {
                    try {
                        thread.join();
                        thread.interrupt();
                        Runtime r = Runtime.getRuntime();
                        r.gc();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                loops--;
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLineFromFile(int f ,long line){
        String val = "" ;
        File file = new File(String.valueOf(f));
        long totalLines = chunk_file_size;

        try {
            if (line == 0) {
                FileReader readfile = new FileReader(file);
                BufferedReader readbuffer = new BufferedReader(readfile);
                val = readbuffer.readLine();
                filePointers[f] = readbuffer;

            }
            else {
                val = filePointers[f].readLine();
                if(totalLines == line+1){
                    filePointers[f].close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return val;

    }

    public static String getLineFromArrayList(int f,long line){
        String val = "" ;
        val = allList.get(f).get((int)line);
        return val;

    }

    public static void mergeKSortedArrays(int noOfFiles, long size, String fname) {
        File f = new File("Sorted"+fname);
        FileWriter fw ;
        BufferedWriter writer ;
        try {
            fw =new FileWriter(f);
            writer =  new BufferedWriter(fw);

            PriorityQueue<MySort.HeapNode> minHeap =
                    new PriorityQueue<>(noOfFiles,
                            (MySort.HeapNode a, MySort.HeapNode b) -> a.value.compareTo(b.value));


            //add first elements in the array to this heap
            for (int i = 0; i < noOfFiles; i++) {
                minHeap.add(new MySort.HeapNode(i, 0, getLineFromFile(i, 0)));
            }

            //Complexity O(n * k * log k)
            for (int i = 0; i < size; i++) {
                //Take the minimum value and put into result
                MySort.HeapNode node = minHeap.poll();

                if (node != null) {
                    writer.write(node.value);
                    //gensorts add \r\n to every line so adding same to merged sorted file to keep size same
                    writer.write("\r\n");
                    if (node.index + 1 < chunk_file_size ) {
                        //Complexity of O(log k)
                        String val = getLineFromFile(node.fileNum,node.index + 1);
                        minHeap.add(new MySort.HeapNode(node.fileNum,
                                node.index + 1,
                                val));
                    }
                }
            }

            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void mergeKSortedArrayList(int noOfFiles, long size, String fname) {
        File f = new File("Sorted"+fname);
        FileWriter fw ;
        BufferedWriter writer ;
        try {
            fw =new FileWriter(f);
            writer =  new BufferedWriter(fw);

            PriorityQueue<MySort.HeapNode> minHeap =
                    new PriorityQueue<>(noOfFiles,
                            (MySort.HeapNode a, MySort.HeapNode b) -> a.value.compareTo(b.value));


            //add first elements in the array to this heap
            for (int i = 0; i < noOfFiles; i++) {
                minHeap.add(new MySort.HeapNode(i, 0, getLineFromArrayList(i, 0)));
            }

            //Complexity O(n * k * log k)
            for (int i = 0; i < size; i++) {
                //Take the minimum value and put into result
                MySort.HeapNode node = minHeap.poll();

                if (node != null) {
                    writer.write(node.value);
                    //gensorts add \r\n to every line so adding same to merged sorted file to keep size same
                    writer.write("\r\n");
                    if (node.index + 1 < chunk_file_size ) {
                        //Complexity of O(log k)
                        String val = getLineFromArrayList(node.fileNum,node.index + 1);
                        minHeap.add(new MySort.HeapNode(node.fileNum,
                                node.index + 1,
                                val));
                    }
                }
            }

            writer.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void deleteTempFiles(int noOfFiles){
        for(int i=0;i< noOfFiles;i++){

            Path filePath = Paths.get(String.valueOf(i));

            try {
                Files.delete(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class HeapNode {
        public int fileNum;
        public long index;
        public String value;

        public HeapNode(int fileNum, long index, String value) {
            this.fileNum = fileNum;
            this.index = index;
            this.value = value;
        }
    }

    static  int partition(ArrayList<String> a, int low, int high)
    {
        String pivot = a.get(high);
        int i = (low-1); // index of smaller element
        for (int j=low; j<high; j++)
        {
            // If current element is smaller than or
            // equal to pivot
            if (a.get(j).compareTo(pivot) <= 0)
            {
                i++;

                String temp = a.get(i);
                a.set(i, a.get(j));
                a.set(j,temp);
            }
        }

        String temp = a.get(i+1);
        a.set(i+1, a.get(high));
        a.set(high,temp);

        return i+1;
    }

    static void quickSort(ArrayList<String> a, int low, int high)
    {
        if (low < high)
        {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(a, low, high);

            // Recursively sort elements before
            // partition and after partition
            quickSort(a, low, pi-1);
            quickSort(a, pi+1, high);
        }
    }
}