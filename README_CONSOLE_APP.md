# Gym Membership Management System

A comprehensive Java console-based application for managing gym memberships using Object-Oriented Programming principles.

## Features

### Core Functionality
- ✅ **Add New Member** - Register members with auto-generated unique IDs
- ✅ **View All Members** - Display members in a formatted table
- ✅ **Update Member** - Modify member information (individual fields or all at once)
- ✅ **Delete Member** - Remove members with confirmation
- ✅ **Search by Name** - Find members by name (partial match supported)
- ✅ **Search by ID** - Quick lookup using member ID
- ✅ **Filter by Payment Status** - View members by Paid/Pending status
- ✅ **Statistics Dashboard** - View system-wide statistics

### Technical Features
- 🎯 **Object-Oriented Design** - Clean separation of concerns with Member class
- 🚀 **Dual Data Structure** - ArrayList for iteration + HashMap for O(1) lookups
- 🔢 **Auto-generated IDs** - Sequential member IDs (GYM1001, GYM1002, etc.)
- 📅 **Smart Date Handling** - Automatic end date calculation based on membership plan
- 🎨 **Beautiful UI** - Professional console interface with formatted tables
- ✨ **Input Validation** - Robust error handling for user inputs

## Member Information

Each member has the following attributes:
- **ID** - Auto-generated unique identifier (e.g., GYM1001)
- **Name** - Member's full name
- **Contact Number** - Phone number
- **Membership Plan** - Monthly (1 month), Quarterly (3 months), or Yearly (12 months)
- **Start Date** - Membership start date
- **End Date** - Auto-calculated based on plan duration
- **Payment Status** - Paid or Pending
- **Created At** - Timestamp of member registration

## How to Run

### Using Command Line

1. **Navigate to the project directory:**
   ```bash
   cd "C:\Users\Merwin Richards M\OneDrive\Desktop\gym-membership-system"
   ```

2. **Compile the Java files:**
   ```bash
   javac -d target/classes src/main/java/com/gym/gym_membership_system/*.java
   ```

3. **Run the application:**
   ```bash
   java -cp target/classes com.gym.gym_membership_system.GymConsoleApp
   ```

### Using Maven

1. **Navigate to the project directory:**
   ```bash
   cd "C:\Users\Merwin Richards M\OneDrive\Desktop\gym-membership-system"
   ```

2. **Compile the project:**
   ```bash
   mvn compile
   ```

3. **Run the console application:**
   ```bash
   mvn exec:java -Dexec.mainClass="com.gym.gym_membership_system.GymConsoleApp"
   ```

### Using IDE (IntelliJ IDEA / Eclipse / VS Code)

1. Open the project in your IDE
2. Navigate to `GymConsoleApp.java`
3. Right-click on the file and select "Run 'GymConsoleApp.main()'"

## Usage Guide

### Main Menu
```
┌────────────────────────────────────────────────────────────────────────┐
│                          MAIN MENU                                     │
├────────────────────────────────────────────────────────────────────────┤
│  1. Add New Member                                                     │
│  2. View All Members                                                   │
│  3. Update Member                                                      │
│  4. Delete Member                                                      │
│  5. Search Member by Name                                              │
│  6. Filter Members by Payment Status                                   │
│  7. Search Member by ID                                                │
│  8. Display Statistics                                                 │
│  0. Exit                                                               │
└────────────────────────────────────────────────────────────────────────┘
```

### Adding a Member
1. Select option `1` from the main menu
2. Enter member details when prompted:
   - Name
   - Contact number
   - Membership plan (1=Monthly, 2=Quarterly, 3=Yearly)
   - Start date (or press Enter for today)
   - Payment status (1=Paid, 2=Pending)
3. System will auto-generate a unique member ID

### Viewing Members
- Select option `2` to see all members in a formatted table
- View details including ID, name, contact, plan, dates, and payment status

### Updating Members
1. Select option `3`
2. Enter the member ID
3. Choose what to update:
   - Individual fields (name, contact, plan, date, payment)
   - All fields at once
4. Changes are saved immediately

### Deleting Members
1. Select option `4`
2. Enter the member ID
3. Review the member details
4. Confirm deletion (yes/no)

### Searching Members
- **By Name** (Option 5): Searches for partial matches
- **By ID** (Option 7): Quick lookup using HashMap (instant)

### Filtering Members
- Select option `6`
- Choose payment status (Paid or Pending)
- View filtered results in a table

### Statistics
- Select option `8` to view:
  - Total member count
  - Payment status breakdown (Paid vs Pending)
  - Membership plan distribution (Monthly, Quarterly, Yearly)

## Data Structures Used

### ArrayList<Member>
- Used for sequential operations
- Enables iteration through all members
- Used in: viewing, searching, filtering

### HashMap<String, Member>
- Key: Member ID (String)
- Value: Member object
- Provides O(1) lookup time complexity
- Used in: quick ID searches, updates, deletions

## Object-Oriented Programming Concepts

### Classes
- **Member** - Core data model with encapsulation
- **GymConsoleApp** - Main application with business logic

### Enums
- **MembershipPlan** - MONTHLY, QUARTERLY, YEARLY
- **PaymentStatus** - PAID, PENDING

### Encapsulation
- Private fields with public getters/setters
- Data validation in setters (e.g., auto-calculating end date)

### Abstraction
- Clean separation between data (Member) and logic (GymConsoleApp)
- Enum abstractions for type safety

## Date Handling
- Uses Java 8+ `LocalDateTime` for date management
- Automatic end date calculation based on membership duration
- Smart date recalculation when plan or start date changes
- Format: `yyyy-MM-dd HH:mm:ss`

## Input Validation
- Numeric input validation with error handling
- Date format validation with fallback to current date
- Menu choice validation with retry logic
- Empty input handling

## Sample Workflow

```
1. Start application
2. Add member "John Doe" with Monthly plan (Paid)
   → Member ID: GYM1001
3. Add member "Jane Smith" with Yearly plan (Pending)
   → Member ID: GYM1002
4. View all members
   → Shows 2 members in table format
5. Search for "John"
   → Finds 1 member
6. Filter by Payment Status: Pending
   → Shows Jane Smith
7. Update GYM1001 - Change payment status to Pending
8. Display Statistics
   → 2 total members, 0 Paid, 2 Pending
9. Delete GYM1002 (with confirmation)
10. Exit application
```

## System Requirements

- **Java Version**: Java 8 or higher (uses LocalDateTime, Lambda expressions)
- **Build Tool**: Maven 3.6+ (optional)
- **OS**: Windows, Linux, macOS
- **Memory**: Minimal (console-based application)

## Project Structure

```
gym-membership-system/
├── src/main/java/com/gym/gym_membership_system/
│   ├── GymConsoleApp.java          # Main application with menu and logic
│   ├── Member.java                  # Member data model
│   └── GymMembershipSystemApplication.java  # Spring Boot entry (not used for console)
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## Key Implementation Details

### Auto-Generated Member IDs
```java
private static AtomicInteger memberIdCounter = new AtomicInteger(1001);
String memberId = "GYM" + memberIdCounter.getAndIncrement();
```

### Dual Data Structure Synchronization
```java
// Add to both structures
membersList.add(newMember);
membersMap.put(memberId, newMember);

// Remove from both structures
membersList.remove(member);
membersMap.remove(memberId);
```

### Smart End Date Calculation
```java
public void setMembershipPlan(MembershipPlan membershipPlan) {
    this.membershipPlan = membershipPlan;
    this.endDate = this.startDate.plusMonths(membershipPlan.getMonths());
}
```

## Future Enhancements (Optional)

- 💾 File persistence (save/load from file)
- 📧 Email notifications for expiring memberships
- 💰 Payment tracking and history
- 📊 Export to CSV/PDF
- 🔐 Admin authentication
- 📱 Member attendance tracking
- 💵 Revenue reports

## Author

Created as a demonstration of Object-Oriented Programming in Java with practical data structure usage (ArrayList + HashMap).

## License

This project is for educational purposes.

---

**Note**: This is a console-based application. For production use, consider adding persistence (database), validation, and error logging.

