package ruzicka.ets.sql;

/**
 * @author czech
 * @since 2024-10-10
 */
public class SqlFillerMisto {

    public static void main(String[] args) {
        StringBuilder sql = new StringBuilder();
        int idMisto = 1; // Start from the first idMisto

        // Generate SQL for sezení s výhledem (with a view) - 78 tables with 6 seats, 5 tables with 4 seats
        int currentAddress = 1;
        for (int i = 1; i <= 78; i++) {
            sql.append(generateInsert(idMisto++, currentAddress++, 2, 6, 6));
        }
        for (int i = 1; i <= 5; i++) {
            sql.append(generateInsert(idMisto++, currentAddress++, 2, 4, 4));
        }

        // Generate SQL for sezení bez výhledem (without a view) - 76 tables with 6 seats
        for (int i = 1; i <= 76; i++) {
            sql.append(generateInsert(idMisto++, currentAddress++, 3, 6, 6));
        }

        // Generate SQL for lóže (lodge) - 11 tables with 6 seats, 5 tables with 10 seats
        for (int i = 1; i <= 11; i++) {
            sql.append(generateInsert(idMisto++, currentAddress++, 4, 6, 6));
        }
        for (int i = 1; i <= 5; i++) {
            sql.append(generateInsert(idMisto++, currentAddress++, 4, 10, 10));
        }

        // Output the generated SQL
        System.out.println(sql);
    }

    // Helper method to generate the SQL insert statement
    private static String generateInsert(int idMisto, int adresa, int idTypMista, int availableQuantity, int quantitySum) {
        return String.format("INSERT INTO misto (idmisto, adresa, idtypmista, avaiablequantity, quantitysum) VALUES (%d, %d, %d, %d, %d);\n",
                idMisto, adresa, idTypMista, availableQuantity, quantitySum);
    }
}
