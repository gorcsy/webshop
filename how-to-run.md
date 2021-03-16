# Buy Our Stuff Webshop

## How To Run in IntelliJ
- import as a Maven project
- click the `Maven` label
- click the project's name
- click `Plugins`
- click `jetty`
- double click `jetty:run`
- open `localhost:8888` in the browser

## Enviromental Variables for run configurations
- Using in memory implementation:
    - DAO_TYPE=MEMORY
- Using database implementation:
    - DAO_TYPE=DATABASE
    - PSQL_USER_NAME=your_user_name
    - PSQL_PASSWORD=your_password
    - PSQL_DB_NAME=buy_our_stuff_db
- Using file implementation:
    - DAO_TYPE=FILE
    
- To run unit tests for database implementation:
    - DAO_TYPE=DATABASE
    - PSQL_USER_NAME=your_user_name
    - PSQL_PASSWORD=your_password
    - PSQL_DB_NAME=test_buy_our_stuff_db
    
    To use database implementation set role to superuser!      
