import java.io.*;
import java.util.ArrayList;

public class SortHelper implements Runnable {


    File file ;

    public SortHelper(File file) {
        this.file = file;
    }


    @Override
    public void run() {
        BufferedReader br = null;

        ArrayList<String> lines = new ArrayList<>();

        try {
            br   = new BufferedReader(new FileReader(file));

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
        quickSort(lines,0,lines.size()-1);

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(file,false));
            for (int i = 0; i < lines.size(); i++) {
                String x = lines.get(i);
                writer.write(x);
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
