package com.example.demo.controllerAPI;


import com.example.demo.connex.Connexion;
import com.example.demo.dao.*;
import com.example.demo.models.Enchere;
import com.example.demo.models.Response;
import com.example.demo.models.TokenUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.ObjectBdd.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/enchere")
@CrossOrigin
public class EnchereRestController {

    EnchereDao ed = new EnchereDao();

    ProduitDao p = new ProduitDao();
    HistoriqueOffreDao hod = new HistoriqueOffreDao();

    PrelevementEnchereDao ped = new PrelevementEnchereDao();

    @GetMapping("listeEnchere")
    public ResponseEntity<List<Enchere>> getListeEnchere() throws Exception {
        Connexion con1 = new Connexion();
        Connection con = ManipDb.pgConnect("postgres","railway","DQqp3epOn5BfOG5n4Onx");
        try{
            List<Enchere> list = ed.getListEnchere(con);
            for(Enchere e : list)
            {
                    ed.EnchereTerminer(con1,e.getIdenchere());
            }
            return new ResponseEntity<List<Enchere>>(list,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally {
            if(con != null) con.close();
            con1.Close();
        }
    }

    @GetMapping("listeEnchereTerminer")
    public ResponseEntity<List<Enchere>> getListeEnchereTerminer() throws Exception {
        Connexion con1 = new Connexion();
        Connection con = ManipDb.pgConnect("postgres","railway","DQqp3epOn5BfOG5n4Onx");
        try{
            List<Enchere> list1 = ed.getListEnchere(con);
            for(Enchere e : list1)
            {
                ed.EnchereTerminer(con1,e.getIdenchere());
            }
            List<Enchere> list = ed.getListEnchereTerminer(con);
            return new ResponseEntity<List<Enchere>>(list,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally {
            if(con != null) con.close();
            con1.Close();
        }
    }


    @GetMapping("ficheEnchere/{idEnchere}")
    public ResponseEntity<List<Object[]>> getFicheEnchere(@PathVariable int idEnchere){
        Connexion con1 = new Connexion();
        try{
            return new ResponseEntity<List<Object[]>>(new EnchereDao().FicheEnchere(con1,idEnchere), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally { con1.Close(); }
    }


    @GetMapping("ListeEnchereUser")
    public ResponseEntity<List<Object[]>> ListeEnchereUser(@RequestHeader("token") String token){
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu = new TokenUser();
        Connexion con1 = new Connexion();
        try{
            if(tud.validTokenUser(token)!=0)
            {
                tu = tud.getTokenUser(token);
                return new ResponseEntity<List<Object[]>>(new EnchereDao().getListeEnchereUser(con1,tu.getIdUtilisateur()), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally { con1.Close(); }
    }

    @PostMapping("AjoutEnchere")
    public Response AjoutEnchere(@RequestHeader("token") String token, @RequestParam("description") String description, @RequestParam("prixminimumvente") float prixminimumvente, @RequestParam("durreenchere") int durreenchere) throws Exception {
        Response response = new Response();
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu;
        Connexion con1 = new Connexion();
        if(tud.validTokenUser(token)!=0)
        {
                tu = tud.getTokenUser(token);
                float montant_user = new UtilisateurDao().getCompteUser(tu.getIdUtilisateur(),con1);
                if(montant_user<prixminimumvente)
                {
                     response.setStatus("400");
                     response.setMessage("votre solde est insuffisante");
                }
                else
                {
                    int result = ed.AjouterEncher(con1,tu.getIdUtilisateur(),description,prixminimumvente,durreenchere);
                    //compte user
                    hod.setCompteUser(tu.getIdUtilisateur(),prixminimumvente,con1);
                    //commission
                    ped.Inserer(con1,result,ed.MontantPrelevee(result));
                    response.setMessage("votre vente a été bien prise en compte");
                    response.setStatus("200");
                    response.setDatas(String.valueOf(result));
                }
        }
       else
       {
            response.setStatus("404");
            response.setMessage("veuillez dabord vous authentifier");
       }
       con1.Close();
        return response;
    }


    @PostMapping("ProduitEnchere/{idEnchere}")
    public Response ProduitEnchere(@PathVariable int idEnchere,@RequestParam("idProduit") int idProduit,@RequestHeader("token") String token) throws Exception {
        Response response = new Response();
        TokenUserDao tud = new TokenUserDao();
        Connexion con1 = new Connexion();
        if(tud.validTokenUser(token)!=0)
        {
            int result = p.AjouterProduitEnchere(con1,idEnchere,idProduit);
            response.setMessage("Ajout produit bien effectuee");
            response.setStatus("200");
            response.setDatas(String.valueOf(result));
        }
        else{
            response.setMessage("token expiré");
            response.setStatus("404");
        }
        con1.Close();
        return response;
    }


    @PostMapping("AjouterPhoto/{idproduit}")
    public Response AjoutPhotoEnchere(@PathVariable("idproduit") int idproduit,@RequestParam("photo") String photo,@RequestHeader("token") String token) throws Exception {
        Response response = new Response();
        TokenUserDao tud = new TokenUserDao();
        Connexion con1 = new Connexion();
        if(tud.validTokenUser(token)!=0)
        {
            p.AjouterPhotoProduit(con1,idproduit,photo);
            response.setMessage("image bien ajoutée");
        }
        else{
            response.setMessage("token expiré");
        }
        response.setMessage("mety");
        con1.Close();
        return response;
    }

    @PostMapping("/rechercheAvancée")
    public List<Enchere> advancedSearch(@RequestParam(required = false, value="datedebut") String startDate,
                                        @RequestParam(required = false, value="datefin") String endDate,
                                        @RequestParam(required = false, value="description") String category,
                                        @RequestParam(required = false, value="status") String auctionStatus,
                                        @RequestParam(required = false, value="motcle") String keywords,
                                        @RequestParam(required = false, value="typecategorie") String typecategorie) throws Exception {

        Connexion con1 = new Connexion();
        PreparedStatement stmt = ed.generateStatement(con1,startDate,endDate,category,auctionStatus,keywords,typecategorie);
        List<Enchere> encheres= null;
        try {
            encheres = ed.getListEnchereRecherche(stmt);
        } catch (Exception e) {
        }
        con1.Close();
        return encheres;
    }
}
