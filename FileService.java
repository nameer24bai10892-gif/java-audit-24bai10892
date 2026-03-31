package service;

import model.Budget;
import model.Category;
import model.Transaction;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileService {

    private static final String DATA_DIR = "data";
    private static final String TX_FILE  = DATA_DIR + "/transactions.csv";
    private static final String BUD_FILE = DATA_DIR + "/budgets.csv";

    public FileService() {
        // make sure the data folder exists
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> result = new ArrayList<>();
        Path p = Paths.get(TX_FILE);

        if (!Files.exists(p)) return result; 

        try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    result.add(Transaction.fromCsv(line));
                } catch (Exception e) {
                    System.err.println("Warning: skipping bad line -> " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Couldn't load transactions: " + e.getMessage());
        }

        return result;
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TX_FILE))) {
            pw.println("# transactions - do not edit manually");
            for (Transaction t : transactions) {
                pw.println(t.toCsv());
            }
        } catch (IOException e) {
            System.err.println("Couldn't save transactions: " + e.getMessage());
        }
    }

    public Map<Category, Budget> loadBudgets() {
        Map<Category, Budget> result = new HashMap<>();
        Path p = Paths.get(BUD_FILE);

        if (!Files.exists(p)) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    Budget b = Budget.fromCsv(line);
                    result.put(b.getCategory(), b);
                } catch (Exception e) {
                    System.err.println("Warning: skipping bad budget line -> " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Couldn't load budgets: " + e.getMessage());
        }

        return result;
    }

    public void saveBudgets(Map<Category, Budget> budgets) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BUD_FILE))) {
            pw.println("# budgets - category,monthly_limit");
            for (Budget b : budgets.values()) {
                pw.println(b.toCsv());
            }
        } catch (IOException e) {
            System.err.println("Couldn't save budgets: " + e.getMessage());
        }
    }
}
