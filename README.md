# Flight Advisor a.k.a cheap-flights
#### API with functionality to find the cheapest flight between two airports.

## Getting started - project description
Application has two databases, H2 in-memory relational database and Neo4j graph database. Graph database
is used because routes between airports and airports themselves can be represented as a graph where airports represent graph nodes
and routes are connections between nodes. By using Neo4j we can get all kinds of additional functionality since Neo4j has plugins that 
include different kinds of search algorithms(e.g. dijkstra and A*). For this case APOC library was used. More about APOC can be found [here](https://neo4j.com/docs/labs/apoc/current/).
I've used dijsktra this time and the price between airports was used as weight(cost function).

One of the reasons that I used Neo4j and is because of it's browser gui. If you start the app using dev or docker profile
(explained below) you will be able to go to http://localhost:7474/browser and using these credientals (neo4j:secret) 
you will be logged in. Import the initial data (also explained below) and watch how Neo4j represents all the graphs. If you want to 
test it, Neo4j uses Cypher as its query language. Query for finding the cheapes flight can be found in _AirportGraphRepository_ as value in Query annotation.  

#### Postman collection
Postman collection is included, so for easier testing import postman collection and environment. Both of these files can be found in 
_postman_ folder in the project root.
When calling ```/authenticate``` endpoint response will be automatically parsed and inserted into access_token variable in postman's environment.

### H2 in-memory
Flyway was used to handle migrations. It stores data in a file on disk, so data would be persisted longer than in memory (easier for testing).

### Neo4j 
Neo4j doesn't support plugins like APOC if it's used as embedded server, so I had to run with docker and pass plugin to a container.
Since plugin is needed each time the container has been run I created a new image with ````docker/neo4j.dockerfile````.

### Security
Standard Spring Security package was used with JWT tokens. User can only have one role, ADMIN or USER. Based on 
role user can consume some endpoints or not. 

### Spring Logging Aspect
Used for logging function calls in ````@RestControllers````. Easier for debugging.

### Initial data - routes and airports
CSV files with ORACLE format are supported. There are two endpoint for importing data:
1. /api/v1/import/airports
2. /api/v1/import/routes

Both endpoint return a response immediately, so they are non-blocking endpoint and file parsing is done in 
new thread since it can take some time to parse large amount of routes and airports and client could get "Connection timeout" which I
wanted to avoid. There is also one more endpoint which is useful when you import new airports and routes, and that endpoint is:
````
/api/v1/import/status?filename=routes.txt
````
Where as query param you can pass filename that you've uploaded and as a response you can get **DONE** or **PARSING**.

I've tested importing of inital data with airports.txt and routes.txt. They can be found in _data_ folder in project root.

## How to run it?

Three spring profiles are included in the project: 

1. **dev**
2. **docker**
3. **prod**

For each profile an admin user is created.
```
Username: ironman

Password: test1234
```
#### **Dev** profile
In order to run it with 'dev' profile you need to set value of environment variable ```spring_profiles_active``` to ```dev```. 
That is one way to do it, or if you use IntelliJ as IDE you can set 'Active profiles' in run configuration. This 
profile will run the app on port 8080. In order to run Neo4j you can use this command to start docker with all necessary variables:
```
docker run --rm -e NEO4J_AUTH=neo4j/secret -p 7474:7474 -v $PWD/plugins:/plugins -e NEO4J_dbms_security_procedures_unrestricted=apoc.\\\* -p 7687:7687 neo4j:3.5
```

### **Docker** profile
Using docker-compose run both, database and api, with docker. Since these two docker images whose dockerfiles can be found in docker folder
require a couple of environment variables. In order to make this easier for you I created a small shell script which will do everything for you.
The script is in project root and can be run with:
```
./docker-compose.sh
```
Just don't forget to make the script executable. If you are on linux user this command:
```shell script
chmod +x docker-compose.sh
```
If you have completed all this you can use postman, curl or any other tool to send request to the API. The same as in dev profile app is on port 8080 and database gui can be found on port 7474.

### **Prod** profile
This profile is used for AWS. The whole project is deployed on AWS using ECS services, Secrets Manager and ECR.
As CI/CD I used jenkins. After the build Jenkins will push the new version of docker container to ECR. ECS service will read that event and try to replace an existing container with the new one.

In this case you can't use Neo4j's GUI. It can only be reached and login screen opened on the same address with 7474 port,
 but in order to authenticate and continue using it Neo4j GUI client uses their own Bolt protocol which relies on websocket, but 
 my current AWS infrastructure supports only HTTP so you will get an error.  

### Jenkinsfile & Jenkins
Jenkins has been set up on a clean AWS EC2 instance with IAM role that has access to ECR. A pipeline project on Jenkins is used for this project. Jenkins has a webhook on GitHub push events, so every time a change is pushed to GitHub, Jenkins will pull _master_ 
branch, build database and api containers and push them to ECR.

### Terraform
However, not 100% finished, only networking is ready, there are still ECS tasks, services and roles missing.

## Next steps
* Add pagination on GET /api/v1/cities
* Finish terraform so project could easily be transferred from one AWS account to another. 
* Split the API into two microservices, the one with users and cities and, and the other one with the cheapest flight calculation classes and neo4j/graph classes. 
The project structure is already "separated" with _graph_ folders inside some packages.



 


