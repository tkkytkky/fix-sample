package main.java.main;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.RuntimeError;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.UnsupportedMessageType;

public class AcceptorApplication implements Application {

    public void onCreate(SessionID sessionId) {
        System.out.println("Acceptor Session created with session ID: " + sessionId);
    }

    public void onLogon(SessionID sessionId) {
        System.out.println("Acceptor Logon successful for session ID: " + sessionId);
    }

    public void onLogout(SessionID sessionId) {
        System.out.println("Acceptor Logout received for session ID: " + sessionId);
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
        System.out.println("Acceptor Received message: " + message);
    	String symbolField = message.getString(quickfix.field.ClOrdID.FIELD);
    	System.out.println(symbolField);
    }

    public static void main(String[] args) {
        try {
            SessionSettings settings = new SessionSettings("acceptor.cfg");
            Application application = new AcceptorApplication();
            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();

            Acceptor acceptor = new SocketAcceptor(
                application, storeFactory, settings, logFactory, messageFactory
            );

            acceptor.start();
        } catch (ConfigError | RuntimeError e) {
            e.printStackTrace();
        }
    }
}
