# Project Report — SmartBudget

**Course:** Programming in Java
**Project:** Bring Your Own Project (BYOP)
**Platform:** VITyarthi

---

## 1. The Problem

I wanted to track my personal spending but every solution I tried either didn't stick or had too much overhead. Spreadsheets felt tedious to maintain. Mobile apps required accounts and had more features than I needed. I decided to build something minimal — a CLI tool that stores data locally and gets out of the way.

The problem is simple: most people don't know where their money goes month to month, and the friction of opening an app or a spreadsheet is often enough to make them stop tracking altogether. A terminal tool has almost no friction — it's one command.

---

## 2. What I Built

SmartBudget is a command-line Java application that lets you:

- Log income and expense transactions with a category, amount, description, and date
- Set monthly spending limits per category
- View monthly and all-time reports
- Search and filter your transaction history

Data is saved to local CSV files. No database, no internet, no account needed.

---

## 3. Design Decisions

**Why CSV instead of a database?**
For a personal-use app, CSV files are fine. They're human-readable, easy to back up, and require no setup. I can open the file in a text editor if something goes wrong. A proper database would be overkill here.

**Why separate the layers?**
I split the code into model, service, and utility packages from the start. This made the code much easier to work with — when I changed how budgets were stored, I only touched `FileService.java`. `Main.java` only handles input/output and delegates everything to `ExpenseService`, which made it easy to keep the menu logic clean.

**Why enums for categories?**
Categories are a closed, fixed set. Using an enum prevents typos, enables compile-time checking, and lets me attach extra data (display name, emoji) directly to each value without a separate lookup.

---

## 4. How the Code Is Organized

| File | What it does |
|---|---|
| `Main.java` | Menu loop, reads user input, delegates to service |
| `Category.java` | Enum with 11 spending/income categories |
| `Transaction.java` | One financial event — amount, type, category, date, description |
| `Budget.java` | Monthly spending limit for a category + progress bar rendering |
| `ExpenseService.java` | Core logic — add/delete/filter transactions, budget management |
| `FileService.java` | Read and write CSV files |
| `ReportGenerator.java` | All formatted output — tables, summaries, progress bars |

---

## 5. Java Concepts Applied

**Enums with behavior:** `Category` isn't just a label — it has a `displayName` and `emoji` field and a custom `toString()`. This made the menu and reports much cleaner.

**Stream API:** Used throughout `ExpenseService` for filtering, sorting, and aggregating transactions. For example, getting total expenses by category uses `filter` + `forEach` + `merge` on a map, then sorts the result using `sorted` + `Collectors.toMap` with a `LinkedHashMap` to preserve order.

**Java Time API:** `LocalDate` for dates, `Month` enum for month selection, `DateTimeFormatter` for ISO-format parsing and formatting. Much cleaner than the old `Date`/`Calendar` approach.

**File I/O:** `BufferedReader` + `FileReader` for reading, `PrintWriter` + `FileWriter` for writing. Added a regex-based CSV parser to handle commas inside quoted description fields:
```java
line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)
```

**Optional:** `getBudget(Category)` returns `Optional<Budget>` so callers can use `ifPresent()` instead of null checks.

**UUID:** Each transaction gets a short unique ID from the first 8 characters of a UUID. Compact enough to type when deleting a record.

---

## 6. Challenges

**Quoted fields in CSV:** A user description like "Lunch, coffee" would break a naive `split(",")`. I solved this with a lookahead regex that ignores commas inside double-quoted strings. Took a while to get right.

**Sorted map by value:** Java's `HashMap` has no ordering. I needed the category spending breakdown sorted by amount. The solution was to stream the entries, sort them, then collect into a `LinkedHashMap` which preserves insertion order.

**Keeping Main.java clean:** Without a UI framework it's easy for the main class to become a mess. I solved this by making sure `Main.java` only handles prompts and printing — all decisions go through `ExpenseService`, and all formatting goes through `ReportGenerator`.

---

## 7. What I Learned

The biggest takeaway was that separating concerns early saves a lot of time later. I also underestimated how much work goes into handling bad user input — every prompt needs its own validation loop. And writing the CSV parser taught me more about regex than I expected.

I also learned that "no database needed" is a legitimate design decision for small tools, not just laziness. The CSV files are easy to debug, easy to version-control (if you want to), and easy to explain to someone else.

---

## 8. What I'd Add Next

- Recurring transactions (auto-log rent, subscriptions)
- Export to PDF or HTML
- JUnit tests for `ExpenseService` and `Transaction.fromCsv()`
- JavaFX GUI — the service layer is already fully decoupled so this wouldn't require rewriting anything
