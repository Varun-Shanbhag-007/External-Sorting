import java.util.ArrayList;

public class QSImpl {
    public static void main(String[] args) {
        ArrayList<String> al = new ArrayList<>();
        al.add("Z");
        al.add("A");
        al.add("A");
        al.add("A");
        al.add("A");
        al.add("R");
        al.add("R");
        al.add("R");
        al.add("R");
        al.add("R");
        al.add("R");
        al.add("R");
        al.add("B");
        System.out.println(al);

        quickSort(al,0,al.size()-1);

        System.out.println(al);



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
