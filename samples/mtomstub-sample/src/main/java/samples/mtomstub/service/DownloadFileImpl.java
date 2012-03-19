package samples.mtomstub.service;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.mail.util.ByteArrayDataSource;

@WebService(endpointInterface="samples.mtomstub.service.DownloadFile", serviceName = "DownloadFileWS")
public class DownloadFileImpl implements DownloadFile {
    public ResponseDownloadFile getFile() throws Exception {
        ResponseDownloadFile rdf = new ResponseDownloadFile();
        String contentType = "text/plain; charset='UTF-8'";
        rdf.setFileName("readme.txt");
        rdf.setFileType(contentType);
        rdf.setFile(new DataHandler(new ByteArrayDataSource("This is the content".getBytes("UTF-8"), contentType)));
        return rdf;
    }
}
