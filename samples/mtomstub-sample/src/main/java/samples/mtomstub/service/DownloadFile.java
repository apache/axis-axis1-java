package samples.mtomstub.service;

import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

@WebService
@MTOM
public interface DownloadFile {
    ResponseDownloadFile getFile() throws Exception;
}
