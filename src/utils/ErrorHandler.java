package utils;

/**
 * Created by Tong on 7/18/2014.
 * ErrorHandler handles every kind of exception that recognized by me.
 * The printed information is mainly for developers using the MusicSearchEngine,
 * to better understand the data flow, but not for the debugging use.
 */
public class ErrorHandler {
    private static final int ERROR_LIMIT = 256;
    private static final String[] ERROR_LIST = new String[ERROR_LIMIT];
    private static boolean isInited = false;
    private static int count = 0;

    private static void init() {
        ERROR_LIST[0] = "PROGRAMMER_BEING_UNHAPPY_ERROR";
        ERROR_LIST[1] = "ERROR_CODE_UNMATCHED_ERROR";
        ERROR_LIST[2] = "FAILED_TO_GET_SOURCE_DOCUMENT_ERROR";
        ERROR_LIST[3] = "FAILED_TO_RESOLVE_XML_ERROR";
        ERROR_LIST[4] = "FAILED_TO_RESOLVE_JSON_ERROR";
        ERROR_LIST[5] = "URL_PATH_FORMAT_ERROR";
        ERROR_LIST[6] = "FAILED_TO_RESOLVE_AUDIO_FROM_DOCUMENT_ERROR";
        ERROR_LIST[7] = "FAILED_TO_RESOLVE_LYRIC_FROM_DOCUMENT_ERROR";
        ERROR_LIST[8] = "SONG_DOES_NOT_EXIST_IN_DATABASE_ERROR";
        ERROR_LIST[9] = "SONG_INFO_UNMATCHED_ERROR";
        ERROR_LIST[10] = "SONG_INFO_MISSING_ERROR";
        ERROR_LIST[11] = "SONG_NUM_LIMIT_EXCEEDED_ERROR";

        System.out.println("|||  Welcome to ErrorHandler.\n" +
                "|||  I'm here to help you understand the process of MusicSearchEngine.\n" +
                "|||  It is necessary to let you know some errors are harmless. Just ignore them.\n" +
                "|||  // And of course, you are not expected to understand this.\n" +
                "|||  You cannot see the above comment, can you?\n" +
                "|||  Okay, any problem, feel free to contact TongZhongyi.");
        isInited = true;
    }

    public static void deal(int errorCode, String description) {
        if (!isInited) init();
        // Exclude Error Handler internal error at the first place.
        if (errorCode < 0 || errorCode >= ERROR_LIMIT || ERROR_LIST[errorCode] == null)
            deal(1);
        System.out.println("\n[" + count++ + "] Oops, an error occurred during the use of MusicSearchEngine.");
        System.out.println("Error overview: " + ERROR_LIST[errorCode]);
        if (description != null)
            System.out.println("Error details: " + description + "\n");
    }

    public static void deal(int errorCode) {
        deal(errorCode, null);
    }
}
