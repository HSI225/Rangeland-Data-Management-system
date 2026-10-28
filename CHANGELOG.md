# Changelog

## 2026.10 - Commercial refinement

- Standardized all Java classes under the `eiseb.serv` package.
- Added centralized database, JSON, servlet and password utilities.
- Replaced plaintext password handling with PBKDF2-HMAC-SHA256 hashing.
- Added automatic upgrade for legacy plaintext passwords after successful login.
- Added authentication filtering and manager-only report protection.
- Added environment-based database configuration while retaining local defaults.
- Rebuilt the interface with responsive shared CSS, external JavaScript and accessible forms.
- Converted destructive actions to POST forms with confirmation.
- Added server-side validation and referential delete protection.
- Added MySQL schema, indexes, constraints, initial users and optional sample data.
- Removed generated build and private IDE artifacts from the source package.
- Added project documentation and deployment guidance.
