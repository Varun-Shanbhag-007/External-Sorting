import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Driver {

    static BufferedReader[] filePointers ;
    static long chunk_file_size;

    public static void main(String[] args) {

        //Data File name
        String fName = args[0];

        File file = new File(fName);

         //8Gb or 4gb
        long maxRam = 8000000000L;

        if(file.length() > maxRam){
            long start = System.currentTimeMillis();
            int chunk = (int) (file.length()/500000000L);
            externalSort(file,chunk);
            long end = System.currentTimeMillis();
            System.out.println("Sorted " + fName + " in :" + (end-start));
            System.out.println("*** Units are in Millis");
        }
        else{
            long start = System.currentTimeMillis();
            inMemorySort(fName);
            long end = System.currentTimeMillis();
            System.out.println("Sorted " + fName + " in :" + (end-start));
            System.out.println("*** Units are in Millis");
        }

    }

    public static void externalSort(File file,int chunk){

        System.out.println(chunk+" Temp Files are being Generated");

        chunk_file_size = file.length()/(chunk*100);

        long start = System.currentTimeMillis();

        //divideFileToChunks(file);

        filePointers = new BufferedReader[chunk];

        long finish = System.currentTimeMillis();

        long timeElapsed = finish - start;

        System.out.println( chunk + " Temp Files Created in: " + timeElapsed);

        start = System.currentTimeMillis();

        //Sort Temp Files

        sortTempFilesMT(file,chunk);

        finish = System.currentTimeMillis();

        timeElapsed = finish - start;

        System.out.println("Temp Files are sorted in :"+timeElapsed);

        start = System.currentTimeMillis();

        //Merge the sorted files
        mergeKSortedArrays(chunk,file.length()/100 , file.getName());

        finish = System.currentTimeMillis();

        timeElapsed = finish - start;

        System.out.println("Temp Files merged in: "+timeElapsed);

        deleteTempFiles(chunk);

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

    public static void divideFileToChunks(File file) {
        int numberOfFiles = 0;

        BufferedReader br = null;
        try {
            br   = new BufferedReader(new FileReader(file));

            long size = file.length();

            String contentLine = br.readLine();
            while (contentLine != null) {

                long counter =  chunk_file_size;

                File f = new File(String.valueOf(numberOfFiles));
                FileWriter fw = new FileWriter(f);
                BufferedWriter writer = new BufferedWriter(fw);
                while (counter != 0) {
                    writer.write(contentLine);
                    writer.write(System.lineSeparator());
                    contentLine = br.readLine();
                    counter--;

                }

                writer.close();
                fw.close();
                numberOfFiles++;
            }

            br.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void sortTempFilesMT(File file , int totalFiles) {


        BufferedReader br = null;
        int loops = totalFiles/8;
        int counter = 0;
        try {
                br   = new BufferedReader(new FileReader(file));
                String contentLine = br.readLine();

                while (loops > 0 && contentLine != null) {

                    ArrayList<Thread> threadPool = new ArrayList<>();


                    for (int i = 0; i < 8; i++) {

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

    public static void mergeKSortedArrays(int noOfFiles, long size, String fname) {

        File f = new File("Sorted"+fname);
        FileWriter fw ;
        BufferedWriter writer ;
        try {
            fw =new FileWriter(f);
            writer =  new BufferedWriter(fw);

        PriorityQueue<Driver.HeapNode> minHeap =
                new PriorityQueue<>(noOfFiles,
                        (Driver.HeapNode a, Driver.HeapNode b) -> a.value.compareTo(b.value));


        //add first elements in the array to this heap
        for (int i = 0; i < noOfFiles; i++) {
            minHeap.add(new Driver.HeapNode(i, 0, getLineFromFile(i, 0)));
        }

        //Complexity O(n * k * log k)
        for (int i = 0; i < size; i++) {
            //Take the minimum value and put into result
            Driver.HeapNode node = minHeap.poll();

            if (node != null) {
                writer.write(node.value);
                //gensorts add \r\n to every line so adding same to merged sorted file to keep size same
                writer.write("\r\n");
                if (node.index + 1 < chunk_file_size ) {
                    //Complexity of O(log k)
                    String val = getLineFromFile(node.fileNum,node.index + 1);
                    minHeap.add(new Driver.HeapNode(node.fileNum,
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
