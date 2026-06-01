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

[`csharp/demo`](./csharp/demo) 下有一个可运行的控制台演示，展示 20+ 种语言：

```bash
cd csharp
dotnet run --project demo/Slugify.MultiLang.Demo
```

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
