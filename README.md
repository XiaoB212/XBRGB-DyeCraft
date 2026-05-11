# DyeCraft（全彩工艺）

> 一个同时支持 **Fabric** 和 **NeoForge** 的 Minecraft 模组，为游戏带来 RGB 全彩染色系统、染料植物、染色台等丰富内容。

- **哔哩哔哩**：[春风溢于华夏](https://space.bilibili.com/354332275)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## ✨ 功能特性

- **🎨 全彩染色**：支持石块、玻璃、混凝土、陶瓦、羊毛共 5 种可染色方块，任意 RGB 颜色随意染。
- **🖥️ 染色台 GUI**：提供直观的交互界面，支持 HEX / RGB / HSB 输入、颜色预览、批量染色（每 16 块消耗 1 组染料）、预设颜色记忆、红石触发自动染色。
- **🌱 染料作物**：红、绿、蓝三种染料花，可种植、收获，骨粉催熟，破坏收获种子与植物。
- **📦 漏斗与自动化**：染色台支持顶部输入、底部输出、侧边输入染料，完整适配漏斗与红石自动化。
- **↩️ 撤销染色**：染色台支持最多 5 步撤销，误操作可快速回退。
- **📟 Jade 集成**：准星指向染色方块时，会显示其颜色信息（HEX、RGB、HSB）。
- **🌍 多语言支持**：完整的中文、英文翻译。
- **⚙️ 模组配置**：提供游戏内配置界面（NeoForge 版），可自定义参数。
- **📦 完美堆叠**：合成、挖掘、创造模式中，所有可染色方块均可完美堆叠。

## 📋 版本支持与依赖

DyeCraft 同时提供 **Fabric** 和 **NeoForge** 版本，请根据你的游戏客户端选择对应的 JAR 文件。

| 平台 | Minecraft 版本 | 加载器版本 | 核心 API 依赖 | Java 版本 |
|------|---------------|-----------|----------------|-----------|
| Fabric | 1.21.1 | Fabric Loader ≥ 0.19.2 | Fabric API ≥ 0.116.11+1.21.1 | **21 推荐** |
| NeoForge | 1.21.1 | NeoForge ≥ 21.1.0 | (内置) | **21 推荐** |

> 可选模组：安装 **Jade**（Fabric 版需 `jade`，NeoForge 版需 `jade`）可在游戏中查看方块颜色信息。

## 🚀 快速开始

### 获取模组

- 在 [Modrinth](https://modrinth.com) 或 [CurseForge](https://www.curseforge.com) 上搜索 **DyeCraft**，下载对应平台的最新版本。
- 或者从 [GitHub Releases](https://github.com/XiaoB212/XBRGB-DyeCraft/releases) 手动下载。

#### 安装到客户端

1. 下载与你的 **Minecraft 加载器** 对应的 DyeCraft JAR（文件名包含 `fabric` 或 `neoforge`）。
2. 将 JAR 文件放入客户端的 `mods` 文件夹。
3. 对于 Fabric 版本，还需将 **Fabric API** 放入 `mods` 文件夹。
4. 启动游戏，享受全彩工艺！

### 从源码构建

`bash
# 1. 克隆仓库
git clone https://github.com/XiaoB212/XBRGB-DyeCraft.git
cd XBRGB-DyeCraft

# 2. 切换到对应平台分支（默认 main 为 Fabric，neoforge 分支为 NeoForge）
git checkout neoforge   # 如果构建 NeoForge 版本

# 3. 清理并构建
./gradlew clean build

# 4. 构建产物位于 build/libs/，文件名包含平台标识
`

## 📖 详细使用指南

### 🔧 合成配方

#### RGB 混色染料

| 合成方式 | 材料 | 产物 |
|----------|------|------|
| 无序合成 | 红色染料 + 绿色染料 + 蓝色染料 | 1 个 RGB 混色染料 |

#### 染料种子

| 种子类型 | 合成材料 | 摆放方式 |
|----------|----------|----------|
| 红色染料种子 | 虞美人 + 红色染料 + 小麦种子 | 3×3 十字形 |
| 蓝色染料种子 | 矢车菊 + 蓝色染料 + 小麦种子 | 3×3 十字形 |
| 绿色染料种子 | 铃兰 + 绿色染料 + 小麦种子 | 3×3 十字形 |

#### 可染色方块

**核心配方（以可染色石块为例）：**

`SSS
SDS
SSS`

- `S` = 平滑石英块
- `D` = RGB 混色染料

**产物**：8 个可染色石块（白色，可后续染色）

**其他变体**：将 `S` 替换为对应材料即可：

| 方块类型 | 核心材料 |
|----------|----------|
| 可染色玻璃 | 白色染色玻璃 |
| 可染色混凝土 | 白色混凝土 |
| 可染色陶瓦 | 白色陶瓦 |
| 可染色羊毛 | 白色羊毛 |

> 合成产物自带 `Color` 组件，初始颜色为白色（#FFFFFF），可与任何白色可染色方块堆叠。

#### 染色台

`DCD
OWO
OOO`

- `C` = 钻石
- `D` = RGB 混色染料
- `O` = 哭泣的黑曜石
- `W` = 工作台

#### 植物分解

| 材料 | 产物 |
|------|------|
| 红色染料植物 | 2 个红色染料 |
| 蓝色染料植物 | 2 个蓝色染料 |
| 绿色染料植物 | 2 个绿色染料 |

### 🌻 染料花种植

1. 将染料种子右键耕地上即可种植。
2. 使用骨粉催熟（生长阶段 0→1→2→3，约 2~3 次成熟）。
3. 成熟后**只能通过破坏来收获**：
    - 未成熟破坏：掉落 1 个种子。
    - 成熟破坏：掉落 1 个对应染料植物 + 3 个种子。
4. 染料植物可用于合成原版染料，种子和植物均可放入堆肥桶（种子 40% 概率，植物 70% 概率）。

### 🖥️ 染色台使用教程

1. **放入方块**：在顶部 3 个输入槽中放入可染色方块。
2. **放入染料**：在下方的红、绿、蓝染料槽中放入对应原版染料（可通过侧边漏斗输入）。
3. **调整颜色**：
    - 直接输入 HEX 值（自动补 `#`）
    - 单独调整 R、G、B 数值
    - 调整 H、S、B（亮度）数值
4. **点击「染色」**：系统会根据方块数量自动消耗染料（每 16 个方块消耗红、绿、蓝染料各 1 个），染色后的物品自动移至底部的 3 个输出槽（可通过下方漏斗抽出）。
5. **设置预设**：点击「预设」按钮将当前颜色保存为默认颜色。之后每次打开染色台，界面会自动显示该颜色。
6. **撤销操作**：若误操作，点击「撤销」按钮恢复至上一次染色前的状态（最多 5 步），界面会显示成功或失败提示。
7. **红石自动化**：染色台接收到红石信号时，若输入槽有物品且染料充足，会自动按默认颜色进行染色。

### 📟 Jade 信息显示

安装 Jade 后，准星指向任何可染色方块，屏幕上方会显示：

`可染色玻璃 #FF0000
RGB: 255, 0, 0
HEX: #FF0000
HSB: 0°, 100%, 100%`

## ❓ 常见问题

### 模组无法加载或缺少依赖

- **Fabric 版本**：请确保已安装 **Fabric API** 且版本匹配。
- **NeoForge 版本**：无需额外 API，但请确认 NeoForge 版本 ≥ 21.1.0。
- 若使用 Jade，请下载对应平台版本。

### 启动客户端时提示 `Unrecognized option: --add-opens`

Gradle 守护进程可能使用了 Java 25+，而 `--add-opens` 参数在 JDK 25 中已被移除。

**解决方法**：

1. 停止所有守护进程：
   `bash
   ./gradlew --stop
   `
2. 确保 `JAVA_HOME` 指向 JDK 21：
   `bash
   # Windows
   set JAVA_HOME=D:\Java\jdk-21
   # macOS / Linux
   export JAVA_HOME=/path/to/jdk-21
   `
3. 重新运行：
   `bash
   ./gradlew runClient
   `

## 📦 跨平台开发

本仓库通过不同分支维护两个平台的代码：

- **`main`**：Fabric 版本
- **`neoforge`**：NeoForge 版本

核心代码保持高度一致，配方、语言文件和贴图等资源在两个平台间共享。

如需参与开发，请根据目标平台切换到对应分支。

## 📞 联系方式

- **哔哩哔哩**：[春风溢于华夏](https://space.bilibili.com/354332275)
- **GitHub Issues**：https://github.com/XiaoB212/XBRGB-DyeCraft/issues
- 欢迎提交 Bug 反馈和功能建议

## 📄 License / 许可证

Copyright [2026] [XiaoB212]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

---

**中文参考译文**  
本软件以 Apache License 2.0（Apache 许可证 2.0 版）授权发布。  
版权所有 © 2026 XiaoB212。  
在遵守本许可证的前提下，你可以自由使用、修改和分发本软件。  
完整的许可证文本请参阅项目根目录下的 `LICENSE` 文件。

---

⭐ 如果你喜欢这个模组，欢迎在 GitHub 上点亮 Star！