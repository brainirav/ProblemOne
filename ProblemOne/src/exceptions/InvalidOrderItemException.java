package exceptions;

public class InvalidOrderItemException extends Exception {
    public String toString(){
        return "You have entered invalid item(s).";
    }
}
