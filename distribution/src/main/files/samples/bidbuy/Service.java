package samples.bidbuy ;

public class Service implements java.io.Serializable {
    private String ServiceName;
    private String ServiceUrl;
    private String ServiceType;
    private String ServiceWsdl;

    public String getServiceName() {
       return ServiceName;
    }

    public void setServiceName(String value) {
       ServiceName = value;
    }

    public String getServiceUrl() {
       return ServiceUrl;
    }

    public void setServiceUrl(String value) {
       ServiceUrl = value;
    }

    public String getServiceType() {
       return ServiceType;
    }

    public void setServiceType(String value) {
       ServiceType = value;
    }

    public String getServiceWsdl() {
       return ServiceWsdl;
    }

    public void setServiceWsdl(String value) {
       ServiceWsdl = value;
    }
}
