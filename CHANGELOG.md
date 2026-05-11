# Changelog (Fabric)

## [v1.1.0] - 2026-05-10
### Added / 新增
- Dyeing Station overhaul: 3 input slots (top), 3 output slots (bottom), dye slots unchanged.  
  染色台界面重构：上方 3 格输入，下方 3 格输出，染料槽位不变。
- Hopper automation: input from above, output from below, dye from sides.  
  漏斗自动化：顶部输入、底部输出、侧面输入染料。
- Redstone dyeing: signal triggers dyeing with preset color.  
  红石染色：收到红石信号时按预设颜色自动染色。
- Preset color button: saves current color as default.  
  预设颜色按钮：保存当前颜色为默认值。
- Full English and Chinese translations for new buttons and messages.  
  新增预设按钮及相关消息的中英文翻译。

### Fixed / 修复
- Fixed mature dye flowers accepting non‑bone‑meal right‑click interactions.  
  修复成熟染料花仍可与非骨粉物品交互的漏洞。

### Changed / 变更
- Fully grown dye flowers can no longer be harvested by right‑click; must be broken to collect items. Bone meal only works on immature crops.  
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