# Quick Testing Guide - Gym Membership System

## Application URLs
- **Frontend**: http://localhost:8081/
- **API Base**: http://localhost:8081/api/members

## Start the Application

### Option 1: Using Maven Wrapper (Windows)
```bash
cd "C:\Users\Merwin Richards M\OneDrive\Desktop\gym-membership-system"
mvnw.cmd spring-boot:run
```

### Option 2: Using JAR File
```bash
cd "C:\Users\Merwin Richards M\OneDrive\Desktop\gym-membership-system"
mvnw.cmd package -DskipTests
java -jar target/gym-membership-system-0.0.1-SNAPSHOT.jar
```

## Test Case 1: Add a New Member

### Steps:
1. Open http://localhost:8081 in your browser
2. Scroll to "Member Management" section or click "Add Member" button
3. Click the blue "+ Add Member" button in the navbar or Members section
4. Fill in the modal form:
   - **Member Name**: John Smith
   - **Contact Number**: +1 (555) 123-4567
   - **Membership Plan**: Monthly
   - **Start Date**: Today (auto-filled)
   - **Payment Status**: Paid
5. Click "Add Member" button

### Expected Results:
✅ Member appears in the table below the form
✅ Network tab shows POST request: `POST http://localhost:8081/api/members`
✅ Response status: 201 Created
✅ Modal closes automatically
✅ Success message shown

## Test Case 2: Verify Data Persistence

### Steps:
1. After adding a member (Test Case 1), refresh the page (F5 or Ctrl+R)
2. Check if the member is still in the table

### Expected Results:
✅ Member still appears in the table after refresh
✅ Data persisted in MySQL database
✅ Can view member details

## Test Case 3: Add Another Member

### Steps:
1. Click "+ Add Member" button again
2. Fill in different member data:
   - **Member Name**: Jane Doe
   - **Contact Number**: +1 (555) 987-6543
   - **Membership Plan**: Quarterly
   - **Start Date**: Today
   - **Payment Status**: Pending
3. Click "Add Member"

### Expected Results:
✅ New member added successfully
✅ Both members visible in table
✅ Each has correct membership plan
✅ "Days Left" column calculated correctly

## Test Case 4: Verify API Response

### Steps:
1. Open browser DevTools (F12)
2. Go to "Network" tab
3. Add a new member (see Test Case 1)
4. In Network tab, find POST request to `/api/members`
5. Click on it and check Response tab

### Expected Response:
```json
{
  "message": "Member added successfully",
  "data": {
    "id": 1,
    "name": "John Smith",
    "contactNumber": "+1 (555) 123-4567",
    "membershipPlan": "Monthly",
    "startDate": "2026-03-09",
    "endDate": "2026-04-09",
    "paymentStatus": "Paid",
    "createdAt": "2026-03-09T14:30:00"
  },
  "timestamp": "2026-03-09T14:30:00"
}
```

## Test Case 5: Search Members

### Steps:
1. Add 2-3 members (see Test Case 1 & 3)
2. In the search box, type a member name
3. Press Enter or wait for automatic filter

### Expected Results:
✅ Table filters to show only matching members
✅ Typing "john" shows John Smith
✅ Typing "jane" shows Jane Doe

## Test Case 6: Filter by Payment Status

### Steps:
1. Have at least 2 members (one Paid, one Pending)
2. Click the "All Payment Status" dropdown
3. Select "Paid"
4. Select "Pending"
5. Select "All Payment Status" again

### Expected Results:
✅ Table filters based on payment status
✅ Shows only Paid members when "Paid" selected
✅ Shows only Pending members when "Pending" selected
✅ Shows all members when "All Payment Status" selected

## Troubleshooting

### Issue: "No POST request in Network tab"
**Solution**: 
- Check if JavaScript loaded correctly
- Verify field IDs match: `memberName`, `memberContact`, `membershipPlan`, `startDate`, `paymentStatus`
- Check console (F12 → Console) for JavaScript errors
- Ensure form has `onsubmit="handleAddMember(event)"`

### Issue: "Failed to add member" error
**Solution**:
- Check if backend is running on port 8081: `http://localhost:8081/`
- Check if MySQL database is running
- Check database credentials in `application.properties`
- Look at Spring Boot console logs for error details

### Issue: "Member disappears on refresh"
**Solution**:
- Check MySQL connection in `application.properties`
- Verify database `gym_db` exists
- Check table `members` was created: `SHOW TABLES IN gym_db;`
- Check Spring Boot logs for database errors

### Issue: "Contact number already exists error"
**Solution**:
- Each member must have unique contact number
- Check existing members in database
- Use different contact number for new member

## Database Verification

### Connect to MySQL and check data:
```sql
-- Connect to database
mysql -u root -p

-- Use the gym database
USE gym_db;

-- View all members
SELECT * FROM members;

-- Count members
SELECT COUNT(*) FROM members;

-- View specific member
SELECT * FROM members WHERE name = 'John Smith';
```

## Logs Location

Spring Boot logs will show in the terminal where you ran the application. Look for:
```
Tomcat started on port(s): 8081
Started GymMembershipSystemApplication in X.XXX seconds
```

## Success Indicators

✅ Application starts without errors
✅ Frontend loads at http://localhost:8081
✅ Add Member form functional
✅ POST requests reach backend
✅ Members persist in database
✅ Page refresh shows saved members
✅ Multiple members can be added
✅ Search and filter work correctly

---
**Ready to Test!** Start the application and follow the test cases above.

