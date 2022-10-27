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
3) initialise any new subscriber microservice by sending all the updated list of Employee to them automatically 
4) notice all the changes that occur in the Employee Active Directory
5) fire Events with all the changes to the other microservice (Attributes changes , Employee added,Employee deleted )



**How to Use the Project**
1) first you have to  establish a connection with the desired Employee Active Directory
2) establish a connection with azure event hub by providing a valid ( connection string , provide azure account-name for storage purpose , account-key) 

|  Field   |  Description|
|spring.cloud.azure.eventhubs.connection-string	Specify the connection string you obtained in your event hub namespace from the Azure portal||
|spring.cloud.azure.eventhubs.processor.checkpoint-store.container-name  |Specify the connection string you obtained in your event hub namespace from the Azure portal  |
|spring.cloud.azure.eventhubs.processor.checkpoint-store.account-key   |  Specify the container of your storage account.   |
|spring.cloud.azure.eventhubs.processor.checkpoint-store.account-name	    |     |
|spring.cloud.stream.bindings.consume-in-0.destination	    |     |
|spring.cloud.stream.bindings.consume-in-0.group |     |
|spring.cloud.stream.bindings.supply-out-0.destination	   |     |
|spring.cloud.stream.eventhubs.bindings.consume-in-0.consumer.checkpoint.mode |     |
|     |     |
|     |     |

3) After successful connection with the active directory you have to use the Get endpoint "/employees/initdb" to initialise the producer database and grap all the available employee data from the active directory
4) Last Step is to call Get endpoint "/employees/syncronize" periodically every 1 hour for example so that it can recognise all the changes in the active directory and sending them via azure event hub to all subscribed microservices