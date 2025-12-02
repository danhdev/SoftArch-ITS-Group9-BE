# AI Service - Database Configuration

## Environment Variables Setup

This application uses environment variables from a `.env` file to configure database connections securely.

### Initial Setup

1. **Copy the example environment file:**
   ```bash
   copy .env.example .env
   ```

2. **Edit the `.env` file** with your actual database credentials:
   ```properties
   # Server Configuration
   SERVER_PORT=8080

   # Database Configuration - PostgreSQL
   DB_URL=jdbc:postgresql://localhost:5432/ai_service_db
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   DB_DRIVER=org.postgresql.Driver
   HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

   # JPA Configuration
   JPA_DDL_AUTO=update
   JPA_SHOW_SQL=true
   ```

### Database Options

#### PostgreSQL (Recommended for Production)
```properties
DB_URL=jdbc:postgresql://localhost:5432/ai_service_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_DRIVER=org.postgresql.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

#### H2 In-Memory Database (For Testing)
```properties
DB_URL=jdbc:h2:mem:testdb
DB_USERNAME=sa
DB_PASSWORD=
DB_DRIVER=org.h2.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.H2Dialect
```

#### MySQL
```properties
DB_URL=jdbc:mysql://localhost:3306/ai_service_db
DB_USERNAME=root
DB_PASSWORD=your_password
DB_DRIVER=com.mysql.cj.jdbc.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
```

### JPA Configuration Options

- **JPA_DDL_AUTO**: Controls database schema generation
  - `update` - Update the schema (recommended for development)
  - `create` - Drop and recreate schema on startup
  - `create-drop` - Create schema on startup, drop on shutdown
  - `validate` - Validate schema but don't make changes (recommended for production)
  - `none` - Disable schema management

- **JPA_SHOW_SQL**: 
  - `true` - Show SQL queries in logs (useful for debugging)
  - `false` - Hide SQL queries (recommended for production)

### Running the Application

```bash
mvnw spring-boot:run
```

The application will automatically load the `.env` file and configure the database connection.

### Security Notes

⚠️ **Important**: 
- Never commit the `.env` file to version control
- The `.env` file is already added to `.gitignore`
- Share `.env.example` instead, which contains no sensitive data
- Each developer/environment should have their own `.env` file with appropriate credentials

### Troubleshooting

If you encounter database connection issues:

1. Verify your database is running
2. Check the credentials in your `.env` file
3. Ensure the database exists (create it if needed):
   ```sql
   CREATE DATABASE ai_service_db;
   ```
4. Check that the database driver is compatible with your database version
5. Review application logs for detailed error messages

