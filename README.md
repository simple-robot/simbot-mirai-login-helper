<div align="center">
    <img src=".simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " />
    <h2>
        - simbot-mirai-login-helper -
    </h2>
    <small>/ Simbot-Mirai 登录验证辅助工具 \</small>
</div>

## 说明

通过使用simbot下内置的设备信息来进行滑动验证的辅助工具。此工具仅用于简化步骤并提供**相对**友好的可视化，但不会提供全自动验证能力。




## 使用

### 安装

#### 方案1 - 下载Release

前往 [releases](https://github.com/simple-robot/simbot-mirai-login-helper/releases) 中选择版本并下载对应平台的安装文件。 下载后在电脑上安装、运行。

#### 方案2 - 自行编译

自行编译源代码并使用。

## 注意事项
如果你在Windows上，需要尝试以**管理员身份**运行。

## 其他说明

#### 版本号

目前版本 `3.x.x` 等同于 `0.x.x`, 请在脑海中自动将版本最前的数字-3。 由于 `macOS`(`dmg` & `pkg`) 打包必须保证版本号符合规则: `MAJOR[.MINOR][.PATCH]` 且:

- `MAJOR` 是大于0的数字;
- `MINOR` 是一个可选的非负整数;
- `PATCH` 是一个可选的非负整数; 因此对于`dmg`和`pkg`文件来说，不能使用最大版本号小于0的版本。因此选择将 `MAJOR` 数字与当前环境下 `simbot` 对应的 `MAJOR` 一致，也就是 `3`。

有关于其他文件的版本说明请参考 [compose-jb/tutorials/Building native distribution/Specifying package version](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution#specifying-package-version)

## License

遵照 [LGPL-3.0](https://www.gnu.org/licenses/lgpl-3.0.html) 协议开源。详情请查看文件 [COPYING](COPYING)
与 [COPYING.LESSER](COPYING.LESSER)。

