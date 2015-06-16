package thealphalabs.alphaapp.model;

/**
 * Created by sukbeom on 15. 6. 15.
 */
// HOME 페이지에 나타낼 데이터 모델
// Header, ImageView, Content 으로 구성되어 있다.
public class HomeDataModel {
    private String header;      // 제목
    private int resId;          // 사진
    private String content;     // 내용
    public HomeDataModel(String header, String content, int resId) {
        this.header = header;
        this.content = content;
        this.resId = resId;
    }

    // setter and getter
    public void setHeader(String header) {
        this.header = header;
    }
    public String getHeader() {
        return this.header;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return this.content;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
    public int getResId() {
        return this.resId;
    }
}
