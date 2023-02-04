package com.example.demo.controllerAPI;


import com.example.demo.ObjectBdd.ManipDb;
import com.example.demo.connex.Connexion;
import com.example.demo.dao.HistoriqueEnchereDao;
import com.example.demo.dao.HistoriqueOffreDao;
import com.example.demo.dao.TokenUserDao;
import com.example.demo.models.ResultatEnchere;
import com.example.demo.models.TokenUser;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.util.List;

@RestController
@RequestMapping("/api/historique")
@CrossOrigin
public class HistoriqueRestController {

    @GetMapping("HistoriqueEncherisseur")
    public ResponseEntity<List<Object[]>> HistoriqueEncherisseur(@RequestHeader("token") String token)
    {
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu;
        Connexion con = new Connexion();
         try {
             if(tud.validTokenUser(token)!=0)
             {
                 tu = tud.getTokenUser(token);
                 return new ResponseEntity<List<Object[]>>(new HistoriqueEnchereDao().HistoriqueEncherisseur(con,tu.getIdUtilisateur()), HttpStatus.OK);
             }
             else {
                 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
             }
         }
         catch(Exception e)
         {
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
         }
         finally { con.Close(); }
    }

    @GetMapping("HistoriqueVente")
    public ResponseEntity<List<Object[]>> HistoriqueVente(@RequestHeader("token") String token)
    {
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu;
        Connexion con = new Connexion();
        try {
            if(tud.validTokenUser(token)!=0)
            {
                tu = tud.getTokenUser(token);
                return new ResponseEntity<List<Object[]>>(new HistoriqueEnchereDao().HistoriqueVente(con,tu.getIdUtilisateur()), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally { con.Close(); }
    }

    @GetMapping("ResultatEnchere")
    public ResponseEntity<List<ResultatEnchere>> HistoriqueVente(@RequestParam("idEnchere") int idEnchere) throws Exception
    {
        Connection con1 = ManipDb.pgConnect("postgres","railway","DQqp3epOn5BfOG5n4Onx");
        try {
                return new ResponseEntity<List<ResultatEnchere>>(new HistoriqueOffreDao().userGagnant(con1,idEnchere), HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally { if(con1 != null) con1.close(); }
    }

    @GetMapping("ResultatEnchere/{idEnchere}")
    public ResponseEntity<List<Object[]>> ResultatEnchere(@PathVariable("idEnchere") int idEnchere)
    {
        Connexion con = new Connexion();
        try {
            return new ResponseEntity<List<Object[]>>(new HistoriqueOffreDao().userGagnantView(con,idEnchere), HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        finally { con.Close(); }
    }

}
