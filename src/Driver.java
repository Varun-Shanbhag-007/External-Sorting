import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Driver {

    static BufferedReader[] filePointers ;

    public static void main(String[] args) {

        //Data File name
        String fname = args[0];

        System.out.println(fname);

        File file = new File(fname);

        int chunk = 10; //8Gb or 4gb
        long start = System.currentTimeMillis();
        divideFileToChunks(file, chunk);

        filePointers = new BufferedReader[chunk];
        System.out.println("Number of Temp Files Created : " + chunk);

        //Sort Temp Files
        //sortTempFiles(noOfFiles);
        sortTempFilesMT(chunk);

        long finish = System.currentTimeMillis();

        long timeElapsed = finish - start;

        System.out.println("Temp Files are sorted in :"+timeElapsed);

        start = System.currentTimeMillis();

        //Merge the sorted files
        mergeKSortedArrays(chunk,file.length()/100);

        finish = System.currentTimeMillis();

        timeElapsed = finish - start;

        System.out.println("Temp Files merged in :"+timeElapsed);

        deleteTempFiles(chunk);



    }

    public static void divideFileToChunks(File file, long chunk) {
        int numberOfFiles = 0;

        long fileSize = file.length();

        Scanner sc = null;
        try {
            sc = new Scanner(file);

            long size = file.length();

            while (sc.hasNextLine()) {

                int counter = (int) (fileSize / (chunk*100));

                File f = new File(String.valueOf(numberOfFiles));
                FileWriter fw = new FileWriter(f);
                BufferedWriter writer = new BufferedWriter(fw);
                while (counter != 0) {
                    writer.write(sc.nextLine());
                    writer.write(" ");
                    writer.write(System.lineSeparator());
                    counter--;

                }

                writer.close();
                fw.close();
                numberOfFiles++;
            }

            sc.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void sortTempFiles(int noOfFiles) {

        noOfFiles = noOfFiles-1;

        while (noOfFiles >= 0) {

            File file = new File(String.valueOf(noOfFiles));

            Scanner sc = null;

            ArrayList<String> lines = new ArrayList<>();

            try {
                sc = new Scanner(file);

                while (sc.hasNextLine()) {
                    lines.add(sc.nextLine());
                }

                sc.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //sort lines of a given file
            //Collections.sort(lines); //inbuilt sort function
            quickSort(lines,0,lines.size()-1);

            try {

                BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
                for (String x : lines) {
                    writer.write(x);
                    writer.write("\n");
                }

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            noOfFiles--;
        }
    }

    public static void sortTempFilesMT(int noOfFiles) {

        noOfFiles = noOfFiles-1;

        while (noOfFiles >= 0) {

            File file = new File(String.valueOf(noOfFiles));

            SortHelper r = new SortHelper(file);
            Thread t = new Thread(r);
            t.run();

            noOfFiles--;
        }
    }

    public static String getLineFromFile(int f ,int line){
        String val = "" ;
        File file = new File(String.valueOf(f));
        int totalLines = (int)Math.ceil((float)file.length()/100);

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

    public static void mergeKSortedArrays(int noOfFiles, long size) {

        File f = new File("Sorted");
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
                writer.write(System.lineSeparator());
                if (node.index + 1 < (int)Math.ceil((float)new File(String.valueOf(node.fileNum)).length()/100) ) {
                    //Complexity of O(log k)
                    String val = getLineFromFile(node.fileNum,node.index + 1);
                    if(!val.equalsIgnoreCase(""))
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
        public int index;
        public String value;

        public HeapNode(int fileNum, int index, String value) {
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
