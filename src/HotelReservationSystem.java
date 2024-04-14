import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



public class HotelReservationSystem {

    private static final  String url ="jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "12345";

    
    public static void main(String[] args) throws InterruptedException, SQLException, ClassNotFoundException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //System.out.println("loaded");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());

        }

        try {
            Connection con = DriverManager.getConnection(url,username,password);
            Statement stm = con.createStatement();
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Enter your choice:");
                int choice = sc.nextInt();

                switch (choice){
                    case 1:
                        reserveRoom(con, sc, stm);
                        break;
                    case 2:
                        viewReservation(con, sc, stm);
                        break;
                    case 3:
                        getRoomNumber(con, sc, stm);
                        break;
                    case 4:
                        updateReservation(con, sc, stm);
                        break;
                    case 5:
                        deleteReservation(con, sc, stm);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid input. Try again.");

                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());

        }

    }




    private static void reserveRoom(Connection con, Scanner sc, Statement stm) {
        System.out.println("Enter guest name:");
        String guestName = sc.next();
        sc.nextLine();

        System.out.println("Enter room number:");
        int room = sc.nextInt();

        System.out.println("Enter contact number");
        String contactNumber= sc.next();
        sc.nextLine();

        String query = "insert into reservation (guest_name, room_number, contact_number) " +
                "values ('"+ guestName + "', "+ room + ", '"+ contactNumber + "');";

        try{

            int rowAffected = stm.executeUpdate(query);

            if(rowAffected > 0){
                System.out.println("Reservation successfully");
            } else {
                System.out.println("Reservation failed.");

            }

        }catch(SQLException e){
            e.printStackTrace();

        }
    }
    private static void viewReservation(Connection con, Scanner sc,Statement stm) {
        String query = "Select reserve_id, guest_name, room_number,contact_number ,reserve_date from reservation;";

        System.out.println("Current Reservations");
        System.out.println("+-----------------+-----------------+---------------+----------------------+-----------------------+");
        System.out.println("| Reservation Id  | Guest           | Room Number   | Contact Number       | Reservation Date      |");
        System.out.println("+-----------------+-----------------+---------------+----------------------+-----------------------+");


        try{

            ResultSet rs = stm.executeQuery(query);

           while (rs.next()){
               int reserve_id = rs.getInt("reserve_id");
               String guest_name = rs.getString("guest_name");
               int room_number = rs.getInt("room_number");
               String contact_number =rs.getString("contact_number");
               String reserve_date = rs.getTimestamp("reserve_date").toString();


               //format and display the reservation data in a table-like format
               System.out.printf("| %-17d| %-13s | %-11d   | %-20s | %-22s|\n",
                       reserve_id, guest_name, room_number,contact_number ,reserve_date);
               System.out.println("+-----------------+-----------------+---------------+----------------------+-----------------------+");
           }
        }catch(SQLException e){
            e.printStackTrace();

        }


    }
    private static void getRoomNumber(Connection con, Scanner sc,Statement stm) {
        System.out.println("Enter reservation id id:");
        int reserve_id = sc.nextInt();
        System.out.println("Enter guest name:");
        String guest_name = sc.next();

        String query = "select room_number from reservation where reserve_id =" + reserve_id + " AND guest_name='" + guest_name + "';";

        try{

            ResultSet rs = stm.executeQuery(query);


            if (rs.next()) {
               int roomNumber = rs.getInt("room_number");
                System.out.println("Room number for reservation id "+ reserve_id+
                                   " and guest name " +guest_name +" is" +roomNumber+ ".");

            }else{
                System.out.println("Reservation not found for given reseravtion id or guestname.");
            }
        }catch(SQLException e){
            e.printStackTrace();

        }

    }

    private static void updateReservation(Connection con, Scanner sc, Statement stm) {

            System.out.println("Enter resrvation ID to update: ");
            int reserveId = sc.nextInt();
            sc.nextLine();//Consume the next line character

            if (!reservationExists( reserveId, stm)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }


            System.out.println("Enter new guest name:");
            String newGuestName = sc.nextLine();
            System.out.println("Enter new room number:");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter new contact Number:");
            String newContactNumber = sc.next();

            //Select reserve_id, guest_name, room_number,contact_number ,reserve_date from reservation

            String query = "update reservation set guest_name='" + newGuestName + "', room_number =" + newRoomNumber +
                    ",contact_number = '" + newContactNumber + "' " +
                    "where reserve_id = " + reserveId + ";";

            try{

            int rowAffected = stm.executeUpdate(query);

            if(rowAffected > 0){
                System.out.println(" Reservation Updated successfully");
            } else {
                System.out.println("Reservation Updated failed.");

            }

        }catch(SQLException e){
            e.printStackTrace();

        }
    }



    private static void deleteReservation(Connection con, Scanner sc, Statement stm) {
        System.out.println("Enter resrvation ID to update: ");
        int reserveId = sc.nextInt();
        sc.nextLine();//Consume the next line character

        if (!reservationExists( reserveId,  stm)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }
        String query = "Delete from reservation where reserve_id =" +reserveId+ ";";
        try{

            int rowAffected = stm.executeUpdate(query);

            if(rowAffected > 0){
                System.out.println(" Reservation deleted successfully");
            } else {
                System.out.println("Reservation deleted failed.");

            }

        }catch(SQLException e){
            e.printStackTrace();

        }
    }
    private static boolean reservationExists( int reserveId, Statement stm) {

            String query =" select reserve_id from reservation where reserve_id = " +reserveId+ ";";

            try {
                ResultSet rs = stm.executeQuery(query);
                return rs.next();


        }catch(SQLException e){
            e.printStackTrace();
            return false;

        }

    }

    private static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i=5;
        while (i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank you Hotel Reservation System!!!");
    }

}