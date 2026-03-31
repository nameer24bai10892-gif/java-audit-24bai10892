import model.Category;
import model.Transaction;
import model.Transaction.Type;
import service.ExpenseService;
import util.ReportGenerator;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static ExpenseService service;
    static ReportGenerator reporter;

    public static void main(String[] args) {
        service  = new ExpenseService();
        reporter = new ReportGenerator(service);

        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1" -> addTransaction(Type.EXPENSE);
                case "2" -> addTransaction(Type.INCOME);
                case "3" -> viewTransactions();
                case "4" -> viewMonthlySummary();
                case "5" -> manageBudgets();
                case "6" -> viewBudgetStatus();
                case "7" -> deleteTransaction();
                case "8" -> reporter.printOverallSummary();
                case "0" -> {
                    System.out.println("  Bye! Keep tracking your spending.\n");
                    running = false;
                }
                default -> System.out.println("  Invalid option, try again.\n");
            }
        }
    }

    static void printBanner() {
        System.out.println("""
                
                ╔══════════════════════════════════════════════════════╗
                ║           💰  SmartBudget — Expense Tracker          ║
                ║         Track spending. Set budgets. Save more.      ║
                ╚══════════════════════════════════════════════════════╝
                """);
    }

    static void printMenu() {
        System.out.print("""
                ──────────────────────────────────────────────────────
                  1. Add Expense
                  2. Add Income
                  3. View Transactions
                  4. Monthly Report
                  5. Manage Budgets
                  6. Budget Status (current month)
                  7. Delete a Transaction
                  8. All-Time Summary
                  0. Exit
                ──────────────────────────────────────────────────────
                  Choice:\s""");
    }

    static void addTransaction(Type type) {
        String label = (type == Type.EXPENSE) ? "Expense 💸" : "Income 💰";
        System.out.println("  -- Add " + label + " --\n");

        double amount = promptDouble("  Amount (₹): ");
        Category cat  = promptCategory();

        System.out.print("  Description: ");
        String desc = sc.nextLine().trim();

        if (desc.isBlank()) {
            System.out.println("  Description can't be empty.\n");
            return;
        }

        LocalDate date = promptDate("  Date (YYYY-MM-DD) [leave blank for today]: ");

        try {
            Transaction t = service.addTransaction(type, amount, cat, desc, date);
            System.out.println("\n  Saved: " + t + "\n");
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage() + "\n");
        }
    }

    static void viewTransactions() {
        System.out.print("""
                  a) All
                  b) By month
                  c) By category
                  d) Search keyword
                  Choice:\s""");

        String sub = sc.nextLine().trim().toLowerCase();
        System.out.println();

        switch (sub) {
            case "a" -> reporter.printTransactions(service.getAllTransactions(), "All Transactions");
            case "b" -> {
                int year = promptYear();
                Month month = promptMonth();
                reporter.printTransactions(service.getByMonth(year, month),
                        month.name() + " " + year);
            }
            case "c" -> {
                Category cat = promptCategory();
                reporter.printTransactions(service.filterByCategory(cat),
                        cat.getDisplayName() + " Transactions");
            }
            case "d" -> {
                System.out.print("  Keyword: ");
                String kw = sc.nextLine().trim();
                reporter.printTransactions(service.searchByKeyword(kw), "Results for \"" + kw + "\"");
            }
            default -> System.out.println("  Invalid option.\n");
        }
    }

    static void viewMonthlySummary() {
        int year    = promptYear();
        Month month = promptMonth();
        reporter.printMonthlySummary(year, month);
    }

    static void manageBudgets() {
        System.out.print("""
                  a) Set/update budget
                  b) Remove budget
                  c) List all budgets
                  Choice:\s""");

        String sub = sc.nextLine().trim().toLowerCase();
        System.out.println();

        switch (sub) {
            case "a" -> {
                Category cat  = promptCategory();
                double limit  = promptDouble("  Monthly limit (₹): ");
                service.setBudget(cat, limit);
                System.out.printf("  Budget set: %s → ₹%.2f/month%n%n",
                        cat.getDisplayName(), limit);
            }
            case "b" -> {
                Category cat = promptCategory();
                service.removeBudget(cat);
                System.out.println("  Budget removed.\n");
            }
            case "c" -> {
                var all = service.getAllBudgets();
                if (all.isEmpty()) {
                    System.out.println("  No budgets set yet.\n");
                } else {
                    System.out.println("  Current budgets:\n");
                    all.forEach((cat, b) ->
                        System.out.printf("  %-26s ₹%,.2f / month%n", cat.toString(), b.getLimit())
                    );
                    System.out.println();
                }
            }
            default -> System.out.println("  Invalid option.\n");
        }
    }

    static void viewBudgetStatus() {
        LocalDate now = LocalDate.now();
        reporter.printBudgetStatus(now.getYear(), now.getMonth());
    }

    static void deleteTransaction() {
        System.out.print("  Enter transaction ID: ");
        String id = sc.nextLine().trim().toUpperCase();
        if (service.deleteTransaction(id)) {
            System.out.println("  Deleted " + id + ".\n");
        } else {
            System.out.println("  No transaction found with ID: " + id + "\n");
        }
    }

    // -- input helpers --

    static double promptDouble(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                double val = Double.parseDouble(sc.nextLine().trim());
                if (val > 0) return val;
                System.out.println("  Must be a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("  That's not a valid number.");
            }
        }
    }

    static Category promptCategory() {
        Category[] cats = Category.values();
        System.out.println();
        for (int i = 0; i < cats.length; i++) {
            System.out.printf("  %2d. %s%n", i + 1, cats[i]);
        }
        while (true) {
            System.out.print("  Pick a category (1-" + cats.length + "): ");
            try {
                int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
                if (idx >= 0 && idx < cats.length) return cats[idx];
                System.out.println("  Out of range.");
            } catch (NumberFormatException e) {
                System.out.println("  Enter a number.");
            }
        }
    }

    static LocalDate promptDate(String msg) {
        while (true) {
            System.out.print(msg);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) return LocalDate.now();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("  Use format YYYY-MM-DD (e.g. 2026-03-15)");
            }
        }
    }

    static int promptYear() {
        while (true) {
            System.out.print("  Year (e.g. 2026): ");
            try {
                int y = Integer.parseInt(sc.nextLine().trim());
                if (y >= 2000 && y <= 2100) return y;
                System.out.println("  Enter a valid year.");
            } catch (NumberFormatException e) {
                System.out.println("  Enter a number.");
            }
        }
    }

    static Month promptMonth() {
        Month[] months = Month.values();
        for (int i = 0; i < months.length; i++) {
            System.out.printf("  %2d. %s%n", i + 1, months[i]);
        }
        while (true) {
            System.out.print("  Month (1-12): ");
            try {
                int m = Integer.parseInt(sc.nextLine().trim());
                if (m >= 1 && m <= 12) return months[m - 1];
                System.out.println("  Must be between 1 and 12.");
            } catch (NumberFormatException e) {
                System.out.println("  Enter a number.");
            }
        }
    }
}
