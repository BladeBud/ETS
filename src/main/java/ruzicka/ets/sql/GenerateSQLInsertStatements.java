package ruzicka.ets.sql;

import java.util.*;

/**
 * Generates SQL INSERT statements for the "misto" table.
 */
public class GenerateSQLInsertStatements {
//----------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        // Pre-defined available quantities for stul IDs
        Map<Integer, Integer> stulData = new HashMap<>();

        // Default quantity is 6
        for (int id = 1; id <= 175; id++) {
            stulData.put(id, 6);
        }

        // Overwriting specific ID quantities
        stulData.put(1, 4);
        stulData.put(31, 4);
        stulData.put(32, 4);
        stulData.put(6, 4);
        stulData.put(7, 4);

        stulData.put(167, 10);
        stulData.put(157, 10);
        stulData.put(158, 10);
        stulData.put(159, 10);
        stulData.put(170, 10);

        stulData.put(164, 12);

        StringBuilder sqlStatements = new StringBuilder();
        int idmisto = 1;
        int globalPoradi = 1;  // Initialize a global counter for poradi

        // Generating SQL statements based on available quantities
        for (Map.Entry<Integer, Integer> entry : stulData.entrySet()) {
            int idstul = entry.getKey();
            int availableQuantity = entry.getValue();

            for (int j = 1; j <= availableQuantity; j++) {
                sqlStatements.append(String.format(
                        "INSERT INTO public.misto (idmisto, poradi, idstul, status) VALUES (%d, '%d', %d, 'A');%n",
                        idmisto, globalPoradi, idstul
                ));
                idmisto++;
                globalPoradi++;  // Increment the global counter
            }
        }

        // Print the generated SQL statements
        System.out.println(sqlStatements.toString());
    }
}