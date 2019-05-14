package cn.laymm.BuiltIn.lmUplod.pojo;

/**
 * Created by Administrator on 2019/1/13 0013.
 */
public class LmUpload {

    private Integer code;
    private String msg;
    private String filename;
    private String name;

    public LmUpload() {
    }

    public LmUpload(Integer code, String filename, String name) {
        this.code = code;
        this.filename = filename;
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LmUpload{" +
                "code=" + code +
                ", filename='" + filename + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
