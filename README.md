# BladeBud/ETS

## Overview
BladeBud/ETS is an Event Ticket System designed to streamline event management and ticketing processes. It provides a comprehensive solution for creating, managing, and selling tickets for events.

## Setup Instructions

### Prerequisites
- Node.js (v14 or higher)
- npm (v6 or higher)
- A running instance of a database (e.g., MySQL, PostgreSQL)

### Step-by-Step Guide

1. **Clone the Repository**
    ```sh
    git clone https://github.com/BladeBud/ETS.git
    cd ETS
    ```

2. **Install Dependencies**
    ```sh
    npm install
    ```

3. **Configure Environment Variables**
    - Create a `.env` file in the root directory.
    - Add the following properties:
        ```
        DB_HOST=your_database_host
        DB_USER=your_database_user
        DB_PASS=your_database_password
        DB_NAME=your_database_name
        PORT=3000

        # IMAP Mail Configuration
        IMAP_HOST=your_imap_host
        IMAP_PORT=your_imap_port
        IMAP_USER=your_imap_user
        IMAP_PASS=your_imap_password
        ```

4. **Initialize the Database**
    - Make sure your database server is running.
    - Run the database initialization script (if applicable):
        ```sh
        npm run db:init
        ```

5. **Run the Application**
    ```sh
    npm start
    ```

6. **Access the Application**
    - Open your browser and go to `http://localhost:3000`.

### Additional Information
- **Configuration**: The `.env` file can be modified to change the application settings.
- **Scripts**: Useful npm scripts include:
    - `npm run dev`: Run the application in development mode with hot-reloading.
    - `npm run test`: Run the test suite.
    - `npm run build`: Build the application for production.

### Troubleshooting
- Ensure that all environment variables are correctly set.
- Check for any errors in the console and consult the logs for more details.

### Contributing
If you wish to contribute to this project, please follow the standard GitHub workflow:
1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Submit a pull request.

For any further questions or issues, please refer to the documentation or open an issue on GitHub.
