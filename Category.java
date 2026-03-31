package model;

// categories for classifying transactions
// added emoji just for fun, makes the CLI look nicer
public enum Category {

    FOOD("Food & Dining", "🍔"),
    TRANSPORT("Transport", "🚗"),
    HOUSING("Housing & Rent", "🏠"),
    HEALTH("Health & Medical", "💊"),
    EDUCATION("Education", "📚"),
    ENTERTAINMENT("Entertainment", "🎮"),
    SHOPPING("Shopping", "🛍"),
    UTILITIES("Utilities", "💡"),
    SALARY("Salary / Income", "💼"),
    FREELANCE("Freelance", "💻"),
    OTHER("Other", "📦");

    private final String displayName;
    private final String emoji;

    Category(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() { return emoji; }

    @Override
    public String toString() {
        return emoji + " " + displayName;
    }
}
