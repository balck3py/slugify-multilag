# Slugify.MultiLang

> 面向 .NET 的多语言、零依赖 slug 生成器 —— 流行 JavaScript 库 [`slugify`](https://github.com/simov/slugify) 的忠实 C# 移植版，扩展了 locale 支持，并对 CJK、阿拉伯文等非拉丁文字做了合理处理。

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![.NET Standard 2.0](https://img.shields.io/badge/.NET%20Standard-2.0-512BD4.svg)](https://learn.microsoft.com/dotnet/standard/net-standard)

## 它能做什么

把任意字符串转换成干净、URL 安全的 slug：

```
"Director Fu: Your alt account got exposed again!"  →  "director-fu-your-alt-account-got-exposed-again"
"Café au lait & cròissant"                          →  "cafe-au-lait-and-croissant"
"傅总：你的马甲 又又又掉了！"                          →  "傅总-你的马甲-又又又掉了"
```

它会把带音标的拉丁字母、希腊文、西里尔文、亚美尼亚文、格鲁吉亚文、越南文等音译为 ASCII，把常见符号和货币符号转成单词，应用各语言专属规则，并保留那些没有合理罗马化方案的文字（CJK、阿拉伯文、天城文、泰文……），从而让 slug 在任何语言下都保持可读。

## 特性

- **零依赖** —— 单个静态帮助类，目标框架 **.NET Standard 2.0**（适用于 .NET Framework 4.6.1+、.NET Core、.NET 5–8+、Mono、Xamarin、Unity）。
- **庞大的内置字符表** —— 拉丁文（含扩展）、希腊文、西里尔文（含哈萨克）、亚美尼亚文、格鲁吉亚文、越南文、货币、标点以及数学/杂项符号。
- **locale 感知** —— 针对 `bg`、`de`、`es`、`fr`、`pt`、`uk`、`vi`、`da`、`nb`、`it`、`nl`、`sv` 提供按语言的覆盖规则（如德语 `ä → ae`、`& → und`）。
- **保留文字** —— CJK / 阿拉伯文 / 天城文 / 泰文等字符原样保留，而非丢弃。
- **可配置** —— 替换字符、是否转小写、严格模式、是否裁剪空白，以及自定义移除正则。
- **可扩展** —— 通过 `Extend` 在运行时注册自定义字符映射。

## 技术原理

每个字符串都经过固定的五步流水线处理：

1. **NFC 归一化** —— 先对输入做 Unicode 规范化（NFC），使预组合形式与分解形式保持一致。
2. **逐字符转换** —— 对每个字符（含 BMP 外的代理对）按以下顺序查表：
   1. *locale 覆盖表* —— 若设置了 `Locale` 且该 locale 对此字符有映射。
   2. *全局字符表* —— 拉丁扩展、希腊文、西里尔文、亚美尼亚文、格鲁吉亚文、越南文、货币符号、数学符号、CJK 标点……
   3. *原样通过* —— 查无此字符则保持不变。
3. **移除遍** —— 用内置（或自定义 `Remove`）正则去掉不适合出现在 URL 中的字符。
4. **严格遍**（默认 `Strict = true`）—— 正则 `[^\p{L}\p{N}\s]` 移除所有非 Unicode 字母（`\p{L}`）、数字（`\p{N}`）、空白的字符。  
   非拉丁文字之所以被**保留**，正是靠这一步：阿拉伯字母、CJK 汉字、韩文、假名、泰文辅音、天城文辅音都满足 `\p{L}`，得以保全。
5. **收尾** —— 连续空白折叠为替换字符（默认 `-`）；可选地进行裁剪和转小写。

### 各文字的处理策略

| 策略 | 文字 / locale | 说明 |
|---|---|---|
| **全量字符表音译** | 拉丁扩展、希腊文、西里尔文（含哈萨克）、亚美尼亚文、格鲁吉亚文 | 非 ASCII 字母 → ASCII（如 `é→e`、`Ж→Zh`、`Ñ→N`） |
| **locale 专属覆盖** | `de`, `es`, `fr`, `pt`, `it`, `nl`, `sv`, `da`, `nb`, `bg`, `uk`, `vi` | 在全局字符表之前应用语言正确的映射（如 `de`：`ü→ue`、`&→und`；`sv`：`ö→oe`、`&→och`；`da`：`ø→oe`、`å→aa`） |
| **字符表兜底** | `pl`, `tr`, `ms`, `id`, `tl` 及其他未注册 locale | 仅使用全局字符表；表中没有的变音符号透传后被严格遍移除 |
| **文字原样保留** | 阿拉伯文、中文、日文、韩文、泰文、天城文 | 无字符表映射，字母经 `\p{L}` 原样保留；CJK 标点（`，`、`。`、`！`、`？`、`：`、`「」`、`【】`、`—`、`·` 等）映射为空格，最终变成分隔符 |

### 泰文与天城文（印地语）—— 组合符号被移除

泰文基础辅音和天城文基础辅音都是 Unicode 字母（`\p{L}`），能完整保留。
但它们的组合元音符号和声调符号属于 Unicode *组合标记*（`\p{M}`）——例如泰文的 `้` `ู` `็`，以及天城文的 `ि` `ा` `ी`。
在默认的 `Strict = true` 模式下，这些标记会被移除，slug 中只剩辅音骨架。
如需保留完整的泰文 / 天城文音节，请将 `Strict` 设置为 `false`。

### 阿拉伯文 —— 字母保留，短元音符号丢弃

阿拉伯字母满足 `\p{L}`，全部保留。哈拉卡特（短元音符号，如 `َ` `ِ` `ُ`）属于组合标记（`\p{M}`），在严格模式下会被移除——这对 URL slug 来说完全合理，阿拉伯语 URL 中本就通常省略这些符号。

## 安装

本仓库直接提供源码。可添加对 `csharp/src/Slugify.MultiLang/Slugify.MultiLang.csproj` 的项目引用，或直接把 `SlugifyHelper.cs` 与 `SlugifySlugOptions.cs` 拷进你的项目。

```xml
<ItemGroup>
  <ProjectReference Include="path/to/Slugify.MultiLang/Slugify.MultiLang.csproj" />
</ItemGroup>
```

## 用法

```csharp
using Slugify.MultiLang;

// 扩展方法写法（默认选项）
string slug = "Hello, World!".Slugify();
// → "hello-world"

// 带选项的静态调用
string s = SlugifyHelper.Slugify("Müller & Söhne", new SlugifySlugOptions
{
    Locale = "de"   // 德语：ü → ue，ö → oe，& → und
});
// → "mueller-und-soehne"

// 自定义替换字符
"Some Text".Slugify("_");
// → "some_text"
```

### 选项

所有选项都在 `SlugifySlugOptions` 上：

| 选项          | 类型      | 默认值  | 说明                                               |
| ------------- | -------- | ------- | -------------------------------------------------- |
| `Replacement` | `string` | `"-"`   | 用于替换空白和分隔符的字符。                          |
| `Lower`       | `bool`   | `true`  | 将结果转为小写（不变区域性）。                        |
| `Strict`      | `bool`   | `true`  | 移除所有非字母、数字、空白的字符。                    |
| `Trim`        | `bool`   | `true`  | 拼接前裁剪首尾空白。                                 |
| `Locale`      | `string?`| `null`  | 用于应用语言专属覆盖规则的 locale 代码。             |
| `Remove`      | `Regex?` | `null`  | 自定义需移除字符的正则（覆盖默认移除模式）。          |

### 扩展字符表

```csharp
SlugifyHelper.Extend(new Dictionary<char, string>
{
    { '☂', "umbrella" },
    { '♛', "queen" },
});

"☂♛".Slugify(); // → "umbrella-queen"
```

## 演示

[`csharp/demo`](./csharp/demo) 下有一个可运行的控制台演示，展示 23 种语言：

```bash
cd csharp
dotnet run --project demo/Slugify.MultiLang.Demo
```

以下所有示例均使用同一个源句——"傅总：你的马甲 又又又掉了！"——翻译成各目标语言后运行的实际输出。

### 有 locale 专属映射

| 语言 | Locale | Slug 输出 |
|---|---|---|
| Español | `es` | `director-fu-tu-cuenta-alternativa-ha-quedado-expuesta-otra-vez-y-otra-vez-y-otra-vez` |
| Português | `pt` | `diretor-fu-sua-conta-alternativa-foi-exposta-de-novo-e-de-novo-e-de-novo` |
| Français | `fr` | `directeur-fu-votre-compte-alternatif-a-ete-expose-encore-et-encore-et-encore` |
| Deutsch | `de` | `direktor-fu-dein-alternativkonto-ist-schon-wieder-und-wieder-und-wieder-aufgeflogen` |
| Italiano | `it` | `direttore-fu-il-tuo-account-alternativo-e-stato-smascherato-ancora-e-ancora-e-ancora` |
| Svenska | `sv` | `direktoer-fu-ditt-alternativa-konto-har-avsloejats-igen-och-igen-och-igen` |
| Dansk | `da` | `direktoer-fu-din-alternative-konto-er-blevet-afsloeret-igen-og-igen-og-igen` |
| Nederlands | `nl` | `directeur-fu-uw-alternatieve-account-is-alweer-en-nog-een-keer-ontmaskerd` |
| Tiếng Việt | `vi` | `giam-doc-phu-tai-khoan-phu-cua-ban-da-bi-lo-lai-va-lai-va-lai` |

> **`vi` 说明：** locale 表仅映射 `Đ/đ → D/d`；越南语丰富的变音字符（`ấ`、`ề`、`ộ` 等）由全局字符表处理。

### 字符表兜底（无专属 locale 映射）

| 语言 | Locale | Slug 输出 |
|---|---|---|
| Polski | `pl` | `dyrektorze-fu-twoje-alternatywne-konto-zostalo-ponownie-i-ponownie-zdemaskowane` |
| Norsk | `no` | `direktor-fu-den-alternative-kontoen-din-har-blitt-avslort-igjen-og-igjen-og-igjen` |
| Türkçe | `tr` | `mudur-fu-sahte-hesabin-yine-yine-yine-desifre-oldu` |
| Bahasa Melayu | `ms` | `pengarah-fu-akaun-tiruan-anda-telah-terdedah-lagi-dan-lagi-dan-lagi` |
| Bahasa Indonesia | `id` | `direktur-fu-akun-samaran-anda-telah-terbongkar-lagi-dan-lagi-dan-lagi` |
| Filipino | `tl` | `direktor-fu-ang-iyong-alternatibong-account-ay-nabunyag-na-naman-at-naman-at-naman` |
| English | `en` | `director-fu-your-alt-account-got-exposed-again-and-again-and-again` |

> **`no` 说明：** 挪威书面语（`nb`）**有**专属 locale 映射（`ø→oe`、`å→aa`、`&→og`）。演示传入的是 `no`，因此走兜底。如需正确的书面挪威语映射，请使用 `Locale = "nb"`。

### 文字原样保留（Unicode 直通）

无字符表映射的非拉丁文字经 `\p{L}` 原样保留：

| 语言 | Locale | Slug 输出 |
|---|---|---|
| العربية | `ar` | `المدير-فو-لقد-تم-كشف-حسابك-البديل-مرة-أخرى-ومرة-أخرى-ومرة-أخرى` |
| 日本語 | `ja` | `傅総-あなたのサブアカウントがまたまたまたバレちゃった` |
| 한국어 | `ko` | `푸-총재-당신의-부계정이-또-또-또-들통났어요` |
| ภาษาไทย | `th` † | `ผอำนวยการฝ-บญชอำพรางของคณถกเปดเผยอกและอกและอกครง` |
| हिन्दी | `hi` † | `नदशक-फ-आपक-वकलपक-खत-फर-और-फर-और-फर-उजगर-ह-गय` |
| 中文 (简体) | `zh` | `傅总-你的马甲-又又又掉了` |
| 中文 (繁體) | `zh-tw` | `傅總-你的馬甲-又又又掉了` |

† 组合元音符号（`\p{M}`）在严格模式下被移除，详见上方[泰文与天城文说明](#泰文与天城文印地语--组合符号被移除)。

## 项目结构

```
csharp/
├── Slugify.MultiLang.slnx              # 解决方案
├── src/Slugify.MultiLang/             # 库本体（netstandard2.0）
│   ├── SlugifyHelper.cs               # 核心逻辑 + 字符表 + locale 映射
│   └── SlugifySlugOptions.cs          # 选项
└── demo/Slugify.MultiLang.Demo/       # 多语言控制台演示（net8.0）
```

## 致谢

字符表与行为衍生自 [simov/slugify](https://github.com/simov/slugify)（MIT 许可）。

## 许可证

基于 [MIT 许可证](./LICENSE) 发布 —— 可自由用于任何用途，包括商业用途，随你折腾。
