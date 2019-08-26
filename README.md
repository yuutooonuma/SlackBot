# SlackBot（製作途中）

# 概要
```
Javaを使ったSlackBot
```

### Eclipseへのimport

1.Select File>Import>Git - Projects from Git  
2.Clone URI  
3.set clone URI to https://github.com/yuutooonuma/SlackBot.git  
4.適宜[NEXT]を押していく  
5."Import as general project"をチェックして、"finish"  を押す


### credentialsをセットする

src/main/java直下にcredentials.propertiesというファイルを作り、以下のように取得したapi tokenをセットします

```
slack.bot_api_token=xoxb-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```


### コマンド一覧
```

ジャンケンモード:ジャンケンモードに入る
予定追加:予定を入力し記録する
確認:入力した予定を確認する
予定削除:入力した予定を削除する
機能確認:コマンド一覧を取得する
```

```
Exmaple00.java:randomチャンネルに対応
Example01.java:ダイレクトメッセージに対応
```
