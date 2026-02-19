package dev.selimaj.session.types;

import com.fasterxml.jackson.databind.JsonNode;

public class Method<Req extends JsonNode, Res extends JsonNode, Err extends JsonNode> {
    private final String name;
    private final Class<Req> reqClass;
    private final Class<Res> resClass;
    private final Class<Err> errClass;

    protected Method(String name, Class<Req> reqClass, Class<Res> resClass, Class<Err> errClass) {
        this.name = name;
        this.reqClass = reqClass;
        this.resClass = resClass;
        this.errClass = errClass;
    }

    public String getName() {
        return name;
    }

    public Class<Req> getReqClass() {
        return reqClass;
    }

    public Class<Res> getResClass() {
        return resClass;
    }

    public Class<Err> getErrClass() {
        return errClass;
    }
}