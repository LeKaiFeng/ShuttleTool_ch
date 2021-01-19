package galaxis.lee.senceControl;

public enum FXMLPages {
    SHOW_LOG("fxml/log.fxml"),DB_CONFIG("fxml/ConfigDB.fxml");

    private String fxml;

    FXMLPages(String fxml) {
        this.fxml = fxml;
    }
    public String getFxml(){
        return this.fxml;
    }
}
