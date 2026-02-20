package dev.selimaj.session.types;

public class Method<Req, Res, Err> {
    private final String name;
    private final Class<Req> reqClass;
    private final Class<Res> resClass;
    private final Class<Err> errClass;

    public Method(String name, Class<Req> reqClass, Class<Res> resClass, Class<Err> errClass) {
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