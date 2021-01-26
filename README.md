# SQL Emulator and Query Cost Analyzer
## Intro
Hello! This was my Masters Project for Western Illinois University (Fall 2020). The goal of this project is to help
students better understand topics that I had a hard time with in my database classes. These topics include:
* Converting Queries to Relational Algebra
* Creation and Optimization of Query Trees
* Query Cost Analysis
* File Structure Recommendations for Improving Query Costs

Additionally, this application also acts as a pseudo SQL emulator. Although writing queries is the main
focus, you have the capability to create tables, modify them, query them, etc. You can
also play as different users, each with their own set of privileges that govern what kinds of commands
they are allowed to execute. This application was developed using Java (Version 8), tested using JUnit 5, and uses
JavaFX for GUI functionality.
## GUI at a Glance
This section is to show you what you're in for when you run the application. I'll describe what is going on in
each screen.
### Terminal Screen
This is where most of the action will take place. Here is a brief description of each button.
* Green Play Button - Executes the command entered the input area. If you don't adhere to the syntax of a 
particular command, my application will either yell at you, blow up, or both.
* Eraser Button - Clears the input/output areas.
* Table Button - Launches a new window displaying the result set of a query.
* Pi Button - Launches a new window displaying a query's equivalent relational algebra.
* Tree Button - Launches a new window displaying a query's equivalent query tree. You can click around through the
various states the tree takes on during the optimization process.
* Dollar Sign Button - Launches a new window displaying a breakdown of the cost of executing a query.
* Folder Button - Launches a new window suggesting what file structures to build in order to improve query costs.
[](src/files/images/readme/TerminalScreen.png)
### Tables Screen
### Users Screen
###
## Query Syntax
Below is a diagram showcasing the accepted syntax of a Query. 
![Screenshot](src/files/images/helpscreen/QueryDiagram.png)
### Query Examples

## Other Commands
Here are the other commands available. Diagrams for these commands can be found in src/files/images/helpscreen/
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
before being executed.

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
![Screenshot](src/files/images/readme/AfterPipelining0.png)
![Screenshot](src/files/images/readme/AfterPipelining1.png)
![Screenshot](src/files/images/readme/AfterPipelining2.png)
