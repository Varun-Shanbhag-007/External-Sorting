import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Driver {
    public static void main(String[] args) {

        //Data File name
        String fname = args[0];

        System.out.println(fname);

        File file = new File(fname);

        int chunk = 1000; //8Gb oor 4gb

        int noOfFiles = divideFiletoChunks(file, chunk);

        System.out.println("Number of Temp Files Created : " + noOfFiles);

        sortTempFiles(noOfFiles);

        System.out.println("Temp Files are sorted");

        

    }


    public static int divideFiletoChunks(File file, long chunk) {
        int numberOfFiles = 0;

        long fileSize = file.length();

        Scanner sc = null;

        try {
            sc = new Scanner(file);

            long size = file.length();

            while (sc.hasNextLine()) {

                int counter = (int) (fileSize / chunk);

                File f = new File(String.valueOf(numberOfFiles));
                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                while (counter != 0) {
                    writer.write(sc.nextLine());
                    writer.write("\n");
                    counter--;

                }

                writer.close();
                numberOfFiles++;
            }

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
            } catch (IOException e) {
                e.printStackTrace();
            }

            noOfFiles--;
        }
    }
}
