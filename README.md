# Eiseb Rangeland Manager

A commercial-grade Jakarta Servlet web application for managing rangeland camps, grazing rotations, vegetation monitoring and water infrastructure.

## Highlights

- Responsive, accessible dashboard and management UI
- Secure PBKDF2 password hashing with automatic legacy-password upgrade
- Session authentication and role-based report access
- Prepared statements and centralized JSON encoding
- Server-side input validation and consistent error handling
- Filterable reports with print and CSV export support
- Clean MySQL schema, indexes, constraints and setup scripts

## Technology stack

- Java/Jakarta EE 11 Web
- GlassFish-compatible servlet application
- NetBeans Ant project structure
- MySQL 8.x
- MySQL Connector/J 9.3.0

## Project structure

```text
Eiseb_RDMS_Updated/
├── database/                 # Schema and optional sample data
├── nbproject/                # NetBeans project metadata
├── src/java/eiseb/serv/      # Servlet, filter and utility classes
├── web/                      # JSP, HTML, CSS, JavaScript and images
│   └── WEB-INF/lib/          # MySQL JDBC driver
├── build.xml
└── README.md
```

## Database setup

1. Create the database and initial users:

   ```bash
   mysql -u root -p < database/schema.sql
   ```

2. Optional demonstration data:

   ```bash
   mysql -u root -p < database/sample_data.sql
   ```

The schema preserves the original application table names and core column contracts.

## Initial accounts

| Username | Role | Development password |
|---|---|---|
| `admin` | admin | `ChangeMe123!` |
| `manager` | manager | `Manager123!` |
| `staff` | staff | `Staff123!` |

Replace these passwords before production deployment. Existing legacy plaintext passwords in an old database are automatically re-hashed after the user's next successful login.

## Configuration

`DBConnection` uses these environment variables when available:

- `EISEB_DB_URL`
- `EISEB_DB_USER`
- `EISEB_DB_PASSWORD`

If they are not set, it falls back to the original local development database:

- URL: `jdbc:mysql://localhost:3306/eiseb_rangeland`
- User: `root`
- Password: blank

See `.env.example` for a template.

## Build and deploy

### NetBeans + GlassFish

1. Open the project in NetBeans.
2. Configure a GlassFish server that supports Jakarta EE 11 Web.
3. Confirm MySQL is running and `database/schema.sql` has been imported.
4. Clean and build the project.
5. Run or deploy the application to GlassFish.
6. Open `http://localhost:8080/Eiseb_RDMS/login.html`.

The application is deployed under the `/Eiseb_RDMS` context root defined in `web/WEB-INF/glassfish-web.xml`.

## Security and operational notes

- Protected pages and JSON endpoints require an authenticated session.
- Manager reports are limited to `admin` and `manager` roles.
- Session cookies are HTTP-only and URL session tracking is disabled.
- Security headers include CSP, frame denial, nosniff and referrer policy.
- Deletion of camps is restricted when related operational records exist.
- Register new accounts as staff; promote trusted users to manager/admin directly in the database.
- For HTTPS production deployments, enable the `Secure` session-cookie flag at the server or load balancer.

## CSV export

The reports page exports the currently filtered camp, monitoring, grazing and water records as CSV files. CSV generation happens in the browser and preserves the current filters.

## Production hardening checklist

- [ ] Replace all seeded account passwords.
- [ ] Create a dedicated least-privilege MySQL user.
- [ ] Set `EISEB_DB_URL`, `EISEB_DB_USER` and `EISEB_DB_PASSWORD`.
- [ ] Serve the application over HTTPS.
- [ ] Enable GlassFish access logs and centralized application logging.
- [ ] Configure database backups and recovery tests.
- [ ] Restrict self-registration if it is not required operationally.
