public class Tuple{

    /*
    * - Requirements:
    * - Create an object that holds two integers
    */


    public int x;
    public int y;

    public Tuple(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Tuple(){
        this.x = 0;
        this.y = 0;
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void setX(int i){
        this.x = i;
    }
    public void setY(int i){
        this.y = i;
    }
    public void set(int x, int y){
        this.x = x;
        this.y = y;
    }
    public String toString(){
        return "("+this.x+","+this.y+")";
    }
     
}
