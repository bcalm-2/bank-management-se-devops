# Bank Management System
> Spring Boot REST API | DevOps Lab Task 3

## Project Structure
```
bank-management/
├── src/
│   ├── main/java/com/bank/
│   │   ├── BankManagementApplication.java
│   │   ├── model/         Account.java, Transaction.java
│   │   ├── repository/    AccountRepository.java, TransactionRepository.java
│   │   ├── service/       AccountService.java
│   │   ├── controller/    AccountController.java
│   │   └── config/        DataSeeder.java, GlobalExceptionHandler.java
│   └── test/java/com/bank/
│       ├── service/       AccountServiceTest.java   (10 unit tests)
│       └── controller/    AccountControllerTest.java (5 integration tests)
├── .github/workflows/ci.yml
├── Dockerfile
└── pom.xml
```

## API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/accounts | Create account |
| GET | /api/accounts | List all accounts |
| GET | /api/accounts/{id} | Get account by ID |
| POST | /api/accounts/{id}/deposit | Deposit funds |
| POST | /api/accounts/{id}/withdraw | Withdraw funds |
| POST | /api/accounts/transfer | Transfer between accounts |
| GET | /api/accounts/{id}/transactions | Transaction history |
| DELETE | /api/accounts/{id} | Delete account |

## Run Locally
```bash
mvn spring-boot:run
```

## Run Tests
```bash
mvn test
```

## Docker
```bash
docker build -t bank-management .
docker run -p 8080:8080 bank-management
```
