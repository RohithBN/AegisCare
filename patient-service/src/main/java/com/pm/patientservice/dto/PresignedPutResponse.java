package com.pm.patientservice.dto;

import java.util.List;
import java.util.Map;

public class PresignedPutResponse {
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PresignedPutResponse(String url, Map<String, List<String>> headers) {
        this.url = url;
        this.headers = headers;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public  String url;
    public Map<String, List<String>> headers;

}
