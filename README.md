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
        100player1|player2|...
        ```

    + Failed:
        ```
        101
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
- Note:
    
    + Hero:
        ```
        x,y
        ```
    + Bullet:
        ```
        x1,y1;x2,y2;...
        ```
    + Flyings:
        ```
        x1,y1;x2,y2,...
        ```
    + Player:
        ```
        hero|score|life|bullets
        ```
