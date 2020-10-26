package cglibGamer;

public class Execute {

    public static void main(String[] args) {
        System.out.println("-----CGLIB代练-----");
        CglibGamePlayer cglibGamePlayer = new CglibGamePlayer();
        cglibGamePlayer.setName("艺术就是派大星");
        CglibProxy proxy = new CglibProxy();
        CglibGamePlayer cglibGamePlayer1 = (CglibGamePlayer)proxy.getInstance(cglibGamePlayer);
        cglibGamePlayer1.login();
    }
}
