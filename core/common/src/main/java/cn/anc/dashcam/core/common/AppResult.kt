package cn.anc.dashcam.core.common // 归属于设置与通用模块的通用包路径

sealed interface AppResult<out T> { // 声明一个带型参协变参数 out T 的密封接口 AppResult，用于统一底层数据回调的载体
    data class Success<T>(val value: T) : AppResult<T> // Success 子类实现：承载成功的业务数据实例值 value of T
    data class Error(val throwable: Throwable) : AppResult<Nothing> // Error 子类实现：承载抛出的失败 Throwable 指针栈，并声明其协变泛型参数为 Nothing
} // 密封接口定义闭合

fun normalizeBrandCode(value: String): String { // 为特定包或反馈功能提供的一个通用的将原始字符串格式化清洗品牌字符串的方法
    return value.trim().lowercase() // 首先切除并洗空前后可能存在的白格和无意义的回车字符，然后统一以小写形式转化回传
} // 该顶层独立函数结束
