package ctl;

import stereotype.Controller;
import annotation.Url;
import annotation.ModelAttribute;
import annotation.RequestParam;
import model.Personne;
import servlet.ModelView;
import servlet.support.SessionAttribute;

/**
 *
 * @author itu
 */
@Controller
public class UserCtl {

    SessionAttribute s = new SessionAttribute();

    //@Url("/form1")
    public ModelView form1(){
        ModelView m = new ModelView();
        m.setUrl("form.jsp");

        Personne r=new Personne();
        r.setAge(1);
        r.setNom("Essai");
        r.setPrenom("EncoreMonGrand");
        
        m.addObject("data",r);
        //m.addObject("m2",s.get("m2"));
        //m.addObject("m3",s.get("m3"));
        return m;
    }

    @Url(action = "/idk", verb = "GET")
    public String getSanAnnot() {
        return "GET Tsisy annot dada ah!";
    }

    @Url(action = "/idk", verb = "GET")
    public String getAnnoted() {
        return "Okay";
    }

    @Url(action = "/getAnnoted", verb = "POST")
    public String tryPost() {
        return "POST Misy annot dada ah!";
    }

    //@Url("/form2")
    public ModelView form2(){
        ModelView m = new ModelView();
        m.setUrl("form.jsp");
        s.remove("m1");
        return m;
    }

    //@Url("/pers")
    public Personne getPers() {
        Personne r=new Personne();
        r.setAge(1);
        r.setNom("Essai");
        r.setPrenom("Encore");
        return r; 
    }
}
