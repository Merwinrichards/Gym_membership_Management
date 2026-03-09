/**
 * Gym Membership Management System - Frontend JavaScript
 * Handles API communication and DOM manipulation
 * Uses Fetch API with async/await for modern HTTP requests
 */

// ========================================
// Global Variables
// ========================================
let allMembers = [];
let filteredMembers = [];
const API_BASE_URL = 'http://localhost:8081/api/members';

// ========================================
// Initialization
// ========================================
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM Content Loaded - Initializing application');

    // Set today's date as default start date
    const today = new Date().toISOString().split('T')[0];
    const startDateInput = document.getElementById('startDate');
    if (startDateInput) {
        startDateInput.value = today;
    }

    // Load members on page load
    loadMembers();

    // Attach event listeners
    attachEventListeners();
});

/**
 * Attach event listeners to form elements and buttons
 */
function attachEventListeners() {
    const memberForm = document.getElementById('memberForm') || document.getElementById('addMemberForm');
    const searchInput = document.getElementById('searchName');
    const filterStatus = document.getElementById('filterStatus');
    const saveEditBtn = document.getElementById('saveEditBtn');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');

    if (memberForm) {
        memberForm.addEventListener('submit', handleAddMember);
    }

    if (searchInput) {
        searchInput.addEventListener('keyup', searchMembers);
    }

    if (filterStatus) {
        filterStatus.addEventListener('change', filterMembersByPaymentStatus);
    }

    if (saveEditBtn) {
        saveEditBtn.addEventListener('click', handleEditMember);
    }

    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', handleDeleteMember);
    }

    // ========================================
    // REAL-TIME VALIDATION - AS USER TYPES
    // ========================================

    // Validate name field in real-time
    const nameField = document.getElementById('memberName');
    if (nameField) {
        nameField.addEventListener('blur', () => {
            validateFieldInRealTime('memberName', nameField.value.trim(), 'name');
        });
        nameField.addEventListener('input', () => {
            if (nameField.classList.contains('is-invalid')) {
                validateFieldInRealTime('memberName', nameField.value.trim(), 'name');
            }
        });
    }

    // Validate contact number field in real-time
    const contactField = document.getElementById('contactNumber') || document.getElementById('memberContact');
    if (contactField) {
        contactField.addEventListener('blur', () => {
            validateFieldInRealTime(contactField.id, contactField.value.trim(), 'contactNumber');
        });
        contactField.addEventListener('input', () => {
            if (contactField.classList.contains('is-invalid')) {
                validateFieldInRealTime(contactField.id, contactField.value.trim(), 'contactNumber');
            }
        });
    }

    // Validate membership plan field in real-time
    const planField = document.getElementById('membershipPlan');
    if (planField) {
        planField.addEventListener('change', () => {
            validateFieldInRealTime('membershipPlan', planField.value, 'plan');
        });
    }

    // Validate start date field in real-time
    const dateField = document.getElementById('startDate');
    if (dateField) {
        dateField.addEventListener('change', () => {
            validateFieldInRealTime('startDate', dateField.value, 'date');
        });
    }

    // Validate payment status field in real-time
    const statusField = document.getElementById('paymentStatus');
    if (statusField) {
        statusField.addEventListener('change', () => {
            validateFieldInRealTime('paymentStatus', statusField.value, 'status');
        });
    }
}

/**
 * Validate individual field in real-time
 * @param {string} fieldId - HTML field ID
 * @param {string} value - Field value
 * @param {string} fieldType - Type of field (name, contactNumber, plan, date, status)
 */
function validateFieldInRealTime(fieldId, value, fieldType) {
    const field = document.getElementById(fieldId);
    const errorElement = document.getElementById(`${fieldId}Error`);
    let errorMessage = '';

    if (!field || !errorElement) return;

    // Validate based on field type
    switch (fieldType) {
        case 'name':
            if (!value) {
                errorMessage = 'Member name is required';
            } else if (value.length < 2) {
                errorMessage = 'Member name must be at least 2 characters long';
            } else if (value.length > 100) {
                errorMessage = 'Member name must not exceed 100 characters';
            } else if (!/^[a-zA-Z\s'-]+$/.test(value)) {
                errorMessage = 'Name can only contain letters, spaces, hyphens, and apostrophes';
            }
            break;

        case 'contactNumber':
            if (!value) {
                errorMessage = 'Contact number is required';
            } else if (value.length < 7) {
                errorMessage = 'Contact number must be at least 7 digits long';
            } else if (value.length > 20) {
                errorMessage = 'Contact number must not exceed 20 characters';
            } else if (!/^[\d\s\-\+\(\)]+$/.test(value)) {
                errorMessage = 'Contact can only contain digits, spaces, hyphens, plus, and parentheses';
            }
            break;

        case 'plan':
            if (!value) {
                errorMessage = 'Membership plan is required';
            } else if (!['Monthly', 'Quarterly', 'Yearly'].includes(value)) {
                errorMessage = 'Invalid membership plan selected';
            }
            break;

        case 'date':
            if (!value) {
                errorMessage = 'Start date is required';
            } else {
                const selectedDate = new Date(value);
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                if (selectedDate < today) {
                    errorMessage = 'Start date cannot be in the past';
                }
            }
            break;

        case 'status':
            if (!value) {
                errorMessage = 'Payment status is required';
            } else if (!['Paid', 'Pending'].includes(value)) {
                errorMessage = 'Invalid payment status selected';
            }
            break;
    }

    // Update field styling and error message
    if (errorMessage) {
        field.classList.add('is-invalid');
        errorElement.textContent = errorMessage;
        errorElement.style.display = 'block';
    } else {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        errorElement.textContent = '';
        errorElement.style.display = 'none';
    }
}

// ========================================
// Load Members
// ========================================
/**
 * Load all members from the API and render them
 * Automatically refreshes table without page reload
 * @async
 */
async function loadMembers() {
    try {
        console.log('📥 Fetching members from API...');
        showLoadingState();

        const response = await fetch(`${API_BASE_URL}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        allMembers = data.data || [];
        filteredMembers = [...allMembers];

        console.log(`✓ Successfully loaded ${allMembers.length} members`);

        // ========================================
        // AUTOMATIC TABLE REFRESH
        // Render table without page reload
        // ========================================
        renderMembersTable(allMembers);
        updateStatistics();

        // Show refresh indicator to user
        showRefreshIndicator();

    } catch (error) {
        console.error('❌ Error loading members:', error);
        showError('Failed to load members. Please try again.');
        showEmptyState('Error loading data. Please refresh the page.');
    }
}

// ========================================
// Add Member
// ========================================

/**
 * Validate add member form inputs
 * @param {string} name - Member name
 * @param {string} contactNumber - Contact number
 * @param {string} membershipPlan - Membership plan
 * @param {string} startDate - Start date
 * @param {string} paymentStatus - Payment status
 * @returns {Array} Array of validation error objects
 */
function validateAddMemberForm(name, contactNumber, membershipPlan, startDate, paymentStatus) {
    const errors = [];

    // Validate name
    if (!name) {
        errors.push({
            field: 'memberName',
            message: 'Member name is required'
        });
    } else if (name.length < 2) {
        errors.push({
            field: 'memberName',
            message: 'Member name must be at least 2 characters long'
        });
    } else if (name.length > 100) {
        errors.push({
            field: 'memberName',
            message: 'Member name must not exceed 100 characters'
        });
    } else if (!/^[a-zA-Z\s'-]+$/.test(name)) {
        errors.push({
            field: 'memberName',
            message: 'Member name can only contain letters, spaces, hyphens, and apostrophes'
        });
    }

    // Validate contact number
    if (!contactNumber) {
        errors.push({
            field: 'contactNumber',
            message: 'Contact number is required'
        });
    } else if (contactNumber.length < 7) {
        errors.push({
            field: 'contactNumber',
            message: 'Contact number must be at least 7 digits long'
        });
    } else if (contactNumber.length > 20) {
        errors.push({
            field: 'contactNumber',
            message: 'Contact number must not exceed 20 characters'
        });
    } else if (!/^[\d\s\-\+\(\)]+$/.test(contactNumber)) {
        errors.push({
            field: 'contactNumber',
            message: 'Contact number can only contain digits, spaces, hyphens, plus, and parentheses'
        });
    }

    // Validate membership plan
    if (!membershipPlan) {
        errors.push({
            field: 'membershipPlan',
            message: 'Membership plan is required'
        });
    } else if (!['Monthly', 'Quarterly', 'Yearly'].includes(membershipPlan)) {
        errors.push({
            field: 'membershipPlan',
            message: 'Invalid membership plan selected'
        });
    }

    // Validate start date
    if (!startDate) {
        errors.push({
            field: 'startDate',
            message: 'Start date is required'
        });
    } else {
        const selectedDate = new Date(startDate);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (selectedDate < today) {
            errors.push({
                field: 'startDate',
                message: 'Start date cannot be in the past'
            });
        }
    }

    // Validate payment status
    if (!paymentStatus) {
        errors.push({
            field: 'paymentStatus',
            message: 'Payment status is required'
        });
    } else if (!['Paid', 'Pending'].includes(paymentStatus)) {
        errors.push({
            field: 'paymentStatus',
            message: 'Invalid payment status selected'
        });
    }

    console.log(`Form validation: ${errors.length} error(s) found`);
    return errors;
}

/**
 * Display form validation errors
 * @param {Array} errors - Array of error objects
 */
function displayFormErrors(errors) {
    errors.forEach(error => {
        const field = document.getElementById(error.field);
        const fallbackField = error.field === 'contactNumber'
            ? document.getElementById('memberContact')
            : null;
        const targetField = field || fallbackField;

        const errorElement = document.getElementById(`${error.field}Error`) ||
            (error.field === 'contactNumber' ? document.getElementById('memberContactError') : null);

        if (targetField) {
            targetField.classList.add('is-invalid');
        }

        if (errorElement) {
            errorElement.textContent = error.message;
            errorElement.style.display = 'block';
        }
    });
}

/**
 * Clear all form validation errors
 */
function clearFormErrors() {
    const formFields = [
        'memberName',
        'contactNumber',
        'membershipPlan',
        'startDate',
        'paymentStatus',
        // Backward-compatible field ids
        'memberContact',
        'memberPlan',
        'memberStartDate',
        'memberPaymentStatus'
    ];

    formFields.forEach(fieldId => {
        const field = document.getElementById(fieldId);
        const errorElement = document.getElementById(`${fieldId}Error`);

        if (field) {
            field.classList.remove('is-invalid');
        }

        if (errorElement) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
        }
    });
}

/**
 * Handle add member - Form validation logic
 */

async function handleAddMember(event) {
    event.preventDefault();

    try {

        const name = document.getElementById("memberName").value.trim();
        const contactNumber = document.getElementById("contactNumber").value.trim();
        const membershipPlan = document.getElementById("membershipPlan").value;
        const startDate = document.getElementById("startDate").value;
        const paymentStatus = document.getElementById("paymentStatus").value;

        const memberData = {
            name: name,
            contactNumber: contactNumber,
            membershipPlan: membershipPlan,
            startDate: startDate,
            paymentStatus: paymentStatus
        };

        console.log("Sending data:", memberData);

        const response = await fetch("http://localhost:8081/api/members", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(memberData)
        });

        if (!response.ok) {
            throw new Error("Failed to save member");
        }

        const result = await response.json();
        console.log("Saved:", result);

        alert("Member added successfully!");

        loadMembers(); // reload table

    } catch (error) {
        console.error("Error:", error);
        alert("Error saving member");
    }
}

// ========================================
// Update Member
// ========================================
/**
 * Store member ID for editing
 * @param {number} memberId - The member ID to edit
 */
function openEditModal(memberId) {
    try {
        const member = allMembers.find(m => m.id === memberId);

        if (!member) {
            showError('Member not found');
            return;
        }

        // Populate modal form
        document.getElementById('editMemberId').value = member.id;
        document.getElementById('editMemberName').value = member.name;
        document.getElementById('editMemberContact').value = member.contactNumber;
        document.getElementById('editMemberPlan').value = member.membershipPlan;
        document.getElementById('editMemberStatus').value = member.paymentStatus;

        // Show modal
        const editModal = new bootstrap.Modal(document.getElementById('editMemberModal'));
        editModal.show();

    } catch (error) {
        console.error('Error opening edit modal:', error);
        showError('Failed to open edit form');
    }
}

/**
 * Handle updating a member
 * @async
 */
async function handleEditMember() {
    try {
        const memberId = document.getElementById('editMemberId').value;
        const name = document.getElementById('editMemberName').value.trim();
        const contactNumber = document.getElementById('editMemberContact').value.trim();
        const membershipPlan = document.getElementById('editMemberPlan').value;
        const paymentStatus = document.getElementById('editMemberStatus').value;

        // Validate inputs
        if (!name || !contactNumber || !membershipPlan || !paymentStatus) {
            showError('All fields are required');
            return;
        }

        // Prepare updated member object
        const updatedMember = {
            name,
            contactNumber,
            membershipPlan,
            paymentStatus
        };

        console.log(`Updating member ${memberId}:`, updatedMember);

        // Make API request
        const response = await fetch(`${API_BASE_URL}/${memberId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedMember)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        console.log('Member updated successfully:', result);

        // Show success message
        showSuccess('Member updated successfully!');

        // Close modal
        bootstrap.Modal.getInstance(document.getElementById('editMemberModal')).hide();

        // Reload members
        await loadMembers();

    } catch (error) {
        console.error('Error updating member:', error);
        showError(`Failed to update member: ${error.message}`);
    }
}

// ========================================
// Delete Member
// ========================================
/**
 * Store member ID for deletion and show confirmation modal
 * @param {number} memberId - The member ID to delete
 */
function openDeleteConfirm(memberId) {
    try {
        const member = allMembers.find(m => m.id === memberId);

        if (!member) {
            showError('Member not found');
            return;
        }

        // Store member ID for deletion
        document.getElementById('confirmDeleteBtn').dataset.memberId = memberId;

        // Update modal message with member name
        const modalBody = document.querySelector('#deleteConfirmModal .modal-body');
        if (modalBody) {
            modalBody.innerHTML = `
                <p>Are you sure you want to delete <strong>${member.name}</strong>?</p>
                <p><strong>This action cannot be undone.</strong></p>
            `;
        }

        // Show modal
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
        deleteModal.show();

    } catch (error) {
        console.error('Error opening delete confirmation:', error);
        showError('Failed to open confirmation dialog');
    }
}

/**
 * Handle deleting a member
 * @async
 */
async function handleDeleteMember() {
    try {
        const memberId = document.getElementById('confirmDeleteBtn').dataset.memberId;

        if (!memberId) {
            showError('Member ID not found');
            return;
        }

        console.log(`Deleting member ${memberId}`);

        // Make API request
        const response = await fetch(`${API_BASE_URL}/${memberId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        console.log('Member deleted successfully:', result);

        // Show success message
        showSuccess('Member deleted successfully!');

        // Close modal
        bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal')).hide();

        // Reload members
        await loadMembers();

    } catch (error) {
        console.error('Error deleting member:', error);
        showError(`Failed to delete member: ${error.message}`);
    }
}

// ========================================
// Search Members
// ========================================
/**
 * Search members by name (case-insensitive partial match)
 * @async
 */
async function searchMembers() {
    try {
        const searchTerm = document.getElementById('searchName').value.toLowerCase().trim();

        if (!searchTerm) {
            filteredMembers = [...allMembers];
            renderMembersTable(filteredMembers);
            return;
        }

        console.log(`Searching for members with term: "${searchTerm}"`);

        // Filter members by name (client-side filtering)
        filteredMembers = allMembers.filter(member =>
            member.name.toLowerCase().includes(searchTerm)
        );

        console.log(`Found ${filteredMembers.length} members matching search`);

        renderMembersTable(filteredMembers);

    } catch (error) {
        console.error('Error searching members:', error);
        showError('Error searching members');
    }
}

// ========================================
// Filter Members by Payment Status
// ========================================
/**
 * Filter members by payment status
 * @async
 */
async function filterMembersByPaymentStatus() {
    try {
        const statusFilter = document.getElementById('filterStatus').value.trim();
        const searchTerm = document.getElementById('searchName').value.toLowerCase().trim();

        console.log(`Filtering by payment status: "${statusFilter}"`);

        // Apply both search and filter
        filteredMembers = allMembers.filter(member => {
            const matchesSearch = !searchTerm || member.name.toLowerCase().includes(searchTerm);
            const matchesStatus = !statusFilter || member.paymentStatus === statusFilter;
            return matchesSearch && matchesStatus;
        });

        console.log(`Found ${filteredMembers.length} members after filtering`);

        renderMembersTable(filteredMembers);

    } catch (error) {
        console.error('Error filtering members:', error);
        showError('Error filtering members');
    }
}

// ========================================
// Render Members Table
// ========================================
/**
 * Dynamically render members table with DOM manipulation
 * AUTOMATICALLY REFRESHES TABLE without page reload
 * Includes smooth fade animations for better UX
 * @param {Array} members - Array of member objects to render
 */
function renderMembersTable(members) {
    try {
        const tbody = document.getElementById('membersTableBody');
        const table = document.getElementById('membersTable');
        const emptyState = document.getElementById('emptyState');

        if (!tbody || !table) {
            console.warn('Table elements not found');
            return;
        }

        // ========================================
        // SMOOTH FADE OUT ANIMATION
        // ========================================
        if (table.style.display === 'table') {
            table.style.transition = 'opacity 0.3s ease-in-out';
            table.style.opacity = '0.6';
        }

        // Clear existing rows
        tbody.innerHTML = '';

        // Show empty state if no members
        if (members.length === 0) {
            table.style.display = 'none';
            emptyState.style.display = 'block';
            emptyState.innerHTML = `
                <div class="text-center py-5">
                    <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                    <h5>No members found</h5>
                    <p>Try adjusting your search or filters</p>
                </div>
            `;
            console.log('✓ Empty state displayed - No members to show');
            return;
        }

        table.style.display = 'table';
        emptyState.style.display = 'none';

        // ========================================
        // RENDER ALL MEMBER ROWS
        // ========================================
        members.forEach((member, index) => {
            const row = createMemberRow(member, index + 1);
            tbody.appendChild(row);
        });

        // ========================================
        // SMOOTH FADE IN ANIMATION
        // New data is now rendered, fade it in
        // ========================================
        setTimeout(() => {
            table.style.opacity = '1';
        }, 50);

        console.log(`✓ Table refreshed successfully with ${members.length} member(s)`);

    } catch (error) {
        console.error('Error rendering members table:', error);
        showError('Error rendering table');
    }
}

/**
 * Create a table row element for a member
 * @param {Object} member - Member object
 * @param {number} index - Row number
 * @returns {HTMLTableRowElement} Table row element
 */
function createMemberRow(member, index) {
    const row = document.createElement('tr');

    // ========================================
    // CHECK IF MEMBERSHIP IS EXPIRED
    // Compares endDate with current date
    // ========================================
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Reset time to start of day for accurate comparison

    const membershipEnd = new Date(member.endDate);
    membershipEnd.setHours(0, 0, 0, 0); // Reset time to start of day

    const isExpired = membershipEnd < today;

    // ========================================
    // HIGHLIGHT EXPIRED MEMBERSHIPS
    // Add CSS class for red background styling
    // ========================================
    if (isExpired) {
        row.classList.add('row-expired'); // CSS class with light red background and left red border
        console.log(`[EXPIRED] Member: ${member.name} | End Date: ${member.endDate} | Days Expired: ${Math.floor((today - membershipEnd) / (1000 * 60 * 60 * 24))} days`);
    }

    // Format dates
    const startDateFormatted = formatDate(member.startDate);
    const endDateFormatted = formatDate(member.endDate);

    // Create status badge
    const statusBadge = member.paymentStatus === 'Paid'
        ? '<span class="badge bg-success">Paid</span>'
        : '<span class="badge bg-warning">Pending</span>';

    // Create status indicator
    const statusIndicator = isExpired
        ? '<span class="badge bg-danger">Expired</span>'
        : '<span class="badge bg-success">Active</span>';

    // Create action buttons
    const actionButtons = `
        <button class="btn btn-sm btn-primary me-2" onclick="openEditModal(${member.id})">
            <i class="fas fa-edit"></i> Edit
        </button>
        <button class="btn btn-sm btn-danger" onclick="openDeleteConfirm(${member.id})">
            <i class="fas fa-trash"></i> Delete
        </button>
    `;

    row.innerHTML = `
        <td>${index}</td>
        <td><strong>${escapeHtml(member.name)}</strong></td>
        <td>${escapeHtml(member.contactNumber)}</td>
        <td>${member.membershipPlan}</td>
        <td>${startDateFormatted}</td>
        <td>${endDateFormatted}</td>
        <td>${statusIndicator}</td>
        <td>${statusBadge}</td>
        <td>${actionButtons}</td>
    `;

    return row;
}

// ========================================
// Update Statistics
// ========================================
/**
 * Update statistics cards with current data
 */
function updateStatistics() {
    try {
        const totalCount = document.getElementById('totalMembers');
        const paidCount = document.getElementById('paidMembers');
        const pendingCount = document.getElementById('pendingMembers');
        const expiredCount = document.getElementById('expiredMembers');

        if (!totalCount || !paidCount || !pendingCount || !expiredCount) {
            console.warn('Statistics elements not found');
            return;
        }

        // Calculate statistics
        const total = allMembers.length;
        const paid = allMembers.filter(m => m.paymentStatus === 'Paid').length;
        const pending = allMembers.filter(m => m.paymentStatus === 'Pending').length;
        const expired = allMembers.filter(m => new Date(m.endDate) < new Date()).length;

        // Update DOM
        totalCount.textContent = total;
        paidCount.textContent = paid;
        pendingCount.textContent = pending;
        expiredCount.textContent = expired;

        console.log(`Statistics updated: Total=${total}, Paid=${paid}, Pending=${pending}, Expired=${expired}`);

    } catch (error) {
        console.error('Error updating statistics:', error);
    }
}

// ========================================
// Utility Functions
// ========================================

/**
 * Format date string to readable format
 * @param {string} dateString - ISO date string
 * @returns {string} Formatted date
 */
function formatDate(dateString) {
    try {
        const options = { year: 'numeric', month: 'short', day: 'numeric' };
        return new Date(dateString).toLocaleDateString('en-US', options);
    } catch (error) {
        console.error('Error formatting date:', error);
        return dateString;
    }
}

/**
 * Escape HTML special characters to prevent XSS
 * @param {string} text - Text to escape
 * @returns {string} Escaped text
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Show loading state in the table
 */
function showLoadingState() {
    const tbody = document.getElementById('membersTableBody');
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <p class="mt-2"><i class="fas fa-sync-alt fa-spin"></i> Refreshing members...</p>
                </td>
            </tr>
        `;
    }
}

/**
 * Show refresh indicator badge
 * Visual feedback that table is being updated
 */
function showRefreshIndicator() {
    const table = document.getElementById('membersTable');
    if (table) {
        const thead = table.querySelector('thead');
        if (thead) {
            // Check if refresh indicator already exists
            let indicator = document.getElementById('tableRefreshIndicator');
            if (!indicator) {
                indicator = document.createElement('div');
                indicator.id = 'tableRefreshIndicator';
                indicator.style.cssText = `
                    position: absolute;
                    top: 10px;
                    right: 10px;
                    background: #3498db;
                    color: white;
                    padding: 5px 12px;
                    border-radius: 20px;
                    font-size: 0.85rem;
                    font-weight: 600;
                    z-index: 10;
                    animation: slideIn 0.3s ease-in-out;
                `;
                indicator.innerHTML = '<i class="fas fa-check-circle"></i> Table updated';
                table.parentElement.style.position = 'relative';
                table.parentElement.appendChild(indicator);

                // Remove indicator after 2 seconds
                setTimeout(() => {
                    if (indicator) {
                        indicator.style.animation = 'slideOut 0.3s ease-in-out forwards';
                        setTimeout(() => indicator.remove(), 300);
                    }
                }, 2000);
            }
        }
    }
}

/**
 * Show empty state message
 * @param {string} message - Message to display
 */
function showEmptyState(message) {
    const tbody = document.getElementById('membersTableBody');
    const table = document.getElementById('membersTable');
    const emptyState = document.getElementById('emptyState');

    if (table) table.style.display = 'none';
    if (tbody) tbody.innerHTML = '';

    if (emptyState) {
        emptyState.style.display = 'block';
        emptyState.innerHTML = `
            <div class="text-center py-5">
                <i class="fas fa-exclamation-circle fa-3x text-danger mb-3"></i>
                <h5>${message}</h5>
            </div>
        `;
    }
}

// ========================================
// Notification Functions
// ========================================

/**
 * Show success notification
 * @param {string} message - Success message
 */
function showSuccess(message) {
    showNotification(message, 'success');
}

/**
 * Show error notification
 * @param {string} message - Error message
 */
function showError(message) {
    showNotification(message, 'danger');
}

/**
 * Show warning notification
 * @param {string} message - Warning message
 */
function showWarning(message) {
    showNotification(message, 'warning');
}

/**
 * Show info notification
 * @param {string} message - Info message
 */
function showInfo(message) {
    showNotification(message, 'info');
}

/**
 * Generic notification display function
 * @param {string} message - Message to display
 * @param {string} type - Alert type (success, danger, warning, info)
 */
function showNotification(message, type = 'info') {
    try {
        const alertContainer = document.getElementById('alertContainer');
        if (!alertContainer) {
            console.warn('Alert container not found, using console instead');
            console.log(`[${type.toUpperCase()}] ${message}`);
            return;
        }

        // Create alert element
        const alertId = `alert-${Date.now()}`;
        const alertClass = `alert alert-${type} alert-dismissible fade show`;

        const alert = document.createElement('div');
        alert.id = alertId;
        alert.className = alertClass;
        alert.innerHTML = `
            <i class="fas fa-${getIconForType(type)}"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        alertContainer.appendChild(alert);

        // Auto-remove after 4 seconds
        setTimeout(() => {
            const alertElement = document.getElementById(alertId);
            if (alertElement) {
                alertElement.remove();
            }
        }, 4000);

        console.log(`[${type.toUpperCase()}] ${message}`);

    } catch (error) {
        console.error('Error showing notification:', error);
    }
}

/**
 * Get icon name for notification type
 * @param {string} type - Alert type
 * @returns {string} Icon name
 */
function getIconForType(type) {
    const icons = {
        'success': 'check-circle',
        'danger': 'times-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

// ========================================
// API Error Handler
// ========================================

/**
 * Handle API errors uniformly
 * @param {Error} error - Error object
 * @returns {string} User-friendly error message
 */
function handleApiError(error) {
    console.error('API Error:', error);

    if (error.message.includes('Failed to fetch')) {
        return 'Network error. Please check your connection and try again.';
    }

    if (error.message.includes('HTTP error')) {
        return 'Server error. Please try again later.';
    }

    return error.message || 'An unexpected error occurred. Please try again.';
}

console.log('Gym Membership Management - JavaScript loaded successfully');

