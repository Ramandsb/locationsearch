package tagbin.in.myapplication.Gcm;

public interface Config {

    // used to share GCM regId with application server - using php app server
    static final String APP_SERVER_URL = "http://192.168.1.16/gcm/?shareRegId=1";
//    public static String BASE_URL="http://192.168.0.4:8001/api/v1/CreateUserResource/";
    public static String BASE_URL="http://api.rastey.com/api/v1/cab/";

    // GCM server using java
    // static final String APP_SERVER_URL =
    // "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "583481635501";
    static final String MESSAGE_KEY = "message";
    /////////////////////////////////////////////

//    Server API Key help
//    AIzaSyDoamwaAHuyTKZ1sTakjLHxjfkWso2tgT4
//    Sender ID help
//    604246263412
    /////////////////////////////////////////////////


}