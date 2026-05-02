# DyeCraft（全彩工艺）

> 一个基于 Fabric 的 Minecraft 模组，为游戏添加 RGB 全彩染色系统、染料植物、染色台等丰富内容。
 
- **哔哩哔哩**：[春风溢于华夏](https://space.bilibili.com/354332275?spm_id_from=333.1007.0.0)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## ✨ 功能特性

- **🎨 全彩染色**：支持石块、玻璃、混凝土、陶瓦、羊毛共 5 种可染色方块，任意 RGB 颜色随意染。
- **🖥️ 染色台 GUI**：提供直观的交互界面，支持 HEX / RGB / HSB 输入、颜色预览、批量染色（每 16 块消耗 1 组染料）。
- **🌱 染料作物**：红、绿、蓝三种染料花，可种植、收获，骨粉催熟，掉落种子与植物。
- **🌍 多语言支持**：完整的中文、英文翻译。
- **📟 Jade 集成**：准星指向染色方块时，会显示其颜色信息（HEX、RGB、HSB）。
- **↩️ 撤销染色**：染色台支持最多 5 步撤销，误操作可快速回退。
- **📦 完美堆叠**：无论合成、挖掘还是创造模式，所有可染色方块都能完美堆叠。

## 📋 环境要求

| 依赖 | 版本要求 |
|------|----------|
| Minecraft | 1.21.1 |
| Fabric Loader | ≥ 0.19.2 |
| Fabric API | ≥ 0.116.11+1.21.1 |
| Java | **21 推荐**（25+ 亦可，需额外步骤） |
| Jade | 可选，用于查看方块颜色 |

## 🚀 快速开始

### 构建模组

`bash
# 1. 清理并构建模组，生成 JAR 文件
./gradlew clean build

# 2. 若系统默认 Java 版本为 25 或更高，先停止旧守护进程
./gradlew --stop

# 3. 启动 Minecraft 测试客户端（可选）
./gradlew runClient
`

构建完成后，JAR 文件生成于 `build/libs/dyecraft-1.0.0.jar`，将其与 Fabric API 一同放入客户端的 `mods` 文件夹即可。

## 📖 详细使用指南

### 🔧 合成配方

#### RGB 混色染料

| 合成方式 | 材料 | 产物 |
|----------|------|------|
| 无序合成 | 红染料 + 绿染料 + 蓝染料 | 1 个 RGB 混色染料 |

#### 染料种子

| 种子类型 | 合成材料 | 摆放方式 |
|----------|----------|----------|
| 红色染料种子 | 虞美人 + 红染料 + 小麦种子 | 3×3 十字形 |
| 蓝色染料种子 | 矢车菊 + 蓝染料 + 小麦种子 | 3×3 十字形 |
| 绿色染料种子 | 铃兰 + 绿染料 + 小麦种子 | 3×3 十字形 |

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

1. 将染料种子种在耕地上。
2. 使用骨粉催熟（生长阶段 0→1→2→3，约 2~3 次成熟）。
3. 成熟后右键或破坏收获：
   - 掉落 1~3 个对应种子（概率：1 个 40%、2 个 40%、3 个 20%）
   - 掉落 1 个对应染料植物
4. 染料植物可用于合成原版染料，或放入堆肥桶（概率 85%）。

### 🖥️ 染色台使用教程

1. **放入方块**：在左侧 3×3 格子中放入可染色方块。
2. **放入染料**：在下方的红、绿、蓝染料槽中放入对应原版染料。
3. **调整颜色**：
   - 直接输入 HEX 值（自动补 `#`）
   - 单独调整 R、G、B 数值
   - 调整 H、S、B（亮度）数值
4. **点击「染色」**：系统会根据方块数量自动消耗染料（每 16 个方块消耗红、绿、蓝染料各 1 个）。
5. **撤销操作**：若误操作，可点击「撤销」按钮恢复至上一次染色前的状态（最多 5 步），界面会显示成功或失败提示。

### 📟 Jade 信息显示

安装 Jade 后，准星指向任何可染色方块，屏幕上方会显示：

`可染色玻璃 #FF0000
RGB: 255, 0, 0
HEX: #FF0000
HSB: 0°, 100%, 100%`

## ❓ 常见问题

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

### 如何安装到普通客户端？

将 `build/libs/dyecraft-1.0.0.jar` 与 Fabric API 一起放入客户端 `mods` 文件夹，并确保安装了对应版本的 Fabric Loader。

### 开发环境如何使用？

用 IntelliJ IDEA 导入项目，Gradle 会自动配置依赖。运行 `./gradlew genSources` 生成源代码，然后启动客户端调试即可。

## 📞 联系方式

- **哔哩哔哩**：[春风溢于华夏](https://space.bilibili.com/354332275?spm_id_from=333.1007.0.0)
- **GitHub Issues**：欢迎提交 Bug 反馈和功能建议

## 📄 License / 许可证

Copyright [2026] [XiaoB212 of copyright owner]

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