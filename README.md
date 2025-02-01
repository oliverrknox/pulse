# Pulse

<details>
    <summary>
        <strong>Table of contents</strong>
    </summary>
    <ul>
        <li>
            <a href="#description">Description</a>
            <ul>
                <li><a href="#built-with">Built with</a></li>
                <li><a href="#integrated-with">Integrated with</a></li>
            </ul>
        </li>
        <li>
            <a href="#getting-started">Getting started</a>
            <ul>
                <li><a href="#installation">Installation</a></li>
                <li><a href="#set-up">Set up</a></li>
            </ul>
        </li>
        <li><a href="#usage">Usage</a></li>
    </ul>
</details>

## Description

Pulse is a REST API designed to manage the subscription of clients to Firebase Cloud Messaging (FCM), enabling push notifications across different platforms. 
The API provides a robust and scalable solution for integrating push notification services into your applications.
The repository includes demo client applications for both mobile and web platforms to showcase the integration and functionality of Pulse.

### Built with

- [Spring WebFlux](https://spring.io)

### Integrated with

- [Firebase](https://firebase.google.com)

## Getting started

### Installation

To install the server

1. Navigate to the root of this project.
2. Run the command:
    ```bash
    ./mvnw clean install
    ```
3. Generate a new private key and download the service account JSON file from Firebase.

To install the android client

1. Navigate to the `/android` directory.
2. Run the command:
   ```bash
   ./gradlew clean build
   ```
3. Download `google-services.json` from Firebase and place in `/android/app` directory.

To install the web client

1. Navigate to the `/web` directory.
2. Run the command:
    ```bash
   npm install
   ```

### Set up

To set up the server locally

- Set the following environment variables:

  | Variable                       | Example                  | Info                                                  |
  |--------------------------------|--------------------------|-------------------------------------------------------|
  | GOOGLE_APPLICATION_CREDENTIALS | ~/serviceAccountKey.json | The fully qualified path to your service account file |

To set up the web client

- Set the following environment variables:

  | Variable                     | Example               | Info                                          |
  |------------------------------|-----------------------|-----------------------------------------------|
  | FIREBASE_API_KEY             | ABC123                | The client's API key from Firebase            |
  | FIREBASE_AUTH_DOMAIN         | pulse.firebaseapp.com | The client's auth domain from Firebase        |
  | FIREBASE_PROJECT_ID          | pulse                 | The client's project ID from Firebase         |
  | FIREBASE_STORAGE_BUCKET      | pulse.appspot.com     | The client's storage bucket from the Firebase |
  | FIREBASE_MESSAGING_SENDER_ID | 123456                | The client's FCM sender ID from the Firebase  |
  | FIREBASE_APP_ID              | 1:abc123:web:abc123   | The client's app ID from the Firebase         |

> The android client automatically gets credentials from `google-services.json` via the Firebase SDK. No further set up required.

## Usage

To run the server

- Run the command:
    ```bash
    ./mvnw clean package
    java -jar target/pulse-{version}.jar
    ```
  
To run the android client

> It is recommended to use Android Studio and an emulator / real device to run the client.

- Open Android Studio at the root or `/android` directory.
- Start `MainActivity.java`.

> If running in IntelliJ then ensure that Java 17 is used for Gradle.
  
To run the web client

- Run the command:
    ```bash
    npm start
    ```
    > To receive notifications you may need to trust the servers self-signed certificate in your browser.
> 
> Both demo clients will require permission for notifications to be granted on their respective platform.
> After that you can send requests to the server with a custom payload and see the notification on the clients.

