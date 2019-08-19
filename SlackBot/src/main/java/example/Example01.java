package example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.riversun.slacklet.SlackletService;
import org.riversun.slacklet.SlackletSession;
import org.riversun.xternal.simpleslackapi.SlackUser;

public class Example01 {

	public static void main(String[] args) throws IOException {


		String botToken = ResourceBundle.getBundle("credentials").getString("slack.bot_api_token");

		SlackletService slackService = new SlackletService(botToken);

		Date date = new Date();

		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();






	      //スポーツ画像格納
	      List<String>list = new ArrayList<String>();
		    list.add("https://1.bp.blogspot.com-q0V5rHBTWoM/W8hEC5vBPYI/AAAAAAABPho/sVAY05zJagk19YWe80ln1I3USD0pxlp1ACLcBGAs/s800/soccer_score_woman.png");
		    list.add("https://3.bp.blogspot.com/-5ubHu65nqLM/XNE-vX5u90I/AAAAAAABSsE/oGTQ9H8-AgENRkEpFx0jPMXQJsw4EfH0gCLcBGAs/s290/camp_wood_candle.png");
		    list.add("https://1.bp.blogspot.com/-2jn0znouzaU/W8hDkKOP_LI/AAAAAAABPdg/CT4qeUObpM4m0jINxf14dyYi-zy12R-jACLcBGAs/s800/baseball_homerun_woman.png");

		  //機能格納
		  List<String>kinoList = new ArrayList<String>();
		    kinoList.add("【時間】");
		    kinoList.add("【ジャンケンモード】");
		    kinoList.add("【スポーツ画像】");
		    kinoList.add("【予定追加】");
		    kinoList.add("【確認】");
		    kinoList.add("【削除】");

		    slackService.addSlacklet(new Slacklet() {

		    	int mode = 0;
				int win = 0;
				int lose = 0;
				String userD = "";
				String usery ="";
				String userName = "";
				String userYotei = "";


			@Override
			public void onDirectMessagePosted(SlackletRequest req, SlackletResponse resp) {

				// BOT宛のダイレクトメッセージがポストされた


				// メッセージを送信したユーザーのメンションを取得する
				String mention = req.getUserDisp();

				String getUserDisp=req.getUserDisp();

				//ユーザーの情報を取得する
				SlackUser SlackUser= req.getSender();




				 // セッションを取得する（セッションはユーザー毎に固有）
				SlackletSession session = req.getSession();

				//ArrayList<String> yoteiList = (ArrayList<String>)session.getAttribute("yotei");
				//if(yoteiList == null)yoteiList = new ArrayList<String>();

				ArrayList<String> yoteList = new ArrayList<String>();



				//メッセージ本文を取得
				String content = req.getContent();


				if(!SlackUser.isBot()){


					if (mode == 2&&usery.equals(getUserDisp)) {
		                   //yoteiList.add(content);
		                   //session.setAttribute("yotei",yoteiList);
		                    resp.reply(content + "を追加しました！");
		                    mention = userName;
		                    content = userYotei;

		                    String sql = "INSERT INTO yotei_table (user_name,user_yotei) VALUES(?, ? )";
		            		try {
		            			PreparedStatement preparedStatement = connection.prepareStatement(sql);
		            			preparedStatement.setString(1, userName);
		            			preparedStatement.setString(2, userYotei);

		            			preparedStatement.execute();
		            		} catch(Exception e) {
		            			e.printStackTrace();
		            		}

		                    mode = 0;
		                    usery = "";
		    				 userName = "";
		    				userYotei = "";
		            		} else if (mode == 3&&usery.equals(getUserDisp)) {
		                	if(yoteList.contains(content)){
			                	//yoteiList.remove(content);
		                    resp.reply(content + "を削除しました！");
		                	}else{
		                	resp.reply(content + "は予定にありません");
		                	}
		                	   mention = userName;
			                    content = userYotei;
		                	String sql =
		            				"DELETE * FROM yotei_table WHERE user_yotei = ? AND user_name = ?";
		            		PreparedStatement preparedStatement;
		            		try {
		            			preparedStatement = connection.prepareStatement(sql);
		            			preparedStatement.setString(1, userYotei);
		            			preparedStatement.setString(2, userName);
		            		} catch (SQLException e) {
		            			e.printStackTrace();
		            		}
		                    mode = 0;
		                    usery = "";
		                    userName = "";
		    				userYotei = "";

		                }else if(mode == 4 &&userD.equals(getUserDisp)){
		            		List<String>listJ = new ArrayList<String>();
		        		    listJ.add("グー");
		        		    listJ.add("チョキ");
		        		    listJ.add("パー");

		        		    Collections.shuffle(listJ);
		        		    list.get(0);

		        		    switch (listJ.get(0)) {
		            		case "グー":
		            		resp.reply("CPU：グー");
		            			break;
		            		case "チョキ":
		            			resp.reply("CPU：チョキ");
		            			break;
		            		case "パー":
		            			resp.reply("CPU：パー");
		            			break;
		            		}

		            		if (content.equals("グー") && listJ.get(0) == "チョキ") {
		            			resp.reply("あなたの勝ちです");
		            			win +=1;
		            			resp.reply(mention+"さんの"+win+"勝"+lose+"敗です");
		            			if(win==3){
		                    		resp.reply("3勝しました、終わります");
		                    		win = 0;
		                    		lose = 0;
		                    		mode = 0;
		                    		userD ="";
		                    	} }else if (content.equals("チョキ") && listJ.get(0) == "パー") {
		            			resp.reply("あなたの勝ちです");
		            			win +=1;
		            			resp.reply(mention+"さんの"+win+"勝"+lose+"敗です");
		            			if(win==3){
		                    		resp.reply("3勝しました、終わります");
		                    		win = 0;
		                    		lose = 0;
		                    		mode = 0;
		                    		userD ="";
		            		}} else if (content.equals( "パー") && listJ.get(0) == "グー") {
		            			resp.reply("あなたの勝ちです");
		            			win +=1;
		            			resp.reply(mention+"さんの"+win+"勝"+lose+"敗です");
		            			if(win==3){
		                    		resp.reply("3勝しました、終わります");
		                    		win = 0;
		                    		lose = 0;
		                    		mode = 0;
		                    		userD ="";
		            		}} else if (content.equals(listJ.get(0))) {
		            			resp.reply("あいこでしょ！");
		            			resp.reply("あなたの手を入力してください");
		            			mode = 4;

		            		} else {
		            			resp.reply("あなたの負けです");
		            			lose +=1;
		            			resp.reply(mention+"さんの"+win+"勝"+lose+"敗です");
		            			if(lose==3){
		            				resp.reply("botが3勝しました 終わります");
		            				win = 0;
		            				lose = 0;
		            				mode = 0;
		            				userD ="";

		            		}}
		            	}else if(mode==0&&content.equals("時間")) {
		            		resp.reply("現在の時刻は\n[" + date + "]です");
					    }else if(mode ==0&&content.equals("ジャンケンモード")) {
					    	mode = 4;
					    	userD = getUserDisp;
					    	resp.reply("ジャンケンモードに入ります,どちらかが3勝するまでやります");
					    	resp.reply("あなたの手を選んで入力してください。グー、チョキ、パー以外を入力すると負けになります");
						}else if(mode==0&&content.equals("スポーツ画像")) {
							 Collections.shuffle(list);
							 resp.reply("、\n[" + list.get(1) + "]こちらです");
						}else if(mode==0&&content.equals("機能確認")){
							for(String kino : kinoList){
								resp.reply("【"+kino+"】");}
						}else if(mode ==0&&content.contentEquals("予定追加")) {
							usery = getUserDisp;
							mode = 2;
							resp.reply("追加したい予定を入力してください");
						}else if(mode==0&&content.contentEquals("確認")) {
							resp.reply(mention+"さんの予定はこちらです");
							//for (String yote : yoteiList) {
								//resp.reply("【"+yote+"】");}
							mention = userName;
		                    content = userYotei;

							String sql =
									"SELECT * FROM yotei_table WHERE user_yotei = ? AND user_name = ?";
							ArrayList<String> exampleDTO = new ArrayList<String>();
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, userName);
								preparedStatement.setString(2, userYotei);
								ResultSet resultSet = preparedStatement.executeQuery();
								while(resultSet.next()) {
									exampleDTO.add(userYotei);
								}
								for(String yote : exampleDTO){
									resp.reply("【"+yote+"】");
								}

						}catch(Exception e) {
	            			e.printStackTrace();
	            		}
}
								else if(mode==0&&content.contentEquals("削除")) {
							usery = getUserDisp;
							mode = 3;
							resp.reply("削除したい予定を入力してください");
						}
					}



				}


			@Override
			public void onMentionedMessagePosted(SlackletRequest req, SlackletResponse resp) {
				// あるチャンネルでこのBOTへのメンション付きメッセージがポストされた(例　「@smilebot おはよう」）

				String content = req.getContent();

				String mention = req.getUserDisp();
				resp.reply("こんにちは、" + mention + "さん。「" + content + "」って言いましたね。");
			}


		});

		slackService.start();

	}

}