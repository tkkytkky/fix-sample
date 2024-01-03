quickfixj構築
	AcceptorApplicationとInitiatorApplicationとMyFixApplication
		AcceptorApplication
			受け手
				メッセージを受けるセッションを起動する
				必要なライブラリ
					quickfixj-core-2.3.1.jar
					slf4j-api-1.7.30.jar
					mina-core-2.1.4.jar
				acceptor.cfgとかで設定を持つ
				mainメソッドを実行するとセッションが起動する
	InitiatorApplication
		送り手
			メッセージを送るだけなはず？
			必要なライブラリ
				（上記）
				quickfixj-messages-fix44-2.3.1.jar





