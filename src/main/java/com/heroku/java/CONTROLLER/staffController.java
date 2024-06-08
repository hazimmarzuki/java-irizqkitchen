package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.heroku.java.MODEL.staff;

import jakarta.servlet.http.HttpSession;

import java.sql.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Map;

import java.util.List;

@Controller
public class staffController {
    private final DataSource dataSource;

    @Autowired
    public staffController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/staffregister")
    public String staffregister(HttpSession session) {
        // int staffsid = (int) session.getAttribute("staffsid");
        // System.out.println("staff id :" + staffsid);
        return "admin/staffregister";
    }

    @GetMapping("/stafflist")
    public String staffList(HttpSession session, Model model) {

        List<staff> staffs = new ArrayList<>();
        // Retrieve the logged-in staff's role from the session
        String staffsrole = (String) session.getAttribute("staffsrole");
        System.out.println("staffrole stafflist : " + staffsrole);
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT staffsid, staffsname, staffsemail, staffspassword, staffsrole FROM staffs WHERE staffsrole=?";
            final var statement = connection.prepareStatement(sql);
            statement.setString(1, "baker");
            final var resultSet = statement.executeQuery();
            System.out.println("pass try stafflist >>>>>");

            while (resultSet.next()) {
                int userid = resultSet.getInt("staffsid");
                String fullname = resultSet.getString("staffsname");
                String email = resultSet.getString("staffsemail");
                String password = resultSet.getString("staffspassword");
                String role = resultSet.getString("staffsrole");
                System.out.println("role while" + email);
                System.out.println("role while" + fullname);

                staffs.add(new staff(userid, fullname, email, password, role));
                model.addAttribute("staffs", staffs);
                model.addAttribute("isAdmin", staffsrole != null && staffsrole.equals("admin")); // Add isAdmin flag to
                                                                                                 // the modelF

            }

            connection.close();

            return "admin/stafflist";
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as desired (e.g., show an error message)
            return "error";
        }

    }

    @GetMapping("/deletestaff/")
    public String deleteStaff(@RequestParam("staffsid") int staffsid, HttpSession session) {
        // Retrieve the logged-in staff's role from the session
        String staffsrole = (String) session.getAttribute("staffsrole");
        System.out.println("delete staff : " + staffsrole);
        System.out.println(staffsid);
        if (staffsrole != null && staffsrole.equals("admin")) {
            try (Connection connection = dataSource.getConnection()) {
                String sql = "DELETE FROM staffs WHERE staffsid = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, staffsid);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    // Deletion successful
                    connection.close();
                    return "redirect:/stafflist"; // Redirect back to the staff list
                } else {
                    // Deletion failed
                    connection.close();
                    return "redirect:/stafflist"; // Redirect to an error page or show an error message
                }

            } catch (SQLException e) {
                e.printStackTrace();
                // Handle the exception as desired (e.g., show an error message)
                return "admin/stafflist"; // Redirect to an error page or show an error message
            }
        }

        // Redirect to an error page or back to the staff list
        return "redirect:/stafflist";
    }

    @PostMapping("/staffregister")
    public String addAccountStaff(HttpSession session, @ModelAttribute("staffregister") staff staff) {
        String fullname = (String) session.getAttribute("staffsname");
        int userid = (int) session.getAttribute("staffsid");

        // debug
        System.out.println("fullname : " + fullname);
        System.out.println("userid : " + userid);
        try {
            Connection connection = dataSource.getConnection();
            String sql1 = "INSERT INTO staffs (staffsname, staffsemail, staffspassword, staffsrole,managersid) VALUES (?,?,?,?,?)";
            final var statement1 = connection.prepareStatement(sql1);

            String fname = staff.getFullname();
            String email = staff.getEmail();
            String password = staff.getPassword();
            System.out.println("password : " + password);
            System.out.println("fullname : " + fname);
            System.out.println("email : " + email);

            statement1.setString(1, fname);
            statement1.setString(2, email);
            statement1.setString(3, password);
            statement1.setString(4, "baker");
            statement1.setInt(5, (int) session.getAttribute("staffsid"));

            statement1.executeUpdate();

            connection.close();
            return "redirect:/login?success=true";

        } catch (SQLException sqe) {
            System.out.println("Error Code = " + sqe.getErrorCode());
            System.out.println("SQL state = " + sqe.getSQLState());
            System.out.println("Message = " + sqe.getMessage());
            System.out.println("printTrace /n");
            sqe.printStackTrace();

            return "redirect:/staffregister";
        } catch (Exception e) {
            System.out.println("E message : " + e.getMessage());
            return "redirect:/staffregister";
        }
    }

    @GetMapping("/staffprofile")
    public String viewprofilestaff(HttpSession session, Model model) {
        String fullname = (String) session.getAttribute("staffsname");
        int userid = (int) session.getAttribute("staffsid");
        String staffrole = (String) session.getAttribute("staffsrole");
        System.out.println("staff fullname : " + fullname);
        System.out.println("staff id : " + userid);
        System.out.println("staff role : " + staffrole);

        if (fullname != null) {
            try {
                Connection connection = dataSource.getConnection();
                final var statement = connection.prepareStatement(
                        "SELECT  staffsname, staffsemail, staffspassword,staffsrole FROM staffs WHERE staffsid = ?");
                statement.setInt(1, userid);
                final var resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String fname = resultSet.getString("staffsname");
                    String email = resultSet.getString("staffsemail");
                    String password = resultSet.getString("staffspassword");
                    String staffsrole = resultSet.getString("staffsrole");
                    // debug
                    System.out.println("fullname from db = " + fname);

                    staff staffprofile = new staff(userid, fname, email, password, staffsrole);

                    model.addAttribute("staffprofile", staffprofile);
                    System.out.println("fullname :" + staffprofile.getFullname());
                    // Return the view name for displaying staff details --debug
                    System.out.println("Session staffprofile : " + model.getAttribute("staffprofile"));

                }
                connection.close();
                return "staffprofile";
            } catch (SQLException e) {
                e.printStackTrace();
                return "login";
            }
        } else {
            return "login";
        }

    }

    // Update Profile staff
    @PostMapping("/staffupdate")
    public String updatestaff(HttpSession session, @ModelAttribute("staffprofile") staff staff, Model model) {
        int staffsid = (int) session.getAttribute("staffsid");
        String staffsrole = (String) session.getAttribute("staffsrole");

        String staffsname = staff.getFullname();
        String staffsemail = staff.getEmail();
        String staffspassword = staff.getPassword();

        // debug
        System.out.println("id update = " + staffsid);
        System.out.println("role update = " + staffsrole);

        try {
            Connection connection = dataSource.getConnection();
            String sql1 = "UPDATE staffs SET staffsname=? ,staffsemail=?, staffsrole=?, staffspassword=? WHERE staffsid=?";
            final var statement = connection.prepareStatement(sql1);

            statement.setString(1, staffsname);
            statement.setString(2, staffsemail);
            statement.setString(3, staffsrole);
            statement.setString(4, staffspassword);
            statement.setInt(5, staffsid);
            statement.executeUpdate();
            System.out.println("debug= " + staffsid + " " + staffsname + " " + staffsrole + " " + staffsemail + " "
                    + staffspassword);

            connection.close();

            return "redirect:/staffprofile?success=true";

        } catch (Throwable t) {
            System.out.println("message : " + t.getMessage());
            System.out.println("error");
            return "redirect:/staffprofile";
        }
    }

    // delete controller
    @GetMapping("/deletestaff")
    public String deleteProfileCust(HttpSession session, Model model) {
        String fullname = (String) session.getAttribute("staffsname");
        int userid = (int) session.getAttribute("staffsid");

        if (fullname != null) {
            try (Connection connection = dataSource.getConnection()) {

                // Delete user record
                final var deleteStaffStatement = connection.prepareStatement("DELETE FROM staffs WHERE staffsid=?");
                deleteStaffStatement.setInt(1, userid);
                int userRowsAffected = deleteStaffStatement.executeUpdate();

                if (userRowsAffected > 0) {
                    // Deletion successful
                    // You can redirect to a success page or perform any other desired actions

                    session.invalidate();
                    connection.close();
                    return "redirect:/";
                } else {
                    // Deletion failed
                    connection.close();
                    System.out.println("Delete Failed");
                    return "admin/deletestaff";

                }
            } catch (SQLException e) {
                // Handle any potential exceptions (e.g., log the error, display an error page)
                e.printStackTrace();

                // Deletion failed
                // You can redirect to an error page or perform any other desired actions
                System.out.println("Error");
            }
        }
        // Username is null or deletion failed, handle accordingly (e.g., redirect to an
        // error page)
        return "staff/stafforder";
    }

}
