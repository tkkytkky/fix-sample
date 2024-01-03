package main.java.main;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.RuntimeError;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.fix44.NewOrderSingle;

public class MyFixApplication implements Application {

    @Override
    public void onCreate(SessionID sessionId) {
        System.out.println("Session created with ID: " + sessionId);
    }

    @Override
    public void onLogon(SessionID sessionId) {
        System.out.println("Logon successful for session ID: " + sessionId);

        // メッセージの作成と送信
        sendTestMessage(sessionId);

        // セッションを閉じる
        closeSession(sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        System.out.println("Logout for session ID: " + sessionId);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        // 送信前の処理 (Appメッセージ用)
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        System.out.println("Received message: " + message.toString());

        // パラメータごとに表示
        String clOrdID = message.getString(quickfix.field.ClOrdID.FIELD);
        String symbol = message.getString(quickfix.field.Symbol.FIELD);
        double price = message.getDouble(quickfix.field.Price.FIELD);

        System.out.println("ClOrdID: " + clOrdID);
        System.out.println("Symbol: " + symbol);
        System.out.println("Price: " + price);
    }

    // メッセージを作成して送信するメソッド
    private void sendTestMessage(SessionID sessionId) {
        NewOrderSingle newOrderSingle = createNewOrderSingleMessage();
        try {
            Session.sendToTarget(newOrderSingle, sessionId);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }

    // NewOrderSingleメッセージを作成するメソッド（適宜変更）
    private NewOrderSingle createNewOrderSingleMessage() {
        NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setString(quickfix.field.ClOrdID.FIELD, "12345");
        newOrderSingle.setString(quickfix.field.Symbol.FIELD, "AAPL");
        newOrderSingle.setDouble(quickfix.field.Price.FIELD, 150.25);
        // 他のパラメータも設定...

        return newOrderSingle;
    }

    // セッションを閉じるメソッド
    private void closeSession(SessionID sessionId) {
        Session.lookupSession(sessionId).logout("Closing session");
    }

    public static void main(String[] args) throws FieldConvertError {
        // Acceptor (サーバー) を起動
        try {
            SessionSettings settings = new SessionSettings("acceptor.cfg");
            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            Acceptor acceptor = new SocketAcceptor(new MyFixApplication(), storeFactory, settings, logFactory, messageFactory);
            acceptor.start();
        } catch (ConfigError | RuntimeError e) {
            e.printStackTrace();
        }

        // Initiator (クライアント) を起動
        try {
            SessionSettings settings = new SessionSettings("initiator.cfg");
            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            Initiator initiator = new SocketInitiator(new MyFixApplication(), storeFactory, settings, logFactory, messageFactory);
            initiator.start();
        } catch (ConfigError | RuntimeError e) {
            e.printStackTrace();
        }
    }

	@Override
	public void fromAdmin(Message arg0, SessionID arg1)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void toAdmin(Message arg0, SessionID arg1) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
