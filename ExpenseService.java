package service;

import model.Budget;
import model.Category;
import model.Transaction;
import model.Transaction.Type;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseService {

    private final FileService fileService;
    private final List<Transaction> transactions;
    private final Map<Category, Budget> budgets;

    public ExpenseService() {
        fileService  = new FileService();
        transactions = fileService.loadTransactions();
        budgets      = fileService.loadBudgets();
    }

    // -- transaction stuff --

    public Transaction addTransaction(Type type, double amount, Category category,
                                      String description, LocalDate date) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than 0.");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Please enter a description.");

        Transaction t = new Transaction(type, amount, category, description, date);
        transactions.add(t);
        fileService.saveTransactions(transactions);
        return t;
    }

    public boolean deleteTransaction(String id) {
        boolean found = transactions.removeIf(t -> t.getId().equalsIgnoreCase(id));
        if (found) fileService.saveTransactions(transactions);
        return found;
    }

    public List<Transaction> getAllTransactions() {
        // return newest first
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> getByMonth(int year, Month month) {
        return transactions.stream()
                .filter(t -> t.getDate().getYear() == year && t.getDate().getMonth() == month)
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> searchByKeyword(String keyword) {
        String kw = keyword.toLowerCase();
        return transactions.stream()
                .filter(t -> t.getDescription().toLowerCase().contains(kw))
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Transaction> filterByCategory(Category cat) {
        return transactions.stream()
                .filter(t -> t.getCategory() == cat)
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .collect(Collectors.toList());
    }

    // -- summary calculations --

    public double totalIncome(List<Transaction> list) {
        return list.stream()
                .filter(t -> t.getType() == Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double totalExpenses(List<Transaction> list) {
        return list.stream()
                .filter(t -> t.getType() == Type.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double netBalance(List<Transaction> list) {
        return totalIncome(list) - totalExpenses(list);
    }

    // returns expense totals per category, sorted high to low
    public Map<Category, Double> spendingByCategory(List<Transaction> list) {
        Map<Category, Double> temp = new HashMap<>();

        list.stream()
            .filter(t -> t.getType() == Type.EXPENSE)
            .forEach(t -> temp.merge(t.getCategory(), t.getAmount(), Double::sum));

        // sort descending by amount and put in LinkedHashMap to keep order
        return temp.entrySet().stream()
                .sorted(Map.Entry.<Category, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // -- budget stuff --

    public void setBudget(Category category, double limit) {
        budgets.put(category, new Budget(category, limit));
        fileService.saveBudgets(budgets);
    }

    public void removeBudget(Category category) {
        budgets.remove(category);
        fileService.saveBudgets(budgets);
    }

    public Map<Category, Budget> getAllBudgets() {
        return Collections.unmodifiableMap(budgets);
    }

    public Optional<Budget> getBudget(Category category) {
        return Optional.ofNullable(budgets.get(category));
    }
}
