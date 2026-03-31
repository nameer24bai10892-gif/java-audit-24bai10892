package util;

import model.Budget;
import model.Category;
import model.Transaction;
import service.ExpenseService;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// all the printing/formatting lives here so Main.java stays clean
public class ReportGenerator {

    private static final String LINE  = "─".repeat(58);
    private static final String DLINE = "═".repeat(58);

    private final ExpenseService service;

    public ReportGenerator(ExpenseService service) {
        this.service = service;
    }

    public void printMonthlySummary(int year, Month month) {
        List<Transaction> txns = service.getByMonth(year, month);
        String label = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;

        System.out.println("\n" + DLINE);
        System.out.printf("  📊  Monthly Summary — %s%n", label);
        System.out.println(DLINE);

        if (txns.isEmpty()) {
            System.out.println("  No transactions for this period.");
            System.out.println(DLINE + "\n");
            return;
        }

        double income   = service.totalIncome(txns);
        double expenses = service.totalExpenses(txns);
        double balance  = service.netBalance(txns);

        System.out.printf("  %-22s ₹%,.2f%n", "Total Income:", income);
        System.out.printf("  %-22s ₹%,.2f%n", "Total Expenses:", expenses);
        System.out.println(LINE);
        System.out.printf("  %-22s %s₹%,.2f%n", "Net Balance:", balance >= 0 ? "+" : "", balance);
        System.out.println(DLINE);

        Map<Category, Double> breakdown = service.spendingByCategory(txns);
        if (!breakdown.isEmpty()) {
            System.out.println("\n  💸  Spending by Category:\n");
            for (Map.Entry<Category, Double> entry : breakdown.entrySet()) {
                Category cat = entry.getKey();
                double spent = entry.getValue();

                System.out.printf("  %-26s ₹%,.2f%n", cat.toString(), spent);

                // show budget bar if one is set for this category
                service.getBudget(cat).ifPresent(b ->
                    System.out.printf("  %s%n", b.progressBar(spent))
                );
            }
        }
        System.out.println("\n" + DLINE + "\n");
    }

    public void printBudgetStatus(int year, Month month) {
        Map<Category, Budget> budgets = service.getAllBudgets();

        if (budgets.isEmpty()) {
            System.out.println("  No budgets set. Use option 5 to add one.\n");
            return;
        }

        String label = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year;
        List<Transaction> txns = service.getByMonth(year, month);
        Map<Category, Double> spending = service.spendingByCategory(txns);

        System.out.println("\n" + DLINE);
        System.out.printf("  🎯  Budget Status — %s%n", label);
        System.out.println(DLINE);

        for (Map.Entry<Category, Budget> entry : budgets.entrySet()) {
            Category cat = entry.getKey();
            Budget b = entry.getValue();
            double spent = spending.getOrDefault(cat, 0.0);

            System.out.printf("%n  %s%n", cat);
            System.out.printf("  Spent: ₹%,.2f  /  Limit: ₹%,.2f%n", spent, b.getLimit());
            System.out.printf("  %s%n", b.progressBar(spent));
        }

        System.out.println("\n" + DLINE + "\n");
    }

    public void printTransactions(List<Transaction> txns, String heading) {
        System.out.println("\n" + DLINE);
        System.out.printf("  📋  %s%n", heading);
        System.out.println(DLINE);
        if (txns.isEmpty()) {
            System.out.println("  Nothing here.");
        } else {
            for (Transaction t : txns) {
                System.out.println("  " + t);
            }
        }
        System.out.println(DLINE + "\n");
    }

    public void printOverallSummary() {
        List<Transaction> all = service.getAllTransactions();

        System.out.println("\n" + DLINE);
        System.out.println("  📈  Overall Summary (All Time)");
        System.out.println(DLINE);

        if (all.isEmpty()) {
            System.out.println("  No transactions yet.\n");
            System.out.println(DLINE + "\n");
            return;
        }

        double income   = service.totalIncome(all);
        double expenses = service.totalExpenses(all);
        double balance  = service.netBalance(all);

        System.out.printf("  %-24s %d%n",    "Total Transactions:", all.size());
        System.out.printf("  %-24s ₹%,.2f%n", "Total Income:",       income);
        System.out.printf("  %-24s ₹%,.2f%n", "Total Expenses:",     expenses);
        System.out.println(LINE);
        System.out.printf("  %-24s %s₹%,.2f%n", "Net Balance:", balance >= 0 ? "+" : "", balance);
        System.out.println(LINE);

        System.out.println("\n  Top Spending Categories:\n");
        Map<Category, Double> breakdown = service.spendingByCategory(all);
        int i = 1;
        for (Map.Entry<Category, Double> e : breakdown.entrySet()) {
            System.out.printf("  %d. %-26s ₹%,.2f%n", i++, e.getKey(), e.getValue());
            if (i > 5) break;
        }
        System.out.println("\n" + DLINE + "\n");
    }
}
