## This is a stupid game we do to pass Network Programming Subject :((

## Mô tả API 

1. Login

- Khi đăng nhập sẽ sử dụng username và password.
- Nếu đăng nhập thành công sẽ trả về một chuỗi các roomId có thể join được ngăn cách bởi dấu phẩy.

- request:
    ```
    1{"username":"","password":""}
    ```
- response:
    ```
    0{"roomId":[1111,123]}
    ```

2. Join room 

- request:
    ```
    2{"romeId":""}
    ```
- response:

    + thành công: 0
    + thất bại: 1

3. Control game

- request:
    ```
    3{"username":"","direction":1,"shot":true,"plane":{"lon":12,"lan":12,bullets:[{"lon":12,"lan":12}, ...]}}
    ```

- response:
    ```
    0{"bot":[{"lon":12,"lan":12}, ...]},"planes":[{"active":true,"lon":12,"lan":12,bullets:[{"lon":12,"lan":12}, ...]}},...]
    ```