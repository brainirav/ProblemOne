import client.Item;
import client.Order;
import exceptions.InvalidOrderItemException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {

    private static List<Order> orders = new ArrayList<>();
    private static List<String> output = new ArrayList<>();


    public static void main(String[] args) throws InvalidOrderItemException{
        takeUserInput();
        printItems(getTwoMostFrequentlyOrderedItems());
    }

    private static void takeUserInput() throws InvalidOrderItemException{
        Scanner scanner = new Scanner(System.in);
        System.out.print("Welcome...\n\n" +
                "Enter Number of Orders: ");

        // Take total number of orders in input
        int orderCount = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter comma separated items for the corresponding order");
        // Run a loop for orders and take items in input for each order
        for(int i=0; i<orderCount; i++){
            System.out.print("Order "+ (i+1) +" -> ");
            String itemsStr = scanner.nextLine();
            //create order object with the entered items and add order object to the order list
            orders.add(new Order(getItemsListFromString(itemsStr)));
        }
    }

    private static List<Item> getItemsListFromString(String itemsStr) throws InvalidOrderItemException{
        List<Item> items = new ArrayList<>();


        try{
            //covert the comma separated item values to list of strings
            List<String> itemIds = new ArrayList<String>(Arrays.asList(itemsStr.split("\\s*,\\s*")));
            //create new Item object for each and add in items list
            for(String id : itemIds){
                items.add(new Item(id));
            }
        }catch(Exception e){
            throw new InvalidOrderItemException();
        }
        return items;
    }

    private static List<String> getTwoMostFrequentlyOrderedItems(){
        List<String> mItemIds = new ArrayList<>();
        //hashmap having item name in key and value contains how many times it is ordered
        Map<String, Integer> itemOrderedCount = new HashMap<>();

        //getting all the items from various orders in a list
        List<String> allItemIds = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(i -> i.getId())
                .collect(Collectors.toList());

        //populating hashmap using all item values
        for(String id : allItemIds){
            if(itemOrderedCount.containsKey(id)){
                itemOrderedCount.put(id, itemOrderedCount.get(id) + 1);
            }else{
                itemOrderedCount.put(id, 1);
            }
        }

        //we need frequently ordered two items.
        //so we will run a loop twice.
        for(int i=0; i<2; i++){
            //We will get one of the orders entry with the highest ordered count

            //if there are two or more items already there in the first cycle, we don't want to add more.
            if (mItemIds.size() >= 2){
                break;
            }

            Map.Entry<String, Integer> maxEntry = null;
            for(Map.Entry<String, Integer> entry : itemOrderedCount.entrySet()){
                if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                    maxEntry = entry;
                }
            }

            //we will create a final variable as we need to use it in a stream
            final int max = maxEntry.getValue();

            /*
            //we will create a set of all the items having the maximum order count
            //(multiple items may have the max ordered count)
            */
            Set<String> maxOrderedItems = itemOrderedCount.keySet().stream()
                    .filter(k -> itemOrderedCount.get(k) == max).collect(Collectors.toSet());


            //we will add all items from set to our list which we will return
            mItemIds.addAll(maxOrderedItems);

            /*
            //we will remove the item(s) which are the most ordered so that in
            //next loop we can find the second most frequently ordered item(s)
            */
            itemOrderedCount.entrySet().removeIf(e -> e.getValue() == max);

        }
        return mItemIds;
    }

    private static void printItems(List<String> itemIds){
        System.out.println("\n");
        for(String id : itemIds){
            int usageCount = 0;
            List<Integer> correspondingOrders = new ArrayList<>();
            for(int i=0; i<orders.size(); i++){
                Order mOrder = orders.get(i);
                for(Item item : mOrder.getItems()){
                    if(item.getId().equals(id)){
                        usageCount++;
                        correspondingOrders.add(i+1);
                    }
                }
            }
            String output = id + " -> usageCount: " + usageCount + ";" +
                    "corresponding orders: ";
            for(Integer orderId : correspondingOrders){
                output += "Order " + orderId + ", ";
            }

            //we will remove extra comma and space while printing the output.
            System.out.println(output.substring(0, output.length()-2));
        }
    }
}
