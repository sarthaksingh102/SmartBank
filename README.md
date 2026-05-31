# SmartBank - Online Banking Management System

A modern, secure online banking platform built with Spring Boot and MySQL. SmartBank allows users to manage their bank accounts, transfer funds, pay bills, and view transaction analytics.

## Project Overview

SmartBank is an enterprise-level banking application designed for final-year student projects. It combines modern web technologies with secure banking practices to provide a comprehensive banking experience.

## Technology Stack

### Backend
- **Spring Boot 3.1.5** - Modern Java framework
- **Spring MVC** - Web application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - ORM and database operations

### Frontend
- **Thymeleaf** - Server-side template engine
- **Bootstrap 5** - Responsive UI framework
- **HTML/CSS** - Markup and styling
- **JavaScript** - Client-side interactions
- **Chart.js** - Data visualization

### Database
- **MySQL** - Relational database

### Additional Libraries
- **Lombok** - Reduce boilerplate code
- **ZXing** - QR code generation and scanning

## Core Features

### 1. User Management
- Secure registration and login
- User profile management
- Role-based access control (User/Admin roles)
- Password encryption using BCrypt

### 2. Account Management
- Create and manage multiple bank accounts
- View account balance and details
- Account types (Savings, Checking, etc.)

### 3. Fund Transfer
- Transfer money between accounts
- Validation of sufficient balance
- Transaction history tracking
- Unique reference numbers for each transaction

### 4. Transaction History
- Complete transaction records
- Categorized transaction types
- Date and time stamps
- Transaction status tracking

### 5. QR Payment
- Generate QR codes for payment requests
- Base64 encoded QR images
- QR expiry management
- Payment simulation

### 6. Bill Payment Module
- Mobile recharge simulation
- Electricity bill payments
- Subscription payments
- Multiple payment providers support

### 7. Scheduled Transactions
- Schedule future fund transfers
- Automatic execution at scheduled time
- Transaction status management
- Scheduled transaction history

### 8. Monthly Expense Analytics
- Visual spending reports using Chart.js
- Category-wise expense breakdown
- Monthly expense trends

### 9. Admin Dashboard
- User management
- Transaction monitoring
- System statistics

## Database Schema

### Tables

1. **users** - User information and authentication
   - id, email (unique), password, fullName, phoneNumber
   - address, city, state, pinCode
   - role (USER/ADMIN), isActive, timestamps

2. **accounts** - Bank accounts
   - id, accountNumber (unique), accountType
   - balance, isActive, timestamps
   - user_id (foreign key)

3. **transactions** - Transaction records
   - id, account_id, transactionType
   - amount, description, status
   - recipientAccount, recipientName
   - referenceNumber (unique), transactionDate

4. **bill_payments** - Bill payment history
   - id, account_id, billType, provider, billerId
   - amount, status, paymentDate, referenceNumber

5. **qr_payments** - QR payment information
   - id, account_id, qrCode (unique), amount
   - description, status, expiryDate
   - scannedBy, scannedAt, createdAt

6. **scheduled_transactions** - Scheduled transfers
   - id, account_id, recipientAccount, recipientName
   - amount, description, scheduledDate
   - status, executedDate, referenceNumber

## Project Structure

```
smartbank/
├── src/
│   ├── main/
│   │   ├── java/com/smartbank/
│   │   │   ├── SmartBankApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   ├── BillPaymentController.java
│   │   │   │   ├── QRPaymentController.java
│   │   │   │   └── ScheduledTransactionController.java
│   │   │   ├── dto/
│   │   │   │   ├── UserRegistrationDto.java
│   │   │   │   ├── FundTransferDto.java
│   │   │   │   └── TransactionDto.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Account.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── BillPayment.java
│   │   │   │   ├── QRPayment.java
│   │   │   │   └── ScheduledTransaction.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── AccountRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   ├── BillPaymentRepository.java
│   │   │   │   ├── QRPaymentRepository.java
│   │   │   │   └── ScheduledTransactionRepository.java
│   │   │   └── service/
│   │   │       ├── CustomUserDetailsService.java
│   │   │       ├── UserService.java
│   │   │       ├── AccountService.java
│   │   │       ├── TransactionService.java
│   │   │       ├── BillPaymentService.java
│   │   │       ├── QRPaymentService.java
│   │   │       └── ScheduledTransactionService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   │           ├── index.html
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── dashboard.html
│   │           ├── transfer.html
│   │           ├── bills.html
│   │           ├── qr-payment.html
│   │           ├── schedule-transfer.html
│   │           ├── account-details.html
│   │           └── error/
│   │               └── 403.html
├── pom.xml
└── README.md
```

## Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd smartbank
   ```

2. **Configure Database**
   - Create a MySQL database named `smartbank`
   - Update `application.properties` with your MySQL credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/smartbank
     spring.datasource.username=root
     spring.datasource.password=your_password
     ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Open browser and navigate to: `http://localhost:8080/smartbank/`

## User Workflow

1. **Registration** - New users create an account with personal details
2. **Login** - Users authenticate with email and password
3. **Dashboard** - View all accounts and quick action buttons
4. **Operations** - Perform banking operations:
   - Transfer funds between accounts
   - Pay bills (mobile recharge, electricity, etc.)
   - Generate QR codes for payments
   - Schedule future transactions
5. **Analytics** - View transaction history and monthly expenses
6. **Logout** - Securely log out of the application

## Security Features

- BCrypt password encryption
- Spring Security framework
- Role-based access control
- CSRF protection
- SQL injection prevention through JPA
- Session management
- Secure authentication middleware

## API Endpoints

### Authentication
- `GET /smartbank/` - Home page
- `GET /smartbank/login` - Login page
- `POST /smartbank/login` - Process login
- `GET /smartbank/register` - Registration page
- `POST /smartbank/register` - Process registration
- `GET /smartbank/logout` - Logout

### Dashboard
- `GET /smartbank/dashboard` - View dashboard
- `GET /smartbank/account/{id}` - View account details

### Transactions
- `GET /smartbank/transfer` - Fund transfer page
- `POST /smartbank/transfer` - Process fund transfer

### Bills
- `GET /smartbank/bills` - Bill payment page
- `POST /smartbank/pay-bill` - Process bill payment

### QR Payment
- `GET /smartbank/qr-payment` - QR payment page
- `POST /smartbank/generate-qr` - Generate QR code

### Scheduled Transactions
- `GET /smartbank/schedule-transfer` - Schedule transfer page
- `POST /smartbank/schedule-transfer` - Schedule transaction

## Running Scheduled Tasks

The application includes a scheduler for executing scheduled transactions:
- Runs every minute
- Checks for transactions scheduled for past dates
- Automatically executes valid transactions
- Updates transaction status and reference numbers

## Testing

The project includes security and integration tests. Run tests with:
```bash
mvn test
```

## Cloud Deployment (Render - 100% Free)

This application is fully configured for a free cloud deployment on **Render.com** using a multi-stage Docker build and a PostgreSQL database.

### 1. Create the Database
1. Go to [Render.com](https://render.com/) and click **New -> PostgreSQL**.
2. Name it (e.g., `smartbank-db`), select the **Free** plan, and click **Create Database**.
3. Once created, copy the **Internal Database URL** (begins with `postgres://`).

### 2. Deploy the Application
1. On your Render Dashboard, click **New -> Web Service**.
2. Connect your GitHub account and select the `SmartBank` repository.
3. In the "Language" or "Environment" dropdown, select **Docker**.
4. Under **Environment Variables**, add the following:
   - `SPRING_PROFILES_ACTIVE` = `prod`
   - `DB_URL` = *(Paste the Internal Database URL from Step 1)*
5. Ensure the **Free** plan is selected, and click **Create Web Service**.

Render will automatically use the included `Dockerfile` to build the Java application and launch it!

## Future Enhancements

- Mobile app integration
- Biometric authentication
- Multi-factor authentication (2FA)
- Investment portfolio management
- Loan application system
- Advanced analytics and reporting
- Real-time notifications
- Cryptocurrency support

## License

This project is provided for educational purposes.

## Contributing

Contributions are welcome! Please follow the coding standards and create a pull request.

## Support

For issues or questions, please contact the development team.

---

**Created:** May 2026  
**Version:** 1.0.0
