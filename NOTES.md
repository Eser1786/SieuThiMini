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

### 11. UTF-8 BOM từ PowerShell WriteAllText gây javac lỗi

- **Vấn đề**: `[System.IO.File]::WriteAllText(path, content)` (không truyền Encoding) ghi file với BOM (`\uFEFF`). `javac` reject file với lỗi `illegal character: '\ufeff'`.
- **Cách phát hiện**: `[System.IO.File]::ReadAllBytes(file)[0..2]` → `0xEF 0xBB 0xBF` là có BOM.
- **Giải pháp**: Luôn dùng `New-Object System.Text.UTF8Encoding $false` (false = no BOM) khi WriteAllText cho file `.java`:
  ```powershell
  $utf8NoBOM = New-Object System.Text.UTF8Encoding $false
  [System.IO.File]::WriteAllText($file, $content, $utf8NoBOM)
  ```
- **Xử lý khi đã có BOM**: Đọc lại → `if ($content.StartsWith([char]0xFEFF)) { $content = $content.Substring(1) }` → ghi lại với encoding no-BOM.

### 12. NhanVienPanel photo chỉ load từ DB — không có fallback filesystem

- **Vấn đề**: `fillDetail()` chỉ check `photoPathMap.get(ma)` (từ DB `photo_path`). Nếu DB `NULL`, không hiển thị ảnh dù file `img/employees/NV001.jpg` tồn tại trên disk.
- **UserPanel hoạt động** vì có fallback: `loadEmployeePhoto(code)` scan `img/employees/<maNV>.<ext>`.
- **Cách sửa**: Sau khi check `photoPathMap` fail, thêm fallback scan filesystem (giống UserPanel).

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
- **Logo app**: 3 file logo:
  - `img/icons/logo transparent.png` — dùng cho header app (`MainPanel.java`) và login dialog (`LoginDialog.java`) — nền transparent, kế bên tên shop
  - `img/icons/logo (white background).jpg` — dùng cho taskbar icon (`GUI.java`)
  - `img/icons/Logo.png` — fallback nếu 2 file trên không tìm thấy
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

## Session 2026-03-10 (phần 2) — Bug fixes & UI polish

### Bug fixes & thay đổi

- **DonHangDetailCard.java**: Fix VAT hiển thị 0đ — khi `inv.getTaxAmount()` null thì tính theo `finalTot * 10 / 110` thay vì bỏ trống
- **TrangChuPanel.java**: Fix 3 nút "Lam moi" → "Làm mới" (dấu tiếng Việt đầy đủ)
- **SanPhamPanel.java**: Fix tab bar hover — khi hover qua tab inactive, text đổi sang màu tối `0x2E2640` thay vì để màu nhạt khó thấy; restore khi mouseExited
- **CategoryPanel.java**: Fix dialog fields — thay `UIUtils.makeField()` (font 20, height 32 → text bị cắt) bằng fields font 13, height 36 như KhuyenMaiPanel; thêm nút Xuất PDF/Excel, Nhập CSV
- **SupplierPanel.java**: Fix dialog fields — tương tự CategoryPanel; thêm nút Xuất PDF/Excel, Nhập CSV

### Pattern quan trọng

- **Dialog field sizing**: Dùng `new JTextField()` + `font 13` + `border compound(lineBorder 0xBBBBBB, empty 4,8,4,8)` + `Dimension(260, 36)` — KHÔNG dùng `UIUtils.makeField()` trong dialog vì font 20 cần height >= 44px
- **UIUtils.makeField()**: Chỉ dùng khi muốn field lớn (font 20) với height tối thiểu 44px
- **Tab hover contrast**: Khi hover tab button trên nền tối, phải set cả background VÀ foreground (tối/sáng tùy bg hover)

---

## Session 2026-03-10 (phần 3) — UI polish tiếp theo

### Thay đổi

- **GUI.java / LoginDialog.java / MainPanel.java**: Logo split — transparent cho header/login, white background jpg cho taskbar icon
- **NhanVienEmployeeDialog.java**: Sửa field size — thay `UIUtils.makeField()` (font 20, h32) bằng font 13, border compound, h36; password/spinner/button đều h36; `cbRole` h36
- **NhanVienEmployeeDialog.java (session 3b)**: Prefill username/salary/password khi sửa (fetch từ DB qua `EmployeeBUS.getEmployeeById`); fix validation password trong edit mode (bỏ yêu cầu chữ+số, chỉ cần >= 6 ký tự); thêm DB persistence: cả add và edit giờ gọi `empBUS.addEmployee()` / `empBUS.updateEmployee()` — trước đây chỉ update tableModel
- **EmployeeDAO.java + EmployeeBUS.java**: Thêm `updateEmployee(EmployeeDTO)` method
- **KhachHangTableCard.java**: Fix NPE trong `showDetailDialog()` — thay `.toString()` bằng `Objects.toString(..., "")` null-safe cho tất cả 11 cột (email KH002 trước đây null trong DB cũ → crash Chi tiết button)
- **04_seed_data_clean.sql**: Thêm `UPDATE customers SET email = 'binhbong@gmail.com' WHERE customer_code = 'KH002' AND (email IS NULL OR email = '')` — patch cho DB đã tồn tại không có email KH002
- **KhoTableCard.java**: Style `btnrefresh` giống NhapKho (bg 0xD9D9D9, hover 0xC5B3E6, Arial Bold 13, border empty 9,14)
- **TrangChuPanel.java**: Style 3 nút "Làm mới" (btnRefresh1/2/3) giống NhapKho; thêm `UIUtils.styleComboBox()` cho `cbChartPeriod`, `statusFilter`, `paymentFilter`
- **KhuyenMaiPanel.java**: Restyle `btnReset` giống DonHang (bg 0xD9D9D9, hover 0xEF9A9A, Arial Bold 13, border empty 9,10, foreground black)

### Employee login (DB)

- `employees.password_hash` hiện lưu plaintext ("123456") — `EmployeeDAO.login()` so sánh direct (không hash)
- Seed data: 7 nhân viên; admin: `user_name=admin`, `password_hash=123456`, `role_id=1`
- Thay đổi mk/tên đăng nhập trong app: khi sửa nhân viên trong NhanVienPanel, giá trị mới sẽ được lưu vào DB và hiệu lực ngay lần đăng nhập tiếp theo

---

## Session 2026-03-10 (phần 3) — Employee photo DB + SanPham dialogs + Conflict resolve

### Thay đổi (phần 3)

- **sql-init/07_add_employee_photo.sql**: `ALTER TABLE employees ADD COLUMN photo_path VARCHAR(255) DEFAULT NULL`
- **EmployeeDTO.java**: Thêm field `photoPath` + getter/setter
- **EmployeeDAO.java**: Tất cả SELECT đọc `photo_path` → `emp.setPhotoPath(...)`; INSERT/UPDATE ghi `photo_path` (tham số 10/9)
- **NhanVienEmployeeDialog.java**:
  - `JSpinner` → `JDateChooser` (`com.toedter.calendar`) cho field ngày sinh
  - Khi chọn ảnh mới: xóa file cũ (`new File(originalPhotoPath).delete()`), lưu `finalPhotoPath`
  - Khi lưu: `emp.setPhotoPath(finalPhotoPath)` trước khi gọi `empBUS.addEmployee()`/`updateEmployee()`
- **NhanVienPanel.java**: `loadEmployees()` gọi `photoPathMap.put(e.getCode(), e.getPhotoPath())` từ DB thay vì scan file-system
- **UserPanel.java**: Đọc ảnh từ `user.getPhotoPath()` (DB) trước, fallback sang `loadEmployeePhoto(code)` (scan thư mục); `hireDate` format `dd/MM/yyyy`; card max-width 640px
- **SanPhamAddDialog.java**: Thêm `UIUtils.styleComboBox(cbNCC/cbDM/cbTT)` sau khi sync với remote (remote đã xóa `fKM`, thêm `ProductBUS.addProduct()` save)
- **SanPhamDetailDialog.java**:
  - Rewrite `showEdit()` — photo section (load/pick/preview 80×80) + 2-col `GridBagLayout` matching AddDialog + `JDateChooser` cho ngaySX/ngayHH + `UIUtils.styleComboBox(cbNCC/cbDM/cbTT)`
  - Xóa `fKM` (field declaration, rows array, model.setValueAt save) để đồng bộ với remote
  - Footer: nút Hủy `0x9B8EA8` + Lưu `0x5C4A7F`
- **Merge conflict**: remote pull `ea16fd4` gây conflict SanPhamAddDialog + SanPhamDetailDialog; giải quyết bằng cách giữ code remote làm base, adapt các thay đổi UI của mình lên trên

### Pattern — ảnh nhân viên

- DB lưu path tuyệt đối (hoặc tương đối từ thư mục project) vào `employees.photo_path`
- Khi upload ảnh mới: xóa file cũ ngay (tránh orphan files), copy file mới vào `img/employees/`
- Load ảnh vòng: `DB path → fallback scan img/employees/MSNV.*`
- Photo preview dùng `ImageIO.read()` → scale `SCALE_SMOOTH` → `ImageIcon`

---

## Session 2026-03-11 — Role-based access, KH auto-code, VAT, UI fixes

### Thay đổi

- **CustomerBUS.java**: `generateCustomerCode()` đổi từ `private` → `public` để GUI gọi được

- **KhachHangPanel.java**:
  - `triggerAddCustomer()`: tự điền `tfMaKH` bằng `new CustomerBUS().generateCustomerCode()`, set read-only (background `0xE8E6F0`)
  - `enableFormFields()`: loại `tfMaKH` ra — luôn không editable dù ở add hay edit mode
  - `saveNewCustomer()`: thay `tableModel.addRow()` bằng `new CustomerBUS().AddCustomer(dto)` → lưu vào DB thật; thành công thì `loadCustomers()` để reload từ DB

- **DonHangCreateCard.java**:
  - **btnTaoKH**: từ `setVisible(false)` → `setVisible(true)`; action listener mở `JDialog` mini ngay trong cửa sổ hiện tại (nhập Họ tên/SĐT/Địa chỉ → auto-generate mã → `CustomerBUS.AddCustomer()` → reload `cbKhachHang` → tự chọn khách mới tạo)
  - **cbNhanVien**: Admin thấy tất cả nhân viên; non-Admin thấy danh sách nhân viên có role `CASHIER`, auto-chọn current user nếu họ là cashier
  - **VAT**: thêm `lbVatVal` label hiển thị "VAT (10%, đã bao gồm)"; `updateTotals()` tính `vat = tot * 10L / 110`; khi lưu invoice, `setTaxAmount(BigDecimal.valueOf(tongCong * 10L / 110))` thay vì `BigDecimal.ZERO`

- **NhapKhoTableCard.java**: `btnNew` (Tạo phiếu nhập) chỉ `setVisible(true)` nếu role là `ADMIN` hoặc `WAREHOUSE`; `false` cho tất cả role khác

- **MainPanel.java**: Khuyến mãi tab từ `if (isAdmin)` → `if (isAdmin || isCashier)` — cashier giờ thấy tab Khuyến mãi

- **SanPhamDetailDialog.java**:
  - Fix `colIdx[]` trong `showDetail()`: `{0,2,9,10,11,4,3,12,5,14,15,6,16,17,18}` (đúng thứ tự: Giá vốn→col4, Giá bán→col3, Số lượng→col12, Tồn kho tối thiểu→col5)
  - Fix `val()` extractions trong `showEdit()` dùng cùng index đúng

- **Nút In PDF / In Excel** (toàn bộ app):
  - Thay "Xuất PDF" → **"In PDF"**, "Xuất Excel" → **"In Excel"** ở tất cả các panel: NhapKhoTableCard, KhachHangTableCard, NhanVienPanel, DonHangTableCard, KhuyenMaiPanel, SupplierPanel, CategoryPanel, SanPhamPanel, KhoTableCard

### Pattern — role-based visibility

- Lấy role: `UserSession.getCurrentUser()` → `new EmployeeBUS().getRole((long) user.getId())` → `role.getName()`
- Role names: `"ADMIN"`, `"MANAGER"`, `"CASHIER"`, `"WAREHOUSE"`, `"SUPPORT"`
- Dùng `setVisible(bool)` cho button, hoặc `if (isAdmin || isCashier)` block cho toàn bộ panel/tab

### Pattern — tạo khách hàng mới inline (trong dialog phụ)

- Vì `DonHangCreateCard` chạy trong `JDialog` (APPLICATION_MODAL), không thể navigate sang `MainPanel.navigateToKhachHangAndAdd()` (disruptive)
- Giải pháp: `JDialog` nhỏ modal on top, chỉ 3 field, auto-generate code, lưu DB, reload combobox parent

---

## Session 2026-03-11 (phần 4) — Hạng tự động, tích điểm, reload KH/SP

### Thay đổi

- **Hạng khách hàng tự động** (không cho phép chọn tay):
  - Bỏ dropdown "Hạng" khỏi dialog Thêm KH (KhachHangTableCard), Sửa KH (readonly JLabel), dialog inline từ tạo đơn hàng (DonHangCreateCard)
  - `tfHang` trong KhachHangFormCard luôn read-only
  - Hạng tính theo điểm tích lũy (`CustomerBUS.tierFromPoints()`):
    - 0–99 điểm → Đồng (REGULAR)
    - 100–499 điểm → Bạc (SILVER)
    - 500–1.999 điểm → Vàng (GOLD)
    - ≥ 2.000 điểm → Kim cương (DIAMOND)

- **Tích điểm tự động khi hoàn thành đơn hàng**:
  - Tỉ lệ: 1 điểm / 10.000 VNĐ
  - `CustomerBUS.addLoyaltyPoints(id, totalAmount)` → cộng điểm + cập nhật hạng
  - `CustomerDAO.updateLoyaltyAndType(id, newPoints, newType)` → UPDATE loyalty_points + customer_type
  - Gọi sau khi lưu đơn thành công trong `DonHangCreateCard`

- **Nút "Làm mới"** thêm vào:
  - Tab Khách hàng (`KhachHangTableCard`) → reload từ DB
  - Tab Sản phẩm (`SanPhamPanel`) → reload từ DB
  - Thiết kế clone từ tab Kho

### Lưu ý

- `CustomerBUS.tierFromPoints()` là `static` → có thể gọi trực tiếp từ bất kỳ đâu
- `updateCustomer()` trong CustomerDAO KHÔNG cập nhật loyalty_points/customer_type → dùng riêng `updateLoyaltyAndType()`
- Khi tạo KH mới luôn gán REGULAR (0 điểm), hạng sẽ tự nâng khi tích điểm đủ

---

## Session 2026-03-11 (phần 3) — Xuất Excel thật, đổi nhãn nút, fix DH002, cửa sổ fullscreen

### Thay đổi

- **ExportUtils.java** — Thêm `xuatExcel()` xuất file `.xls` thật (SpreadsheetML XML, không cần Apache POI):
  - Header row màu tím `#AF9FCB`, in đậm
  - Bỏ cột "Thao tác" khi xuất
  - `xuatCSV()` giờ là deprecated wrapper gọi sang `xuatExcel()`

- **Đổi nhãn nút xuất** (toàn bộ 9 panel):
  - `"In CSV"` → `"Xuất Excel"` (tất cả nút Excel)
  - `"In PDF"` → `"Xuất PDF"` (tất cả nút PDF)
  - Các panel: `KhachHangTableCard`, `KhuyenMaiPanel`, `DonHangTableCard`, `KhoTableCard`, `NhapKhoTableCard`, `NhanVienPanel`, `CategoryPanel`, `SanPhamPanel`, `SupplierPanel`

- **sql-init/04_seed_data_clean.sql** — Fix DH002 total sai:
  - `total_amount` + `subtotal`: `65000` → `110000` (5×5000 + 1×30000 + 5×11000 = 110,000)

- **GUI.java** — Kích thước cửa sổ:
  - Windowed mặc định: `1000×700` (trước là `1440×1024`)
  - Minimum size: `800×600`
  - Khởi động sau đăng nhập: `setExtendedState(JFrame.MAXIMIZED_BOTH)` → toàn màn hình
  - Vẫn cho phép resize tự do

### Lưu ý

- SpreadsheetML: viết XML với `<?mso-application progid="Excel.Sheet"?>` → Excel mở được `.xls` không cần thư viện ngoài
- `setExtendedState(MAXIMIZED_BOTH)` phải gọi sau khi `new GUI()` và trước `setVisible(true)`
- `setSize(800, 600)` là size khi restore-down từ maximize

---

## Session 2026-03-11 (phần 2) — In CSV, NhapKho invoice code, KH inline, last_purchase, UI fixes

### Thay đổi

- **Nút "In Excel" → "In CSV"** (toàn bộ app):
  - Đổi label `"In Excel"` → `"In CSV"` ở tất cả 9 panel: `KhachHangTableCard`, `KhuyenMaiPanel`, `DonHangTableCard`, `KhoTableCard`, `NhapKhoTableCard`, `NhanVienPanel`, `CategoryPanel`, `SanPhamPanel`, `SupplierPanel`

- **NhapKhoFormCard.java** — Fix "Số HĐ nhập" preview:
  - Trước: tạo random `NK-yyyyMMdd-XXXX`
  - Sau: gọi `new PurchaseInvoicesBUS().generateInvoiceCode()` → tạo `PN{yyyyMMdd}-{seq}` đúng chuẩn (ví dụ `PN20260311-003`)
  - `PurchaseInvoicesBUS.generateInvoiceCode()` đổi từ `private` → `public`

- **CategoryPanel.java** — Fix cột STT:
  - Thêm `setMinWidth(50)` + `setMaxWidth(60)` cho cột 0 → không bị dãn rộng
  - Áp `centerRenderer` riêng cho cột 0 → số STT hiển thị chính giữa

- **DonHangCreateCard.java** — Tạo khách hàng mới inline:
  - `btnTaoKH` từ `setVisible(false)` → `setVisible(true)`
  - Action mở `JDialog` đầy đủ (Mã KH auto, Tên, SĐT, Email, Địa chỉ, Hạng, Trạng thái)
  - Sau khi lưu: reload `allCustomers` từ DB, repopulate `cbKhachHang`, auto-chọn khách vừa tạo

- **CustomerDAO.java + CustomerBUS.java** — Fix `last_purchase` không được ghi DB:
  - Thêm method `updateLastPurchase(int customerId, LocalDateTime)` → `UPDATE customers SET last_purchase=? WHERE customer_id=?`
  - `updateCustomer()` cũ KHÔNG có `last_purchase` trong SQL → không bao giờ lưu được
  - `DonHangCreateCard` khi lưu đơn gọi `customerBUS.updateLastPurchase(id, now())` thay vì `updateCustomer()`

### Lưu ý

- Format mã phiếu nhập kho: `PN` + `yyyyMMdd` + `-` + 3 chữ số tuần tự (`PN20260301-001`)
- Format mã đơn bán hàng: `HN` + `yyyyMMdd` + `-` + 3 chữ số tuần tự (`HN20260301-001`)
- `updateCustomer()` trong `CustomerDAO` chỉ update: `full_name, phone, email, address, customer_type, status` — KHÔNG update `last_purchase`. Dùng `updateLastPurchase()` riêng khi cần.

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
