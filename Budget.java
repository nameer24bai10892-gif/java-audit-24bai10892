package model;

public class Budget {

    private final Category category;
    private double limit;

    public Budget(Category category, double limit) {
        if (limit <= 0)
            throw new IllegalArgumentException("Budget must be a positive value.");
        this.category = category;
        this.limit = limit;
    }

    public Category getCategory() { return category; }
    public double getLimit() { return limit; }

    public void setLimit(double newLimit) {
        if (newLimit <= 0)
            throw new IllegalArgumentException("Budget must be a positive value.");
        this.limit = newLimit;
    }

    public double usagePercent(double spent) {
        return (spent / limit) * 100.0;
    }

    // draws a simple 20-char bar, e.g. [████████████░░░░░░░░] 60.0%
    public String progressBar(double spent) {
        int filled = (int) Math.min(20, (spent / limit) * 20);
        String bar = "█".repeat(filled) + "░".repeat(20 - filled);
        double pct = usagePercent(spent);

        String warning = "";
        if (pct >= 100) warning = "  ⚠ OVER BUDGET";
        else if (pct >= 80) warning = "  ⚡ WARNING";

        return String.format("[%s] %.1f%%%s", bar, pct, warning);
    }

    // csv format: CATEGORY_NAME,limit
    public String toCsv() {
        return category.name() + "," + String.format("%.2f", limit);
    }

    public static Budget fromCsv(String line) {
        String[] p = line.split(",");
        return new Budget(Category.valueOf(p[0].trim()), Double.parseDouble(p[1].trim()));
    }
}
