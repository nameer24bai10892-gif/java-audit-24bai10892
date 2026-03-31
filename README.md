# SmartBudget 💰

A command-line expense tracker built in Java. Log income and expenses, set monthly budgets per category, and see where your money actually goes.

Built as my BYOP capstone project for the Programming in Java course.

---

## Why I built this

I kept trying to track my spending in a spreadsheet and always gave up after a week. Mobile apps are either too complex or need an account. I just wanted something simple I could run in a terminal and actually stick with — so I built it.

---

## Features

- Add income and expense transactions with category, amount, description, date
- 11 spending categories (Food, Transport, Housing, etc.)
- Set monthly budget limits per category
- Visual progress bars showing how close you are to each limit
- Monthly reports — income vs expenses, net balance, breakdown by category
- All-time summary with top 5 spending categories
- Search transactions by keyword, filter by category or month
- Delete transactions by ID
- Everything saved locally to CSV files, no internet needed

---

## Project structure

```
SmartBudget/
├── src/
│   ├── Main.java                  # menu loop and user input
│   ├── model/
│   │   ├── Category.java          # enum for transaction categories
│   │   ├── Transaction.java       # represents one income/expense record
│   │   └── Budget.java            # monthly spending limit for a category
│   ├── service/
│   │   ├── ExpenseService.java    # all the business logic
│   │   └── FileService.java       # reading/writing CSV files
│   └── util/
│       └── ReportGenerator.java   # formats and prints all the reports
├── data/                          # auto-created on first run (gitignored)
├── run.sh                         # build + run (Mac/Linux)
├── run.bat                        # build + run (Windows)
└── .gitignore
```

---

## Requirements

- Java 17 or above
- A terminal

Check your version: `java -version`

---

## How to run

**Mac / Linux:**
```bash
chmod +x run.sh
./run.sh
```

**Windows:**
```
run.bat
```

**Or manually:**
```bash
mkdir -p out
javac -d out -sourcepath src src/Main.java src/model/*.java src/service/*.java src/util/*.java
java -cp out Main
```

---

## Usage

When you start the app you'll get a menu:

```
  1. Add Expense
  2. Add Income
  3. View Transactions
  4. Monthly Report
  5. Manage Budgets
  6. Budget Status (current month)
  7. Delete a Transaction
  8. All-Time Summary
  0. Exit
```

**Adding an expense:**
```
Choice: 1
  -- Add Expense 💸 --

  Amount (₹): 350
   1. 🍔 Food & Dining
   2. 🚗 Transport
   ...
  Pick a category: 1
  Description: Dinner with friends
  Date (leave blank for today):

  Saved: [A3F2C1B8] 2026-03-31  -₹350.00  Dinner with friends   🍔  (EXPENSE)
```

**Budget progress bar:**
```
  🍔 Food & Dining           ₹2,800.00
  [████████████████████░░░░] 93.3%  ⚡ WARNING
```

---

## Data storage

Transactions and budgets are saved as CSV files in a `data/` folder. The folder is gitignored by default so your personal data doesn't end up on GitHub.

```
data/transactions.csv
data/budgets.csv
```

---

## Java concepts used

- OOP and encapsulation (model classes with private fields)
- Enums with fields and methods (`Category`)
- Java Collections — ArrayList, HashMap, LinkedHashMap
- Stream API — filter, map, sorted, collect
- Java Time API — LocalDate, Month, DateTimeFormatter
- File I/O — BufferedReader, PrintWriter
- Exception handling — input validation, file errors
- Optional, Generics, UUID
- Lambda expressions

---

## Possible improvements

- Export reports to PDF or HTML
- Recurring transactions (e.g. auto-log monthly rent)
- JavaFX GUI (the service layer is already separate, would be easy to hook up)
- Unit tests with JUnit

---

## License

MIT
