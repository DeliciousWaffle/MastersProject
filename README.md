# SQL Emulator and Query Cost Analyzer
## Table of Contents
1. [Introduction](#Introduction)
2. [Query Syntax](#Query Syntax)
## Introduction
Hello! This was my Master's Project for Western Illinois University (Fall 2020). The goal of this project is to help
students better understand topics that I had a hard time with in my database classes. These topics include:
* Converting Queries to Relational Algebra
* Creation and Optimization of Query Trees
* Query Cost Analysis
* File Structure Recommendations for Improving Query Costs

Additionally, this application also acts as a pseudo SQL emulator. Although writing queries is the main
focus, you have the capability to create tables, modify them, etc. You can
also play as different users, each with their own set of privileges that govern what kinds of commands
they are allowed to execute. This application was developed using Java (Version 8), tested using JUnit 5, and uses
JavaFX for GUI functionality.
## GUI at a Glance
This section is to show you what you're in for when you run the application. Each image is taken directly from the
application. I'll describe what is going on in each screen.
### Terminal Screen
This is where most of the action will take place. Here is a brief description of each button.
* Green Play Button - Executes the command entered the input a rea. If you don't adhere to the syntax of a 
particular command, my application will either yell at you, blow up, or both.
* Eraser Button - Clears the input/output areas.
* Table Button - Launches a new window displaying the result set of a query.
* Pi Button - Launches a new window displaying a query's equivalent relational algebra.
* Tree Button - Launches a new window displaying a query tree representing the query. You can click around through the
various states the tree takes on during the optimization process in this window.
* Dollar Sign Button - Launches a new window displaying a breakdown of the cost of executing a query.
* Folder Button - Launches a new window suggesting what file structures to build in order to improve query costs.
![Screenshot](src/files/images/readme/TerminalScreen.png)
### Tables Screen
This is where one can browse through the various tables of the system. You can also build file structures on columns
of tables in order to improve query costs.
![Screenshot](src/files/images/readme/TablesScreen.png)
### Users Screen
This is where one can view and play as the various users of the system. The user that you choose determines what
you are allowed to do in the system.
![Screenshot](src/files/images/readme/UsersScreen.png)
### Options Screen
This is where one can change how to application behaves. There is also an option to save changes made or restore
back to the initial database.
![Screenshot](src/files/images/readme/OptionsScreen.png)
### Help Screen
This is where one can view the ER diagram representing the database or syntax diagrams of all the commands available.
![Screenshot](src/files/images/readme/HelpScreen.png)
## Query Syntax
Since queries are the main focus of this application, here is a diagram outlining a query's accepted syntax. Here are
some things to note:
* The case of keywords (strings in white nodes) does not matter
* 
![Screenshot](src/files/images/helpscreen/QueryDiagram.png)
### Query Examples
Here are some examples of queries that one can write.

    SELECT CustomerID, FirstName, LastName
    FROM Customers
    WHERE CustomerID > 20 AND CustomerID < 40; 
    
    SELECT FirstName, LastName
    FROM Customers, CustomerPurchaseDetails
    WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID
        AND PaymentMethod = "Discover"; 
    
    SELECT PaymentMethod, COUNT(PaymentMethod)
    FROM CustomerPurchaseDetails
    GROUP BY PaymentMethod
    HAVING COUNT(PaymentMethod) > 10;
## Other Commands
Here are the other commands available. Diagrams outlining their syntax can be found in src/files/images/helpscreen/
* CREATE TABLE
* ALTER TABLE
* DROP TABLE
* INSERT
* UPDATE
* DELETE
* GRANT
* REVOKE
## Query Tree Example
Here is an example of a query being transformed into a query tree. The query tree goes through an optimization process
before being executed. This is to make execution of a query more efficient. Each section will show the tree after 
applying a transformation. This is the query that we're working with.

    SELECT FirstName, LastName
    FROM Customers, CustomerPurchaseDetails
    WHERE Customers.CustomerID = CustomerPurchaseDetails.CustomerID
        AND PaymentMethod = "Discover";
    
### Initial Query Tree
![Screenshot](src/files/images/readme/InitialQueryTree.png)
### After Breaking Up Selections
![Screenshot](src/files/images/readme/AfterBreakingUpSelections.png)
### After Pushing Down Selections
![Screenshot](src/files/images/readme/AfterPushingDownSelections.png)
### After Forming Joins
![Screenshot](src/files/images/readme/AfterFormingJoins.png)
### After Cascading and Pushing Down Projections
![Screenshot](src/files/images/readme/AfterCascadingAndPushingDownProjections.png)
### Pipelining Subtrees
#### First Iteration
![Screenshot](src/files/images/readme/AfterPipeliningSubtrees0.png)
#### Second Iteration
![Screenshot](src/files/images/readme/AfterPipeliningSubtrees1.png)
#### Third Iteration
![Screenshot](src/files/images/readme/AfterPipeliningSubtrees2.png)
