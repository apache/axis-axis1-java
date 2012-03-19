package samples.mtomstub.service;

import javax.activation.DataHandler;

public class ResponseDownloadFile {
    private String fileName;
    private String fileType;
    private DataHandler file;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public DataHandler getFile() {
        return file;
    }

    public void setFile(DataHandler file) {
        this.file = file;
    }
}
