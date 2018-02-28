/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.tudd.productivity.controller;

import java.util.ArrayList;
import java.util.Timer;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.bean.ApplicationScoped;
import org.json.JSONObject;
import se.kth.id1212.tudd.productivity.integration.ProcessReader;
import se.kth.id1212.tudd.productivity.integration.ProductivityDAO;

@ApplicationScoped
@Stateless
public class Controller {

    @EJB
    ProductivityDAO ProductivityDAO;

    Timer timer;
    boolean twoMinutesPassed = false;

    public Controller() {
        try {
            ProductivityDAO = new ProductivityDAO();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void start() {
        ProcessReader reader = new ProcessReader();
        timer = new Timer();
        TimerTask everyTenMins = new TimerTask() {
            @Override
            public void run() {
                checkProductivity();
            }
        };

        timer.schedule(everyTenMins, 01, 1000 * 60);
    }

    public void checkProductivity() {
        ProcessReader reader = new ProcessReader();
        ArrayList<String> checked = reader.readProcesses();
        if (checked.contains("gaming")) {
            checked.remove("productive");
        }
        for (String category : checked) {
            if (twoMinutesPassed) {
                updateCategory(category);
            }
        }
        twoMinutesPassed = true;
    }

    private void updateCategory(String categoryName) {
        try {
            int timeInDatabase = ProductivityDAO.getCategoryTime(categoryName);
            ProductivityDAO.updateCategoryTime(categoryName, 1+timeInDatabase);
        } catch (Exception ex) {
            System.out.println("could not get category name");
        }
    }

    public String getProductivity() {
        JSONObject response = new JSONObject();
        String[] categories = {"gaming", "productive", "browsing"};
        try {
            HashMap<String, Integer> categoryResult = ProductivityDAO.getCategories();
            for (String category : categories) {
                response.put(category, categoryResult.get(category));
            }
        } catch (Exception ex) {
            return "{\"error\":\"An error occured.\"}";
        }
        return response.toString();
    }

}
