package com.example.demo.controllerVIEW;


import com.example.demo.ObjectBdd.ManipDb;
import com.example.demo.connex.Connexion;
import com.example.demo.dao.AdminDao;
import com.example.demo.dao.PrelevementEnchereDao;
import com.example.demo.dao.TokenAdminDao;
import com.example.demo.dao.UtilisateurDao;
import com.example.demo.models.Admin;
import com.example.demo.models.CategorieProduit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.List;

@Controller
public class AdministrateurController {

//    Connexion con = null;
//
//    Connection con1 = ManipDb.pgConnect("postgres","railway","xdUc1BXEMu9U6UjW8VmL");

    PrelevementEnchereDao p = new PrelevementEnchereDao();
    AdminDao a = new AdminDao();

    CategorieProduit cp = new CategorieProduit();
    UtilisateurDao ud = new UtilisateurDao();

    public AdministrateurController() throws Exception {
    }

    @GetMapping("/")
    public String logadmin(Model model) {
        model.addAttribute("email", "admin@gmail.com");
        model.addAttribute("mdp","admin");
        return "logadmin";
    }


    @PostMapping("/login")
    public String traitementLogin(HttpServletRequest request,Model m) throws Exception{
        HttpSession session = request.getSession(true);
        String id = request.getParameter("email");
        String mdp = request.getParameter("mdp");
        AdminDao dao = new AdminDao();
        TokenAdminDao tokendao= new TokenAdminDao();
        if(dao.login(id,mdp)!=null){
            Admin admin = dao.login(id,mdp);
            String token_admin = tokendao.insertTokenAdmin(admin);
            session.setAttribute("admin", admin);
            session.setAttribute("token",token_admin);
            return "redirect:/backOffice";
        }else{
            return "redirect:/?error=1";
        }
    }

    @RequestMapping("/backOffice")
    public String backOffice(HttpServletRequest request) throws Exception {

        HttpSession session = request.getSession();
        Connection con1 = ManipDb.pgConnect("postgres","railway","DQqp3epOn5BfOG5n4Onx");
        Connexion con = new Connexion();
        String redirect = "";
        if(session.getAttribute("admin")!=null){
            List<CategorieProduit> listeCategorie = cp.getListCategorie(con1);
            request.setAttribute("chiffreAffaire",p.ChiffreAffaire(con));
            request.setAttribute("listeCategorie",listeCategorie);
            redirect = "backOffice";
        }else {
            redirect = "/";
        }
        con.Close();
        return redirect;
    }

    @RequestMapping("/ListeRechargementCompte")
    public String ListeRechargementCompte(HttpServletRequest request) throws Exception{
        HttpSession session = request.getSession();
        Connexion con = new Connexion();
        String redirect = "";
        if(session.getAttribute("admin")!=null){
            request.setAttribute("chiffreAffaire",p.ChiffreAffaire(con));
            List<Object[]> listeRechargementCompte = a.listeRechargementCompte(con);
            request.setAttribute("listeRechargementCompte", listeRechargementCompte);
            redirect = "ListeRechargementCompte";
        }else {
            redirect = "/";
        }
        con.Close();
        return redirect;
    }

    @RequestMapping("/Validation/{idRechargementCompte}/{idUtilisateur}/{montant}")
    public String ListeRechargementCompte(HttpServletRequest request,@PathVariable int idRechargementCompte,@PathVariable int idUtilisateur,@PathVariable float montant) throws Exception{
        HttpSession session = request.getSession();
        Connexion con = new Connexion();
        String redirect = "";
        if(session.getAttribute("admin")!=null){
            a.ValiderRechargementCompte(idRechargementCompte,con);
            a.setCompteUser(idUtilisateur,montant,con);
            request.setAttribute("chiffreAffaire",p.ChiffreAffaire(con));
            List<Object[]> listeRechargementCompte = a.listeRechargementCompte(con);
            request.setAttribute("listeRechargementCompte", listeRechargementCompte);
            redirect = "redirect:/backOffice";
        }else {
            redirect = "/";
        }
        con.Close();
        return redirect;
    }

    @RequestMapping("/logout")
    public String deconnexion(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("admin");
        TokenAdminDao dao = new TokenAdminDao();
        dao.deleteTokenAdmin((String) session.getAttribute("token"), admin.getIdadmin());
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/newCategorie")
    public String newCategorie(HttpServletRequest request) throws Exception {
        String typecategorie = request.getParameter("typeCategorie");
        Connexion con = new Connexion();
        cp.setTypeCategorie(typecategorie);
        cp.NewCategorie(con);
        con.Close();
        return "redirect:/backOffice";
    }

    @PostMapping("/pourcentage")
    public String setPourcentage(HttpServletRequest request) throws Exception {
        float pourcentage = Float.parseFloat(request.getParameter("pourcentage"));
        Connexion con = new Connexion();
        p.setPourcentage(con,pourcentage);
        con.Close();
        return "redirect:/backOffice";
    }


}

