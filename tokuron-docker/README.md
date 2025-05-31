# notebooks
- Jupyter Notebookのノートブックファイル(.ipynbファイル)を置くところ

# mysql
- conf.d
設定ファイルを置くところ
- initdb.d
データベースの初期値を指定するところ

# docker-compose.yml
- サーバーの構築の記述ファイル

# server_create.bat
- サーバーを構築するためのbatファイル
- 初回は実行後、構築が終わるまで300秒くらいかかる

# server_down.bat
- サーバーを消すためのbatファイル

# server_delete.bat
- サーバー構築に関するデータ（docker image, 永続化されたデータ, ネットワーク）を全て消去し初期化するためのbatファイル

# server_pause.bat
- サーバーを一時停止するためのbatファイル
- 基本はserver_downを使う


# server_restart.bat
- 一時停止したサーバーをもう一度動作させるためのbatファイル
