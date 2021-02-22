import java.util.*;

public class WebCrawler {

    public static void main(String[] args){

        if(args.length == 0){
            System.out.println("\nPlease input as follow : <startLink> <tag> <attribute> <maxVisits> <Threads>");
            System.out.println("\n<maxVisit> if not specified is infinite.");
            System.out.println("<Threads> if not specified will be automatic to the number of links");
            System.exit(1);
        }

        int numberOfThreads = Integer.parseInt(args[4]);
        String startLink = args[0];
        String tag = args[1];
        String attr = args[2];
        int numberOfVisits = Integer.parseInt(args[3]);
        start(numberOfThreads,startLink, tag,attr, 10);
    }

    public static void start(int numberOfThreads, String startLink, String tag, String attr, int numberOfVisits){

        System.out.print("[Initializing...] "+numberOfThreads+" Threads on "+startLink);
        System.out.print(" looking for Tag <"+tag+"> and Attribute "+attr+"\n");

        Set<String> visitedUrl = new HashSet<>();

        Queue<String> toVisitUrl = new LinkedList<>();

        toVisitUrl.add(startLink);


        System.out.println("[Starting] Crawling...");
        System.out.println("[Start Time] " +new Date());
        while(numberOfVisits > visitedUrl.size() && ! toVisitUrl.isEmpty()){
            ArrayList<Crawler> crawlers = new ArrayList<>();


            for(int i=0; i<numberOfThreads; i++){
                String url = toVisitUrl.poll();
                Crawler crawler = new Crawler(url, tag, attr);
                crawlers.add(crawler);
                visitedUrl.add(url);
                crawler.start();
            }

            stopAllThreads(crawlers);

            for(Crawler crawler : crawlers){
                toVisitUrl.addAll(crawler.getNextToVisit());
            }
        }
        System.out.println("[End Time] " +new Date());
        if(numberOfVisits < visitedUrl.size()) System.out.println("[Warning!] : Crawling stopped before limit");
        System.out.println("[Log] : Visited "+visitedUrl.size()+ " links");
    }

    public static void stopAllThreads(ArrayList<Crawler> crawlers){
        crawlers.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e){}
        });
    }
}
