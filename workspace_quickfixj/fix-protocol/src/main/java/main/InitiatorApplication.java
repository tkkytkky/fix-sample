package main.java.main;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
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
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;

public class InitiatorApplication implements Application {
	
    public void onCreate(SessionID sessionId) {
        System.out.println("Initiator Session created with session ID: " + sessionId);
    }

    public void onLogon(SessionID sessionId) {
        System.out.println("Initiator Logon successful for session ID: " + sessionId);
    }

    public void onLogout(SessionID sessionId) {
        System.out.println("Initiator Logout received for session ID: " + sessionId);
    }

    public void toAdmin(Message message, SessionID sessionId) {
        // メッセージが送信される前に編集できる処理をここに実装
    }

    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        // 受信したメッセージのバリデーションや処理をここに実装
    }

    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        // アプリケーションレベルでのメッセージの送信前に編集できる処理をここに実装
    }

    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        // 受信したアプリケーションレベルのメッセージを処理する
        System.out.println("Initiator Received message: " + message);
    }

    public static void main(String[] args) {
        try {
            SessionSettings settings = new SessionSettings("initiator.cfg");
            Application application = new InitiatorApplication();
            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            Initiator initiator = new SocketInitiator(
                application, storeFactory, settings, logFactory, messageFactory
            );

            initiator.start();

            // Heartbeatメッセージの作成
            Message heartbeatMessage = createHeartbeatMessage();

            // 送信
            SessionID sessionId = initiator.getSessions().get(0); // 適切なセッションIDを取得
            
            MyFixApplication myFixApplication = new MyFixApplication();
            myFixApplication.onLogon(sessionId);
            Session.sendToTarget(heartbeatMessage, sessionId);
            
            quickfix.fix44.NewOrderSingle newOrderSingle = new quickfix.fix44.NewOrderSingle();
            newOrderSingle.setString(quickfix.field.ClOrdID.FIELD, "12345");
            newOrderSingle.setString(quickfix.field.Symbol.FIELD, "AAPL");
            newOrderSingle.setDouble(quickfix.field.Price.FIELD, 150.25);
            
            Session.sendToTarget(newOrderSingle, sessionId);

            // 切断する場合
            // initiator.stop();
        } catch (ConfigError | RuntimeError | SessionNotFound e) {
            e.printStackTrace();
        }
    }

    private static Message createHeartbeatMessage() {
        // 必要に応じて、適切なFIXメッセージを作成
        Message heartbeatMessage = new quickfix.fix44.Heartbeat();
        return heartbeatMessage;
    }
}
