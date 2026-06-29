# 宝可梦对战模拟器 (Pokemon Battle Simulator)

[![Java](https://img.shields.io/badge/Java-23-blue.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

## 项目简介

本项目是一款基于 Java Swing 开发的桌面回合制对战游戏，灵感来源于 Pokemon Showdown 的对战系统。

## 主要功能

- **队伍系统**：从6只宝可梦中选择6只组成队伍，支持首发选择和战斗中换人
- **战斗系统**：速度决定出手顺序，包含属性克制、技能命中率、特殊技能效果（日光束蓄力、自我再生、寄生种子、催眠粉）
- **AI系统**：敌人被克制时主动换人，死亡后自动选择最优属性精灵
- **数据持久化**：SQLite 存储精灵数据，Gson 实现存档读档
- **UI系统**：精灵图片显示、血条颜色变化、实时战斗日志

## 技术栈

- **语言**：Java 23
- **GUI**：Swing
- **数据库**：SQLite (sqlite-jdbc)
- **JSON处理**：Gson
- **构建工具**：IntelliJ IDEA

## 快速开始

### 环境要求
- JDK 23 或更高版本
- Windows / macOS / Linux

### 运行步骤
1. 克隆仓库：
   ```bash
   git clone https://github.com/CHIXES/PokemonBattleSimulator.git
   ```
2. 使用 IntelliJ IDEA 打开项目
3. 运行 `InitDB.java` 初始化数据库
4. 运行 `Main.java` 启动游戏

### 直接运行
前往 [Releases](https://github.com/CHIXES/PokemonBattleSimulator/releases) 下载最新安装包，双击即可安装运行。


## 项目结构

```
PokemonBattleSimulator/
├── src/                    # 源代码
│   ├── model/              # 实体类（Pokemon及子类）
│   ├── dao/                # 数据访问层
│   ├── service/            # 业务逻辑层（BattleService）
│   ├── gui/                # 图形界面（Swing）
│   ├── util/               # 工具类
│   └── exception/          # 自定义异常
├── libs/                   # 第三方库
├── images/                 # 精灵图片
├── pokemon.db              # SQLite数据库
└── README.md
```

## 开源协议

本项目采用 MIT 协议，详情请见 [LICENSE](LICENSE) 文件。
