public class Circle extends Thread{
    private ImageSB sb;
    public Circle(ImageSB s){
        sb = s;
    }
    public void run(){
        while(!sb.OK){
            Thread thread = new Thread(sb);
            thread.start();
            try {
                Thread.sleep(1000*60*9);
                sb.stopThread();
                Thread.sleep(2000);
                sb.startThread();
                System.out.println("==========重启=========");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
