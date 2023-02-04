package com.example.demo.controllerAPI;



import com.example.demo.ObjectBdd.ManipDb;
import com.example.demo.connex.Connexion;
import com.example.demo.dao.TokenUserDao;
import com.example.demo.dao.UtilisateurDao;
import com.example.demo.models.Response;
import com.example.demo.models.TokenUser;
import com.example.demo.models.Utilisateur;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;

@RestController
@CrossOrigin
@RequestMapping("/api/utilisateur")
public class UtilisateurRestController {

    UtilisateurDao ud = new UtilisateurDao();
    Connection con;
    {
        try {
            con = ManipDb.pgConnect("postgres","railway","xdUc1BXEMu9U6UjW8VmL");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    Connexion con1 = new Connexion();

    @PostMapping("/Inscription")
    public Response traitementInscription(@RequestParam("nom") String nom, @RequestParam("prenom") String prenom, @RequestParam("email") String email, @RequestParam("mdp") String mdp)
    {
        Response response = new Response();
//        con1.Resolve();
        try{
            ud.Inscription(con1,nom,prenom,email,mdp);
            response.setStatus("200");
            response.setMessage("Iscription reussie");
        }
        catch(Exception e)
        {
            response.setStatus("400");
            response.setMessage("Inscripiton impossible");
        }
//        con1.CloseSC();
        return response;
    }

    @PostMapping("/login")
    public Response login(@RequestParam("email")String email,@RequestParam("mdp")String mdp) throws Exception {
        String token = null;
        Response response = new Response();
        TokenUserDao t = new TokenUserDao();
        Utilisateur u = ud.login(email,mdp);
        if(u!=null)
        {
            token = t.insertTokenUser(u);
            response.setStatus("200");
            response.setMessage("login reussi");
            response.setDatas(token);
        }else{
            response.setStatus("401");
            response.setMessage("Mot de passe ou email incorrect");
            response.setDatas(null);
        }
        return response;
    }

    @PostMapping("/rechargementCompte")
    public Response rechargementCompte(@RequestParam("montant") float montant,@RequestHeader("token") String token)
    {
        Response response = new Response();
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu;
//        con1.Resolve();
        try {
                    if(tud.validTokenUser(token)!=0)
                    {
                    tu = tud.getTokenUser(token);
            ud.rechargerCompte(tu.getIdUtilisateur(),montant,con1);
            response.setMessage("transaction effectuee");
              }
             else
             {
                response.setMessage("expiration token");
             }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
//            con1.CloseSC();
            return response;
        }
    }

    @GetMapping("/getCompteUser")
    public float getCompteUser(@RequestHeader("token") String token) throws Exception {
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu;
        if(tud.validTokenUser(token)!=0)
        {
            tu = tud.getTokenUser(token);
            return ud.getCompteUser(tu.getIdUtilisateur(),con1);
        }
        else {
            return 0.0f;
        }
    }


}

