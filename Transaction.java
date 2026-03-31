package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Transaction {

    public enum Type {
        INCOME, EXPENSE
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String id;
    private final Type type;
    private final double amount;
    private final Category category;
    private final String description;
    private final LocalDate date;

    public Transaction(Type type, double amount, Category category, String description, LocalDate date) {
        // generate short id from UUID - first 8 chars is enough to be unique for personal use
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public Transaction(String id, Type type, double amount, Category category, String description, LocalDate date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }

    public double getSignedAmount() {
        return type == Type.INCOME ? amount : -amount;
    }

    public String toCsv() {
        return String.join(",",
                id,
                type.name(),
                String.format("%.2f", amount),
                category.name(),
                "\"" + description.replace("\"", "'") + "\"",
                date.format(DATE_FMT)
        );
    }

    public static Transaction fromCsv(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        String id         = parts[0].trim();
        Type type         = Type.valueOf(parts[1].trim());
        double amount     = Double.parseDouble(parts[2].trim());
        Category category = Category.valueOf(parts[3].trim());
        String desc       = parts[4].trim().replaceAll("^\"|\"$", "");
        LocalDate date    = LocalDate.parse(parts[5].trim(), DATE_FMT);

        return new Transaction(id, type, amount, category, desc, date);
    }

    @Override
    public String toString() {
        String sign = (type == Type.INCOME) ? "+" : "-";
        String shortDesc = description.length() > 20
                ? description.substring(0, 17) + "..."
                : description;
        return String.format("[%s] %s  %s₹%.2f  %-20s  %s  (%s)",
                id, date.format(DATE_FMT), sign, amount, shortDesc, category.getEmoji(), type);
    }
}
