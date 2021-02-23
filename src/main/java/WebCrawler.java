import java.util.*;
import java.util.stream.Collectors;

public class WebCrawler {

    public static void main(String[] args){

        if(args.length == 0){
            System.out.println("\nPlease input as follow : <startLink> <tag> <attribute> <maxVisits> <Threads>");
            System.out.println("\n<maxVisit> if not specified is infinite.");
            System.out.println("<Threads> if not specified will be automatic to the number of links");
            System.exit(1);
        }

        Config configuration = new Config(args);

        start(configuration);
    }

    public static void start(Config config){
        printStartLog(config);
        Set<String> visitedUrl = executeBreadthFirstSearch(config);
        printFinishLog(config, visitedUrl);
    }

    private static Set<String> executeBreadthFirstSearch(Config config){

        Set<String> visitedUrl = new HashSet<>();
        Queue<String> toVisitUrls = new LinkedList<>();
        toVisitUrls.add(config.START_LINK);

        while(config.MAX_LINKS > visitedUrl.size() && ! toVisitUrls.isEmpty()){

            System.out.println("[DONE] "+visitedUrl.size()+ " links.");
            toVisitUrls.removeAll(visitedUrl);
            System.out.println("[TO FETCH] "+toVisitUrls.size()+ " links");

            ArrayList<Crawler> crawlers = generateCrawlers(config, toVisitUrls);

            startCrawlers(crawlers);
            joinCrawlers(crawlers);

            visitedUrl.addAll(getCrawledLinks(crawlers));
            toVisitUrls.addAll(getFetchedUrlFromCrawlers(crawlers));
        }

        return visitedUrl;
    }

    private static void joinCrawlers(ArrayList<Crawler> crawlers){

        System.out.println("[WAITING] "+crawlers.size()+" Crawlers");

        crawlers.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e){}
        });

        System.out.println("[LOG] DONE.");
    }

    private static Queue<String> getFetchedUrlFromCrawlers(ArrayList<Crawler> crawlers){

        Queue<String> fetchedUrl = new LinkedList<>();

        for(Crawler crawler : crawlers){
            fetchedUrl.addAll(crawler.getNextToVisit());
        }

        return fetchedUrl;
    }

    private static ArrayList<Crawler> generateCrawlers(Config config, Queue<String> toVisitUrl){

        int numberOfThreads = config.NUMBER_THREADS;
        if(numberOfThreads == 0) numberOfThreads = toVisitUrl.size();

        ArrayList<Crawler> crawlers = new ArrayList<>();

        for(int i=0; i<numberOfThreads; i++){
            String url = toVisitUrl.poll();
            Crawler crawler = new Crawler(url, config.TAG, config.ATTRIBUTE);
            crawlers.add(crawler);
        }

        return crawlers;
    }

    private static void startCrawlers(ArrayList<Crawler> crawlers){

        System.out.println("[SENDING] "+crawlers.size()+" Crawlers");

        crawlers.forEach(thread-> {
                thread.start();
        });

        System.out.println("[LOG] Crawlers sent");

    }

    private static List<String> getCrawledLinks(ArrayList<Crawler> crawlers){
        return crawlers.stream()
                .map(x -> x.getToVisit())
                .collect(Collectors.toList());
    }

    private static void printStartLog(Config config){
        System.out.print("[INITIALIZING] "+config.NUMBER_THREADS
                +" Threads on "+config.START_LINK);
        System.out.print(" looking for Tag <"+config.TAG+"> and Attribute "+config.ATTRIBUTE+"\n");
        System.out.println("[STARTING] Crawling...");
        System.out.println("[START-TIME] " +new Date());
    }

    private static void printFinishLog(Config config, Set<String> visitedUrl){
        System.out.println("[END-TIME] " +new Date());
        if(config.MAX_LINKS < visitedUrl.size()) System.out.println("[WARNING!] : Crawling passed the limit");
        System.out.println("[LOG] : Visited "+visitedUrl.size()+ " links");
    }
}
