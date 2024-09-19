Tổng quan: Đây là phần backend cho ứng dụng quản lý thư viện Library, phần database đã được deploy trên Render, file cấu hình application.properties đã có sẵn ở đây.

Hướng dẫn cài đặt
Bước 1:
Java Development Kit (JDK) 21:  tải JDK từ trang chính thức của Oracle hoặc Adoptium.
Maven: tải Maven từ trang chính thức của Apache Maven.

=Sau khi cài đặt, kiểm tra phiên bản của JDK và Maven bằng các lệnh sau trong terminal (hoặc command prompt):

Kiểm tra phiên bản JDK:
java -version

Kiểm tra phiên bản Maven:

mvn -version

Nếu mọi thứ đã được cài đặt đúng cách, bạn sẽ thấy thông tin về phiên bản của JDK và Maven trong terminal, nếu không thấy thì hãy thử cài đặt lại.
(Hướng dẫn cài maven:https://maven.apache.org/install.html.

Hướng dẫn cài JDK:https://docs.oracle.com/en/java/javase/21/install/overview-jdk-installation.html)

Bước 2: Tải mã nguồn từ GitHub
Truy cập vào repo trên Github
Tìm nút "Code" (thường là một nút xanh) và nhấn vào đó.
Chọn "Download ZIP".
Giải nén file ZIP vào thư mục mà bạn muốn.

Bước 3: Điều hướng đến thư mục dự án
Mở terminal (hoặc command prompt) và chuyển vào thư mục mà bạn vừa giải nén (dùng lệnh cd hoặc open in terminal)

Bước 4: Cài đặt các phụ thuộc
Sử dụng Maven để cài đặt các phụ thuộc cần thiết cho dự án,
file application.properties đã có sẵn

Bước 5: Chạy ứng dụng
Sau khi hoàn tất cài đặt, bạn có thể chạy ứng dụng bằng lệnh:
mvn spring-boot:run

Bước 6: Kiểm tra ứng dụng
Mở trình duyệt và truy cập vào địa chỉ http://localhost:8080 (hoặc cổng mà bạn đã cấu hình) để kiểm tra xem ứng dụng có đang chạy không.
