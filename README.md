

## 修改记录

### 1. 使用 Room 进行数据持久化

之前数据存在内存里，应用一关就没了。现在接入 Room KMP 数据库，数据持久化到本地磁盘。

主要改动：
- `AccountBook` 和 `Bill` 添加 `@Entity` 和 `@PrimaryKey` 注解，映射为数据库表
- 新建 `AccountBookDao` 和 `BillDao`，用 SQL 语句定义增删改查
- 新建 `AppDatabase` 作为 Room 数据库入口
- 新建 `RoomLedgerRepository` 实现 `LedgerRepository` 接口，内部调用 DAO 操作数据库
- ViewModel 改为接受 `LedgerRepository` 参数，不再硬编码 `InMemoryLedgerRepository`
- Android 和 Desktop 各自提供 `getDatabaseBuilder()` 的 actual 实现（Android 需要 Context，Desktop 用临时目录）

注：Room KMP 的 DAO 方法必须加 `suspend`，否则非 Android 平台编译报错。这导致 Repository 接口和 ViewModel 也要跟着改，ViewModel 里用 `scope.launch { }` 包裹所有数据库调用。

### 2. 账单按照时间排序，账单时间初始化有误

新增账单时，`editingBill` 是 `Bill(0, 0, "", 0.0, 0, "")`，它的 `date` 为 0。代码用 `editingBill?.let { formatDate(it.date) }` 初始化日期，因为 `editingBill` 不为 null，走了 `let` 分支，把 `date=0` 格式化成了 `1970-01-01`。

**解决**：改为判断 `isEditing`（即 `editingBill.id != 0L`），编辑模式用原日期，新增模式用当前日期。

### 3. 账本余额无法区别正负

余额显示用了 `formatAmountAbs()`，这个函数只返回绝对值，不显示正负号。余额为负时用户看到的还是正数。

**解决**：余额为负时显示 `-¥`，为正时显示 `¥`，用 `if (balance < 0) "-¥ " else "¥ "` 判断。

### 4. 优化错误提醒

之前保存账单时，金额或日期验证失败直接 `return@clickable` 静默返回，用户点了保存没反应，不知道哪里错了。

**解决**：新增 `errorMsg` 状态，验证失败时设置具体错误信息（"请输入有效金额"或"日期格式不正确，请使用 yyyy-MM-dd"），在金额输入区下方用红色文字显示，保存成功后自动消失。
