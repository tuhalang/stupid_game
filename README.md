## This is a stupid game we do to pass Network Programming Subject :((

## Description API 

### I. Protocol

| Type         | Prefix      |
|--------------|-------------|
| Login        |      00     |
| Register     |      01     |
| Start Game   |      10     |
| Control Game |      11     |

#### 1. Login

- Request:
    ```
    00username|password
    ```
- Response:
    
    + Success:

        ```
        000LOGIN SUCCESSFULLY !
        ```

    + Failed:

        ```
        001LOGIN FAILED !
        ```
#### 2. Register
- Request:
    ```
    01username|password
    ```
- Response:

    + Success:
        ```
        010REGISTER SUCCESSFULLY ! PLEASE LOGIN !
        ```
    + Failed:
        ```
        011REGISTER FAILED !
        ```
#### 3. Start Game
- Request:
    ```
    10
    ```
- Response:

    + Success:
        ```
        // TODO 
        ```

    + Failed:
        ```
        // TODO 
        ```


#### 4. Control Game
- Request:
    ```
    11hero|bullets
    ```
- Response:

    + Success:
        ```
        110flyings|player1|player2|...
        ```