---
name: cpp-coding-standards
description: C/C++ coding standards and best practices for the dashcam DVR project. Use this skill when writing, reviewing, or modifying C/C++ code to ensure compliance with project conventions including naming rules, error handling, logging, and code organization.
---

# C/C++代码规范 (C/C++ Coding Standards)

本 skill 定义了 C/C++ 项目的代码编写规范，AI 助手在生成或修改代码时必须遵循以下规则。

## 适用场景

- 编写新的 C/C++ 代码
- 审查现有代码
- 重构或修改代码

## 使用方法

在处理 C/C++ 代码时，自动应用以下规范。如果用户要求审查代码，请对照以下规则进行检查。

---

## 规范细则

### 1. 代码命名规范

#### 1.1 通用命名规则

- **有意义的名字**：变量、函数和类等的名称应当清晰表达其用途或功能。例如，使用 `calculate_area` 而不是 `calc_a`
- **命名风格一致性**：使用驼峰命名法（CamelCase）或下划线分隔（snake_case），但要确保项目内部保持一致
- **类名**：使用大写字母开头（PascalCase）
- **变量和函数名**：C语言使用小写+下划线，C++使用小写首字符+驼峰式
- **避免过短或过长的名字**：名字应该足够简短以便记忆，同时又足够描述清楚其意义
- **使用前缀或后缀指示类型**：如数据库的名字以 `db_` 开头，表的名字以 `t_` 开头，按钮的名字以 `btn_` 开头，图片的以 `img_` 开头
- **避免使用易混淆的字符**：比如数字1与字母l，数字0与字母O
- **常量命名**：全大写并用下划线分隔单词，例如 `MAX_LENGTH`
- **全局命名统一**：代表同一事物的全局名字要统一，如设备WiFi配网叫 `ap_config`，则全局相关的都叫 `ap_config`

#### 1.2 命名注意事项

- 不要使用单个字符作为变量名（除了简单的循环计数器 i, j, k 等）
- 避免使用保留字或关键字作为标识符
- 不要使用过于通用的名字（如 data, info, value）
- 避免使用不同含义却相同的名字
- 禁止使用非ASCII字符作为标识符
- 避免过度缩写

#### 1.3 C语言命名规范

C语言代码中，函数和变量都使用**小写+下划线**命名风格。

**函数命名规则：**
- **对外函数**：直接使用模块前缀（如 `page_win_xxx`）
- **静态函数（文件内部）**：在模块前缀前加单下划线（如 `_page_win_xxx`）

**错误示例：**
```c
int getMacAddress(char *buf);  // 驼峰命名
int get_macAddress(char *buf);  // 混合命名

// 静态函数未加下划线前缀
static int page_win_init(void);  // 错误：静态函数应加_前缀
static void handle_event(void);  // 错误：静态函数应加_前缀
```

**正确示例：**
```c
// 对外函数
int get_mac_address(char *buf);
int wifi_service_start(wifi_service_s *service);

// 静态函数（文件内部使用）加下划线前缀
static int _page_win_init(void);
static void _page_win_handle_event(void);
static bool _is_valid_index(int32_t index);
```

#### 1.4 C++命名规范

C++代码中：
- **类名**：使用大写首字符+驼峰式（PascalCase）命名
- **函数和变量**：使用小写+下划线命名风格（snake_case）
- **类成员变量（私有/保护）**：添加前缀 `m_`，如 `m_is_inited`、`m_data`

**错误示例：**
```cpp
class wifiManager {  // 类名首字母应大写
    void StartService();  // 驼峰命名，不符合规范
    int connectionCount;  // 驼峰命名，且缺少m_前缀
};

void ProcessData();  // 驼峰命名
```

**正确示例：**
```cpp
class WifiManager {
    void start_service();
    int m_connection_count;  // 私有变量添加m_前缀
};

class DataProcessor {
private:
    bool m_is_inited;      // 私有变量m_前缀
    int32_t m_data;        // 私有变量m_前缀
    
public:
    void start_service();
    int32_t process_data();
};

void process_data();
int32_t connection_count;
```

---

### 2. 数据类型定义规范

数据类型不使用原始类型，使用固定宽度的类型定义，确保跨平台兼容性。

**不推荐的方式（平台相关）：**
```c
int32_t my_int = 100;           // 即使使用int32_t，变量名也不应使用原始类型名
uint32_t my_uint = 1000;
int64_t my_long = -10;
size_t my_size = 90;            // 禁用size_t

printf("Value: %d\n", my_int);        // int 类型不确定
printf("Value: %ld\n", my_long);      // long 大小不确定
printf("Size: %zu\n", my_size);       // size_t 平台相关
```

**推荐的方式（平台无关）：**
```c
#include <inttypes.h>
#include <stdint.h>

int32_t my_int32 = 100;
uint64_t my_uint64 = 1000;
int8_t my_int8 = -10;
uint32_t my_uint32 = 500;

printf("Value: %" PRId32 "\n", my_int32);
printf("Value: %" PRIu64 "\n", my_uint64);
printf("Value: %" PRId8 "\n", my_int8);
```

**说明：**
- 使用 `<stdint.h>` 中的固定宽度类型（`int32_t`, `uint32_t`, `int64_t`, `uint64_t` 等）
- 禁用 `size_t`, `long int`, `unsigned int` 等原始格式
- 使用 `<inttypes.h>` 中的格式化宏进行打印
- **所有示例代码必须使用固定宽度类型**，禁止使用 `int`, `long`, `unsigned` 等原始类型

---

### 3. 枚举类型定义前缀统一

枚举类型的命名必须有统一的前缀，并且以 `_e` 结尾。

**错误示例：**
```c
typedef enum {
    WIFI_SOUND_CONNECTING,
    WIFI_SOUND_CONNECT_SUCCESS,
    WIFI_SOUND_CONNECT_FAILED,
} wifi_sound_type_e;
```

**正确示例：**
```c
typedef enum {
    WIFI_SOUND_TYPE_CONNECTING,
    WIFI_SOUND_TYPE_CONNECT_SUCCESS,
    WIFI_SOUND_TYPE_CONNECT_FAILED,
} wifi_sound_type_e;
```

---

### 4. 代码块必须使用花括号

所有控制语句（if、for、while、do-while等）即使只有单行代码，也必须使用花括号 `{}`。

**错误示例：**
```c
if (a == b)
    c = 1;

for (int i = 0; i < 10; i++)
    printf("%d\n", i);
```

**正确示例：**
```c
if (a == b) {
    c = 1;
}

for (int i = 0; i < 10; i++) {
    printf("%d\n", i);
}
```

---

### 5. 代码紧凑性规范

代码应保持相对紧凑，避免过多的空行。逻辑紧密相关的代码行之间不需要空行。

**错误示例（过于松散）：**
```c
pthread_mutex_lock(&s_priv.mutex);

count = s_priv.count;

pthread_mutex_unlock(&s_priv.mutex);
```

**错误示例（过于紧凑）：**
```c
int count;
pthread_mutex_lock(&s_priv.mutex);
count = s_priv.count;
pthread_mutex_unlock(&s_priv.mutex);
return count;
```

**正确示例：**
```c
int count;

pthread_mutex_lock(&s_priv.mutex);
count = s_priv.count;
pthread_mutex_unlock(&s_priv.mutex);

return count;
```

**说明：**
- 变量定义后、函数返回前需要有空行
- 紧密相关的代码操作（如加锁-操作-解锁）之间不应有空行
- 不同的代码逻辑块之间可以用空行分隔

---

### 6. 头文件API声明规范

头文件中的API函数声明应遵循以下规范：

1. **函数声明之间不留空行**
2. **按照调用顺序排列** - 将通常先调用的函数放在前面

**错误示例：**
```c
void util_thread_init(void);

int util_thread_create(pthread_t *tid, const char *name, void *(*start_routine)(void *), void *arg);

int util_thread_get_count(void);

int util_thread_get_list(util_thread_info_s *list, int max_count);
```

**正确示例：**
```c
void util_thread_init(void);
int util_thread_create(pthread_t *tid, const char *name, void *(*start_routine)(void *), void *arg);
int util_thread_get_count(void);
int util_thread_get_list(util_thread_info_s *list, int max_count);
```

**说明：**
- 紧凑的函数声明便于浏览所有API
- 按调用顺序排列符合用户的使用习惯（先init，再create，再get等）

---

### 7. API前缀必须与文件名保持一致

头文件暴露的API函数名必须以文件名（不含扩展名）作为前缀。

**错误示例：**
文件 `wifi_setting.c` / `wifi_setting.h`
```c
int load_wifi_config(void);      // 错误：前缀不是wifi_setting
int setting_save(void);          // 错误：前缀不是wifi_setting
void wifiConfigGet(void);        // 错误：使用了驼峰命名且前缀不完整
```

**正确示例：**
文件 `wifi_setting.c` / `wifi_setting.h`
```c
int wifi_setting_load(void);
int wifi_setting_save(void);
int wifi_setting_get(wifi_setting_s **setting);
```

**说明：**
- 如果文件名为 `xxx_yyy.c`，则API前缀必须是 `xxx_yyy_`
- 这条规范确保API的命名空间与文件模块一一对应，便于代码组织和维护

---

### 8. 头文件尾部的格式规范

头文件尾部的 `__cplusplus` 闭合和最终的 `#endif` 之间不应留空行。

**错误示例：**
```c
#ifndef __UTIL_THREAD_H__
#define __UTIL_THREAD_H__

#ifdef __cplusplus
extern "C" {
#endif

// ... API declarations ...

#ifdef __cplusplus
}
#endif

#endif //__UTIL_THREAD_H__
```

**正确示例：**
```c
#ifndef __UTIL_THREAD_H__
#define __UTIL_THREAD_H__

#ifdef __cplusplus
extern "C" {
#endif

// ... API declarations ...

#ifdef __cplusplus
}
#endif
#endif //__UTIL_THREAD_H__
```

**说明：**
- 尾部的 `#endif` 应紧跟在 `__cplusplus` 的闭合之后，无空行
- 保持头文件结尾紧凑

---

### 9. C源文件函数顺序规范

C语言源文件(.c)中的函数定义顺序必须与头文件(.h)中的函数声明顺序保持一致。

**头文件 (util_thread.h)：**
```c
void util_thread_init(void);
int util_thread_create(pthread_t *tid, const char *name, void *(*start_routine)(void *), void *arg);
int util_thread_get_count(void);
int util_thread_get_list(util_thread_info_s *list, int max_count);
```

**错误示例 (util_thread.c)：**
```c
// 函数顺序与头文件不一致
int util_thread_get_count(void) { ... }
int util_thread_create(...) { ... }
void util_thread_init(void) { ... }
int util_thread_get_list(...) { ... }
```

**正确示例 (util_thread.c)：**
```c
// 函数顺序与头文件一致
void util_thread_init(void) { ... }
int util_thread_create(...) { ... }
int util_thread_get_count(void) { ... }
int util_thread_get_list(...) { ... }
```

**说明：**
- 源文件中的函数实现顺序必须与头文件中的声明顺序一致
- 便于快速定位函数实现，提高代码可读性
- 如果头文件中有静态函数声明，也应在源文件中保持相同顺序

---

### 10. 变量使用前必须初始化

所有变量在使用前必须初始化，特别是字符串和数组类型的变量。

**错误示例：**
```c
char buf[256];
strcpy(buf, "hello");  // buf未初始化

int count;
pthread_mutex_lock(&mutex);
count = get_count();  // 使用前未初始化
pthread_mutex_unlock(&mutex);
```

**正确示例：**
```c
char buf[256] = {0};
memset(buf, 0, sizeof(buf));
strcpy(buf, "hello");

int count = 0;
pthread_mutex_lock(&mutex);
count = get_count();
pthread_mutex_unlock(&mutex);
```

**说明：**
- 所有局部变量在定义时都必须初始化
- 数值类型初始化为 `0`
- 指针类型初始化为 `NULL`
- 字符串/数组使用 `{0}` 或 `memset` 初始化

---

### 11. 日志打印规范

#### 11.1 日志级别

- **ERROR**：错误，程序无法完成某功能
- **WARN**：警告，潜在问题，但程序仍可运行
- **INFO**：信息，记录系统运行状态（如启动、关闭等关键操作）
- **DEBUG**：用于开发阶段排查问题

#### 11.2 日志使用规范

所有日志打印必须使用 `utils_log*()` 函数（或项目指定的日志接口）。

**错误示例：**
```c
printf("Failed to open file\n");           // 错误：使用了printf
fprintf(stderr, "Error: %s\n", msg);       // 错误：使用了fprintf
DEBUG_LOG("Something happened");           // 错误：使用了其他日志函数
utils_loge("Failed to Open File");         // 错误：包含大写字母
utils_logi("WiFi Connected Successfully"); // 错误：包含大写字母

// 多余的前缀，因为utils_loge会自动打印函数名
if (!s_priv.inited) {
    utils_loge("util_thread: not initialized");
    return -1;
}

// 多余的换行
utils_loge("not initialized\r");
utils_loge("not initialized\n");
utils_loge("not initialized\r\n");
```

**正确示例：**
```c
utils_loge("failed to open file");
utils_logi("wifi connected successfully");
utils_loge("malloc failed, size=%d", size);
```

**说明：**
- 为方便后续排查问题，所有日志打印必须使用`utils_log*()`函数（或项目指定的日志接口）
```c
utils_logd("debug");
utils_logi("info");
utils_logw("wraning");
utils_loge("error");
```
- `utils_log*`函数内部已支持打印函数名、行号及换行，因此内容要尽可能简洁
- 不需要在消息中包含函数名前缀
- 除首字母外，打印内容必须是小写，便于阅读和维护
- 禁止使用其他日志函数（printf、fprintf、syslog等）
- **例外**：如果日志内容中包含函数名，则函数名保持原有的大小写
```c
char *json_str = cJSON_Print(root);
if (json_str == NULL) {
    utils_loge("cJSON_Print failed");
    cJSON_Delete(root);
    return -1;
}
```

---

### 12. 错误处理必须添加日志打印

所有错误处理分支都必须添加 `utils_log*()` 打印。

**错误示例：**
```c
if (ptr == NULL) {
    return -1;  // 缺少错误打印
}

if (fd < 0) {
    // 没有打印错误信息
    return -1;
}
```

**正确示例：**
```c
if (ptr == NULL) {
    utils_loge("null pointer");
    return -1;
}

if (fd < 0) {
    utils_loge("open failed");
    return -1;
}

if (!s_priv.inited) {
    utils_loge("not initialized");
    return -1;
}
```

---

### 13. 内存分配失败必须添加日志打印

内存分配失败一定要打印log。

**示例：**
```c
bool allocate_mem()
{
    char *data = (uint8_t *)malloc(100);
    if (data == NULL) {
        utils_loge("malloc failed");
        return false;
    }
    
    uint8_t *data2 = new uint8_t[1024];
    if (data2 == nullptr) {
        utils_loge("new failed");
        return false;
    }
    
    return true;
}
```

---

### 14. 资源释放函数统一使用 `free` 作为命名后缀

资源释放函数统一使用 `free` 作为命名后缀，而非 `destroy` 或其他。

**错误示例：**
```c
wifi_service_destroy(wifi_service_s **service);
buffer_destroy(buffer_s *buf);
```

**正确示例：**
```c
wifi_service_free(wifi_service_s **service);
buffer_free(buffer_s *buf);
```

---

### 15. 字符串操作使用 `snprintf`

字符串操作尽可能使用 `snprintf` 函数，防止缓冲区溢出。

**错误示例：**
```c
char buf[32];
strcpy(buf, long_string);  // 可能溢出
sprintf(buf, "%s", long_string);  // 可能溢出
strcat(buf, suffix);  // 可能溢出
```

**正确示例：**
```c
char buf[32];
snprintf(buf, sizeof(buf), "%s", long_string);
snprintf(buf, sizeof(buf), "%s%s", existing, suffix);
```

---

### 16. Git提交规范

#### 16.1 标准格式

```bash
git commit -m "<type>(<scope>): <subject>"
```

完整格式包含三个部分：

```
<type>(<scope>): <subject>

<body>

<footer>
```

Header部分只有一行，包括三个字段：type（必需）、scope（可选）和subject（必需）。

#### 16.2 Type类型

| 类型 | 说明 |
|------|------|
| feat | 新功能（feature） |
| fix | 修补bug |
| docs | 文档（documentation） |
| style | 格式（不影响代码运行的变动） |
| refactor | 重构（即不是新增功能，也不是修改bug的代码变动） |
| test | 增加测试 |
| chore | 构建过程或辅助工具的变动 |
| perf | 性能优化 |
| build | 改变了build工具（如grunt换成了npm） |

**注意：** type后面需要一个空格！

#### 16.3 提交示例

**简短提交（单行）：**
```bash
git commit -m "feat(auth): 新增用户登录功能"
git commit -m "fix(api): 修复数据查询接口500错误"
git commit -m "docs: 更新项目README文档"
```

**详细提交（多行）：**
```bash
git commit -m "docs: 更新代码审核与架构规范文档

- 统一 C++ 命名风格
- 明确数据类型定义标准
- 补充回调函数命名规则"
```

或带分类前缀的格式：
```bash
git commit -m "docs: 更新代码审核与架构规范文档

- 规范：统一 C++ 命名风格
- 定义：明确数据类型定义标准
- 命名：补充回调函数命名规则"
```

---

### 17. 回调函数命名规范

回调函数类型和设置回调的API必须遵循统一的命名模板。

**命名模板：**

```c
// 回调函数类型定义
typedef int32_t (*prefix_on_xxx_callback_f)(prefix_t *prefix, const data_t *data);

// 设置回调的API
int32_t prefix_set_on_xxx_callback(prefix_t *prefix, prefix_on_xxx_callback_f callback);
```

**命名规则：**
- **回调类型名**：`<prefix>_on_<event>_callback_f`（使用 `_f` 后缀表示函数指针类型）
- **设置回调函数名**：`<prefix>_set_on_<event>_callback`
- **回调函数参数**：第一个参数为上下文对象（非const指针），第二个参数为回调数据（const指针）

**错误示例：**
```c
// 错误：类型名不符合规范
typedef void (*callback_t)(uvcreader_s *uvcreader, uvcreader_data_s *data);

// 错误：缺少_on_前缀
typedef int32_t (*uvcreader_ready_callback_f)(uvcreader_s *uvcreader, const uvcreader_data_s *data);

// 错误：设置函数名不符合规范
void uvcreader_register_callback(uvcreader_on_ready_callback_f cb);
```

**正确示例：**
```c
// 定义回调函数类型（使用_f后缀）
typedef int32_t (*uvcreader_on_ready_callback_f)(uvcreader_s *uvcreader, const uvcreader_data_s *data);
typedef int32_t (*uvcreader_on_error_callback_f)(uvcreader_s *uvcreader, const uvcreader_error_s *error);

// 设置回调的API
int32_t uvcreader_set_on_ready_callback(uvcreader_s *uvcreader, uvcreader_on_ready_callback_f callback);
int32_t uvcreader_set_on_error_callback(uvcreader_s *uvcreader, uvcreader_on_error_callback_f callback);

// C++中同样遵循此规范（作为using别名时）
class UvcReader {
public:
    // 回调类型定义（using别名）
    using on_ready_callback_f = int32_t (*)(uvcreader_t *reader, const uvcreader_data_t *data);
    using on_error_callback_f = int32_t (*)(uvcreader_t *reader, const uvcreader_error_t *error);
    
    // 设置回调函数
    int32_t set_on_ready_callback(on_ready_callback_f callback);
    int32_t set_on_error_callback(on_error_callback_f callback);
};
```

**说明：**
- 回调函数类型统一使用 `_f` 后缀（表示 function pointer）
- 回调函数命名格式统一为 `on_<event>_callback`，表示"当某事件发生时回调"
- 设置回调的API统一使用 `set_on_<event>_callback` 命名
- 回调数据参数使用 `const` 修饰（只读），上下文参数不加 const（可能需要在回调中修改）
- 返回值建议使用 `int32_t` 表示回调执行结果（0表示成功，非0表示失败）

---

### 18. Const正确性规范

函数参数在函数体内不被修改时，必须添加 `const` 修饰符，以明确语义并启用编译器优化检查。

**错误示例：**
```c
// 字符串参数未被修改但未加const
int32_t get_mac_address(char *buf);

// 结构体指针参数未被修改但未加const
int32_t verify_config(config_s *cfg);

// 只读缓冲区未加const
void process_buffer(uint8_t *data, int32_t len);
```

**正确示例：**
```c
// 字符串参数添加const修饰
int32_t get_mac_address(const char *buf);

// 结构体指针参数添加const修饰
int32_t verify_config(const config_s *cfg);

// 只读缓冲区添加const修饰
void process_buffer(const uint8_t *data, int32_t len);

// 需要修改的参数不加const
void fill_buffer(uint8_t *data, int32_t len);
```

**说明：**
- 输入参数（只读）必须加 `const`
- 输出参数或需要修改的参数不加 `const`
- `const` 位置：`const type_t *ptr` 或 `type_t const *ptr`（指针指向的内容不可变）
- 指针本身不可变：`type_t *const ptr`（较少使用）

---

### 18. Namespace使用规范

C++代码中，**禁止使用 `using namespace` 指令和 `using` 声明**，必须使用完全限定名（fully qualified name）。

**唯一的例外**：定义**回调函数类型**时，可以在类内部或函数内部使用 `using` 定义类型别名。

**错误示例：**
```cpp
// 头文件中使用using namespace（全局命名空间污染）
namespace media {
    class Player;
}
using namespace media;  // 错误：在头文件中使用using namespace

// 源文件中使用using directive（全局污染）
using namespace utils;  // 错误：禁止using namespace

// 在命名空间外部使用using声明
using utils::Logger;    // 错误：禁止在全局作用域使用using声明

// 错误：函数内部非回调场景使用using
void process_data() {
    using json = nlohmann::json;  // 错误：非回调函数场景禁止使用using
    json data;
}
```

**正确示例：**
```cpp
// 头文件中：仅声明命名空间内的内容
namespace media {
    class Player {
        void start_playback();
    };
}

// 源文件中：使用完全限定名
void init_player() {
    media::Player player;           // 正确：完全限定名
    utils::Logger::init();          // 正确：完全限定名
    
    int32_t result = utils::config::load();  // 嵌套命名空间也完全限定
}

// 类定义中的命名空间使用
class MyClass {
public:
    void set_callback(media::Callback cb);  // 正确：完全限定名
};

// 唯一例外：定义回调函数类型时可以使用using（必须遵循回调函数命名规范）
class EventHandler {
public:
    // 正确：类内部使用using定义回调函数类型别名（遵循回调函数命名规范）
    using on_complete_callback_f = std::function<int32_t (EventHandler *handler, const event_data_t *data)>;
    using on_progress_callback_f = std::function<int32_t (EventHandler *handler, const progress_t *progress)>;
    
    int32_t set_on_complete_callback(on_complete_callback_f callback);
    int32_t set_on_progress_callback(on_progress_callback_f callback);
    
private:
    on_complete_callback_f m_on_complete;
    on_progress_callback_f m_on_progress;
};
```

**说明：**
- **绝对禁止**：
  - 禁止 `using namespace xxx;` 指令（任何作用域）
  - 禁止 `using xxx::yyy;` 声明（任何作用域）
  - 禁止在头文件中使用任何形式的 using
  - 禁止在函数/类内部使用 `using` 定义**非回调类型**的别名
- **唯一例外**：
  - **仅回调函数类型定义**可以在类内部或函数内部使用 `using` 定义类型别名
  - 回调类型命名必须遵循回调函数命名规范（见第17节）：使用 `on_<event>_callback_f` 格式
- **目的**：避免命名空间污染，提高代码可读性和可维护性，限制 using 的滥用

---

### 19. 结构体命名规范

结构体使用 `typedef` 定义，统一以 `_t` 作为类型名后缀。若结构体需要自引用（如链表节点），则在结构体标签前加单下划线。

**错误示例：**
```c
// 错误：直接使用struct，未使用typedef
struct config {
    int32_t timeout;
};
struct config cfg;  // 使用时需要写struct

// 错误：使用后缀不规范
struct wifi_cfg_s {
    int32_t timeout;
};

// 错误：自引用时结构体标签与类型别名相同
typedef struct node_t {
    struct node_t *next;  // 某些编译器会警告
    int32_t data;
} node_t;
```

**正确示例：**
```c
// 场景1：无需自引用的结构体 - 只使用类型别名，省略结构体标签
typedef struct {
    int32_t m_timeout;
    char m_ssid[64];
} wifi_config_t;

// 场景2：需要自引用的结构体（如链表）- 结构体标签加下划线前缀
typedef struct _list_node_t {
    struct _list_node_t *m_next;  // 自引用使用带下划线的标签
    int32_t m_data;
} list_node_t;

// 场景3：树结构自引用
typedef struct _tree_node_t {
    struct _tree_node_t *m_left;
    struct _tree_node_t *m_right;
    int32_t m_value;
} tree_node_t;

// 使用示例
wifi_config_t cfg;  // 简洁：无需struct关键字
list_node_t *node = NULL;  // 使用类型别名
```

**说明：**
- **格式**：统一使用 `typedef struct ... xxx_yyy_t` 形式
- **自引用处理**：若结构体内部需要引用自身（链表、树等），结构体标签前加单下划线（`_xxx_t`），类型别名不加下划线（`xxx_t`）
- **非自引用**：若无需自引用，直接省略结构体标签，只保留类型别名
- **后缀**：统一使用 `_t` 后缀，禁用 `_s`、`_st` 等其他后缀
- **优势**：使用typedef后，定义变量时无需写 `struct` 关键字，代码更简洁

---

### 20. 头文件包含路径规范

头文件包含**禁止使用多级相对路径**，所有头文件路径应在 CMake 中配置。源文件中直接使用文件名即可。

**错误示例：**
```c
// 禁止：使用多级路径
#include "utils/thread/thread.h"
#include "media/core/decoder.h"
#include "../include/config.h"

// 禁止：使用相对路径穿越
#include "../../middleware/hal/gpio.h"
```

**正确示例：**
```c
// 正确：直接使用文件名
#include "thread.h"
#include "decoder.h"
#include "config.h"
#include "gpio.h"

// CMakeLists.txt 中配置头文件搜索路径
// target_include_directories(target PRIVATE
//     ${CMAKE_SOURCE_DIR}/middleware/utils/thread
//     ${CMAKE_SOURCE_DIR}/middleware/media/core
//     ${CMAKE_SOURCE_DIR}/middleware/hal
// )
```

**说明：**
- 源文件中只使用文件名（如 `#include "xxx.h"`）
- 头文件搜索路径统一在 CMakeLists.txt 中配置
- 禁止使用相对路径（`./`、`../`）
- 禁止使用多级目录路径（`aaa/bbb/ccc.h`）
- 优势：重构时无需修改源文件，路径变更只需调整 CMake 配置

---

### 21. 头文件宏定义命名规范

头文件保护宏（include guard）必须使用双下划线前缀和后缀，格式为 `__FILENAME_H__`。

**错误示例：**
```c
// 错误：无双下划线前缀和后缀
#ifndef MP4UTIL_COMMON_H
#define MP4UTIL_COMMON_H

// 错误：使用单下划线
#ifndef _UTIL_THREAD_H_
#define _UTIL_THREAD_H_

// 错误：使用pragma once（本项目统一使用宏保护）
#pragma once
```

**正确示例：**
```c
// 正确：双下划线前缀和后缀
#ifndef __MP4UTIL_COMMON_H__
#define __MP4UTIL_COMMON_H__

// 正确：文件名全部大写，用下划线连接
#ifndef __UTIL_THREAD_H__
#define __UTIL_THREAD_H__

// 正确：嵌套路径的头文件
#ifndef __MEDIA_CORE_DECODER_H__
#define __MEDIA_CORE_DECODER_H__

// 文件末尾的#endif注释也保持一致
#endif // __UTIL_THREAD_H__
```

**说明：**
- 格式：`__<FILENAME>_H__`（双下划线 + 全大写文件名 + 双下划线）
- 文件名中的路径分隔符 `/` 替换为下划线 `_`
- 文件名中的 `.` 保留或替换为下划线（统一即可）
- 禁止使用 `#pragma once`（为保证跨编译器兼容性）
- 所有头文件必须包含保护宏

---

### 22. 空白字符规范

代码中**禁止出现尾随空白**（trailing whitespace）。空行、语句末尾不得包含空格或Tab字符。

**错误示例：**
```c
// 空行包含空格或Tab（用·表示空格，→表示Tab）
int32_t foo(void) {
···
    return 0;
→→→
}

// 语句末尾有多余空格
int32_t count = 0;···
char buf[256];→→→

// 函数参数后有多余空格
void init( int32_t mode );···

// 行尾注释前有多余空格
int32_t x = 0;··// 初始化
```

**正确示例：**
```c
// 空行无任何字符
int32_t foo(void) {

    return 0;

}

// 语句末尾无多余空格
int32_t count = 0;
char buf[256];

// 函数参数紧凑
void init(int32_t mode);

// 注释前保留一个空格
int32_t x = 0; // 初始化
```

**说明：**
- 空行必须完全为空（无任何可见或不可见字符）
- 代码行末尾不得有空格或Tab
- 建议使用编辑器配置自动删除尾随空白
- Git diff 时会标记尾随空白为红色，提交前应检查
- 配置 `.editorconfig` 或 IDE 设置自动处理

---

### 23. 资源释放接口返回类型规范

资源释放接口（`xxx_free`）和资源停止接口（`xxx_stop`）统一返回 `void` 类型，不返回错误码。

**错误示例：**
```c
// 错误：释放接口返回int
int wifi_service_free(wifi_service_s **service);

// 错误：停止接口返回int
int32_t media_player_stop(media_player_s *player);

// 错误：返回bool表示成功
bool buffer_free(buffer_s *buf);
```

**正确示例：**
```c
// 正确：释放接口返回void
void wifi_service_free(wifi_service_s **service);

// 正确：停止接口返回void
void media_player_stop(media_player_s *player);

// 正确：其他释放/停止接口也返回void
void thread_pool_free(thread_pool_s **pool);
void decoder_stop(decoder_s *dec);
void config_free(config_s **cfg);
```

**说明：**
- `xxx_free()` 函数：用于释放资源，统一返回 `void`
- `xxx_stop()` 函数：用于停止操作，统一返回 `void`
- 原因：
  - 释放/停止操作失败时通常无法恢复
  - 调用者通常不检查返回值
  - 内部错误应在函数内部记录日志
  - 如果资源为空指针，应安全返回不报错
- 双指针参数（`**`）的释放函数，释放后将指针置为 `NULL`

---

## 规范检查清单

在提交代码前，请确认：

- [ ] 所有单行if/for/while都有花括号
- [ ] 所有变量使用前已初始化（特别是字符串用memset）
- [ ] 所有错误分支都有日志打印
- [ ] 内存分配失败要有日志打印
- [ ] 释放函数使用free命名
- [ ] 字符串操作使用snprintf
- [ ] C代码使用小写+下划线命名
- [ ] C++代码函数和变量使用小写+下划线，类名使用大写首字符+驼峰式
- [ ] C++私有成员变量添加m_前缀
- [ ] 枚举值有统一前缀，类型名以_e结尾
- [ ] 结构体有统一前缀，类型名以_t结尾
- [ ] API前缀与文件名一致
- [ ] 日志使用utils_log*且内容小写
- [ ] 代码保持紧凑，避免过多空行
- [ ] 所有局部变量定义时初始化
- [ ] 头文件API声明之间无空行，按调用顺序排列
- [ ] 头文件尾部 `__cplusplus` 闭合与 `#endif` 之间无空行
- [ ] C源文件函数定义顺序与头文件声明顺序一致
- [ ] Git提交符合规范格式
- [ ] 不修改的函数参数添加const修饰
- [ ] 禁止使用using namespace，使用完全限定名
- [ ] 头文件包含使用文件名，禁止多级路径
- [ ] 头文件保护宏使用双下划线格式 `__XXX_H__`
- [ ] 代码中无尾随空白（空格/Tab）
- [ ] 资源释放和停止接口返回void