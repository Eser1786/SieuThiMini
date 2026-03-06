# SieuThiMini — Ghi chú dự án

## Hướng đi phát triển

### Mục tiêu tổng quan
Phần mềm quản lý siêu thị mini desktop (Java Swing), kết nối DB qua Docker, hỗ trợ:
- Quản lý sản phẩm, kho, khách hàng, nhân viên
- Quản lý đơn hàng (tạo, theo dõi, xuất PDF/Excel)
- Quản lý khuyến mãi (voucher + giảm giá sản phẩm)
- Xuất hóa đơn PDF, xuất danh sách Excel/CSV

### Tính năng cần làm tiếp
- [ ] Kết nối thực tế với DB cho DiscountDAO (bảng khuyến mãi)
- [ ] Chức năng tìm kiếm & phân trang cho tất cả bảng
- [ ] Phân quyền người dùng (Admin vs nhân viên)
- [ ] Thống kê doanh thu trên TrangChuPanel
- [ ] In hóa đơn từ popup đơn hàng
- [ ] Lịch sử giao dịch khách hàng

---

## Lỗi đã gặp — Không lặp lại!

### 1. Git tracking file .class trong `bin/`
- **Vấn đề**: `.gitignore` có `bin/` nhưng file `.class` vẫn hiển thị trong Changes.
- **Nguyên nhân**: File đã được track trước khi thêm vào `.gitignore`. Git tiếp tục theo dõi file đã tracked dù có trong `.gitignore`.
- **Cách sửa**: Chạy `git rm -r --cached bin/` để xóa khỏi index (file vẫn còn trên disk). Chỉ cần chạy 1 lần.
- **Phòng tránh**: Luôn thêm `bin/`, `out/`, `*.class` vào `.gitignore` **trước** khi commit lần đầu.

### 2. Vietnamese font/encoding hiển thị ký tự □
- **Vấn đề**: Label tiếng Việt hiển thị ký tự □ (vuông rỗng) thay vì chữ.
- **Nguyên nhân**: Font không hỗ trợ Unicode, hoặc file `.java` không được đọc đúng encoding UTF-8.
- **Cách sửa**:
  1. Compile với flag `-encoding UTF-8`.
  2. Dùng Unicode escape trong string literal (ví dụ: `"Th\u00f4ng tin"` thay vì gõ thẳng nếu file có vấn đề encoding).
  3. Dùng font `"Arial"` / `"Segoe UI"` thay vì `"Dialog"` hoặc font hệ thống.

### 3. BoxLayout alignment gây lệch component
- **Vấn đề**: Components trong BoxLayout (Y_AXIS) bị lệch sang phải hoặc trái.
- **Nguyên nhân**: Một component có `AlignmentX` khác với các component còn lại (ví dụ `LEFT_ALIGNMENT` trộn với `CENTER_ALIGNMENT`).
- **Cách sửa**: Tất cả components trong cùng một BoxLayout PHẢI cùng `AlignmentX`. Thêm `.setAlignmentX(Component.CENTER_ALIGNMENT)` cho tất cả gồm cả `JSeparator`, `JScrollPane`, `JLabel`.
- **Phòng tránh**: Khi thêm component mới vào BoxLayout, luôn set `AlignmentX` khớp với các component xung quanh.

### 4. CardLayout content không hiển thị sau khi chỉnh màu
- **Vấn đề**: Sau khi thay đổi màu sắc của một panel, toàn bộ content bên trong biến mất, chỉ còn màu nền phẳng.
- **Nguyên nhân**: Thay đổi layout/structure của panel thay vì chỉ thay đổi màu. Thường do refactor quá tay.
- **Cách sửa**: Chỉ thay đổi giá trị màu (`Color` constant), không đụng đến layout/hierarchy của components.
- **Phòng tránh**: Khi user chỉ yêu cầu "đổi màu", chỉ sửa dòng `new Color(...)` — không tái cấu trúc gì thêm.

### 5. JTable rows không hiển thị dữ liệu
- **Vấn đề**: Table header hiển thị đúng nhưng rows không thấy dữ liệu dù đã `addRow()`.
- **Nguyên nhân**: `setPreferredScrollableViewportSize` quá nhỏ, hoặc `JScrollPane` không có kích thước hợp lý.
- **Cách sửa**: Gọi `table.setPreferredScrollableViewportSize(new Dimension(width, height))` với chiều cao phù hợp.

### 6. Git push bị rejected (non-fast-forward)
- **Vấn đề**: `git push` báo lỗi "rejected: non-fast-forward" vì remote có commit mới hơn local.
- **Cách sửa**: `git pull --rebase` trước khi push.
- **Nếu có unstaged changes**: `git stash` → `git pull --rebase` → `git stash pop` → `git push`.
- **Nếu rebase conflict với binary files (`.class`)**: `git rm -r --cached bin/` → `git add -A` → `git rebase --continue`.

### 7. Popup dialog vs CardLayout
- **Vấn đề cũ**: Tạo đơn hàng dùng chung CardLayout, nhấn "Tạo đơn hàng" thay toàn bộ màn hình.
- **Giải pháp mới**: Dùng `JDialog` (APPLICATION_MODAL) để mở form tạo đơn hàng như popup riêng biệt.
- **Lưu ý**: `showCard(CARD_TABLE)` phải kiểm tra nếu có dialog đang mở thì `dispose()` thay vì switch card.

---

## Quy ước code

- **Package**: `GUI.TenModule` (ví dụ `GUI.DonHang`, `GUI.KhachHang`)
- **Màu chủ đạo**: Purple/Lavender theme — `#5C4A7F` (dark), `#D1C4E9` (light), `#F8F7FF` (bg)
- **Font**: `"Arial"` hoặc `"Segoe UI"` — không dùng font hệ thống mặc định
- **Compile**: `javac -encoding UTF-8 -cp "lib\*" -sourcepath src -d bin "@sources.txt"`
- **Run**: Debug Configuration đã setup trong `.vscode/launch.json`
- **Ảnh sản phẩm**: `img/products/` — ảnh icons app: `img/icons/`
- **Số tiền format**: `String.format("%,.0fđ", amount)` — dấu phẩy ngăn cách hàng nghìn

---

## Cấu trúc thư mục quan trọng

```
src/
├── BUS/          # Business logic layer
├── DAO/          # Data access layer (DB)
├── DTO/          # Data Transfer Objects + enums
└── GUI/
    ├── DonHang/  # Quản lý đơn hàng (Table + Detail + Invoice + Create cards)
    ├── KhachHang/
    ├── KhuyenMai/
    ├── SanPham/
    └── ...
img/
├── icons/        # App icons (imageplaceholder.svg, ...)
└── products/     # Ảnh sản phẩm
lib/              # .jar dependencies
sql-init/         # Docker DB init scripts
```
