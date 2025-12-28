# English Supporter Backend

Spring Boot backend application for English vocabulary learning. This application scrapes word data from Cambridge Dictionary and tratu.soha.vn to provide comprehensive English-Vietnamese dictionary functionality.

## Features

- **Word Scraping**: Automatically scrapes word definitions, pronunciations, examples, and Vietnamese translations
- **Word Management**: Add words to personal collection (MyWords)
- **Flashcards**: Generate flashcards from your word collection with spaced repetition algorithm
- **RESTful API**: Clean REST API for frontend integration

## Technology Stack

- **Spring Boot 3.2.0**
- **Spring Data JPA** - Database access
- **MariaDB** - Database
- **Jsoup** - HTML parsing for web scraping
- **WebFlux** - Reactive HTTP client
- **Lombok** - Reduce boilerplate code

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- **MariaDB Server** (see [DATABASE_SETUP.md](DATABASE_SETUP.md) for setup instructions)

## Setup

1. **Setup MariaDB Database** (see [DATABASE_SETUP.md](DATABASE_SETUP.md) for detailed instructions):
   - Install MariaDB Server
   - Create database: `CREATE DATABASE english_supporter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
   - Update database credentials in `src/main/resources/application.properties`

2. Clone the repository:
```bash
git clone <repository-url>
cd english-supporter-backend
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Words

#### Get Word by Text
```
GET /api/words/{word}
```
Returns word details including definitions, examples, and Vietnamese translations.

**Response:**
```json
{
  "id": 1,
  "text": "hello",
  "type": "exclamation",
  "pronunciation": "/həˈloʊ/",
  "definitions": [...],
  "categories": [...],
  "inMyWords": false
}
```

#### Search and Scrape Word
```
POST /api/words/search
Content-Type: application/json

{
  "word": "hello"
}
```
Searches for a word and scrapes it if not already in database.

### My Words

#### Get All My Words
```
GET /api/mywords
```
Returns all words in your personal collection.

#### Add Word to My Words
```
POST /api/mywords/{wordId}
```
Adds a word to your personal collection.

#### Delete Word from My Words
```
DELETE /api/mywords/{wordId}
```
Removes a word from your personal collection.

### Flashcards

#### Get Flashcards
```
GET /api/flashcards?limit=30
```
Returns flashcards for spaced repetition learning. Words are sorted by last shown date (oldest first) and shuffled.

**Query Parameters:**
- `limit` (optional): Number of flashcards to return (default: 30)

## Database Schema

The application uses SQLite database with the following tables:

- **words**: Main word table (text, type, pronunciation)
- **engdefs**: English definitions
- **examples**: Example sentences for definitions
- **categories**: Word categories (e.g., "Từ đồng nghĩa", "Từ trái nghĩa")
- **meanings**: Vietnamese meanings for each category
- **mywords**: User's personal word collection with tracking (last_shown, show_count)

## Configuration

Application configuration is in `src/main/resources/application.properties`:

- **Database**: MariaDB (default: `localhost:3306/english_supporter`)
  - Update `spring.datasource.url`, `username`, and `password` as needed
  - See [DATABASE_SETUP.md](DATABASE_SETUP.md) for detailed setup
- **Server Port**: 8080 (configurable)
- **CORS**: Enabled for all origins (configure as needed for production)

### Database Configuration Example

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/english_supporter
spring.datasource.username=root
spring.datasource.password=your_password
```

## Improvements over Original Flask Version

1. **Better Error Handling**: Comprehensive exception handling with proper HTTP status codes
2. **Reactive HTTP Client**: Uses WebFlux for non-blocking HTTP requests
3. **DTO Pattern**: Clean separation between entities and API responses
4. **Transaction Management**: Proper transaction handling for data consistency
5. **Logging**: Structured logging with SLF4J
6. **Validation**: Request validation using Jakarta Validation
7. **Type Safety**: Strong typing with Java generics

## Notes

- The application scrapes data from external websites. Be respectful of their terms of service and rate limits.
- Database tables are created automatically on first run (via `spring.jpa.hibernate.ddl-auto=update`).
- For production, consider:
  - Using environment variables for database credentials
  - Configuring proper CORS settings
  - Adding authentication/authorization
  - Implementing rate limiting
  - Adding caching for frequently accessed words
  - Setting up database backups

## License

This project is for educational purposes.

