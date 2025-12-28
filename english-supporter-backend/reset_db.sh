#!/bin/bash

echo "Resetting database..."
echo

mysql -u root -pcmanh7524 < src/main/resources/db/migration/reset_database.sql

if [ $? -eq 0 ]; then
    echo
    echo "Database reset successfully!"
    echo "You can now restart the application."
else
    echo
    echo "Error resetting database. Please check your MySQL connection."
fi

