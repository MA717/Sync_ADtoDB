## Employee Synchronize Service

A microService which notices any changes in the Employee Active directory . if it founds change occurred in the active
directory
it will fire an event that contain the change that has happened to all the other microservices which subscribed on the
same event hub

### Introduction

Microservice which responsible for  syncronizing between the Employees data in the Active Directory and the other microservices databases in easy way. it allows the other
microservice
to access all the updated list of Employees in the company without needing to deal with ldap queries.Moreover, it sends
events which contain the
changes that occurred in every employee tuple to all the other subscribed microservices automatically.It can also
initialise the other
microservices databases by sending them a snapshot with the actual employee active directory.

### Features
    1. Fetch all the Employees from the Employee Active Directory and save them in internal Postgres doker container
    2. Initialise any new subscriber microservices by sending all the updated list of Employee to them 
    3. Notice all the changes that occur in the Employee Active Directory
    5. Fire Events with all the changes to the other microservice (Attributes changes , Employee added,Employee deleted ) via Azure Event Hub 


### How to Use the Project

1) First you have to establish a connection with the desired Employee Active Directory by providing the following enviroment variables
####
| Parameter |  Description   |
| :-------- | -------------------------------- |
| ${MT_ELS_LDAP_URLS}|  Specify the local Server of the active directory IP  Address         |
| ${MT_ELS_LDAP_BIND_DN}| Specify your server account username   |
| ${MT_ELS_LDAP_BIND_PASSWORD}	 | Specify your server account password      |

2) Establish a connection with the Postgres Docker Container
####
| Parameter |  Description   |
| :-------- | -------------------------------- |
| ${URL}|  Set it to the jdbc connection string         |
| ${USER_NAME}| Set it with your Database Name    |
| ${PASSWORD}|  Set it with your Database password  |


3) Establish a connection with azure event hub by providing the following eviroment variables.
####
| Parameter            | Description                                                                                   |
|:---------------------|-----------------------------------------------------------------------------------------------|
| ${CONNECTION_STRING} | Specify the connection string you obtained in your event hub namespace from the Azure portal. |
| ${ACCOUNT_KEY}	      | Specify the access-key of your storage account.                                               |
| ${CONTAINER_NAME}	   | Specify the container of your storage account.                                                |
| ${DESTINATION}	      | Specify the event hub you use                                                                 |

4) After successful connections you can use the end points provided below to control the microservice




#### Intialise the Syncronizer Microservice and fetch all data from Active directory and save it in the database. This route is only needed to be called once
```http
  GET /employees/initdb 
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| none      |     -      | No Parameter Required           |

#### Syncronizing the Data Base with the Active Directory . This Route has to be called periodically

```http
  GET /employees/syncronize
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| none      |     -      | No Parameter Required           |

#### Retrieving all the employees in Active Directory and Sending them to the Consumer Microservice via HTTP Response
```http
  GET /employees/consumerInitializer
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| none      |     -      | No Parameter Required           |




## Authors

- [@MohamedAyman](https://github.com/MA717)




