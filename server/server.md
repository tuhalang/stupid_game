# I. Mô tả chung 

- Khi Server hoạt động sẽ có 2 Thread chính
    
    + Thread Main: chịu trách nhiệm đón nhận xử lý request đế n.
    + Thread System: chịu trách nhiệm xử lý phản hồi các request <strong>LOGIN</strong>, <strong>JOIN ROOM</strong> của user.

- Khi user connect đến server sẽ được sinh ra 1 Thread riêng để xử lý các message gửi lên.
- Khi game được start sẽ được sinh ra 1 Thread để có thể xử lý chung cho tất cả người chơi.

# II. Mô tả chi tiết các luồng hoạt động

## 1. Luồng xử lý khi start server

- Khi start server sẽ khởi tạo 2 Thread 
    
    + Thread Main
    + Thread System 

- Thread Main:
    
    + Khởi tạo đối tượng Game: chứa thông tin về game 
    + Khởi tạo ThreadPoolExecute: giới hạn user truy cập, tránh quá tải server
    + Xử lý nhận kết nối user trong lần đầu tiên

- Thread System:

    + Khởi tạo một đối tượng BlockingQueue: chứa các message về message command được mô tả ở phần 3 
    + Hàm xử lý chính sẽ có xử lý và phản hồi command trong hàng đợi.

## 2. Luồng xử lý khi user connect 

- Khi connect user sẽ gửi message đầu tiên là 1 chuỗi định danh cho user ở đây sử  dụng username.
- Server nhận được message sẽ khởi tạo một đối tượng player được định danh bởi message gửi lên.
- Tùy vào chế độ cấu hình của game là tự động gán room hoặc để user chọn phòng:
    + Nếu để chế độ tự động gán phòng: thực hiện gán player vào phòng còn trống, nếu không còn phòng sẽ trả về lỗi không còn phòng trống.
    + Nếu để chế độ user chọn phòng: trả về cho user danh sách các id phòng còn có thể join
- Start thread của player để có thể trao đổi message.

## 3. Luồng xử lý khi nhận được message command trong thread của user

- Hiện tại đang chia thành 3 loại command với 3 prefix riêng cho từng loại để có thể phân biệt:
    
    | PREFIX | COMMAND      |
    |--------|--------------|
    |1       | LOGIN        |
    |2       | REGISTER     |
    |3       | JOIN ROOM    |
    |4       | CONTROL GAME |

- Như vậy với từng loại command ta sẽ add message command vào từng BlockingQueue khác nhau để cho các Thread khác xử lý
    
    + Với prefix là 1 hoặc 2: add message command, socket vào systemCommandsQueue
    + Với prefix là 3: add message command, socket, currentTime vào commandsQueue

## 4. Luồng xử lý cho command LOGIN  

### 4.1 Client

- Các bước thực hiện:

    B1: Tạo connect socket như mô tả ở phần 2

    B2: Đọc thông tin username và password từ giao diện 

    B3: Kiểm tra định dạng của username và password tạo đối tượng User và truyền vào username, password

    B4: Gửi request với message có dạng 1{"username":"hungpv","password":"123456"}

- Cách chuyển từ Object sang dạng json 
    ```
    String json = Convertor.objectToJson(object);
    ```
- Cách gửi dữ liệu lên server 
    ```
    // Connect server 
    Communication com = Communication.getIntance(ip, port);

    // Send message 
    com.send(message);
    ```

### 4.2 Server

- Bản tin request để login sẽ có dạng: 1{"username":"hungpv","password":"123456"}
- Bản tin response có dạng:
    + Thành công: 1|room_id_1|room_id_2|...
    + Thất bại:   0

    B1: Cắt prefix command ra khỏi chuỗi để được {"username":"hungpv","password":"123456"}

    B2: Map chuỗi JSON thành đối tượng user 

    B3: Truy vấn database Redis với key là username -> so sánh password và trả về kết quả login

- Cách map json thành object (giả sử là class User)
    ```
    User user = Convertor.jsonToObject(json, User.class);
    ```

- Cách kết nối jedis để truy vấn user 
    ```
    JedisSentinelPool pool = JedisConnectionPool.getPoolConnection();
    Jedis jedis = null;
    try{
        jedis = pool.getResource();
        jedis.select(JedisConstant.JEDIS_DB_USER);
        
        // query info 

    }catch(Exception e){
        // handle exception
    }finally{
        if(jedis != null && jedis.isConnected()){
            jedis.close();
        }
    }
    ```

- Cách lấy danh sách room_id còn trống 
    ```
    Game game = Game.getIntance();
    String listRoom = game.getListRoomEmpty();
    ```

## 5. Luồng xử lý cho command REGISTER 

- Xử lý tương tự như command LOGIN 
- Chỉ khác khi đăng kí thành công thì trả về : 1
- Khi đăng kí thất bại thì trả về: 0
- Khi đăng kí phải kiểm tra username đã tồn tại hay chưa.

## 6. Luồng xử lý cho command JOIN ROOM 

comming soon ...

## 7. Luồng xử lý khi start game 

comming soon ...

## 8. Luồng xử lý cho command CONTROL GAME 

comming soon ...
