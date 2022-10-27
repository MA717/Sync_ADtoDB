**Employee Synchronize Service**

A microService which notices any changes in the Employee Active directory . if it founds  change occurred in the active directory
it will fire an event that contain the change that has happened to all the other microservices which subscribed on the same event hub


**Introduction**

Synchronize microservice provides access to all the employee active directory data in easy way. it allows the other service
to access all the updated list of Employees in the company without needing to deal with ldap queries.Moreover, it sends events which contain the
changes that occurred in every employee tuple to all the other subscribed microservices automatically.It can also initialise the other
microservices databases by sending them a snapshot with the actual employee active directory.


**Features**

1) fetch All the data from the Employee Active Directory 
2) Save the Data in Postgres Docker Container 
3) initialise any new subscriber microservice by sending all the updated list of Employee to them
4) notice all the change that occur in the Employee Active Directory
5) fire Events with all the changes to the other microservice (Attributes change,new Employee added,Employee deleted )




