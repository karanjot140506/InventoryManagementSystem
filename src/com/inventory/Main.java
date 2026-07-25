package com.inventory;

import com.inventory.billing.BillGenerator;
import com.inventory.db.DatabaseInitializer;
import com.inventory.manager.InventoryManager;
import com.inventory.model.Bill;
import com.inventory.model.Product;
import com.inventory.report.SalesReportGenerator;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class Main {


    private static final InventoryManager inventoryManager = new InventoryManager();

    private static final BillGenerator billGenerator =
            new BillGenerator(inventoryManager);

    private static final SalesReportGenerator reportGenerator =
            new SalesReportGenerator(inventoryManager);


    private static final Scanner scanner = new Scanner(System.in);



    public static void main(String[] args) {


        System.out.println(
                "DB URL BEING USED: "
                        + com.inventory.db.DBConfig.URL
        );


        // CREATE DATABASE TABLES FIRST
        DatabaseInitializer.initialize();


        System.out.println(
                "Starting Inventory Management System..."
        );


        inventoryManager.loadFromDatabase();



        boolean running = true;


        while(running){


            printMenu();


            String choice = scanner.nextLine().trim();



            switch(choice){


                case "1":
                    addProduct();
                    break;


                case "2":
                    updateStock();
                    break;


                case "3":
                    generateBill();
                    break;


                case "4":
                    searchProducts();
                    break;


                case "5":
                    showSalesReport();
                    break;


                case "6":
                    listAllProducts();
                    break;


                case "0":

                    running=false;

                    System.out.println(
                            "Exiting... Goodbye!"
                    );

                    break;



                default:

                    System.out.println(
                            "Invalid choice, try again."
                    );
            }

        }


        scanner.close();

    }





    private static void printMenu(){


        System.out.println(
                "\n=========== INVENTORY MANAGEMENT SYSTEM ==========="
        );


        System.out.println("1. Add Product");
        System.out.println("2. Update Stock");
        System.out.println("3. Generate Bill");
        System.out.println("4. Search Products");
        System.out.println("5. Sales / Stock Report (multithreaded)");
        System.out.println("6. List All Products");
        System.out.println("0. Exit");


        System.out.print("Enter choice: ");

    }





    private static void addProduct(){


        System.out.print("Product name: ");
        String name=scanner.nextLine();


        System.out.print("Category: ");
        String category=scanner.nextLine();


        System.out.print("Price: ");
        double price=readDouble();


        System.out.print("Initial quantity: ");
        int qty=readInt();



        Product p =
                inventoryManager.addProduct(
                        name,
                        category,
                        price,
                        qty
                );


        if(p!=null){

            System.out.println(
                    "Product added successfully ID: "
                            + p.getId()
            );

        }
        else{

            System.out.println(
                    "Failed to add product"
            );

        }

    }





    private static void updateStock(){


        System.out.print("Product ID: ");

        int id=readInt();



        Product p =
                inventoryManager.getProduct(id);



        if(p==null){

            System.out.println(
                    "Product not found"
            );

            return;

        }



        System.out.println(
                "Current Stock: "
                        + p.getQuantity()
        );



        System.out.print(
                "New quantity: "
        );


        int qty=readInt();



        boolean result =
                inventoryManager.updateStock(
                        id,
                        qty
                );



        System.out.println(
                result ?
                        "Stock Updated":
                        "Update Failed"
        );

    }





    private static void generateBill(){


        System.out.print(
                "Customer name: "
        );


        String customer =
                scanner.nextLine();



        Bill bill =
                new Bill(
                        billGenerator.nextLocalBillId(),
                        customer
                );



        while(true){


            System.out.print(
                    "Product ID (0 finish): "
            );


            int id=readInt();



            if(id==0)
                break;



            System.out.print(
                    "Quantity: "
            );


            int qty=readInt();



            billGenerator.addItemToBill(
                    bill,
                    id,
                    qty
            );

        }



        if(bill.getItems().isEmpty()){

            System.out.println(
                    "Bill cancelled"
            );

            return;

        }



        int saved =
                billGenerator.finalizeBill(
                        bill
                );


        System.out.println(
                bill.printBill()
        );


        System.out.println(
                "Bill saved ID: "
                        + saved
        );


    }





    private static void searchProducts(){


        System.out.print(
                "Keyword: "
        );


        String key=scanner.nextLine();



        List<Product> list =
                inventoryManager.searchProducts(key);



        for(Product p:list){

            System.out.println(p);

        }


    }





    private static void showSalesReport(){


        try{


            List<SalesReportGenerator.CategoryReport> reports =
                    reportGenerator.generateReport(5);



            reportGenerator.printReport(reports);



        }
        catch(Exception e){

            e.printStackTrace();

        }


    }





    private static void listAllProducts(){


        List<Product> products =
                inventoryManager.getAllProducts();



        if(products.isEmpty()){

            System.out.println(
                    "No products in inventory yet."
            );

            return;

        }



        for(Product p:products){

            System.out.println(p);

        }

    }





    private static int readInt(){

        while(true){

            try{

                return Integer.parseInt(
                        scanner.nextLine()
                );

            }
            catch(Exception e){

                System.out.print(
                        "Enter valid number: "
                );

            }

        }

    }





    private static double readDouble(){

        while(true){

            try{

                return Double.parseDouble(
                        scanner.nextLine()
                );

            }
            catch(Exception e){

                System.out.print(
                        "Enter valid price: "
                );

            }

        }

    }

}