# Changelog (NeoForge) / 更新日志 (NeoForge)

## [v1.1.0] - 2026-05-10

### Added / 新增
- Initial NeoForge release based on Fabric v1.1.0.  
  基于 Fabric v1.1.0 的首个 NeoForge 版本。
- Dyeing Station overhaul with input/output separation, hopper support, redstone dyeing.  
  染色台重构：输入/输出分离、漏斗支持、红石染色。
- Preset color button with automatic loading.  
  预设颜色按钮，自动加载保存的默认颜色。
- Built-in configuration GUI accessible from Mods menu (NeoForge exclusive).  
  通过模组菜单可打开的配置界面（NeoForge 专属）。
- Full English and Chinese translations.  
  完整中英文翻译。

### Fixed / 修复
- Fixed mature dye flowers accepting non-bone-meal right-click interactions.  
  修复成熟染料花仍可与非骨粉物品交互的漏洞。

### Changed / 变更
- Fully grown dye flowers cannot be harvested by right-click; must be broken. Bone meal only works on immature crops.  
  成熟染料花无法右键收获，必须破坏获取；骨粉仅对未成熟作物有效。
- Adjusted compost chances: Dye Seeds 40%, Dye Plants 70%.  
  堆肥概率调整：染料种子 40%，染料植物 70%。
- Undo is now limited to the last dye operation only; closing the interface automatically clears the undo record.
  撤销功能改为仅可撤销最近一次染色，关闭界面自动清除撤销记录。

## [v1.0.0] - 2026-04-01
### Added / 新增
- Initial release with RGB mixed dye, dyeable blocks (stone, glass, concrete, terracotta, wool).  
  首次发布：RGB混色染料、5种可染色方块。
- Dyeing Station GUI with HEX/RGB/HSB input and batch dyeing.  
  染色台GUI（HEX/RGB/HSB输入、批量染色）。
- Dye crops (red, blue, green) with seeds and plants.  
  染料作物（红、蓝、绿）及种子、植物。
- Bone meal growth acceleration.  
  骨粉催熟。
- Jade compatibility to show block colors.  
  Jade兼容显示方块颜色。
- Undo feature (up to 5 steps).  
  撤销染色（最多5步）。
- Full English and Chinese translations.  
  完整中英文翻译。