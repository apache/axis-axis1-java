package samples.mtomstub;

import java.net.URL;

import javax.xml.ws.Endpoint;

import junit.framework.TestCase;
import samples.mtomstub.service.DownloadFileImpl;
import samples.mtomstub.stub.DownloadFile;
import samples.mtomstub.stub.DownloadFileServiceLocator;

public class TestDownloadFile extends TestCase {
    public void test() throws Exception {
        // TODO: allocate random port here
        String url = "http://localhost:8080/DownloadFile";
        Endpoint endpoint = Endpoint.publish(url, new DownloadFileImpl());
        DownloadFile downloadFile = new DownloadFileServiceLocator().getDownloadFilePort(new URL(url));
        downloadFile.getFile().getFile().writeTo(System.out);
        endpoint.stop();
    }
}
