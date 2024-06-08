package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.User;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
// import java.util.ArrayList;
// import java.util.Map;
import java.sql.SQLException;

// import org.jscience.physics.amount.Amount;
// import org.jscience.physics.model.RelativisticModel;
// import javax.measure.unit.SI;
@SpringBootApplication
@Controller

public class loginController {
    private final DataSource dataSource;

    @Autowired
    public loginController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String Loginpage(@RequestParam(name = "success", required = false) Boolean success, HttpSession session,
            String email, String password, User user, Model model) {

        // String returnPage = null;

        try {
            // String returnPage = null;
            Connection connection = dataSource.getConnection();

            String sql = "SELECT staffsid,staffsname,staffsemail,staffspassword,staffsrole FROM staffs WHERE staffsemail=? ";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            final var resultSet = statement.executeQuery();

            System.out.println("staffs pass : " + password);
            System.out.println("staffsemail : " + email);
            if (resultSet.next()) {

                int userid = resultSet.getInt("staffsid");
                String fullname = resultSet.getString("staffsname");
                String staffsemail = resultSet.getString("staffsemail");
                String staffspassword = resultSet.getString("staffspassword");
                String staffsrole = resultSet.getString("staffsrole");

                System.out.println(fullname);
                // if they're admin
                System.out.println("Email : " + staffsemail.equals(email) + " | " + email);
                System.out.println("Password status : " + staffspassword.equals(password));

                if (staffsemail.equals(email)
                        && staffspassword.equals(password)) {

                    session.setAttribute("staffsname", fullname);
                    session.setAttribute("staffsid", userid);
                    if (staffsrole.equals("admin")) {

                        session.setAttribute("staffsrole", "admin");
                        connection.close();
                        // debug
                        System.out.println("admin name : " + fullname);
                        System.out.println("admin id: " + userid);
                        System.out.println("admin role: " + staffsrole);
                        return "redirect:/stafforder?success=true";

                    } else if (staffsrole.equals("baker")) {

                        session.setAttribute("staffsrole", "baker");
                        connection.close();

                        // debug
                        System.out.println("staff name : " + fullname);
                        System.out.println("staff id: " + userid);
                        System.out.println("staff role: " + staffsrole);
                        return "redirect:/stafforder?success=true";
                    }
                }
            }

            String sql2 = "SELECT custid,custname,custemail,custpassword,custaddress,custphone FROM customers WHERE custemail=? ";
            final var statement2 = connection.prepareStatement(sql2);
            statement2.setString(1, email);

            final var resultSet2 = statement2.executeQuery();
            while (resultSet2.next()) {
                int userid = resultSet2.getInt("custid");
                String fullname = resultSet2.getString("custname");
                String custemail = resultSet2.getString("custemail");
                String custpassword = resultSet2.getString("custpassword");

                System.out.println("fullname : " + fullname);
                if (custemail.equals(email)
                        && custpassword.equals(password)) {

                    session.setAttribute("custid", userid);
                    session.setAttribute("custname", fullname);

                    connection.close();
                    return "redirect:/catalogue?success=true";
                }
            }

            connection.close();
            return "redirect:/login?success=false";

        } catch (SQLException sqe) {
            System.out.println("Error Code = " + sqe.getErrorCode());
            System.out.println("SQL state = " + sqe.getSQLState());
            System.out.println("Message = " + sqe.getMessage());
            System.out.println("printTrace /n");
            sqe.printStackTrace();

            return "redirect:/login?error";
        } catch (Exception e) {
            System.out.println("E message : " + e.getMessage());
            return "redirect:/login?error";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
