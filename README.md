## What's new in this version

Initial Release

## StarChat


This Java-based chat project is designed to provide a real-time messaging experience using an IRIS database. The application integrates with ChatGPT for enhanced chat functionalities and utilizes Liquibase for database migrations.

## Features

- Real-time chat functionality.
- Integration with ChatGPT for intelligent responses.
- IRIS database for data storage.
- Liquibase for managing database schema changes.

## Prerequisites

Before running the project, make sure you have the following installed:

- Java Development Kit (JDK) 11
- Docker (For running IRIS)
- Dependencies specified in the `pom.xml` file
- ChatGPT API key

## Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/chat-project.git
```
### Step 2: Go to the project

```bash
cd StarChat/
```
### Step 3: Set Up Environment Variables

```bash
# Database Configuration
DATABASE_CONNECTION_URI=jdbc:IRIS://127.0.0.1:1972/USER
DATABASE_PASSWORD=1423
DATABASE_USER=_SYSTEM

#File Upload Location
RESOURCE_LOCATION=/D:/StarChat/uploads/
UPLOAD_DIR=D:/StarChat/uploads

# ChatGPT API Key
CHATGPT_API_KEY=your_chatgpt_api_key
CHAT_GPT_URI=https://api.openai.com/v1/chat/completions
```
### Step 4: Run Docker Compose for IRIS

```bash
docker-compose up -d
```
### Step 4: Access the Application
Go to the address http://localhost:3000/

## How to use

[![Watch the video](https://www.youtube.com/watch?v=vWz1xE0YWfM/0.jpg)](https://www.youtube.com/watch?v=vWz1xE0YWfM&ab_channel=AnnaDiak)

## Contributing

Pull requests are welcome. For major changes, please open an issue first

## License

[MIT](https://choosealicense.com/licenses/mit/)