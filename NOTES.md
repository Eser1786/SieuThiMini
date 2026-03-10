# SieuThiMini — Ghi chú dự án

## Hướng đi phát triển

### Mục tiêu tổng quan
Phần mềm quản lý siêu thị mini desktop (Java Swing), kết nối DB qua Docker, hỗ trợ:
- Quản lý sản phẩm, kho, khách hàng, nhân viên
- Quản lý đơn hàng (tạo, theo dõi, xuất PDF/Excel)
- Quản lý khuyến mãi (voucher + giảm giá sản phẩm)
- Xuất hóa đơn PDF, xuất danh sách Excel/CSV

### Tính năng cần làm tiếp
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

### 8. CRLF line endings block multi-line replace_string_in_file

- **Vấn đề**: `replace_string_in_file` dùng `\n` để match — fail hoàn toàn trên file có `\r\n` (Windows CRLF) khi oldString có nhiều dòng.
- **Cách phát hiện**: `(Get-Content file -Raw) -match "\r\n"` → True thì file là CRLF.
- **Giải pháp**: Viết PowerShell `.ps1` đọc file bằng `[System.IO.File]::ReadAllText`, normalize `\r\n` → `\n`, tìm match, thay thế bằng `.Replace()`, rồi khôi phục CRLF và ghi lại. Chạy qua `powershell -ExecutionPolicy Bypass -File patch.ps1`.
- **Phòng tránh**: Các file `.java` mới nên được tạo với LF (hoặc chấp nhận dùng script khi cần patch multi-line).

### 10. Pattern xác nhận thoát / hủy thao tác

- **Thoát app (X button)**: Trong `GUI.java`, dùng `setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)` + `addWindowListener(new java.awt.event.WindowAdapter() { windowClosing → JOptionPane.showConfirmDialog → System.exit(0) })`. KHÔNG dùng `EXIT_ON_CLOSE` trực tiếp.
- **Hủy trong dialog có form trống**: Chỉ `dlg.dispose()` — không hỏi.
- **Hủy trong dialog có dữ liệu (add form)**: Duyệt `tfs[]` array: `for (JTextField f : tfs) if (!f.getText().trim().isEmpty()) dirty = true;`. Nếu dirty → `JOptionPane.showConfirmDialog(dlg, "Bạn có chắc muốn hủy? Thông tin đã nhập sẽ mất.", ...)`. Nếu có `JPasswordField` thêm: `|| new String(pfPass.getPassword()).trim().length() > 0`.
- **Hủy trong edit dialog (pre-filled)**: Luôn confirm — `JOptionPane.showConfirmDialog(popup, "Bạn có chắc muốn hủy? Thay đổi chưa lưu sẽ bị mất.", ...)` → `if YES: popup.dispose()`.
- **Không thêm xác nhận cho**: "Đóng" view-only popup (KhachHang detail), standalone `main()` test frames (KhoPanel.main), "Đã hủy" trạng thái đơn hàng (đó là biz ops, không phải dialog cancel).

### 9. Pattern tái sử dụng — NhanVienPanel (và các panel tương tự)

- **Cột mật khẩu**: Dùng `PasswordCellRenderer` kế thừa `DefaultTableCellRenderer`; field `Set<Integer> revealedRows` lưu row nào đang hiện. Click vào 28px cuối ô password để toggle reveal/hide. Thêm `HierarchyListener` vào constructor để auto-clear `revealedRows` khi tab bị ẩn.
- **Header bảng chuẩn** (theo KhachHang): `Font("Arial", BOLD, 16)`, `setPreferredSize(new Dimension(0, 52))`, `setBackground(new Color(0xAF9FCB))`, `setForeground(WHITE)`.
- **Tìm kiếm an toàn**: Dùng `Pattern.quote(kw)` trước khi  truyền vào `RowFilter.regexFilter(...)` để input của user không bị interpret là regex.
- **Dialog thêm mới**: `JPasswordField` với nút toggle show/hide (`echoChar = 0` ↔ `•`). Collect tất cả lỗi vào `List<String> errs` trước rồi hiển thị cùng một lần (all-at-once validation), không return sớm từng lỗi. Kiểm tra trùng mã trước khi thêm.

### 7. Popup dialog vs CardLayout

- **Vấn đề cũ**: Tạo đơn hàng dùng chung CardLayout, nhấn "Tạo đơn hàng" thay toàn bộ màn hình.
- **Giải pháp mới**: Dùng `JDialog` (APPLICATION_MODAL) để mở form tạo đơn hàng như popup riêng biệt.
- **Lưu ý**: `showCard(CARD_TABLE)` phải kiểm tra nếu có dialog đang mở thì `dispose()` thay vì switch card.

---

## Tính năng đã hoàn thành

### Soft-delete (is_deleted)
- Bảng: `customers`, `products`, `employees`, `discounts`
- DAO: `getAllXxx()` filter `WHERE is_deleted = 0`; `deleteXxx()` set `is_deleted = 1` (+ `status = 'INACTIVE'` cho discounts)
- GUI: Nút xóa hiện confirm 2 bước (1 lần `JOptionPane.showConfirmDialog`, 1 lần `JOptionPane.showInputDialog` yêu cầu nhập "XOA")
- NhanVienPanel crash fix: mảng `cols[]` thiếu entry `"employee_id"` gây `ArrayIndexOutOfBoundsException` (7 >= 7)

### DonHangTableCard — Bộ lọc kết hợp
- Nút **Reset** (thay cho "✕") reset toàn bộ: clear date pickers, `cbLoc.setSelectedIndex(0)`, `tfTim.setText("")`, reload DB
- Nút **Lọc ngày** sau khi tải dữ liệu theo khoảng ngày còn chạy thêm `applyFilter` để áp trạng thái + tìm kiếm lên trên kết quả đó
- `cbLoc` và `tfTim` filter qua `TableRowSorter` (`applyFilter` Runnable) — không reload DB
- Đã xóa listener `cbLoc.addActionListener(e -> filterByStatus(...))` dư thừa (filterByStatus reload DB toàn bộ, không cần thiết)

### DonHangCreateCard — Combobox mã khuyến mãi
- Thay `JTextField + JButton` bằng `JComboBox<String>` load từ `DiscountBUS.getAllDiscounts()` (chỉ lấy status=ACTIVE)
- Hiển thị thêm `lbDiscStatus` (JLabel italic) mô tả discount: "Giảm 10% → -50,000đ" hoặc "Giảm cố định -X" hoặc "⚠ Đơn tối thiểu: Xđ"
- Hỗ trợ cả `PERCENT` và `FIXED` discount type; kiểm tra `minOrderAmount`
- Khi nhấn Hủy/reset: `cbMaKM.setSelectedIndex(0)` tự động clear label và reset discAmt về 0

---

## Quy ước code

- **Package**: `GUI.TenModule` (ví dụ `GUI.DonHang`, `GUI.KhachHang`)
- **Màu chủ đạo**: Purple/Lavender theme — `#5C4A7F` (dark), `#D1C4E9` (light), `#F8F7FF` (bg)
- **Font**: `"Arial"` hoặc `"Segoe UI"` — không dùng font hệ thống mặc định
- **Compile**: `javac -encoding UTF-8 -cp "lib\*" -sourcepath src -d bin "@sources.txt"`
- **Run**: Debug Configuration đã setup trong `.vscode/launch.json`
- **Ảnh sản phẩm**: `img/products/` — ảnh icons app: `img/icons/`
- **Logo app**: `img/icons/Logo.png` — được load vào taskbar icon (`GUI.java`) và header/login (`MainPanel.java`, `LoginDialog.java`)
- **Số tiền format**: `String.format("%,.0fđ", amount)` — dấu phẩy ngăn cách hàng nghìn

---

## Session 2026-03-10 — UI Overhaul

### Thay đổi

- **GUI.java**: Sửa title "Siu Thị 36" → "Siêu Thị 36"; thêm taskbar icon load từ `img/icons/Logo.png`
- **LoginDialog.java / MainPanel.java**: Fix logo path (chính xác tại `img/icons/Logo.png`)
- **SanPhamPanel.java**: Thay `JTabbedPane` bằng custom tab bar (3 JButton styled, dark header `0x2F2C35`, active `0x5C4A7F`); styled `cbLoc` với `UIUtils.styleComboBox()`; `ActionButtonRenderer` wrap button trong `JPanel` với `FlowLayout` để không chiếm hết cell
- **CategoryPanel.java**: Fix bug `editCategory()` đọc sai cột — đọc `col 0` (STT) thay vì `col 1` (ID) gây `ClassCastException` khi nhấn Sửa
- **SupplierPanel.java**: Fix layout (2 `add(NORTH)` → `BoxLayout northPanel`); `showSupplierDialog()` restyle đầy đủ (header tím, `UIUtils.makeField()`, footer nút styled); `ActionRenderer`/`ActionEditor` dùng `UIUtils.makeActionButton()`; table header tím `0xAF9FCB`; alternating rows; styled combobox
- **Emoji fix**: Tất cả `🔄`/`🔍`/`\uD83D\uDD0D` → text thường trên 10 file (TrangChuPanel, CategoryPanel, SupplierPanel, SanPhamPanel, NhanVienPanel, NhapXuatTableCard, DonHangTableCard, KhachHangTableCard, NhapKhoTableCard, KhuyenMaiPanel)

---

## Cấu trúc thư mục quan trọng

```text
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
