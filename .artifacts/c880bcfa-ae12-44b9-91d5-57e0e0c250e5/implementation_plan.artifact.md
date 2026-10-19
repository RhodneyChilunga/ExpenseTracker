# Student Wallet Expense Tracker - Implementation Plan

Implement a fully-featured student wallet expense and income tracking application matching the Wallet green theme and functional requirements.

## User Review Required

> [!IMPORTANT]
> - **Theme**: Adopting the vibrant Wallet green color scheme (`#10B981` / `#00C853`) as requested.
> - **Architecture**: Adding a `WalletViewModel` with state management for reactive UI updates when adding, editing, filtering, or deleting transactions and setting budget limits.

## Open Questions

- None. All functional requirements (income/expense management, categorization, totals, balance, search/filter, budgets, notifications) are clearly defined.

## Proposed Changes

### [Models & ViewModel]
#### [NEW] [WalletViewModel.kt](file:///C:/Users/bsc_com_11_23/AndroidStudioProjects/ExpenseTracker6/app/src/main/java/com/example/expensetracker/viewmodel/WalletViewModel.kt)
- Defines `Transaction` data class (id, title, amount, type [INCOME/EXPENSE], category, date, description).
- Manages state for transactions list, income total, expense total, balance, filter/search query, category budget limits, and budget alerts.
- Methods: `addTransaction`, `updateTransaction`, `deleteTransaction`, `setBudgetLimit`, `setSearchQuery`, `setCategoryFilter`.

### [UI Components & Screens]
#### [MODIFY] [Theme.kt](file:///C:/Users/bsc_com_11_23/AndroidStudioProjects/ExpenseTracker6/app/src/main/java/com/example/expensetracker/ui/theme/Theme.kt)
- Update primary color scheme to Wallet Green (`#10B981` / `#00C853`).

#### [NEW] [WalletHomeScreen.kt](file:///C:/Users/bsc_com_11_23/AndroidStudioProjects/ExpenseTracker6/app/src/main/java/com/example/expensetracker/ui/WalletHomeScreen.kt)
- Main wallet dashboard with Wallet Green header, total balance, total income, total expense cards.
- Quick summary and budget alert banners.

#### [NEW] [TransactionsScreen.kt](file:///C:/Users/bsc_com_11_23/AndroidStudioProjects/ExpenseTracker6/app/src/main/java/com/example/expensetracker/ui/TransactionsScreen.kt)
- View all transactions (income & expense).
- Search and filter by category or date.
- Edit and Delete options.

#### [NEW] [AddEditTransactionDialog.kt](file:///C:/Users/bsc_com_11_23/AndroidStudioProjects/ExpenseTracker6/app/src/main/java/com/example/expensetracker/ui/AddEditTransactionDialog.kt)
- Dialog/Screen to add or edit income (amount, source, date, description) and expenses (amount, category [food, transport, rent, school, airtime, shopping, entertainment], date, description).

#### [NEW] [BudgetScreen.kt](file:///C:/Users/bsc_com_11_23/AndroidStudioProjects/ExpenseTracker6/app/src/main/java/com/example/expensetracker/ui/BudgetScreen.kt)
- Set spending/budget limits for selected categories.
- Visual indicators and notifications when spending reaches or exceeds the budget limit.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/bsc_com_11_23/AndroidStudioProjects/ExpenseTracker6/app/src/main/java/com/example/expensetracker/MainActivity.kt)
- Wire up `WalletViewModel` and navigation between Dashboard, Transactions, Budgets, and Add Transaction.

## Verification Plan

### Automated Tests
- Build test with `gradle_build("app:assembleDebug")`.

### Manual Verification
- Verify Compose Preview rendering.
- Test adding income and expense, auto-calculation of totals and balance, searching/filtering, editing/deleting, and budget limits with alerts.
