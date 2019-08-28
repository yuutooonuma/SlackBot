# 概要 #
```
Slack上で入力されたコメントに対して反応をするbot
```

# Requirement #

### Eclipseへのimport

1.Select File>Import>Git - Projects from Git  
2.Clone URI  
3.set clone URI to https://github.com/yuutooonuma/SlackBot.git  
4.適宜[NEXT]を押していく  
5."Import as general project"をチェックして、"finish"  を押す


### credentialsをセットする

src直下にcredentials.propertiesというファイルを作り、以下のように取得したapi tokenをセットします

```
slack.bot_api_token=xoxb-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```  
src直下にdbconnector.propertiesというファイルを作り、以下のように取得したur,user,passwordをセットします
```
URL=xxxxxxxxxxxxxxxxxxxx
USER=xxxxxxxxxxxxx
PASSWORD=xxxxxxxxxx
```


### Dependencies ###
```
mysql-connector-java-8.0.17.jar
slacklet-1.0.4.jar
commons-logging-1.2.jar
httpclient-4.5.9.jar
httpcore-4.4.11.jar
httpmime-4.5.9.jar
simpleslackapi-1.2.0.jar
slacklet-1.0.4.jar
slf4j-api-1.7.26.jar
threetenbp-1.4.0.jar
tyrus-standalone-client-1.15.jar
```



# コマンド一覧 #
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
