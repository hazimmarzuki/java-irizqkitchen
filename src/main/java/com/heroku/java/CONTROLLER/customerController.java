package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.User;
import com.heroku.java.MODEL.customer;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
// import java.util.ArrayList;
// import java.util.Map;
import java.sql.SQLException;

// import org.jscience.physics.amount.Amount;
// import org.jscience.physics.model.RelativisticModel;
// import javax.measure.unit.SI;
@SpringBootApplication
@Controller
public class customerController {
    private final DataSource dataSource;

    @Autowired
    public customerController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // @Autowired
    // private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/customerregister")
    public String addAccount(HttpSession session, @ModelAttribute("customerregister") customer cust, User user) {
        try {
            Connection connection = dataSource.getConnection();
            String sql = "INSERT INTO customers (custname, custemail, custpassword, custaddress,custphone) VALUES (?,?,?,?,?)";
            final var statement1 = connection.prepareStatement(sql);

            String fullname = cust.getFullname();
            String email = cust.getEmail();
            String password = cust.getPassword();
            String custsaddress = cust.getCustaddress();
            String custsphone = cust.getCustphone();

            statement1.setString(1, fullname);
            statement1.setString(2, email);
            statement1.setString(3, password);
            statement1.setString(4, custsaddress);
            statement1.setString(5, custsphone);
            statement1.executeUpdate();

            // debug
            System.out.println("phonenumber: " + custsphone);

            connection.close();
            return "redirect:/login?success=true";

        } catch (SQLException sqe) {
            System.out.println("Error Code = " + sqe.getErrorCode());
            System.out.println("SQL state = " + sqe.getSQLState());
            System.out.println("Message = " + sqe.getMessage());
            System.out.println("printTrace /n");
            sqe.printStackTrace();

            return "redirect:/userregister?success=false";
        } catch (Exception e) {
            System.out.println("E message : " + e.getMessage());
            return "redirect:/userregister?success=false";
        }
    }

    @GetMapping("/custprofile")
    public String viewprofile(@RequestParam(name = "success", required = false) Boolean success, HttpSession session,
            Model model, customer cust) {
        String fullname = (String) session.getAttribute("custname");
        int userid = (int) session.getAttribute("custid");

        if (fullname != null) {

            try {
                Connection connection = dataSource.getConnection();
                final var statement = connection.prepareStatement("SELECT* FROM customers WHERE custid=?");
                statement.setInt(1, userid);
                final var resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String custname = resultSet.getString("custname");
                    String custemail = resultSet.getString("custemail");
                    String custpassword = resultSet.getString("custpassword");
                    String custaddress = resultSet.getString("custaddress");
                    String custphone = resultSet.getString("custphone");

                    // debug
                    System.out.println("fullname from db = " + custname);

                    customer custprofile = new customer(userid, custname, custemail, custpassword, custaddress,
                            custphone);

                    model.addAttribute("custprofile", custprofile);
                    System.out.println("fullname :" + custprofile.fullname);
                    // Return the view name for displaying customer details --debug
                    System.out.println("Session custprofile : " + model.getAttribute("custprofile"));

                }
                connection.close();
                return "custprofile";
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return "/login";
        }
        return "catalogue";

    }

    // Update Profile Customer
    @PostMapping("/updatecust")
    public String updateCust(HttpSession session, @ModelAttribute("custprofile") customer cust, Model model) {

        int custid = (int) session.getAttribute("custid");

        String custname = cust.getFullname();
        String custemail = cust.getEmail();
        String custaddress = cust.getCustaddress();
        String custpassword = cust.getPassword();
        String custphone = cust.getCustphone();

        try {
            Connection connection = dataSource.getConnection();
            String sql = "UPDATE customers SET custname=? ,custemail=?, custpassword=?, custaddress=?, custphone=? WHERE custid=?";

            final var statement = connection.prepareStatement(sql);
            statement.setString(1, custname);
            statement.setString(2, custemail);
            statement.setString(3, custpassword);
            statement.setString(4, custaddress);
            statement.setString(5, custphone);
            statement.setInt(6, custid);
            statement.executeUpdate();
            System.out.println(
                    "debug= " + custname + " " + custemail + " " + custpassword + " " + custphone + " " + custid);
            System.out.println("id database : " + custid);

            connection.close();

            return "redirect:/custprofile?success=true";

        } catch (Throwable t) {
            System.out.println("message : " + t.getMessage());
            System.out.println("error");
            return "redirect:/custprofile?success=false";
        }
    }

    // delete cust controller
    @GetMapping("/deletecust")
    public String deleteProfileCust(HttpSession session, customer customer, Model model) {
        String fullname = (String) session.getAttribute("custname");
        int userid = (int) session.getAttribute("custid");

        if (fullname != null) {
            try (Connection connection = dataSource.getConnection()) {
                // Check if the customer has associated orders
                PreparedStatement checkOrdersStatement = connection
                        .prepareStatement("SELECT COUNT(*) FROM orders WHERE custid = ?");
                checkOrdersStatement.setInt(1, userid);
                ResultSet ordersResultSet = checkOrdersStatement.executeQuery();
                ordersResultSet.next(); // Move the cursor to the first row
                int orderCount = ordersResultSet.getInt(1);
                if (orderCount > 0) {
                    // Customer has associated orders, prevent deletion
                    // You can redirect to a page indicating that the customer cannot be deleted due
                    // to active orders
                    return "redirect:/custprofile?success=false";
                }

                // No associated orders, proceed with customer deletion
                PreparedStatement deleteCustomerStatement = connection
                        .prepareStatement("DELETE FROM customers WHERE custid=?");
                deleteCustomerStatement.setInt(1, userid);
                int customerRowsAffected = deleteCustomerStatement.executeUpdate();

                if (customerRowsAffected > 0) {
                    // Deletion successful
                    session.invalidate();
                    connection.close();
                    return "redirect:/";
                } else {
                    // Deletion failed
                    return "redirect:/custprofile?error=deleteFailed";
                }
            } catch (SQLException e) {
                // Handle any potential exceptions (e.g., log the error, display an error page)
                e.printStackTrace();

                // Deletion failed
                System.out.println("Error");
                return "user/custprofile";
            }
        }
        // fullname is null or deletion failed, handle accordingly (e.g., redirect to an
        // error page)
        System.out.println("deletion failed");
        return "redirect:/custprofile";
    }

}
