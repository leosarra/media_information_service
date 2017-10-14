package core;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import framework.*;
import mediacontent.*;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Controller
public class MainController {

    @GetMapping("/media_book")
    public String mediaBookForm(Model model) {
        Media b = new Media();
        model.addAttribute("media", b);
        return "media_book";
    }

    @GetMapping("/media_music")
    public String mediaMusicForm(Model model) {
        Media b = new Media();
        model.addAttribute("media", b);
        return "media_music";
    }

    @GetMapping("/media_film")
    public String mediaFilmForm(Model model) {
        Media b = new Media();
        model.addAttribute("media", b);
        return "media_film";
    }


    @PostMapping("/media_book")
    public String mediaBookSubmit(@ModelAttribute Media media, Model model) {
        System.out.println("TITOLO:"+ media.getTitle());
        System.out.println(media.getISBN());
        LinkedList<BookInfo> a = ApiOperations.bookGetInfo(media.getTitle(), media.getISBN(), "4");
        model.addAttribute("mediaList", a);
        return "result_book";
    }


    @PostMapping("/media_film")
    public String mediaFilmSubmit(@ModelAttribute Media media, Model model) {
        LinkedList<FilmInfo> a = null;
        try {
            a = ApiOperations.filmGetInfo(media.getTitle(), "4");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).toString();
        }

        model.addAttribute("mediaList", a);
        return "result_film";
    }

    @PostMapping("/media_music")
    public String mediaMusicSubmit(@ModelAttribute Media media, Model model) {
        LinkedList<MusicInfo> a = null;
        try {
            a = ApiOperations.musicGetInfo(media.getTitle(), "4");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).toString();
        }
        model.addAttribute("mediaList", a);
        return "result_music";
    }


    @GetMapping("/authentication")
    public String authForm(Model model) {
        Authentication a = new Authentication();
        model.addAttribute("authentication", a);
        return "authentication";
    }

    @PostMapping("/authentication")
    public String authSubmit(@ModelAttribute Authentication a) {
        if (a.getContent().equals("Ciao ciao")) return "redirect:/";
        return "result_auth";
    }


    @RequestMapping(value = "/drivecallback", method = {RequestMethod.GET, RequestMethod.POST})
    public Greeting driveFlow(@RequestParam(value = "code", defaultValue = "nope") String code) {
        if (code.equals("nope")) {

            System.out.println("Errore, code vuoto");
        }
        String client_id_web = MyAPIKey.getDrive_id();
        String client_secret_web =MyAPIKey.getDrive_secret();

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource webResource = client.resource(UriBuilder.fromUri("https://www.googleapis.com/oauth2/v4/token").build());
        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add("code", code);
        formData.add("client_id", client_id_web);
        formData.add("redirect_uri", "http://localhost:8080/drivecallback");
        formData.add("client_secret",
                client_secret_web);
        formData.add("grant_type", "authorization_code");
        ClientResponse response1 = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        String json = response1.getEntity(String.class);
        System.out.println(json);
        JSONObject jsonObj = new JSONObject(json);
        String token=jsonObj.getString("access_token");
        System.out.println(token);
        try {
            List<String> names= GDrvApiOp.retrieveAllFiles(token,"Università");
            List<FilmInfo> films=new LinkedList<FilmInfo>();
            List<BookInfo> books=new LinkedList<BookInfo>();
            List<MusicInfo> songs=new LinkedList<MusicInfo>();
            MediaOperations.findMediaInfo(names,books,films,songs);
            System.out.println(films.toString());
            System.out.println(books.toString());
            System.out.println(songs.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return new Greeting();


    }

    @RequestMapping(value = "/dropboxcallback", method = {RequestMethod.GET, RequestMethod.POST})

    public Greeting dropboxFlow(@RequestParam(value = "code", defaultValue = "nope") String code) {
        System.out.println("TEST");
        System.out.println(code);
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource webResource = client.resource(UriBuilder.fromUri("https://api.dropboxapi.com/oauth2/token").build());
        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add("code", code);
        formData.add("client_id", MyAPIKey.getDropbox_id());
        formData.add("redirect_uri", "http://localhost:8080/dropboxcallback");
        formData.add("client_secret",
                MyAPIKey.getDropbox_secret());
        formData.add("grant_type", "authorization_code");
        ClientResponse response1 = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
        String json = response1.getEntity(String.class);
        JSONObject jsonObj = new JSONObject(json);
        String token=jsonObj.getString("access_token");
        DbxAPIOp.dropboxGetFiles(token);

        return new Greeting();

    }

}







