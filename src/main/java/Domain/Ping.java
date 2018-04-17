package Domain;

public class Ping {
    private String endPoint;
    private String body;
    private String response;

    public Ping(String endPoint, String body) {
        this.endPoint = endPoint;
        this.body = body;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getBody() {
        return body;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
