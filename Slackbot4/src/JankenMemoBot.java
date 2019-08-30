import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.riversun.slacklet.SlackletService;
import org.riversun.xternal.simpleslackapi.SlackChannel;
import org.riversun.xternal.simpleslackapi.SlackUser;

public class JankenMemoBot {

	public static void main(String[] args) throws IOException {

		String botToken = ResourceBundle.getBundle("credentials").getString("slack.bot_api_token");

		SlackletService slackService = new SlackletService(botToken);

		DBConnector dbConnector = new DBConnector();
		Connection connection = dbConnector.getConnection();

		DateUtil dateUtil = new DateUtil();

		  //機能格納
		  List<String>kinoList = new ArrayList<String>();
		    kinoList.add("ジャンケンモード");
		    kinoList.add("予定追加");
		    kinoList.add("確認");
		    kinoList.add("削除");
		    kinoList.add("戦績確認");

		    slackService.addSlacklet(new Slacklet() {

		    	int mode = 0;
				int win = 0;
				int lose = 0;
				String userD = "";
				String usery ="";
				String yoteiName ="";
				String userYotei = "";

				@Override
			public void onDirectMessagePosted(SlackletRequest req, SlackletResponse resp) {

				// BOT宛のダイレクトメッセージがポストされた

				// メッセージを送信したユーザーのメンションを取得する
				String mention = req.getUserDisp();
				//ユーザーの情報を取得する
				String getUserDisp=req.getUserDisp();
				//ユーザーの情報を取得する
				SlackUser SlackUser= req.getSender();
				//ユーザーの名前を取得する
				String realName = req.getSender().getRealName();
				//メッセージ本文を取得
				String content = req.getContent();

				if(!SlackUser.isBot()){
					//------------------予定追加モード------------------
					if (mode == 2&&usery.equals(getUserDisp)) {
		                    resp.reply(content + "を追加しました！");

		                    yoteiName=realName;
		                    userYotei=content;
		                    //予定を追加するSQL
		                    String sql = "INSERT INTO yotei  (user_name,user_yotei,insert_date) VALUES(?,?,? )";
		            		try {
		            			PreparedStatement preparedStatement = connection.prepareStatement(sql);
		            			preparedStatement.setString(1, yoteiName);
		            			preparedStatement.setString(2, userYotei);
		            			preparedStatement.setString(3, dateUtil.getDate());

		            			preparedStatement.execute();
		            		} catch(Exception e) {
		            			e.printStackTrace();
		            		}
		            		mode = 0;
		                    usery = "";
		    				yoteiName = "";
		    				userYotei = "";
		            		} else if (mode == 3&&usery.equals(getUserDisp)) {
		            			yoteiName=realName;
		            			userYotei=content;
		            			//予定があるか確認するSQL
		            			String sql =
										"SELECT * FROM yotei WHERE   user_name = ?" ;
								try {
									PreparedStatement preparedStatement = connection.prepareStatement(sql);
									preparedStatement.setString(1, yoteiName);
									ResultSet rs = preparedStatement.executeQuery();
									if(rs.next()) {

										if(rs.getString("user_yotei").contains(content)) {
											resp.reply(content+"を削除しました");
										}else {
											resp.reply(content+"は予定にありません");
										}}
						}catch(Exception e) {
		            			e.printStackTrace();
		            		}
								//予定を削除するSQL
								String sql2 =
		            				"DELETE  FROM yotei WHERE user_yotei = ? AND user_name = ?";
		            		PreparedStatement preparedStatement;
		            		try {
		            			preparedStatement = connection.prepareStatement(sql2);
		            			preparedStatement.setString(1, userYotei);
		            			preparedStatement.setString(2, yoteiName);
		            			preparedStatement.executeUpdate();
		            		} catch (SQLException e) {
		            			e.printStackTrace();
		            		}
		                    mode = 0;
		                    usery = "";
		                    yoteiName = "";
		    				userYotei = "";
		    				//---------------ジャンケンモード----------------
		                }else if(mode == 4 &&userD.equals(getUserDisp)){

		            		List<String>listJ = new ArrayList<String>();
		        		    listJ.add(":fist:");
		        		    listJ.add(":v:");
		        		    listJ.add(":wave:");

		        		    Collections.shuffle(listJ);
		        		    listJ.get(0);

		        		    switch (listJ.get(0)) {
		            		case ":fist:":
		            		resp.reply("CPU：:fist:");
		            			break;
		            		case ":v:":
		            			resp.reply("CPU：:v:");
		            			break;
		            		case ":wave:":
		            			resp.reply("CPU：:wave:");
		            			break;
		            		}

		            		if (content.equals("グー") && listJ.get(0) == ":v:") {
		            			resp.reply("あなたの勝ちです");
		            			win +=1;
		            			resp.reply(mention+"さんの"+win+"勝"+lose+"敗です");
		            			if(win==2){
		                    		resp.reply("2勝しました、終わります");
		                    		//戦績を記録するSQL
		                    		String sql2 = "UPDATE janken SET total_win = total_win +1 WHERE user_name = ? ";
		                    		try {
		                    			PreparedStatement preparedStatement = connection.prepareStatement(sql2);
				            			preparedStatement.setString(1,yoteiName);
				            			preparedStatement.executeUpdate();
				            		} catch(Exception e) {
				            			e.printStackTrace();
				            		}
		                    		win = 0;
		                    		lose = 0;
		                    		mode = 0;
		                    		userD ="";
		                    		yoteiName = "";
		                    	} }else if (content.equals("チョキ") && listJ.get(0) == ":wave:") {
		            			resp.reply("あなたの勝ちです");
		            			win +=1;
		            			resp.reply(mention+"さんの"+win+"勝"+lose+"敗です");
		            			if(win==2){
		                    		resp.reply("2勝しました、終わります");
		                    		//戦績を記録するSQL
		                    		String sql2 = "UPDATE janken SET total_win = total_win +1 WHERE user_name = ? ";
		                    		try {
		                    			PreparedStatement preparedStatement = connection.prepareStatement(sql2);
				            			preparedStatement.setString(1,yoteiName);
				            			preparedStatement.executeUpdate();
				            		} catch(Exception e) {
				            			e.printStackTrace();
				            		}
		                    		win = 0;
		                    		lose = 0;
		                    		mode = 0;
		                    		userD ="";
		                    		yoteiName = "";
		            		}} else if (content.equals( "パー") && listJ.get(0) == ":fist:") {
		            			resp.reply("あなたの勝ちです");
		            			win +=1;
		            			resp.reply(mention+"さんの"+win+"勝"+lose+"敗です");
		            			if(win==2){
		                    		resp.reply("2勝しました、終わります");
		                    		//戦績を記録するSQL
		                    		String sql2 = "UPDATE janken SET total_win = total_win +1 WHERE user_name = ? ";
		                    		try {
		                    			PreparedStatement preparedStatement = connection.prepareStatement(sql2);
				            			preparedStatement.setString(1,yoteiName);
				            			preparedStatement.executeUpdate();
				            		} catch(Exception e) {
				            			e.printStackTrace();
				            		}
		                    		win = 0;
		                    		lose = 0;
		                    		mode = 0;
		                    		userD ="";
		                    		yoteiName = "";
		            		}} else if (content.equals(listJ.get(0))) {
		            			resp.reply("あいこでしょ！");
		            			resp.reply("あなたの手を入力してください");
		            			mode = 4;

		            		}  else if (content.equals("グー")&&listJ.get(0)==":fist:") {
		            			resp.reply("あいこでしょ！");
		            			resp.reply("あなたの手を入力してください");
		            			mode = 4;

		            		} else if (content.equals("パー")&&listJ.get(0)==":wave:") {
		            			resp.reply("あいこでしょ！");
		            			resp.reply("あなたの手を入力してください");
		            			mode = 4;

		            		} else if (content.equals("チョキ")&&listJ.get(0)==":v:") {
		            			resp.reply("あいこでしょ！");
		            			resp.reply("あなたの手を入力してください");
		            			mode = 4;

		            		}else {
		            			resp.reply("あなたの負けです");
		            			lose +=1;
		            			resp.reply(mention+"さんの"+win+"勝"+lose+"敗です");
		            			if(lose==2){
		            				resp.reply("botが2勝しました 終わります");
		            				win = 0;
		            				lose = 0;
		            				mode = 0;
		            				userD ="";
		            				yoteiName = "";

		            		}}
		            		//ジャンケンモードに入る
		            	}else if(mode ==0&&content.equals("ジャンケンモード")) {
					    	mode = 4;
					    	userD = getUserDisp;
					    	yoteiName=realName;
					    	String sql = "INSERT INTO janken  (user_name) VALUES(?)";
		            		try {
		            			PreparedStatement preparedStatement = connection.prepareStatement(sql);
		            			preparedStatement.setString(1, yoteiName);
		            			preparedStatement.execute();
		            		} catch(Exception e) {
		            			e.printStackTrace();
		            		}
		            		//試合数を記録するSQL
		            		String sql2 = "UPDATE janken SET total_game = total_game +1 WHERE user_name = ? ";
                    		try {
                    			PreparedStatement preparedStatement = connection.prepareStatement(sql2);
		            			preparedStatement.setString(1,yoteiName);
		            			preparedStatement.executeUpdate();
		            		} catch(Exception e) {
		            			e.printStackTrace();
		            		}
					    	resp.reply("ジャンケンモードに入ります,どちらかが2勝するまでやります");
					    	resp.reply("あなたの手を選んで入力してください。グー、チョキ、パー以外を入力すると負けになります");
						}else if(mode==0&&content.equals("機能確認")){
							for(String kino : kinoList){
								resp.reply("【"+kino+"】");}
						}else if(mode ==0&&content.contentEquals("予定追加")) {
							usery = getUserDisp;
							mode = 2;
							resp.reply("追加したい予定を入力してください");
						}else if(mode==0&&content.contentEquals("確認")) {
							resp.reply(mention+"さんの予定はこちらです");
							yoteiName=realName;
							//予定を抽出するSQL
							String sql =
									"SELECT * FROM yotei WHERE   user_name = ?" ;
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, yoteiName);
								ResultSet rs = preparedStatement.executeQuery();
								while(rs.next()) {
									resp.reply(rs.getString("user_yotei"));
								}
								}catch(Exception e) {
	            			e.printStackTrace();
	            			}
						}else if(mode==0&&content.contentEquals("削除")) {

							usery = getUserDisp;
							mode = 3;
							resp.reply("削除したい予定を入力してください");

					}else if(mode==0&&content.contentEquals("戦績確認")) {
						resp.reply(mention+"さんの戦績はこちらです");
						yoteiName=realName;
						//戦績を抽出するSQL
						String sql =
								"SELECT * FROM janken WHERE   user_name = ?" ;
						try {
							PreparedStatement preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, yoteiName);
							ResultSet rs = preparedStatement.executeQuery();
							while(rs.next()) {
								resp.reply(rs.getString("total_game")+"試合");
								resp.reply(rs.getString("total_win")+"勝");
								}
							}catch(Exception e) {
		            			e.printStackTrace();
		            			}
						}else if(content.equals("予定ランダム抽出")) {
						yoteiName=realName;
						//予定をランダム抽出するSQL
						String sql = "SELECT * FROM yotei WHRER user_name = ? ORDER BY RAND() LIMIT 1 ";
						try {
							PreparedStatement preparedStatement = connection.prepareStatement(sql);
							preparedStatement.setString(1, yoteiName);
							ResultSet rs = preparedStatement.executeQuery();
							while(rs.next()) {
								resp.reply(rs.getString("user_yotei"));}
							}catch(Exception e) {
		            			e.printStackTrace();
		            			}
					}
				}
				}

			@Override
			public void onMessagePosted(SlackletRequest req, SlackletResponse resp) {

				// メッセージがユーザーからポストされた

				// メッセージがポストされたチャンネルを取得する
				SlackChannel channel = req.getChannel();

				// メッセージを送信したユーザーのメンションを取得する
				String mention = req.getUserDisp();



				//ユーザーの情報を取得する
				SlackUser SlackUser= req.getSender();
				String realName = req.getSender().getRealName();

				String getUserDisp=req.getUserDisp();


				if ("random".equals(channel.getName())) {

					String content = req.getContent();

					if (!SlackUser.isBot()) {
						if (mode == 2 && usery.equals(getUserDisp)) {
							resp.reply(content + "を追加しました！");

							yoteiName = realName;
							userYotei = content;

							String sql = "INSERT INTO yotei  (user_name,user_yotei) VALUES(?,? )";
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, yoteiName);
								preparedStatement.setString(2, userYotei);

								preparedStatement.execute();
							} catch (Exception e) {
								e.printStackTrace();
							}
							mode = 0;
							usery = "";
							yoteiName = "";
							userYotei = "";
						} else if (mode == 3 && usery.equals(getUserDisp)) {
							yoteiName = realName;
							userYotei = content;

							String sql = "SELECT * FROM yotei WHERE   user_name = ?";
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, yoteiName);
								ResultSet rs = preparedStatement.executeQuery();
								if (rs.next()) {

									if (rs.getString("user_yotei").contains(content)) {
										resp.reply(content + "を削除しました");
									} else {
										resp.reply(content + "は予定にありません");
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							String sql2 = "DELETE  FROM yotei WHERE user_yotei = ? AND user_name = ?";
							PreparedStatement preparedStatement;
							try {
								preparedStatement = connection.prepareStatement(sql2);
								preparedStatement.setString(1, userYotei);
								preparedStatement.setString(2, yoteiName);
								preparedStatement.executeUpdate();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							mode = 0;
							usery = "";
							yoteiName = "";
							userYotei = "";

						} else if (mode == 4 && userD.equals(getUserDisp)) {

							List<String> listJ = new ArrayList<String>();
							listJ.add(":fist:");
							listJ.add(":v:");
							listJ.add(":wave:");

							Collections.shuffle(listJ);
							listJ.get(0);

							switch (listJ.get(0)) {
							case ":fist:":
								resp.reply("CPU：:fist:");
								break;
							case ":v:":
								resp.reply("CPU：:v:");
								break;
							case ":wave:":
								resp.reply("CPU：:wave:");
								break;
							}

							if (content.equals("グー") && listJ.get(0) == ":v:") {
								resp.reply("あなたの勝ちです");
								win += 1;
								resp.reply(mention + "さんの" + win + "勝" + lose + "敗です");
								if (win == 2) {
									resp.reply("2勝しました、終わります");

									String sql2 = "UPDATE janken SET total_win = total_win +1 WHERE user_name = ? ";
									try {
										PreparedStatement preparedStatement = connection.prepareStatement(sql2);
										preparedStatement.setString(1, yoteiName);
										preparedStatement.executeUpdate();
									} catch (Exception e) {
										e.printStackTrace();
									}
									win = 0;
									lose = 0;
									mode = 0;
									userD = "";
									yoteiName = "";
								}
							} else if (content.equals("チョキ") && listJ.get(0) == ":wave:") {
								resp.reply("あなたの勝ちです");
								win += 1;
								resp.reply(mention + "さんの" + win + "勝" + lose + "敗です");
								if (win == 2) {
									resp.reply("2勝しました、終わります");

									String sql2 = "UPDATE janken SET total_win = total_win +1 WHERE user_name = ? ";
									try {
										PreparedStatement preparedStatement = connection.prepareStatement(sql2);
										preparedStatement.setString(1, yoteiName);
										preparedStatement.executeUpdate();
									} catch (Exception e) {
										e.printStackTrace();
									}
									win = 0;
									lose = 0;
									mode = 0;
									userD = "";
									yoteiName = "";
								}
							} else if (content.equals("パー") && listJ.get(0) == ":fist:") {
								resp.reply("あなたの勝ちです");
								win += 1;
								resp.reply(mention + "さんの" + win + "勝" + lose + "敗です");
								if (win == 2) {
									resp.reply("2勝しました、終わります");
									String sql2 = "UPDATE janken SET total_win = total_win +1 WHERE user_name = ? ";
									try {
										PreparedStatement preparedStatement = connection.prepareStatement(sql2);
										preparedStatement.setString(1, yoteiName);
										preparedStatement.executeUpdate();
									} catch (Exception e) {
										e.printStackTrace();
									}
									win = 0;
									lose = 0;
									mode = 0;
									userD = "";
									yoteiName = "";
								}
							} else if (content.equals(listJ.get(0))) {
								resp.reply("あいこでしょ！");
								resp.reply("あなたの手を入力してください");
								mode = 4;

							} else if (content.equals("グー") && listJ.get(0) == ":fist:") {
								resp.reply("あいこでしょ！");
								resp.reply("あなたの手を入力してください");
								mode = 4;

							} else if (content.equals("パー") && listJ.get(0) == ":wave:") {
								resp.reply("あいこでしょ！");
								resp.reply("あなたの手を入力してください");
								mode = 4;

							} else if (content.equals("チョキ") && listJ.get(0) == ":v:") {
								resp.reply("あいこでしょ！");
								resp.reply("あなたの手を入力してください");
								mode = 4;

							} else {
								resp.reply("あなたの負けです");
								lose += 1;
								resp.reply(mention + "さんの" + win + "勝" + lose + "敗です");
								if (lose == 2) {
									resp.reply("botが2勝しました 終わります");
									win = 0;
									lose = 0;
									mode = 0;
									userD = "";
									yoteiName = "";

								}
							}
						} else if (mode == 0 && content.equals("ジャンケンモード")) {
							mode = 4;
							userD = getUserDisp;
							yoteiName = realName;
							String sql = "INSERT INTO janken  (user_name) VALUES(?)";
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, yoteiName);
								preparedStatement.execute();
							} catch (Exception e) {
								e.printStackTrace();
							}
							String sql2 = "UPDATE janken SET total_game = total_game +1 WHERE user_name = ? ";
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql2);
								preparedStatement.setString(1, yoteiName);
								preparedStatement.executeUpdate();
							} catch (Exception e) {
								e.printStackTrace();
							}
							resp.reply("ジャンケンモードに入ります,どちらかが2勝するまでやります");
							resp.reply("あなたの手を選んで入力してください。グー、チョキ、パー以外を入力すると負けになります");
						} else if (mode == 0 && content.equals("機能確認")) {
							for (String kino : kinoList) {
								resp.reply("【" + kino + "】");
							}
						} else if (mode == 0 && content.contentEquals("予定追加")) {
							usery = getUserDisp;
							mode = 2;
							resp.reply("追加したい予定を入力してください");
						} else if (mode == 0 && content.contentEquals("確認")) {
							resp.reply(mention + "さんの予定はこちらです");
							yoteiName = realName;

							String sql = "SELECT * FROM yotei WHERE   user_name = ?";
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, yoteiName);
								ResultSet rs = preparedStatement.executeQuery();
								while (rs.next()) {
									resp.reply(rs.getString("user_yotei"));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else if (mode == 0 && content.contentEquals("削除")) {
							usery = getUserDisp;
							mode = 3;
							resp.reply("削除したい予定を入力してください");

						} else if (mode == 0 && content.contentEquals("戦績確認")) {
							resp.reply(mention + "さんの戦績はこちらです");
							yoteiName = realName;
							String sql = "SELECT * FROM janken WHERE   user_name = ?";
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, yoteiName);
								ResultSet rs = preparedStatement.executeQuery();
								while (rs.next()) {
									resp.reply(rs.getString("total_game") + "試合");
									resp.reply(rs.getString("total_win") + "勝");

								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}else if(content.equals("予定ランダム抽出")) {
							yoteiName=realName;
							String sql = "SELECT * FROM yotei WHERE user_name = ? ORDER BY RAND() LIMIT 1 ";
							try {
								PreparedStatement preparedStatement = connection.prepareStatement(sql);
								preparedStatement.setString(1, yoteiName);
								ResultSet rs = preparedStatement.executeQuery();
								while(rs.next()) {
									resp.reply(rs.getString("user_yotei"));}
								}catch(Exception e) {
			            			e.printStackTrace();
			            			}
						}
					}
					}
				}


		});

		slackService.start();

	}

}