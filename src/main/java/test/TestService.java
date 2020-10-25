package test;

import annotation.Autowired_other;
import annotation.Service_other;

@Service_other
public class TestService {

    private String name;

    @Autowired_other
    private TestDao dao;

    public void talk(){
        System.out.println("this is method");
        dao.say();
    }
}
