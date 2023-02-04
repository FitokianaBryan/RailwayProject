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
    Connexion con = new Connexion();
    Connection con1;
    {
        try {
            con1 = ManipDb.pgConnect("postgres","railway","xdUc1BXEMu9U6UjW8VmL");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("HistoriqueEncherisseur")
    public ResponseEntity<List<Object[]>> HistoriqueEncherisseur(@RequestHeader("token") String token)
    {
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu;
//        con.Resolve();
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
//         finally { con.CloseRC(); }
    }

    @GetMapping("HistoriqueVente")
    public ResponseEntity<List<Object[]>> HistoriqueVente(@RequestHeader("token") String token)
    {
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu;
//        con.Resolve();
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
//        finally { con.CloseRC(); }
    }

    @GetMapping("ResultatEnchere")
    public ResponseEntity<List<ResultatEnchere>> HistoriqueVente(@RequestParam("idEnchere") int idEnchere) throws Exception
    {
        try {
            if(con1 == null) {
                con1 = ManipDb.pgConnect("postgres","railway","9EHRLZ2xGeZ0Vu7ZMuAn");
            }
                return new ResponseEntity<List<ResultatEnchere>>(new HistoriqueOffreDao().userGagnant(con1,idEnchere), HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
//        finally { con1.close(); }
    }

    @GetMapping("ResultatEnchere/{idEnchere}")
    public ResponseEntity<List<Object[]>> ResultatEnchere(@PathVariable("idEnchere") int idEnchere)
    {
        try {
//            con.Resolve();
            return new ResponseEntity<List<Object[]>>(new HistoriqueOffreDao().userGagnantView(con,idEnchere), HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
//        finally { con.CloseRC(); }
    }

}
