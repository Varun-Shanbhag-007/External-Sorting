import java.io.*;
import java.util.ArrayList;

public class InMemorySortHelper implements Runnable {

    ArrayList<String> lines;


    public InMemorySortHelper( ArrayList<String> lines) {
        this.lines=lines;
    }


    @Override
    public void run() {
        //sort lines of a given file chunk
        quickSort(lines, 0, lines.size() - 1);

        Runtime r = Runtime.getRuntime();
        r.gc();

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
