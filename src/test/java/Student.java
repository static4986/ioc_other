public class Student {

    private String name="惠普";

    private static String staticName= "联想";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getStaticName() {
        return staticName;
    }

    public static void setStaticName(String staticName) {
        Student.staticName = staticName;
    }

    public void sayHuipu(){
        System.out.println("**********"+name);
    }

    public void sayLenovo(){
        System.out.println("************"+staticName);
    }
}
