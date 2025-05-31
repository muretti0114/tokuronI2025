# tokuron-docker

データ構造論（2025～）の演習環境を構築するためのDockerファイル

## 展開されるコンテナ
### MySQL (tokuron-mysql)
- バージョン：8.0
    - 日本語設定済み
- ポート：3306
- テスト用DB： tsubuyaki
- テスト用USER： tsubuyaki

### phpmyadmin (tokuron-phpmyadmin)
- ポート：3001
- アクセスURL： http://localhost:3001
- 自動的にtokuron-mysqlにrootログインする

### tomcat (tokuron-tomcat)
- バージョン：10.1.41-jdk21

## フォルダ構成
### mysql
mysqlのフォルダ
- conf.d
設定ファイルを置くところ
- initdb.d
データベースの初期値を指定するところ

### webapps
Tomcatのwarファイルをデプロイするフォルダ
- http://localhost:8080/app/
    - app.warのアクセスURL

