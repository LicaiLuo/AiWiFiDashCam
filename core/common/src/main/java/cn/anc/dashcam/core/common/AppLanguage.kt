package cn.anc.dashcam.core.common // 归属于基础公共功能模块 core:common 的统一包装路径

import java.util.Locale // 导入 Java 国际化首选项标准区域 Locale 类

enum class AppLanguage( // 定义本 App 当前所认可的多语言配置枚举类
    val tag: String, // 每一个语种枚举携带的对应 RFC 标准的语言字符串标识 tag
) { // 选项体
    ZH_CN( // 中文字符集 ZH_CN 枚举项
        tag = "zh-CN", // 对应的中国大陆简繁体地区化识别编码
    ), // 结束 ZH_CN 的选项构造
    EN( // 英文字符集 EN 枚举项
        tag = "en", // 对应的基础英语系国标识别编码
    ); // 选项体轮廓线结束

    val locale: Locale // 该选项代表的的 Locale 对象的扩展 getter 属性
        get() = Locale.forLanguageTag(tag) // 基于当前持有的 tag 返回一个对应的 Locale 标准实体包装

    companion object { // 静态工具类承载区
        fun fromTag(tag: String?): AppLanguage? { // 提供快捷的方法，自 tag 字符串反向解码为 AppLanguage 枚举类型
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } // 轮询遍历所有枚举表单，寻找首个长相（忽略字母大小写差异）等效于入参 tag 的关联项
        } // fromTag 方法完结

        fun fromLocale(locale: Locale): AppLanguage { // 提供快捷的方法，将 Java 原生的 Locale 实例智能判定归宿为适合的 AppLanguage 种类
            return if (locale.language.equals("zh", ignoreCase = true)) { // 询问该环境的主语言首字是否为中文 "zh" 字符
                ZH_CN // 相符则恒定降落在 ZH_CN 枚下
            } else { // 其余所有未知或外国语种
                EN // 默认保底重定在首选通行的 EN 英文枚下
            } // 判断完成
        } // fromLocale 转换方法完结
    } // companion static 块结束
} // 枚举类声明全满结束
