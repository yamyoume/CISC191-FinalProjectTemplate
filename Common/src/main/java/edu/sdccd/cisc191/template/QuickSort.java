package edu.sdccd.cisc191.template;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.IntStream;


public class QuickSort {
    public static void main(String[] args) {
    }

    public static void quickSort(ArrayList<Entry> arr, String field, int low, int high) {
        if (low < high) {
            int partitionIndex = partition(arr, field, low, high);

            quickSort(arr, field, low, partitionIndex - 1);
            quickSort(arr, field, partitionIndex + 1, high);
        }
    }

    public static int partition(ArrayList<Entry> arr, String field, int low, int high) {
        String pivot = "";
        final int[] i = {0};
        if (field == "Description") {
            pivot = arr.get(high).getDescription();
            i[0] = low - 1;

            String finalPivot = pivot;
            IntStream.range(low, high)
                    .filter(j -> arr.get(j).getDescription().compareTo(finalPivot) <= 0)
                    .forEach(j -> {
                        i[0]++;
                        swap(arr, i[0], j);
                    });

        }
        if (field == "Amount Low-to-High") {
            double dPivot = Double.parseDouble(arr.get(high).getAmount());
            i[0] = low - 1;

            IntStream.range(low, high)
                    .filter(j -> Double.parseDouble(arr.get(j).getAmount()) <= dPivot)
                    .forEach(j -> {
                        i[0]++;
                        swap(arr, i[0], j);
                    });

        }
        if (field == "Amount High-to-Low") {
            double dPivot = Double.parseDouble(arr.get(high).getAmount());
            i[0] = low - 1;

            IntStream.range(low, high)
                    .filter(j -> Double.parseDouble(arr.get(j).getAmount()) >= dPivot)
                    .forEach(j -> {
                        i[0]++;
                        swap(arr, i[0], j);
                    });
        }
        if (field == "Date") {
            pivot = arr.get(high).getDate().toString();
            i[0] = low - 1;

            String finalPivot1 = pivot;
            IntStream.range(low, high)
                    .filter(j -> arr.get(j).getDate().compareTo(Timestamp.valueOf(finalPivot1)) <= 0)
                    .forEach(j -> {
                        i[0]++;
                        swap(arr, i[0], j);
                    });

        }

        swap(arr, i[0] + 1, high);

        return i[0] + 1;
    }

    public static void swap(ArrayList<Entry> arr, int i, int j) {
        Entry temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }
}
