## This is a stupid game we do to pass Network Programming Subject :((

## Description API 

### I. Requirement
1. JDK8
2. Mongo8
3. NetBeans

### II. Protocol

#### 1. Response symbol
| Type         | Prefix      |
|--------------|-------------|
| Login        |      00     |
| Register     |      01     |
| Start Game   |      03     |
| Join room    |      02     |
| Finish Game  |      04     |

##### 1.1. Login

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
##### 1.2. Register
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
##### 1.4. Join Room
- Request:
    ```
    02playRoomID
    ```
- Response:

    + Success:
        ```
        020
        ```
    + Failed:
        ```
        021
        ```
##### 1.3. Start Game
- Request:
    ```
    03roomID
    ```
- Response:

    + Success:
        ```
        030player1|player2|...
        ```

    + Failed:
        ```
        031
        ```
##### 1.4. Finish Game
- Response:
    + Success:
        ```
        04username1|score1|isalive1|username2|score2|isalive2|...
        ```
##### 1.5. Control Game
- Request:
    ```
    1hero|bullets
    ```
- Response:

    + Success:
        ```
        flyings|hero1|score1|life1|bullets1|hero2||score2|life2|bullets2|...
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
