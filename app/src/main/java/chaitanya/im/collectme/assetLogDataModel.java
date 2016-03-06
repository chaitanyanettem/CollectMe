package chaitanya.im.collectme;

public class assetLogDataModel {
    public String logMsg;
    public long timeOfLog;

    assetLogDataModel() {

    }

    assetLogDataModel(String logMsg, long timeOfLog) {
        this.logMsg = logMsg;
        this.timeOfLog = timeOfLog;
    }

    public String getLogMsg() {
        return logMsg;
    }

    public long getTimeOfLog() {
        return timeOfLog;
    }
}
