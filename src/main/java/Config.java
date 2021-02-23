public class Config {

    final protected int NUMBER_THREADS;
    final protected String START_LINK;
    final protected String TAG;
    final protected String ATTRIBUTE;
    final protected int MAX_LINKS;

    public Config(String[] args){
        this.START_LINK = args[0];
        this.TAG = args[1];
        this.ATTRIBUTE = args[2];
        this.MAX_LINKS = Integer.parseInt(args[3]);
        this.NUMBER_THREADS = Integer.parseInt(args[4]);
    }
}
