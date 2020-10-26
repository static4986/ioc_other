package cglibGamer;

public class CglibGamePlayer {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void login(){
        System.out.println("玩家"+this.getName()+"登录了游戏");
    }

    public void killLittleSoldier(){
        System.out.println("玩家"+this.getName()+"杀死了50个小兵");
    }
}
