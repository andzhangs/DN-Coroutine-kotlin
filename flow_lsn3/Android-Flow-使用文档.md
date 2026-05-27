# Android Kotlin Flow 完全使用指南

> 本文档系统性地介绍 Kotlin Flow 在 Android 开发中的使用，从基础概念到高级应用，循序渐进。

---

## 目录

- [第一部分：入门篇](#第一部分入门篇)
- [第二部分：进阶篇](#第二部分进阶篇)
- [第三部分：高级篇](#第三部分高级篇)
- [第四部分：最佳实践与常见问题](#第四部分最佳实践与常见问题)

---

## 第一部分：入门篇

### 1.1 什么是 Flow？

Flow 是 Kotlin 协程库中的**冷数据流**（Cold Stream），用于异步地发射多个值。与 `suspend` 函数只能返回单个值不同，Flow 可以按顺序发射多个值。

**核心特性：**
- **冷流**：只有在被收集（collect）时才会开始执行
- **声明式**：基于操作符链式调用
- **协程集成**：天然支持结构化并发和取消
- **背压处理**：发送方和接收方速度不匹配时自动挂起

### 1.2 Flow vs 其他方案对比

| 特性 | Flow | LiveData | RxJava |
|------|------|----------|--------|
| 冷流/热流 | 冷流（默认） | 热流 | 两者都有 |
| 生命周期感知 | 需手动处理 | 自动 | 需手动处理 |
| 线程切换 | 协程调度器 | 主线程 | Scheduler |
| 背压处理 | 自动挂起 | 无 | 多种策略 |
| 学习曲线 | 中等 | 低 | 高 |
| 操作符丰富度 | 丰富 | 少 | 非常丰富 |

### 1.3 基础使用

#### 创建 Flow

```kotlin
// 方式一：flow 构建器
val numbersFlow: Flow<Int> = flow {
    for (i in 1..5) {
        delay(1000) // 模拟异步操作
        emit(i)     // 发射值
    }
}

// 方式二：flowOf（已知固定值）
val fixedFlow = flowOf(1, 2, 3, 4, 5)

// 方式三：asFlow（集合转换）
val listFlow = listOf("A", "B", "C").asFlow()

// 方式四：从回调转换
val callbackFlow = callbackFlow<String> {
    val listener = object : SomeListener {
        override fun onResult(value: String) {
            trySend(value)
        }
    }
    registerListener(listener)
    awaitClose { unregisterListener(listener) }
}
```

#### 收集 Flow

```kotlin
// 在协程中收集
lifecycleScope.launch {
    numbersFlow.collect { value ->
        println("收到: $value")
    }
}
```

> ⚠️ **注意**：`collect` 是挂起函数，会一直挂起直到 Flow 完成或协程被取消。

#### 在 Android 中安全收集

```kotlin
// ✅ 推荐：使用 repeatOnLifecycle
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiStateFlow.collect { state ->
            updateUI(state)
        }
    }
}

// ✅ 推荐：使用 flowWithLifecycle（单个 Flow）
lifecycleScope.launch {
    viewModel.uiStateFlow
        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .collect { state ->
            updateUI(state)
        }
}
```

### 1.4 基础操作符

#### 转换操作符

```kotlin
// map：转换每个值
val doubled = numbersFlow.map { it * 2 }

// filter：过滤值
val evenNumbers = numbersFlow.filter { it % 2 == 0 }

// transform：灵活转换（可以发射 0 个或多个值）
val transformed = numbersFlow.transform { value ->
    emit("即将发射: $value")
    emit(value.toString())
}
```

#### 终端操作符

```kotlin
// collect：收集所有值
numbersFlow.collect { println(it) }

// toList：收集为 List
val list: List<Int> = numbersFlow.toList()

// first：获取第一个值
val first: Int = numbersFlow.first()

// single：获取唯一值（多于一个则抛异常）
val single: Int = flowOf(42).single()

// reduce：累积计算
val sum: Int = numbersFlow.reduce { acc, value -> acc + value }

// fold：带初始值的累积计算
val sumWithInitial: Int = numbersFlow.fold(100) { acc, value -> acc + value }
```

#### 大小限制操作符

```kotlin
// take：只取前 N 个值
val firstThree = numbersFlow.take(3)

// takeWhile：满足条件时继续取
val lessThanFive = numbersFlow.takeWhile { it < 5 }

// drop：跳过前 N 个值
val skipTwo = numbersFlow.drop(2)
```

### 1.5 Flow 的上下文

```kotlin
// flowOn：改变上游的执行上下文
val dataFlow = flow {
    // 在 IO 线程执行
    val data = loadFromNetwork()
    emit(data)
}.flowOn(Dispatchers.IO)

// collect 始终在调用者的上下文中执行
lifecycleScope.launch(Dispatchers.Main) {
    dataFlow.collect { data ->
        // 在主线程更新 UI
        textView.text = data
    }
}
```

> ⚠️ **重要规则**：`flowOn` 只影响它上游的操作符，不影响下游。

---

## 第二部分：进阶篇

### 2.1 StateFlow

`StateFlow` 是一个**热流**，始终持有一个当前值，适合表示 UI 状态。

```kotlin
class MyViewModel : ViewModel() {
    // 私有的可变 StateFlow
    private val _uiState = MutableStateFlow(UiState())
    // 对外暴露不可变的 StateFlow
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { currentState ->
            currentState.copy(name = name)
        }
    }
}

data class UiState(
    val name: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```

**StateFlow 特性：**
- 始终有值（初始值必须提供）
- 只发射最新值给新的收集者
- 值相同时不会重复发射（基于 `equals` 判断）
- 永不完成（没有 `onCompletion`）

#### 在 Activity/Fragment 中收集

```kotlin
class MyFragment : Fragment() {
    private val viewModel: MyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.nameText.text = state.name
                    binding.loading.isVisible = state.isLoading
                    state.errorMessage?.let { showError(it) }
                }
            }
        }
    }
}
```

### 2.2 SharedFlow

`SharedFlow` 是一个**热流**，可以有多个订阅者，适合事件分发。

```kotlin
class MyViewModel : ViewModel() {
    // 一次性事件（如 Toast、导航）
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun onButtonClick() {
        viewModelScope.launch {
            _events.emit(UiEvent.ShowToast("操作成功"))
            _events.emit(UiEvent.NavigateBack)
        }
    }
}

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    object NavigateBack : UiEvent()
}
```

**SharedFlow 配置参数：**

```kotlin
MutableSharedFlow<T>(
    replay: Int = 0,            // 重放给新订阅者的历史值数量
    extraBufferCapacity: Int = 0, // 额外缓冲容量
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND // 缓冲区满时的策略
)
```

**BufferOverflow 策略：**
- `SUSPEND`：挂起发送方（默认）
- `DROP_OLDEST`：丢弃最旧的值
- `DROP_LATEST`：丢弃最新的值

#### StateFlow vs SharedFlow

| 特性 | StateFlow | SharedFlow |
|------|-----------|------------|
| 初始值 | 必须有 | 可选（replay） |
| 相同值去重 | 是 | 否 |
| 适用场景 | UI 状态 | 一次性事件 |
| 新订阅者 | 收到当前值 | 收到 replay 缓存 |

### 2.3 组合操作符

#### combine：组合多个 Flow

```kotlin
val nameFlow = MutableStateFlow("张三")
val ageFlow = MutableStateFlow(25)

// 任一 Flow 发射新值时，combine 都会重新计算
val profileFlow: Flow<String> = combine(nameFlow, ageFlow) { name, age ->
    "$name, $age 岁"
}

// 也可以链式调用
val result = nameFlow.combine(ageFlow) { name, age ->
    "$name, $age 岁"
}
```

#### zip：配对组合

```kotlin
val names = flowOf("张三", "李四", "王五")
val ages = flowOf(25, 30, 28)

// 一一配对，以较短的 Flow 为准
names.zip(ages) { name, age ->
    "$name: $age 岁"
}.collect { println(it) }
// 输出：张三: 25 岁, 李四: 30 岁, 王五: 28 岁
```

#### merge：合并多个 Flow

```kotlin
val flow1 = flowOf(1, 2, 3).onEach { delay(100) }
val flow2 = flowOf(10, 20, 30).onEach { delay(150) }

// 按时间顺序交错发射
merge(flow1, flow2).collect { println(it) }
```

#### flatMapLatest：映射并取消旧流

```kotlin
val searchQuery = MutableStateFlow("")

// 每次搜索词变化时，取消上一次搜索，启动新搜索
val searchResults: Flow<List<Result>> = searchQuery
    .debounce(300) // 防抖
    .filter { it.isNotEmpty() }
    .flatMapLatest { query ->
        repository.search(query) // 返回 Flow<List<Result>>
    }
```

#### flatMapConcat：顺序映射

```kotlin
// 按顺序处理每个值产生的 Flow
val sequential = flowOf(1, 2, 3).flatMapConcat { value ->
    flow {
        emit("$value-a")
        delay(100)
        emit("$value-b")
    }
}
// 输出：1-a, 1-b, 2-a, 2-b, 3-a, 3-b（严格顺序）
```

#### flatMapMerge：并发映射

```kotlin
// 并发处理每个值产生的 Flow
val concurrent = flowOf(1, 2, 3).flatMapMerge(concurrency = 2) { value ->
    flow {
        emit("$value-a")
        delay(100)
        emit("$value-b")
    }
}
// 输出顺序不确定（并发执行）
```

### 2.4 错误处理

#### catch 操作符

```kotlin
val safeFlow = flow {
    emit(1)
    throw RuntimeException("出错了")
    emit(2) // 不会执行
}.catch { exception ->
    // 处理异常
    emit(-1) // 可以发射备用值
    // 或记录日志
    Log.e("Flow", "Error: ${exception.message}")
}
```

> ⚠️ **注意**：`catch` 只能捕获它**上游**的异常，不能捕获下游（`collect` 中）的异常。

```kotlin
// ❌ 错误：catch 无法捕获 collect 中的异常
flow.catch { /* ... */ }
    .collect { value ->
        throw RuntimeException() // catch 捕获不到
    }

// ✅ 正确：使用 onEach + launchIn 替代
flow.onEach { value ->
        processValue(value) // 异常会被 catch 捕获
    }
    .catch { exception -> handleError(exception) }
    .launchIn(scope)
```

#### retry / retryWhen

```kotlin
// retry：失败时重试指定次数
val retryFlow = networkRequest()
    .retry(3) { cause ->
        // 返回 true 则重试
        cause is IOException
    }

// retryWhen：更精细的重试控制
val retryWhenFlow = networkRequest()
    .retryWhen { cause, attempt ->
        if (cause is IOException && attempt < 3) {
            delay(1000L * (attempt + 1)) // 指数退避
            true
        } else {
            false
        }
    }
```

#### onCompletion

```kotlin
val flow = flowOf(1, 2, 3)
    .onCompletion { cause ->
        if (cause == null) {
            println("Flow 正常完成")
        } else {
            println("Flow 异常结束: ${cause.message}")
        }
    }
```

### 2.5 缓冲与背压

#### buffer：缓冲发射值

```kotlin
// 无 buffer：发射和收集交替执行（慢）
// 有 buffer：发射和收集并发执行（快）
val bufferedFlow = flow {
    for (i in 1..5) {
        delay(100) // 发射耗时 100ms
        emit(i)
    }
}.buffer() // 默认 64 容量的缓冲

lifecycleScope.launch {
    bufferedFlow.collect { value ->
        delay(300) // 处理耗时 300ms
        println(value)
    }
}
```

#### conflate：只保留最新值

```kotlin
// 收集慢于发射时，跳过中间值，只处理最新值
val conflatedFlow = flow {
    for (i in 1..5) {
        delay(100)
        emit(i)
    }
}.conflate()

conflatedFlow.collect { value ->
    delay(300)
    println(value) // 可能输出：1, 3, 5（跳过了 2, 4）
}
```

#### collectLatest：取消旧的处理

```kotlin
// 新值到来时，取消正在进行的处理
flow {
    for (i in 1..5) {
        delay(100)
        emit(i)
    }
}.collectLatest { value ->
    println("开始处理 $value")
    delay(300) // 模拟耗时处理
    println("完成处理 $value") // 只有最后一个值能完成
}
```

### 2.6 防抖与节流

#### debounce：防抖

```kotlin
// 停止输入一段时间后才发射最新值
val searchQuery = MutableStateFlow("")

searchQuery
    .debounce(300L) // 300ms 内无新值才发射
    .filter { it.length >= 2 }
    .flatMapLatest { query ->
        searchRepository.search(query)
    }
    .collect { results ->
        showResults(results)
    }
```

#### sample：采样

```kotlin
// 每隔固定时间取最新值
val highFrequencyFlow = flow {
    while (true) {
        emit(System.currentTimeMillis())
        delay(10)
    }
}

highFrequencyFlow
    .sample(1000L) // 每秒取一个值
    .collect { timestamp ->
        updateUI(timestamp)
    }
```

---

## 第三部分：高级篇

### 3.1 自定义操作符

```kotlin
// 自定义去重操作符（按属性去重）
fun <T, K> Flow<T>.distinctUntilChangedBy(keySelector: (T) -> K): Flow<T> = flow {
    var previousKey: K? = null
    collect { value ->
        val key = keySelector(value)
        if (key != previousKey) {
            previousKey = key
            emit(value)
        }
    }
}

// 使用
data class User(val id: Int, val name: String, val age: Int)

usersFlow
    .distinctUntilChangedBy { it.name } // 名字相同时不重复发射
    .collect { user -> updateUI(user) }
```

```kotlin
// 自定义 throttleFirst（节流）
fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var lastEmitTime = 0L
    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmitTime >= windowDuration) {
            lastEmitTime = currentTime
            emit(value)
        }
    }
}

// 使用：按钮点击防重
buttonClickFlow
    .throttleFirst(1000L) // 1秒内只响应第一次点击
    .collect { handleClick() }
```

### 3.2 callbackFlow：将回调转为 Flow

```kotlin
// 将 LocationManager 回调转为 Flow
fun locationUpdates(context: Context): Flow<Location> = callbackFlow {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            trySend(location) // 非挂起版本的 send
        }

        override fun onProviderDisabled(provider: String) {
            close(Exception("GPS 已关闭"))
        }
    }

    locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, 1000L, 10f, listener
    )

    // awaitClose 在 Flow 被取消时调用
    awaitClose {
        locationManager.removeUpdates(listener)
    }
}

// 使用
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        locationUpdates(requireContext())
            .catch { e -> showError(e.message) }
            .collect { location ->
                updateMap(location)
            }
    }
}
```

### 3.3 channelFlow：并发发射

```kotlin
// flow {} 中不能切换上下文或并发发射
// channelFlow {} 支持并发发射
fun loadDataFromMultipleSources(): Flow<Data> = channelFlow {
    // 并发从多个数据源加载
    launch {
        val localData = loadFromDatabase()
        send(localData)
    }
    launch {
        val remoteData = loadFromNetwork()
        send(remoteData)
    }
}
```

### 3.4 StateFlow 的高级用法

#### stateIn：将冷流转为 StateFlow

```kotlin
class UserRepository(private val api: UserApi) {
    // 将网络请求结果转为 StateFlow
    val userProfile: StateFlow<UserProfile?> = flow {
        emit(api.fetchProfile())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 5秒无订阅者停止
        initialValue = null
    )
}
```

**SharingStarted 策略：**

```kotlin
// Eagerly：立即开始，永不停止
SharingStarted.Eagerly

// Lazily：第一个订阅者出现时开始，永不停止
SharingStarted.Lazily

// WhileSubscribed：有订阅者时启动，无订阅者时停止
SharingStarted.WhileSubscribed(
    stopTimeoutMillis = 5000,  // 最后一个订阅者消失后等 5 秒停止
    replayExpirationMillis = 0 // replay 缓存过期时间（0 = 永不过期）
)
```

#### shareIn：将冷流转为 SharedFlow

```kotlin
// 多个收集者共享同一个网络请求
val sharedNews: SharedFlow<List<News>> = newsRepository
    .fetchLatestNews()
    .shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        replay = 1
    )
```

### 3.5 Flow 与 Room 数据库

```kotlin
// DAO 层返回 Flow
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<User?>

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>
}

// ViewModel 中使用
class UserListViewModel(private val userDao: UserDao) : ViewModel() {
    val users: StateFlow<List<User>> = userDao.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
```

### 3.6 Flow 测试

```kotlin
// 使用 Turbine 库进行 Flow 测试
@Test
fun `test state flow updates`() = runTest {
    val viewModel = MyViewModel()

    viewModel.uiState.test {
        // 验证初始值
        assertEquals(UiState(), awaitItem())

        // 触发操作
        viewModel.loadData()

        // 验证加载中状态
        assertEquals(UiState(isLoading = true), awaitItem())

        // 验证数据加载完成
        val finalState = awaitItem()
        assertFalse(finalState.isLoading)
        assertNotNull(finalState.data)

        cancelAndIgnoreRemainingEvents()
    }
}

// 不用 Turbine 的手动测试方式
@Test
fun `test flow emission`() = runTest {
    val flow = flowOf(1, 2, 3).map { it * 2 }

    val result = flow.toList()
    assertEquals(listOf(2, 4, 6), result)
}
```

### 3.7 多 Flow 并发收集

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        // ❌ 错误：第二个 collect 永远不会执行（第一个会一直挂起）
        viewModel.stateA.collect { /* ... */ }
        viewModel.stateB.collect { /* ... */ }
    }
}

// ✅ 正确：使用多个 launch
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch {
            viewModel.stateA.collect { /* ... */ }
        }
        launch {
            viewModel.stateB.collect { /* ... */ }
        }
        launch {
            viewModel.events.collect { /* ... */ }
        }
    }
}
```

### 3.8 Flow 与协程取消

```kotlin
// Flow 会响应协程取消
val job = lifecycleScope.launch {
    infiniteFlow.collect { value ->
        if (shouldStop(value)) {
            cancel() // 取消协程，Flow 也会停止
            // 或者使用 return@collect（仅跳过当前值）
        }
    }
}

// 外部取消
job.cancel()

// 使用 takeWhile 优雅停止
infiniteFlow
    .takeWhile { value -> !shouldStop(value) }
    .collect { process(it) }
```

---

## 第四部分：最佳实践与常见问题

### 4.1 ViewModel 中的最佳实践

```kotlin
class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // ✅ 推荐：UI 状态使用 StateFlow
    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    // ✅ 推荐：一次性事件使用 SharedFlow
    private val _events = MutableSharedFlow<OrderEvent>()
    val events: SharedFlow<OrderEvent> = _events.asSharedFlow()

    // ✅ 推荐：组合多个数据源
    val orderWithUser: StateFlow<OrderWithUser?> = combine(
        orderRepository.getOrder(orderId),
        userRepository.getCurrentUser()
    ) { order, user ->
        OrderWithUser(order, user)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // ✅ 推荐：使用 update 原子更新
    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val orders = orderRepository.fetchOrders()
                _uiState.update { it.copy(orders = orders, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
                _events.emit(OrderEvent.ShowError(e.message ?: "未知错误"))
            }
        }
    }
}
```

### 4.2 常见错误与解决方案

#### 错误一：忘记使用 repeatOnLifecycle

```kotlin
// ❌ 危险：Activity/Fragment 在后台时仍在收集
lifecycleScope.launch {
    viewModel.uiState.collect { state ->
        updateUI(state) // 后台时可能 crash
    }
}

// ✅ 安全：只在可见时收集
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { state ->
            updateUI(state)
        }
    }
}
```

#### 错误二：在 StateFlow 中使用可变对象

```kotlin
// ❌ 错误：List 相同引用不会触发更新
private val _items = MutableStateFlow(mutableListOf<Item>())
fun addItem(item: Item) {
    _items.value.add(item) // StateFlow 不会发射新值！
}

// ✅ 正确：使用不可变集合 + copy
private val _items = MutableStateFlow<List<Item>>(emptyList())
fun addItem(item: Item) {
    _items.update { currentList ->
        currentList + item // 创建新 List
    }
}
```

#### 错误三：SharedFlow 事件丢失

```kotlin
// ❌ 问题：如果没有订阅者，事件会丢失
private val _events = MutableSharedFlow<Event>()

// ✅ 方案一：设置 replay（适合状态型事件）
private val _events = MutableSharedFlow<Event>(replay = 1)

// ✅ 方案二：设置 extraBufferCapacity + DROP_OLDEST
private val _events = MutableSharedFlow<Event>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
```

#### 错误四：多次 collect 阻塞

```kotlin
// ❌ 错误：第二个 collect 被阻塞
viewModel.flowA.collect { /* ... */ }
viewModel.flowB.collect { /* 永远不会执行 */ }

// ✅ 正确：分开 launch
launch { viewModel.flowA.collect { /* ... */ } }
launch { viewModel.flowB.collect { /* ... */ } }
```

### 4.3 性能优化

#### 避免不必要的重组/刷新

```kotlin
// 使用 distinctUntilChanged 避免重复值
viewModel.uiState
    .map { it.userName } // 只关注 userName
    .distinctUntilChanged() // 相同值不再发射
    .collect { name -> binding.tvName.text = name }

// 使用 mapNotNull 过滤空值
viewModel.uiState
    .mapNotNull { it.errorMessage }
    .collect { error -> showError(error) }
```

#### 合理使用 WhileSubscribed

```kotlin
// 配置旋转等配置变更的缓冲时间
val uiState = combine(source1, source2) { a, b -> merge(a, b) }
    .stateIn(
        scope = viewModelScope,
        // 给 5 秒时间应对配置变更，避免重复请求
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )
```

### 4.4 完整的 MVI 架构示例

```kotlin
// ---------- State ----------
data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ---------- Intent (User Action) ----------
sealed class SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent()
    object ClearQuery : SearchIntent()
    data class ResultClicked(val result: SearchResult) : SearchIntent()
}

// ---------- Side Effect ----------
sealed class SearchEffect {
    data class NavigateToDetail(val id: String) : SearchEffect()
    data class ShowToast(val message: String) : SearchEffect()
}

// ---------- ViewModel ----------
class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<SearchEffect>()
    val effects: SharedFlow<SearchEffect> = _effects.asSharedFlow()

    private val _intents = MutableSharedFlow<SearchIntent>(extraBufferCapacity = 64)

    init {
        handleIntents()
    }

    fun sendIntent(intent: SearchIntent) {
        viewModelScope.launch {
            _intents.emit(intent)
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intents.collect { intent ->
                when (intent) {
                    is SearchIntent.QueryChanged -> handleQueryChanged(intent.query)
                    is SearchIntent.ClearQuery -> handleClearQuery()
                    is SearchIntent.ResultClicked -> handleResultClicked(intent.result)
                }
            }
        }
    }

    private suspend fun handleQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.length >= 2) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val results = repository.search(query)
                _uiState.update { it.copy(results = results, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun handleClearQuery() {
        _uiState.update { SearchUiState() }
    }

    private suspend fun handleResultClicked(result: SearchResult) {
        _effects.emit(SearchEffect.NavigateToDetail(result.id))
    }
}

// ---------- Fragment ----------
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 监听用户输入
        binding.searchInput.addTextChangedListener { text ->
            viewModel.sendIntent(SearchIntent.QueryChanged(text.toString()))
        }

        binding.clearButton.setOnClickListener {
            viewModel.sendIntent(SearchIntent.ClearQuery)
        }

        // 收集 UI 状态
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.loading.isVisible = state.isLoading
                        adapter.submitList(state.results)
                        binding.errorText.text = state.error
                        binding.errorText.isVisible = state.error != null
                    }
                }
                launch {
                    viewModel.effects.collect { effect ->
                        when (effect) {
                            is SearchEffect.NavigateToDetail -> navigateToDetail(effect.id)
                            is SearchEffect.ShowToast -> showToast(effect.message)
                        }
                    }
                }
            }
        }
    }
}
```

### 4.5 常用扩展函数

```kotlin
/**
 * 在 Fragment 中安全收集 Flow 的扩展
 */
fun <T> Fragment.collectFlow(flow: Flow<T>, action: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(action)
        }
    }
}

// 使用
collectFlow(viewModel.uiState) { state ->
    updateUI(state)
}

/**
 * 将 Flow 转为只观察某个属性变化
 */
fun <T, R> StateFlow<T>.observeProperty(
    lifecycleOwner: LifecycleOwner,
    selector: (T) -> R,
    action: (R) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@observeProperty
                .map(selector)
                .distinctUntilChanged()
                .collect(action)
        }
    }
}

// 使用
viewModel.uiState.observeProperty(viewLifecycleOwner, { it.userName }) { name ->
    binding.tvName.text = name
}
```

---

## 参考资料

- [Kotlin Flow 官方文档](https://kotlinlang.org/docs/flow.html)
- [Android 中的 Kotlin Flow](https://developer.android.com/kotlin/flow)
- [StateFlow 和 SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [repeatOnLifecycle API 说明](https://developer.android.com/topic/libraries/architecture/coroutines#repeatonlifecycle)
- [Flow 测试指南](https://developer.android.com/kotlin/flow/test)

---

> 📅 生成日期：2026-05-27  
> 🔖 适用版本：Kotlin 1.9+, kotlinx-coroutines 1.7+, Lifecycle 2.6+
