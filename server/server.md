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
    |2       | JOIN ROOM    |
    |3       | CONTROL GAME |

- Như vậy với từng loại command ta sẽ add message command vào từng BlockingQueue khác nhau để cho các Thread khác xử lý
    
    + Với prefix là 1 hoặc 2: add message command, socket vào systemCommandsQueue
    + Với prefix là 3: add message command, socket, currentTime vào commandsQueue

## 4. Luồng xử lý cho command LOGIN  

comming soon ...

## 5. Luồng xử lý cho command JOIN ROOM 

comming soon ...

## 6. Luồng xử lý khi start game 

comming soon ...

## 7. Luồng xử lý cho command CONTROL GAME 

comming soon ...
