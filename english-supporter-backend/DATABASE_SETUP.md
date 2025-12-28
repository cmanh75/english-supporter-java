# MariaDB Setup Guide

## Prerequisites

1. Install MariaDB Server
   - **Windows**: Download from [MariaDB Downloads](https://mariadb.org/download/)
   - **Linux**: `sudo apt-get install mariadb-server` (Ubuntu/Debian) or `sudo yum install mariadb-server` (CentOS/RHEL)
   - **macOS**: `brew install mariadb`

## Database Setup Steps

### 1. Start MariaDB Service

**Windows:**
```bash
# Start MariaDB service from Services or:
net start MariaDB
```

**Linux:**
```bash
sudo systemctl start mariadb
sudo systemctl enable mariadb  # Enable auto-start on boot
```

**macOS:**
```bash
brew services start mariadb
```

### 2. Secure Installation (First time setup)

```bash
sudo mysql_secure_installation
```

Follow the prompts to set root password and secure the installation.

### 3. Create Database

Login to MariaDB:
```bash
mysql -u root -p
```

Create database and user:
```sql
CREATE DATABASE english_supporter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create a dedicated user (optional, recommended for production)
CREATE USER 'english_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON english_supporter.* TO 'english_user'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

### 4. Update Application Configuration

Edit `src/main/resources/application.properties`:

```properties
# For root user
spring.datasource.url=jdbc:mariadb://localhost:3306/english_supporter
spring.datasource.username=root
spring.datasource.password=your_root_password

# OR for dedicated user
spring.datasource.url=jdbc:mariadb://localhost:3306/english_supporter
spring.datasource.username=english_user
spring.datasource.password=your_password
```

### 5. Using Environment Variables (Recommended)

For better security, use environment variables:

**application.properties:**
```properties
spring.datasource.url=jdbc:mariadb://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:english_supporter}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
```

**Set environment variables:**
- **Windows (PowerShell):**
  ```powershell
  $env:DB_HOST="localhost"
  $env:DB_PORT="3306"
  $env:DB_NAME="english_supporter"
  $env:DB_USERNAME="root"
  $env:DB_PASSWORD="your_password"
  ```

- **Linux/macOS:**
  ```bash
  export DB_HOST=localhost
  export DB_PORT=3306
  export DB_NAME=english_supporter
  export DB_USERNAME=root
  export DB_PASSWORD=your_password
  ```

### 6. Verify Connection

Run the application:
```bash
mvn spring-boot:run
```

The application will automatically create tables on first run (due to `spring.jpa.hibernate.ddl-auto=update`).

## Troubleshooting

### Connection Refused
- Ensure MariaDB service is running
- Check if MariaDB is listening on port 3306: `netstat -an | grep 3306`

### Access Denied
- Verify username and password in `application.properties`
- Check user privileges: `SHOW GRANTS FOR 'username'@'localhost';`

### Database Not Found
- Ensure database exists: `SHOW DATABASES;`
- Create database if missing (see step 3)

### Character Encoding Issues
- Ensure database uses UTF8MB4: `ALTER DATABASE english_supporter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

## Production Recommendations

1. **Use dedicated database user** (not root)
2. **Use environment variables** for credentials
3. **Set up connection pooling** (HikariCP is included by default)
4. **Enable SSL** for remote connections
5. **Regular backups**: `mysqldump -u root -p english_supporter > backup.sql`
6. **Monitor performance** with MariaDB monitoring tools


