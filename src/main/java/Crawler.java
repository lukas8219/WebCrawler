import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Crawler extends Thread {

    private ArrayList<String> nextToVisit;
    private String toVisit;
    private String tag;
    private String attribute;

    public Crawler(String toVisit, String tag, String attribute){
        this.toVisit = toVisit;
        this.tag = tag;
        this.attribute = attribute;
        this.nextToVisit = new ArrayList<>();
    }

    public ArrayList<String> getNextToVisit(){
        return this.nextToVisit;
    }

    @Override
    public void run(){
        //exec crawling.

        if(this.toVisit == null) return;
        if(! this.toVisit.contains("https") ||
                ! this.toVisit.contains("http")) this.toVisit = "https:"+this.toVisit;

        try {
            Connection connect = Jsoup.connect(this.toVisit);
            Document doc = connect.get();
            Elements element = doc.select(tag+"["+attribute+"]");
            for(Element e : element){
                String fetchedUrl = e.attributes().get(attribute);
                //TODO refactor conditional
                if(fetchedUrl.contains("https://") ||
                fetchedUrl.contains("http://")) this.nextToVisit.add(fetchedUrl);
            }
        } catch(IOException e){
        }

    }
}
