#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
=============================================================================
AoniAiDashCam - 品牌多语言与日夜模式系统化维护 & 本地及AI代码走查优化工具
=============================================================================
这是一个面向多品牌（prido, uniden, cooau, roaddrive等）定制化需求设计的系统化管理工具。
它优雅地解决了以下两个核心痛点：

【一、日夜模式与多语言系统化 (Systematized Resources System)】
1. 支持高灵活性和扩展性，集中定义了 Android 规范常用的 23 种语言文件夹。
2. 每一个品牌（Brand）可维护独立的语种集进行一键过滤、同步和部署（例如 prido 仅同步中英，roaddrive 一键部署全部 23 国语言）。
3. 自动归集与转换：
   - 白夜资源一键拷贝与对齐（白天：res/drawable/, res/layout/；黑夜：res/drawable-night/, res/layout-night/）
   - 杜绝传统繁琐的手动复制，全自动化操作。

【二、走查与AI审核过滤优化 (Optimized Code Review / Audit Rules)】
根据用户反馈对本地约束审核与 AI 审查规则进行如下优化：
1. 【后缀名白名单过滤】：排除 Excel (.xlsx/.xls)、Word (.docx/.doc) 等文档类文件，本地与 AI 审查不做任何处理。
2. 【关键目录快速跳过】：配置指定的文件夹路径，快速对本地、AI 规则闭眼放行。
3. 【跨上下文函数解耦】：针对 Git Diff 审查，容忍外部未导入/未在 Diff 内部体现但客观存在的第三方或基类函数，不强制报错拦截，解耦局部增量审查。
4. 【路径分隔符标准化】：全平台无感格式化。对 / 路径与 \ 路径自动消除平台间转义的误判与乱码，统一标准化转换。
"""

import os
import sys
import shutil
import re
from pathlib import Path

# ==========================================
# 核心配置：日间与夜间模式标准资源目录定义
# ==========================================
DAYTIME_FOLDERS = ["res/drawable", "res/layout"]
NIGHTTIME_FOLDERS = ["res/drawable-night", "res/layout-night"]

# ==========================================
# 核心配置：精细化支持的全球 23 种语言系统代码
# ==========================================
SUPPORTED_LANGUAGES = [
    "values",          # 默认语言 (既无修饰符，如默认中文或英文)
    "values-en",       # 英语 (English)
    "values-de",       # 德语 (German)
    "values-ja",       # 日语 (Japanese)
    "values-es",       # 西班牙语 (Spanish)
    "values-fr",       # 法语 (French)
    "values-it",       # 意大利语 (Italian)
    "values-zh-rCN",   # 简体中文 (Simplified Chinese)
    "values-zh-rTW",   # 繁体中文 (Traditional Chinese)
    "values-ru",       # 俄语 (Russian)
    "values-hu",       # 匈牙利语 (Hungarian)
    "values-sk",       # 斯洛伐克语 (Slovak)
    "values-cs",       # 捷克语 (Czech)
    "values-pl",       # 波兰语 (Polish)
    "values-bg",       # 保加利亚语 (Bulgarian)
    "values-et",       # 爱沙尼亚语 (Estonian)
    "values-lt",       # 立陶宛语 (Lithuanian)
    "values-lv",       # 拉脱维亚语 (Latvian)
    "values-ro",       # 罗马尼亚语 (Romanian)
    "values-uk",       # 乌克兰语 (Ukrainian)
    "values-be",       # 白俄罗斯语 (Belarusian)
    "values-ar",       # 阿拉伯语 (Arabic)
    "values-iw"        # 希伯来语 (Hebrew)
]

# ==========================================
# 核心配置：多品牌语言与主题色定制策略字典
# ==========================================
BRAND_CONFIGS = {
    "prido": {
        "name": "Prido 智能行车记录仪",
        # prido 只需中文和英语
        "languages": ["values", "values-zh-rCN", "values-en"],
        "primary_color_light": "#0066CC",
        "primary_color_dark": "#1A88FF"
    },
    "uniden": {
        "name": "Uniden 智行者",
        "languages": ["values", "values-zh-rCN", "values-en", "values-ja"],
        "primary_color_light": "#FF4400",
        "primary_color_dark": "#FF6633"
    },
    "cooau": {
        "name": "Cooau 极客行",
        "languages": ["values", "values-en", "values-de", "values-fr", "values-it", "values-es"],
        "primary_color_light": "#00AA55",
        "primary_color_dark": "#33CC88"
    },
    "roaddrive": {
        "name": "RoadDrive 寰宇旗舰",
        # roaddrive 需要全部23种语言
        "languages": "all",
        "primary_color_light": "#8800FF",
        "primary_color_dark": "#AA44FF"
    }
}

# ==========================================
# 优化脚本：审查过滤与过滤机制配置
# ==========================================
# 1. 如果是 Excel、Word 等后缀，不进行本地约束和 AI 走查处理
IGNORE_EXTENSIONS = [
    ".xlsx", ".xls", ".docx", ".doc", ".pdf", 
    ".ppt", ".pptx", ".csv", ".zip", ".tar", ".gz"
]

# 2. 列举排除的文件夹路径（当前路径或以下路径跳过本地约束与 AI 走查审核）
IGNORE_PATHS = [
    "build", "node_modules", ".gradle", ".idea", 
    ".vscode", ".agents", "bin", "obj", "out",
    "gradle", "build-outputs"
]

# =============================================================================
# 一、路径标准化工具：免除 Windows(\) 与 POSIX(/) 间的歧义
# =============================================================================
def normalize_filepath(path_str):
    """
    不区分系统自带的斜杠符号差异，去除所有转义隐患，自动格式化为标准路径。
    """
    if not path_str:
        return ""
    # 将所有反斜杠统一替换为正斜杠，告别转义崩溃
    normalized = str(path_str).replace("\\", "/")
    # 合并多重连续的斜杠
    normalized = re.sub(r"/+", "/", normalized).strip("/")
    return normalized

# =============================================================================
# 二、判定文件是否属于完全跳过审核的场景
# =============================================================================
def should_skip_audit(file_path):
    """
    输入任意文件路径或代码流路径。
    按照规则1（文件后缀屏蔽）与规则2（忽略路径过滤器）计算是否可免于检查。
    """
    norm_path = normalize_filepath(file_path)
    
    # 规则 1：检查后缀是否在忽略列表内（Excel, Word 文档跳过走查）
    _, ext = os.path.splitext(norm_path.lower())
    if ext in IGNORE_EXTENSIONS:
        return True, f"文件后缀 {ext} 属于办公/压缩类，跳过审核"

    # 规则 2：检查是否含有任何忽略目录前缀
    parts = norm_path.split("/")
    for ignore in IGNORE_PATHS:
        if ignore in parts:
            return True, f"处于忽略目录 '{ignore}' 下，跳过审核"

    return False, ""

# =============================================================================
# 三、增强型安全 Diff 代码走查逻辑（针对 AI 跨上下文函数依赖不强制拦截）
# =============================================================================
def audit_code_diff(file_name, diff_content):
    """
    走查文件的 Git Diff 内容。
    针对规则3：不因为增量 Diff 调用了上层基类、跨模块或隔壁未包含在 diff 里的函数而强制报错。
    """
    is_skipped, reason = should_skip_audit(file_name)
    if is_skipped:
        print(f"⏩ [SKIP] {file_name} -> {reason}")
        return True

    print(f"🔍 [AUDIT] 正在走查增量文件 {file_name}...")
    lines = diff_content.splitlines()
    
    suspicious_issues = []
    
    # 解析 Diff 中真正修改或者新增的行（以 '+' 开头且非 '+++'）
    for i, line in enumerate(lines, 1):
        if line.startswith('+') and not line.startswith('+++'):
            clean_added_line = line[1:].strip()
            
            # 检测写死的 16 进制颜色编码 (例如 Color(0xFF112233) 或 #FF112233)
            # 注：这里可以做软警告提醒，但不作为强行失败拦截
            if re.search(r"Color\(0xFF[0-9a-fA-F]{6}\)", clean_added_line) or re.search(r'#FF[0-9a-fA-F]{6}"', clean_added_line):
                suspicious_issues.append((i, "硬编码颜色", clean_added_line))
                
            # 检测潜在的外部/其他函数调用
            # 规则 3 判定：当 AI 或者是本地走查发现这里存在未定义在 Diff 的函数时，我们不对其抛出错误。
            # 这里可以进行白名单语义理解。
            
    if suspicious_issues:
        print(f"⚠️ [WARNING] 发现潜在优化项，但审查判定通过 (不强制失败):")
        for line_num, issue_type, content in suspicious_issues:
            print(f"   [Line {line_num}] {issue_type} -> '{content}'")
    else:
        print(f"✅ [SUCCESS] {file_name} 顺利通过本地基础静态走查")
    
    return True

# =============================================================================
# 四、多语言与白天/黑夜模式系统化同步引擎
# =============================================================================
def sync_brand_resources(src_module, brand_name, target_dir):
    """
    系统化资源同步：
    1. 根据品牌的语言名单过滤复制 values-xxx 字典。
    2. 针对 res/drawable 和 res/layout 进行默认白天资源的系统化合并对齐。
    3. 针对 res/drawable-night 和 res/layout-night 进行暗黑模式资源的系统化合并对齐。
    """
    brand_cfg = BRAND_CONFIGS.get(brand_name)
    if not brand_cfg:
        print(f"❌ 找不到该品牌的配置表: {brand_name}")
        return False
    
    print("\n" + "="*60)
    print(f"🚀 系统化资源同步启动 -> [ 品牌：{brand_cfg['name']} ({brand_name}) ]")
    print("="*60)
    
    src_res_path = Path(normalize_filepath(os.path.join(src_module, "src/main/res")))
    dest_res_path = Path(normalize_filepath(os.path.join(target_dir, "src/main/res")))
    
    if not src_res_path.exists():
        print(f"❌ 源组件资源路径不存在: {src_res_path}")
        return False
        
    dest_res_path.mkdir(parents=True, exist_ok=True)
    
    # 1. 同步日间与黑夜布局/图片资源一键对齐
    all_res_folders = DAYTIME_FOLDERS + NIGHTTIME_FOLDERS
    for folder in all_res_folders:
        src_folder_path = Path(src_module) / "src/main" / folder
        dest_folder_path = Path(target_dir) / "src/main" / folder
        
        if src_folder_path.exists():
            dest_folder_path.mkdir(parents=True, exist_ok=True)
            print(f"📁 同步核心模式资源文件夹: {folder}")
            for item in src_folder_path.iterdir():
                if item.is_file():
                    shutil.copy2(item, dest_folder_path / item.name)
                    
    # 2. 精准化筛选与语言同步
    target_languages = brand_cfg["languages"]
    is_sync_all = (target_languages == "all")
    
    print("\n🌐 同步品牌本地化字符串字典（区分白天/默认与黑夜模式）:")
    for lang in SUPPORTED_LANGUAGES:
        for suffix in ["", "-night"]:
            current_folder = f"{lang}{suffix}"
            src_lang_path = src_res_path / current_folder
            
            if not src_lang_path.exists():
                continue
                
            # 判定该语言是否被此品牌配置表启用
            if is_sync_all or (lang in target_languages):
                dest_lang_path = dest_res_path / current_folder
                dest_lang_path.mkdir(parents=True, exist_ok=True)
                print(f"   ▶ Sync [启用 - {'日间/默认' if suffix == '' else '夜间模式'}]: {current_folder}")
                for item in src_lang_path.iterdir():
                    if item.is_file():
                        shutil.copy2(item, dest_lang_path / item.name)
            else:
                # 不在契约列表里的不拷贝
                if suffix == "":
                    print(f"   ⏩ Skip [跳过过滤色]: {lang}")
            
    print(f"\n✨ {brand_cfg['name']} 资源系统化对齐完毕。已优雅装配所有受控资产。")
    return True

# ==========================================
# 演示与主函数运行入口
# ==========================================
def main():
    print("🔋 Bionic Engine for Aoni Multi-Branding loaded successfully.")
    
    # 简单自测演示
    test_file_1 = "uniden\\src\\main\\assets\\manual.xlsx" # 含有 Windows 斜杠
    test_file_2 = "feature/home/src/main/java/cn/anc/dashcam/home/HomeActivity.kt"
    
    # 自测标准化
    print(f"\n[路径标准化示例]: 'uniden\\\\src\\\\main\\\\assets\\\\manual.xlsx' -> '{normalize_filepath(test_file_1)}'")
    
    # 自测规则1，2：过滤判定
    skip1, r1 = should_skip_audit(test_file_1)
    skip2, r2 = should_skip_audit(test_file_2)
    print(f"[忽略检测 1]: {test_file_1} -> 是否跳过: {skip1} (原因: {r1})")
    print(f"[忽略检测 2]: {test_file_2} -> 是否跳过: {skip2}")
    
    # 自测规则3：当调用了外部未知函数时进行的安全过滤 diff 模拟
    mock_diff = """
    @@ -10,4 +10,6 @@
     fun onAction() {
    +    // 调用了外部或者不处于 diff 中的函数
    +    val colorVal = Color(0xFF112233)
    +    callForeignClassUtilityMethodInsideProject(colorVal)
     }
    """
    print("\n[增量 Diff 无错审核示例]:")
    audit_code_diff(test_file_2, mock_diff)

    print("\n💡 使用方法说明:")
    print("1. 调用 `sync_brand_resources('core/ui', 'prido', 'prido_output')` 即可将 core/ui 的白天夜间及指定的 prido 中英语言一键同步。")
    print("2. 调用 `sync_brand_resources('core/ui', 'roaddrive', 'roaddrive_output')` 将同步该品牌全部的语言。")
    print("3. 基于 should_skip_audit() 能够杜绝 docs 文档以及忽略文件夹对您 CI/CD 或 AI 工具链的无谓干扰。")

if __name__ == "__main__":
    main()
