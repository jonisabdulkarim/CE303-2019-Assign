public class Request {

    RequestType type;
    String[] params;

    private Request(RequestType type, String... params){
        this.type = type;
        this.params = params;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        for (String param : params) sb.append(" ").append(param);
        return sb.toString();
    }

    static Request parse(String line){
        String[] args = line.trim().split("\\s+");
        try{
            switch(args[0].toUpperCase()){
                case "MOVE":
                    return new Request(RequestType.MOVE, args[1], args[2], args[3]);
                case "EXIT":
                    return new Request(RequestType.EXIT);
            }
        } catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return new Request(RequestType.INVALID, line);
    }

}
