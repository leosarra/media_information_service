package core;

import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import framework.ApiOperations;
import framework.BadStatus;
import mediacontent.BookInfo;
import mediacontent.FilmInfo;
import mediacontent.GameInfo;
import mediacontent.MusicInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;

@Controller
public class RestServiceController {

    @RequestMapping(value="/search", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String searchRequest(@RequestParam(value="type") String type, @RequestParam(value="query") String name) {
        String ret;

        try {
        if(type.equals("book")) {
            ret=new Gson().toJson(ApiOperations.bookGetInfo(name,"0","all"));
        }
        else if (type.equals("film")) ret = new Gson().toJson(ApiOperations.filmGetInfo(name, "all"));

        else if (type.equals("music")) ret=new Gson().toJson(ApiOperations.musicGetInfo(name,"all"));
        else if (type.equals("game")) ret=new Gson().toJson(ApiOperations.gameGetInfo(name,"all"));
        else return null;
        return ret;
        } catch (UnirestException e) {
            e.printStackTrace();
            return new Gson().toJson(new BadStatus("Internal error"));
        }

    }


    @RequestMapping(value="/film/search", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String searchFilmRequest(@RequestParam(value="type") String type, @RequestParam(value="query") String name, @RequestParam(value="max_result",required = false, defaultValue="all") String max_result) {
        String ret;
        LinkedList<FilmInfo> lis;
        try {
            lis=ApiOperations.filmGetInfo(name,max_result);
        } catch (UnirestException e) {
            return new Gson().toJson(new BadStatus("Internal Error"));
        }
        return new Gson().toJson(lis);

    }

    @RequestMapping(value="/music/search", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String searchMusicRequest(@RequestParam(value="query") String name, @RequestParam(value="max_result",required = false, defaultValue="all") String max_result) {
        String ret;
        LinkedList<MusicInfo> lis;
        try {
            lis=ApiOperations.musicGetInfo(name,max_result);
        } catch (UnirestException e) {
            e.printStackTrace();
            return new Gson().toJson(new BadStatus("Internal Error"));
        }
        return new Gson().toJson(lis);

    }

    @RequestMapping(value="/book/search", method = RequestMethod.GET, produces = "application/json")
    public  @ResponseBody String searchBookRequest(@RequestParam(value="query") String name, @RequestParam(value="max_result",required = false, defaultValue="all") String max_result,
                                                   @RequestParam(value="isbn",required = false,defaultValue = "0")String isbn) {
        String ret;
        LinkedList<BookInfo> lis;
        try {
            lis=ApiOperations.bookGetInfo(name,isbn,max_result);
        } catch (UnirestException e) {
            e.printStackTrace();
            return new Gson().toJson(new BadStatus("Bad Request"));
        }
        return new Gson().toJson(lis);

    }

    @RequestMapping(value="/game/search", method = RequestMethod.GET, produces = "application/json")
    public  @ResponseBody String searchGameRequest(@RequestParam(value="query") String name, @RequestParam(value="max_result",required = false, defaultValue="all") String max_result) {
        String ret;
        LinkedList<GameInfo> lis;
        try {
            lis=ApiOperations.gameGetInfo(name,max_result);
        } catch (UnirestException e) {
            e.printStackTrace();
            return new Gson().toJson(new BadStatus("Internal error"));
        }
        return new Gson().toJson(lis);

    }



}
