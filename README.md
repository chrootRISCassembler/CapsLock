# これは何?

A simple game launcher

ゲームランチャー

## License - ライセンス
![AGPL logo](https://www.gnu.org/graphics/agplv3-155x51.png) 

This project is licensed under [GNU AFFERO GENERAL PUBLIC LICENSE Version 3](https://www.gnu.org/graphics/license-logos.html) - see the [LICENSE](LICENSE) file for details

このプログラムは[AGPLv3](https://www.gnu.org/graphics/license-logos.html)の元で配布されています. 

## How to build - ビルド方法

[Gradle](https://gradle.org/)をインストールし``gradle build``

## ビルド済みの実行可能なJarファイルを入手する.
[jar保管場所](https://github.com/chrootRISCassembler/dependence)にある.

### ビルド済みのJarに対応するソースコードを入手する
1. このリポジトリをローカルに``git clone``する
2. ビルド済みJarからBuildInfo.txtを取りだす.
3. BuildInfo.txt内の``git_commit_hash``の値を確認して``git checkout``

## 備考

登録済みゲームのランチャー上の表示を確認するために,このプログラムを単体で動作させるべきではない.
[登録ツール](https://github.com/chrootRISCassembler/KiddyRegister)のプレビューモードを使用すべき.
