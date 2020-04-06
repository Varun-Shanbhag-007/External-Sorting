import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Driver {

    public static void main(String[] args) {

        //Data File name
        String fname = args[0];

        System.out.println(fname);

        File file = new File(fname);

        int chunk = 10; //8Gb or 4gb
        long start = System.currentTimeMillis();
        int noOfFiles = divideFileToChunks(file, chunk);

        System.out.println("Number of Temp Files Created : " + noOfFiles);

        //Sort Temp Files
        sortTempFiles(noOfFiles);

        long finish = System.currentTimeMillis();

        long timeElapsed = finish - start;

        System.out.println("Temp Files are sorted in :"+timeElapsed);

        start = System.currentTimeMillis();

        //Merge the sorted files
        mergeKSortedArrays(noOfFiles,file.length()/100);

        finish = System.currentTimeMillis();

        timeElapsed = finish - start;

        System.out.println("Temp Files merged in :"+timeElapsed);

        //deleteTempFiles(noOfFiles);



    }

    public static int divideFileToChunks(File file, int chunk) {
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

        return numberOfFiles;

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
            Collections.sort(lines);

            try {
                PrintWriter pw = new PrintWriter(file); //this  line truncate the file

                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (String x : lines) {
                    writer.write(x);
                    writer.write("\n");
                }

                writer.close();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            noOfFiles--;
        }
    }

    public static String getLineFromFile(File f , int line){

        int totalLines = (int)Math.ceil((float)f.length()/100);
        String text = "";

        if(line > totalLines)
            return text;
        try {
            RandomAccessFile access = new RandomAccessFile(f, "r");
            access.seek(line*100);
            text = access.readLine().trim();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
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
            minHeap.add(new Driver.HeapNode(i, 0, getLineFromFile(new File(String.valueOf(i)),0)));
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
                    String val = getLineFromFile(new File(String.valueOf(node.fileNum)), node.index + 1);
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
}
