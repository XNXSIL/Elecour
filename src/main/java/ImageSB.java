import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import sun.misc.BASE64Encoder;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
public class ImageSB implements Runnable{
    private String name;
    private String password;
    private String[] courseid;
    private String[] coursenumber;
    private boolean[] state;
    private boolean stop;
    public boolean OK;
    private JTextArea result;
    int totalnumber;
    private static final String STR_URL = "http://210.39.12.30/xsxkapp/sys/xsxkapp/*default/index.do";
    private static final String Other = "aRecommendCourse";
    private static final String[] Course = new String[5];
    private static final String[] Content = new String[5];
    private HtmlPage mainPage;
    private DomElement body;
    private DomElement course;
    public ImageSB(String name, String password, JTextArea resultArea){
        this.name = name;
        this.password = password;
        Course[0] = "aProgramCourse";
        Course[1] = "aUnProgramCourse";
        Course[2] = "aRetakeCourse";
        Course[3] = "aSportCourse";
        Course[4] = "aMoocCourse";
        Content[0] = "program";
        Content[1] = "unProgram";
        Content[2] = "retake";
        Content[3] = "sport";
        Content[4] = "mooc";
        stop = false;
        OK = false;
        this.result = resultArea;
    }
    public void run() {
        try{
            //模拟火狐浏览器
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //浏览器的配置
            SetOptions(webClient);
            //打开网页并获取页面信息
            HtmlPage page = webClient.getPage(STR_URL);
            //获取页面中验证码信息
            DomElement domElement = page.getElementById("vcodeImg");
            //获取验证码图片URL
            String str = domElement.getAttribute("src");
            System.out.println(str);
            //获取验证码的内容
            String YZM = Aliyun_YZMSB.getContext(TO_BASE64(str));
            System.out.println(YZM);
            //输入用户、密码、验证码
            DomElement name = page.getElementById("loginName");
            DomElement password = page.getElementById("loginPwd");
            DomElement code = page.getElementById("verifyCode");
            DomElement submit = page.getElementById("studentLoginBtn");
            name.setAttribute("value",this.name);
            password.setAttribute("value",this.password);
            code.setAttribute("value",YZM);
            //登陆
            submit.click();
            //开始选课
            DomElement courseBtn = page.getElementById("courseBtn");
            mainPage = courseBtn.click();
            //查看收藏夹中的课程，并将课程编号导入链表中
            DomElement aside = mainPage.getElementById("cvAside").getFirstElementChild().getFirstElementChild();
            DomElement store = aside.getElementsByTagName("div").get(3);
            store.click();
            DomElement courses = mainPage.getElementById("cvCollectionCourse").getLastElementChild();
            int number = courses.getChildElementCount();
            courseid = new String[number];
            coursenumber = new String[number];
            state = new boolean[number];
            totalnumber = number;
            int Total = number;
            Iterable<DomElement> course_child = courses.getChildElements();
            for(DomElement x : course_child){
                number--;
                courseid[number] = x.getFirstElementChild().asText();
                coursenumber[number] = x.getElementsByTagName("div").get(3).asText();
                state[number] = false;
            }
            DomElement hadSelect = mainPage.getElementById("selectcourse_num");
            hadSelect.click();
            DomElement Cour = mainPage.getElementById("cvTbody");
            Iterable<DomElement> hadCourse_child = Cour.getChildElements();
            for(DomElement x: hadCourse_child){
                for(int i=0; i<Total; i++){
                    String tname = x.getElementsByTagName("td").get(1).asText();
                    if(coursenumber[i].equals(tname)){
                        state[i] = true;
                        result.append(courseid[i] + "已选！\n");
                        totalnumber--;
                    }
                }
            }
            while(totalnumber > 0 && !stop){
                if(number == Total)
                    number = 0;
                if(!state[number]){
                    SelectCourse(number);
                }
                number++;
                Thread.sleep(200);
            }
            if(totalnumber <= 0)
                OK = true;
        } catch (Exception e) {
            stopThread();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            startThread();
        }
    }
    public void stopThread(){
        stop = true;
    }
    public void startThread(){
        stop = false;
    }
    public void SelectCourse(int number) throws IOException {
        for(int i=0; i<5; i++){
            DomElement aProgramCourse = mainPage.getElementById(Course[i]);
            aProgramCourse.click();
            DomElement search = mainPage.getElementById(Content[i] + "Search");
            search.setAttribute("value",courseid[number]);
            aProgramCourse = mainPage.getElementById(Other);
            aProgramCourse.click();
            aProgramCourse = mainPage.getElementById(Course[i]);
            aProgramCourse.click();
            body = mainPage.getElementById(Content[i] + "Body");
            course = body.getFirstElementChild();
            if(!course.getFirstElementChild().asText().equals("没有数据")){
                course.click();
                DomElement section = course.getElementsByTagName("section").get(0);
                Iterable<DomElement> course_child = section.getChildElements();
                for(DomElement x : course_child){
                    String techear = x.getFirstElementChild().getFirstElementChild().getFirstElementChild().asText();
                    if(techear.indexOf(coursenumber[number]) != -1){
                        result.append(techear + "   ");
                        if(x.getFirstElementChild().getElementsByTagName("div").get(3).asText().indexOf("不") == -1){
                            x.click();
                            DomElement OK = x.getLastElementChild().getLastElementChild().getFirstElementChild();
                            OK.click();
                            state[number] = true;
                            totalnumber--;
                            result.append("抢课成功!\n");
                        }else{
                            result.append("抢课失败!\n");
                        }
                    }
                }
                break;
            }
        }
    }
    /**
     * 讲图片网络资源进行base64编码
     * @param ImgUrl
     * @return
     * @throws IOException
     */
    public String TO_BASE64(String ImgUrl) throws IOException {
        String base64String;
        URL url = new URL(ImgUrl);
        URLConnection connection = url.openConnection();
        try(
                InputStream in = connection.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
        ){
            System.out.println(" start to download");
            int len;
            byte[] buff = new byte[1024];
            while((len = in.read(buff)) != -1)
                out.write(buff,0,len);
            byte[] data = out.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            base64String = "data:image/jpeg;base64," + encoder.encode(data);
        }
        return base64String;
    }
    public void SetOptions(WebClient webClient){
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setTimeout(10000);
        webClient.getOptions().setDoNotTrackEnabled(false);
        webClient.setJavaScriptTimeout(8000);
        webClient.waitForBackgroundJavaScript(500);
    }
}
