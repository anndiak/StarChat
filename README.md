## What's new in this version

Initial Release

## StarChat


This Java-based chat project is designed to provide a real-time messaging experience using an IRIS Cloud SQL database. The application integrates with ChatGPT for enhanced chat functionalities and utilizes Liquibase for database migrations.

## Features

- Real-time chat functionality.
- Integration with ChatGPT for intelligent responses.
- IRIS Cloud SQL for data storage.
- Liquibase for managing database schema changes.
- Java Native API
- Java Gateway

## Prerequisites

Before running the project, make sure you have the following installed:

- Java Development Kit (JDK) 11
- Cloud SQL deployment
- Docker (Optional. If you do not have IRIS Cloud SQL deployment)
- Dependencies specified in the `pom.xml` file
- Maven
- ChatGPT API key

## Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/chat-project.git
```
### Step 2: Go to the cloned project

```bash
cd StarChat/
```
You must select the appropriate branch based on your storage choice. Opt for the 'master' branch if you're utilizing local storage and the 'cloud' branch for CloudSQL storage. While the functionality remains consistent across both branches, it's essential to note that Cloud SQL lacks support for Globals. Consequently, the Java Native API won't function in this scenario. We address this limitation on the 'cloud' branch by using a dedicated table instead of Globals.

Also, there is a difference in how the application is connected to the ChatGPT. On the 'cloud' branch, we are using ChatGPT`s client, on 'master' - Java Gateway Service together with Java Native API.

### Step 3: Setup the IRIS storage
You have 2 options to set up IRIS storage:

#### 1. IRIS Cloud SQL
Register on [IRIS Cloud SQL](https://portal.dap.isccloud.io/) and create the Cloud SQL deployment (it can be a trial period). After the account creation follow the [instructions](https://community.intersystems.com/post/connecting-cloud-sql-dbeaver-using-ssltls) to access the cloud storage.

#### 2. Local IRIS (with Java Gateway service)
Run Docker:
```bash
docker-compose up -d
```

### Step 4: Set Up Environment Variables

```bash
# Database Configuration
DB_HOST=k8s-c8bff1fb-a15...elb.us-east-1.amazonaws.com
DB_NAMESPACE=USER
DB_PASSWORD=password
DB_PORT=443
DB_USER=SQLAdmin
SECURITY_LEVEL=10 #Only for Cloud SQL

#SSL
SSL_CONFIG_FILE_PATH=D:\\StarChat\\certs\\SSLConfig.properties #Only for Cloud SQL

#File Upload Location
RESOURCE_LOCATION=/D:/StarChat/uploads/
UPLOAD_DIR=D:/StarChat/uploads
```
### Step 5: Apply migration to DB

#### For Cloud SQL:

Create tables manually using .sql files from src/main/resources/liquibase/ .

#### For local IRIS:

Run Liquibase plugin:

```bash
mvn liquibase:update
```

### Step 6: Access the Application
Go to the address http://localhost:3000/

## How to use

[![Watch the video](https://www.youtube.com/watch?v=vWz1xE0YWfM/0.jpg)](https://www.youtube.com/watch?v=vWz1xE0YWfM&ab_channel=AnnaDiak)

### Installing with IPM (For using ChatGPT module)

Run Java Gateway (Note: do not forget to specify 'CHAT_GPT_API_KEY' in .yml):

```bash
docker-compose up -d
```
Install the package:

```bash
zpm "install starchat"
```
How to use:
```
write ##class(dc.starchat.ChatGPTMain).GetAnswer("java-gateway", 55555, "ChatGPT", "How do you do")
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first

## License

[MIT](https://choosealicense.com/licenses/mit/)